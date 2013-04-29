/* 
 * JBoss, Home of Professional Open Source 
 * Copyright 2013 Red Hat Inc. and/or its affiliates and other contributors
 * as indicated by the @author tags. All rights reserved. 
 * See the copyright.txt in the distribution for a 
 * full listing of individual contributors.
 *
 * This copyrighted material is made available to anyone wishing to use, 
 * modify, copy, or redistribute it subject to the terms and conditions 
 * of the GNU Lesser General Public License, v. 2.1. 
 * This program is distributed in the hope that it will be useful, but WITHOUT A 
 * WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A 
 * PARTICULAR PURPOSE.  See the GNU Lesser General Public License for more details. 
 * You should have received a copy of the GNU Lesser General Public License, 
 * v.2.1 along with this distribution; if not, write to the Free Software 
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, 
 * MA  02110-1301, USA.
 */
package org.switchyard.bus.camel.audit;

import org.apache.camel.AsyncCallback;
import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.apache.camel.model.ProcessDefinition;
import org.apache.camel.model.ProcessorDefinition;
import org.apache.camel.processor.DelegateAsyncProcessor;
import org.apache.log4j.Logger;

/**
 * Debugging processor which display information about exchange during processing
 * by SwitchYard handlers.
 */
public class StepwiseProcessor extends DelegateAsyncProcessor {

    private final ProcessDefinition _definition;
    private Logger _logger = Logger.getLogger(StepwiseProcessor.class);

    /**
     * Creates new processor.
     * 
     * @param target Target processor.
     * @param definition Definition to wrap.
     */
    public StepwiseProcessor(Processor target, ProcessDefinition definition) {
        super(target);
        this._definition = definition;
    }

    @Override
    public boolean process(Exchange exchange, final AsyncCallback callback) {
        ProcessorDefinition<?> parent = _definition.getParent();
        String parentStr = parent.getClass().getName();
        if (_definition.getParent().getParent() != null) {
            parentStr += " > " + _definition.getParent().getParent().getClass().getName();
        }
        _logger.debug("Before " + _definition.getRef() + " " + parentStr);
        return super.process(exchange, new AsyncCallback() {
            @Override
            public void done(boolean doneSync) {
                if (doneSync) {
                    _logger.debug("after " + _definition.getRef());
                    callback.done(doneSync);
                }
            }
        });
    }
}
