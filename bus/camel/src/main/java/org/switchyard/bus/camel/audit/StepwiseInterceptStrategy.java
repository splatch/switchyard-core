package org.switchyard.bus.camel.audit;

import org.apache.camel.CamelContext;
import org.apache.camel.Processor;
import org.apache.camel.model.ProcessorDefinition;
import org.apache.camel.spi.InterceptStrategy;

public class StepwiseInterceptStrategy implements InterceptStrategy {

    @Override
    public Processor wrapProcessorInInterceptors(CamelContext context,
        ProcessorDefinition<?> definition, Processor target,
        Processor nextTarget) throws Exception {
        return new StepwiseProcessor(target, definition);
    }

}
