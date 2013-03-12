package org.switchyard.bus.camel;

import org.apache.camel.AsyncCallback;
import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.apache.camel.processor.DelegateAsyncProcessor;

public class InOutProcessor extends DelegateAsyncProcessor {

    public InOutProcessor(Processor target) {
        super(target);
    }

    @Override
    public boolean process(final Exchange exchange, final AsyncCallback callback) {
        return super.process(exchange, new AsyncCallback() {
            @Override
            public void done(boolean doneSync) {
                if (doneSync) {
                    if (exchange.hasOut()) {
                        exchange.setOut(exchange.getIn());
                    }
                    callback.done(doneSync);
                }
            }
        });
    }

}
