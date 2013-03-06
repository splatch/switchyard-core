package org.switchyard.bus.camel.audit;

import org.apache.camel.AsyncCallback;
import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.apache.camel.processor.DelegateAsyncProcessor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class StepwiseProcessor extends DelegateAsyncProcessor {

    private Logger logger = LoggerFactory.getLogger(StepwiseProcessor.class);

    public StepwiseProcessor(Processor target) {
        super(target);
    }

    @Override
    public boolean process(final Exchange exchange, final AsyncCallback callback) {
        return super.process(exchange, new AsyncCallback() {
            @Override
            public void done(boolean doneSync) {
                if (doneSync) {
                    logger.info("Going into " + StepwiseProcessor.this.getProcessor() + "\n"
                         + exchange.getExchangeId() + " " + exchange.isFailed() + " "
                         + exchange.getIn() + " " + (exchange.hasOut() ? exchange.getOut() : null));
                    callback.done(doneSync);
                }
            }
        });
    }

}
