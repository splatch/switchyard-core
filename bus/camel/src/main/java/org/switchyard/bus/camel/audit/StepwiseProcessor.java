package org.switchyard.bus.camel.audit;

import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.apache.camel.model.ProcessorDefinition;
import org.apache.camel.processor.DelegateProcessor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class StepwiseProcessor extends DelegateProcessor {

    private Logger logger = LoggerFactory.getLogger(StepwiseProcessor.class);
    private final ProcessorDefinition<?> definition;

    public StepwiseProcessor(Processor target, ProcessorDefinition<?> definition) {
        super(target);
        this.definition = definition;
    }

    @Override
    public void process(final Exchange exchange) throws Exception {
        logger.info("Going into " + getProcessor() + " " + definition.getShortName() + " (child of: " + definition.getParent().getShortName() +")");
        super.processNext(exchange);
        logger.info("After " + getProcessor() + " " + exchange);
    }

}
