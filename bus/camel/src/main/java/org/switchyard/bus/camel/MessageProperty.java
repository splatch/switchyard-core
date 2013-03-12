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
package org.switchyard.bus.camel;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import org.apache.camel.Exchange;
import org.apache.camel.Message;
import org.switchyard.Property;
import org.switchyard.Scope;

abstract class MessageProperty extends CamelProperty {

    protected final Exchange _exchange;

    public MessageProperty(Scope scope, Exchange exchange, String name) {
        super(scope, name);
        _exchange = exchange;
    }

    protected abstract Message getMessage();

    @Override
    public Object getValue() {
        return getMessage() != null ? getMessage().getHeader(getName()) : null;
    }

    @Override
    public Property setValue(Object val) {
        if (getMessage() != null) {
            getMessage().setHeader(getName(), val);
        }
        return this;
    }

    @Override
    public void remove() {
        if (getMessage() != null) {
            getMessage().removeHeader(getName());
        }
    }

    @Override
    public boolean exists() {
        return getMessage() != null && getMessage().getHeaders().containsKey(getName());
    }

    @Override
    @SuppressWarnings("unchecked")
    protected Map<String, Set<String>> getLabelsBag() {
        return getMessage() != null ? getMessage().getHeader(LABELS, Map.class) : new HashMap<String, Set<String>>();
    }

    public static class InMessageProperty extends MessageProperty {
        public InMessageProperty(Exchange exchange, String name) {
            super(Scope.IN, exchange, name);
        }

        @Override
        protected Message getMessage() {
            return _exchange.getIn();
        }
    }

    public static class OutMessageProperty extends MessageProperty {
        public OutMessageProperty(Exchange exchange, String name) {
            super(Scope.OUT, exchange, name);
        }

        @Override
        protected Message getMessage() {
            return _exchange.hasOut() ? _exchange.getOut() : null;
        }
    }

}
