package org.switchyard.bus.camel;

import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.apache.camel.processor.DelegateProcessor;

public class InOutProcessor extends DelegateProcessor {

    public InOutProcessor(Processor target) {
        super(target);
    }

    @Override
    public void process(final Exchange exchange) throws Exception {
        super.processNext(exchange);
        if (!exchange.hasOut()) {
            exchange.setOut(exchange.getIn());
        }
    }

    @Override
    public String toString() {
        return "InOutProcessor [" + getProcessor() + "]";
    }
}
