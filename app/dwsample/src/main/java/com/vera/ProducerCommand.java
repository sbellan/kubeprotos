package com.vera;

import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
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
public class ProducerCommand extends ConfiguredCommand<DwsampleConfiguration> {

    static Logger logger = LoggerFactory.getLogger(ProducerCommand.class);

    private final static String QUEUE_NAME = "task_queue";

    protected ProducerCommand(String name, String description) {
        super(name, description);
    }

    @Override
    protected void run(Bootstrap bootstrap, Namespace namespace, DwsampleConfiguration configuration) throws Exception {
        ConnectionFactory factory = new ConnectionFactory();
        factory.setHost("rabbitmq.test-rabbitmq");
        factory.setPort(5672);

        Channel channel = establishChannel(factory);

        try {

            while (true) {
                try {
                    String message = String.valueOf(System.currentTimeMillis());
                    channel.basicPublish("", QUEUE_NAME, null, message.getBytes("UTF-8"));
                    logger.info(" [x] Producing '" + message + "'");
                    Thread.sleep(2000);
                } catch (IOException e) {
                    logger.error("Lost connection.. reestablishing", e);
                    closeSilently(channel);
                    channel = establishChannel(factory);
                }
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
