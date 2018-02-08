package com.vera;

import com.rabbitmq.client.AMQP;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import com.rabbitmq.client.Consumer;
import com.rabbitmq.client.DefaultConsumer;
import com.rabbitmq.client.Envelope;
import com.rabbitmq.client.ShutdownListener;
import com.rabbitmq.client.ShutdownSignalException;
import io.dropwizard.Configuration;
import io.dropwizard.cli.ConfiguredCommand;
import io.dropwizard.setup.Bootstrap;
import net.sourceforge.argparse4j.inf.Namespace;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.concurrent.TimeoutException;

/**
 * Created by temp on 2/5/18.
 */
public class ConsumerCommand extends ConfiguredCommand<DwsampleConfiguration> {

    static Logger logger = LoggerFactory.getLogger(ConsumerCommand.class);

    private final static String QUEUE_NAME = "task_queue";

    protected ConsumerCommand(String name, String description) {
        super(name, description);
    }

    @Override
    protected void run(Bootstrap bootstrap, Namespace namespace, DwsampleConfiguration configuration) throws Exception {
        ConnectionFactory factory = new ConnectionFactory();
        factory.setHost("rabbitmq.test-rabbitmq");
        factory.setPort(5672);

        Channel channel = establishChannel(factory);

        try {
            Consumer consumer = new DefaultConsumer(channel) {
                @Override
                public void handleDelivery(String consumerTag, Envelope envelope, AMQP.BasicProperties properties, byte[] body)
                        throws IOException {
                    String message = new String(body, "UTF-8");
                    logger.info(" [x] Processing '" + message + "'");
                    try {
                        Thread.sleep(1000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            };

            channel.basicConsume(QUEUE_NAME, true, consumer);

            while (true) {
                Thread.sleep(30000);
            }
        } finally {
            if (channel != null) {
                closeSilently(channel);
            }
        }
    }

    private Channel establishChannel(ConnectionFactory factory) throws InterruptedException {
        boolean connected = false;
        Connection connection = null;
        Channel channel = null;
        while (!connected) {
            try {
                connection = factory.newConnection();
                channel = connection.createChannel();
                channel.queueDeclare(QUEUE_NAME, false, false, false, null);
                connected = true;
            } catch (Exception e) {
                logger.error("Failed to connect to broker at {}/{}.. will retry", factory.getHost(), factory.getPort(), e);
                closeSilently(channel);
                Thread.sleep(30000);
            }
        }
        return channel;
    }

    private void closeSilently(Channel channel) {
        if (channel != null) {
            Connection connection = channel.getConnection();
            try {
                channel.close();
            } catch (IOException ignore) {
            } catch (TimeoutException e1) {
                logger.warn("Timeout exception on channel close", e1);
            }
            if (connection != null) {
                try {
                    connection.close();
                } catch (IOException ignore) {
                }
            }
        }
    }
}
