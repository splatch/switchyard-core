package org.switchyard.bus.camel;

import org.apache.camel.CamelContext;
import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.apache.camel.processor.ErrorHandlerSupport;
import org.apache.camel.util.ServiceHelper;
import org.switchyard.bus.camel.processors.Processors;

public class SwitchYardErrorHandler extends ErrorHandlerSupport {

    private final Processor output;

    public SwitchYardErrorHandler(CamelContext camelContext, Processor output) {
        this.output = output;
    }

    @Override
    public void process(Exchange exchange) throws Exception {
        output.process(exchange);
        if (exchange.getException() != null) {
            exchange.setProperty(Exchange.EXCEPTION_CAUGHT, exchange.getException());
            handleException(exchange);
        }
    }

    protected void handleException(Exchange exchange) throws Exception {
        exchange.getContext().getRegistry().lookup(Processors.ERROR_HANDLING.name(),
            Processor.class).process(exchange);
    }


    @Override
    public boolean supportTransacted() {
        return true;
    }

    @Override
    public Processor getOutput() {
        return output;
    }

    @Override
    protected void doStart() throws Exception {
        ServiceHelper.startServices(output);
    }

    @Override
    protected void doStop() throws Exception {
    }

    @Override
    protected void doShutdown() throws Exception {
        ServiceHelper.stopAndShutdownServices(output);
    }

}
