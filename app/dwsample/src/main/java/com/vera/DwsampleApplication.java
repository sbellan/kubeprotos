package com.vera;

import com.vera.resources.HelloWorldResource;
import io.dropwizard.Application;
import io.dropwizard.jersey.setup.JerseyEnvironment;
import io.dropwizard.setup.Bootstrap;
import io.dropwizard.setup.Environment;

public class DwsampleApplication extends Application<DwsampleConfiguration> {

    public static void main(final String[] args) throws Exception {
        DwsampleApplication dwsampleApplication = new DwsampleApplication();
        dwsampleApplication.run(args);
    }

    @Override
    public String getName() {
        return "dwsample";
    }

    @Override
    public void initialize(final Bootstrap<DwsampleConfiguration> bootstrap) {
        bootstrap.addCommand(new ProducerCommand("producer", "producer"));
        bootstrap.addCommand(new ConsumerCommand("consumer", "consumer"));
    }

    @Override
    public void run(final DwsampleConfiguration configuration,
                    final Environment environment) {
        JerseyEnvironment jersey = environment.jersey();
        jersey.register(new HelloWorldResource());
    }

}
