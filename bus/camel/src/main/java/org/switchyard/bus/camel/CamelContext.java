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

import java.util.HashSet;
import java.util.Set;

import org.apache.camel.Exchange;
import org.switchyard.Context;
import org.switchyard.Property;
import org.switchyard.Scope;
import org.switchyard.bus.camel.MessageProperty.InMessageProperty;
import org.switchyard.bus.camel.MessageProperty.OutMessageProperty;

public class CamelContext implements Context {

    private final Exchange _exchange;

    public CamelContext(Exchange exchange) {
        _exchange = exchange;
    }

    @Override
    public CamelProperty getProperty(String name) {
        return getProperty(name, Scope.EXCHANGE);
    }

    @Override
    public CamelProperty getProperty(String name, Scope scope) {
        return property(name, scope);
    }

    @Override
    public Object getPropertyValue(String name) {
        CamelProperty property = property(name, Scope.EXCHANGE);
        return property == null ? null : property.getValue();
    }

    @Override
    public Set<Property> getProperties() {
        return getProperties(Scope.EXCHANGE);
    }

    @Override
    public Set<Property> getProperties(Scope scope) {
        return properties(scope);
    }

    @Override
    public void removeProperty(Property property) {
        ((CamelProperty) property).remove();
    }

    @Override
    public void removeProperties() {
        removeProperties(Scope.EXCHANGE);
        removeProperties(Scope.IN);
        removeProperties(Scope.OUT);
    }

    @Override
    public void removeProperties(Scope scope) {
        for (Property property : getProperties(scope)) {
            ((CamelProperty) property).remove();
        }
    }

    @Override
    public Property setProperty(String name, Object val) {
        return setProperty(name, val, Scope.EXCHANGE);
    }

    @Override
    public Property setProperty(String name, Object val, Scope scope) {
        return property(name, scope, true).setValue(val);
    }

    @Override
    public Context setProperties(Set<Property> properties) {
        for (Property property : properties) {
            setProperty(property.getName(), property.getValue(), property.getScope());
        }
        return this;
    }

    private CamelProperty property(String name, Scope scope) {
        return property(name, scope, false);
    }

    private CamelProperty property(String name, Scope scope, boolean create) {
        if (name.startsWith("org.switchyard.bus.camel")) {
            return null;
        }
        CamelProperty property;
        switch (scope) {
        case IN: 
            property = new InMessageProperty(_exchange, name);
            break;
        case OUT: 
            property = new OutMessageProperty(_exchange, name);
            break;
        default:
            property = new ExchangeProperty(_exchange, name);
        }

        return property.exists() || create ? property : null;
    }

    private Set<Property> properties(Scope scope) {
        Set<Property> properties = new HashSet<Property>();
        switch (scope) {
        case IN:
            for (String name : InMessageProperty.getNames(_exchange)) {
                CamelProperty property = property(name, scope);
                if (property != null) {
                    properties.add(property);
                }
            }
            break;
        case OUT:
            for (String name : OutMessageProperty.getNames(_exchange)) {
                CamelProperty property = property(name, scope);
                if (property != null) {
                    properties.add(property);
                }
            }
            break;
        default:
            for (String name : ExchangeProperty.getNames(_exchange)) {
                CamelProperty property = property(name, scope);
                if (property != null) {
                    properties.add(property);
                }
            }
        }
        return properties;
    }

    @Override
    public Context copy() {
        return new CamelContext(_exchange.copy());
    }

    @Override
    public Set<Property> getProperties(String label) {
        Set<Property> properties = new HashSet<Property>();
        for (Property property : getProperties()) {
            if (property.hasLabel(label)) {
                properties.add(property);
            }
        }
        return properties;
    }

    @Override
    public void removeProperties(String label) {
        for (Property property : getProperties()) {
            if (property.hasLabel(label)) {
                ((CamelProperty) property).remove();
            }
        }
    }

}
