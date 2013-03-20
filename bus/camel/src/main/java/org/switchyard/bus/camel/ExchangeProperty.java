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
import org.switchyard.Property;
import org.switchyard.Scope;

class ExchangeProperty extends CamelProperty {

    private final Exchange _exchange;

    public ExchangeProperty(Exchange exchange, String name) {
        super(Scope.EXCHANGE, name);
        _exchange = exchange;
    }

    @Override
    public boolean exists() {
        return _exchange.getProperties().containsKey(getName());
    }

    @Override
    public Object getValue() {
        return _exchange.getProperty(getName());
    }

    @Override
    public Property setValue(Object val) {
        _exchange.setProperty(getName(), val);
        return this;
    }

    @Override
    public void remove() {
        _exchange.removeProperty(getName());
    }

    @Override
    @SuppressWarnings("unchecked")
    protected Map<String, Set<String>> getLabelsBag() {
        if (!_exchange.getProperties().containsKey(LABELS) || _exchange.getProperty(LABELS) == null) {
            _exchange.setProperty(LABELS, new HashMap<String, Set<String>>());
        }
        return _exchange.getProperty(LABELS, Map.class);
    }

    public static Set<String> getNames(Exchange exchange) {
        return exchange.getProperties().keySet();
    }

}