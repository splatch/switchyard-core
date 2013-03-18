package org.switchyard.bus.camel.processors;

import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.switchyard.HandlerException;
import org.switchyard.bus.camel.CamelExchange;

public class ErrorHandlingProcessor implements Processor {

    @Override
    public void process(Exchange exchange) throws Exception {
        CamelExchange ex = new CamelExchange(exchange);
        Throwable content = detectHandlerException(exchange.getProperty(Exchange.EXCEPTION_CAUGHT, Exception.class));
        ex.sendFault(ex.createMessage().setContent(content));
    }

    private Throwable detectHandlerException(Throwable throwable) {
        if (throwable instanceof HandlerException) {
            return (HandlerException) throwable;
        }
        return new HandlerException(throwable);
    }

}
