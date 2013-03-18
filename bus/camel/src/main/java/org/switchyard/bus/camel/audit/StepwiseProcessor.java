package org.switchyard.bus.camel.audit;

import org.apache.camel.AsyncCallback;
import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.apache.camel.model.ProcessorDefinition;
import org.apache.camel.processor.DelegateAsyncProcessor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class StepwiseProcessor extends DelegateAsyncProcessor {

    private Logger logger = LoggerFactory.getLogger(StepwiseProcessor.class);
	private final ProcessorDefinition<?> definition;

    public StepwiseProcessor(Processor target, ProcessorDefinition<?> definition) {
        super(target);
        this.definition = definition;
    }

    @Override
    public boolean process(final Exchange exchange, final AsyncCallback callback) {
		logger.info("Going into " + StepwiseProcessor.this.getProcessor() + " " + definition.getShortName() + " (child of: " + definition.getParent().getShortName());
        return super.process(exchange, new AsyncCallback() {
            @Override
            public void done(boolean doneSync) {
                if (doneSync) {
                    logger.info("After " + StepwiseProcessor.this.getProcessor() + " " + exchange);
                    callback.done(doneSync);
                }
            }
        });
    }

}
