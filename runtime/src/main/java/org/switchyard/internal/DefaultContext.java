/* 
 * JBoss, Home of Professional Open Source 
 * Copyright 2011 Red Hat Inc. and/or its affiliates and other contributors
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

package org.switchyard.internal;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.switchyard.Context;
import org.switchyard.Property;
import org.switchyard.Scope;
import org.switchyard.label.BehaviorLabel;
import org.switchyard.serial.graph.AccessType;
import org.switchyard.serial.graph.Strategy;

/**
 * Base context implementation.
 */
@Strategy(access=AccessType.FIELD)
public class DefaultContext implements Context {

    private Scope _scope;
    private Map<String, Property> _properties = new HashMap<String, Property>();

    /**
     * Create a new DefaultContext instance using the specified property map.
     * @param defaultScope Scope handled by context.
     * @param properties context properties
     */
    public DefaultContext(Scope defaultScope, Map<String, Property> properties) {
        _scope = defaultScope;
        _properties = properties;
    }

    public DefaultContext(Scope defaultScope) {
        _scope = defaultScope;
    }

    public DefaultContext() { }

    @Override
    @SuppressWarnings("unchecked")
    public <T> T getPropertyValue(String name) {
       Property prop = _properties.get(name);
       if (prop != null) {
           return (T) prop.getValue();
       }
       return null;
    }

    @Override
    public void removeProperties() {
        _properties.clear();
    }

    @Override
    public Context setProperties(Set<Property> properties) {
        for (Property p : properties) {
            _properties.put(p.getName(), p);
        }
        return this;
    }

    @Override
    public Set<Property> getProperties() {
        return new HashSet<Property>(_properties.values());
    }

    @Override
    public void removeProperty(Property property) {
        _properties.remove(property.getName());
    }

    @Override
    public Property getProperty(String name) {
        return _properties.get(name);
    }

    @Override
    public Property setProperty(String name, Object val) {
        Property p = new ContextProperty(name, _scope, val);
        _properties.put(p.getName(), p);
        return p;
    }

    @Override
    public Context copy() {
        Context ctx = new DefaultContext(_scope, new HashMap<String, Property>());
        for (Property property : getProperties()) {
            if (!property.hasLabel(BehaviorLabel.TRANSIENT.label())) {
                String[] labels = property.getLabels().toArray(new String[property.getLabels().size()]);
                ctx.setProperty(property.getName(), property.getValue()).addLabels(labels);
            }
        }
        return ctx;
    }

    @Override
    public Set<Property> getProperties(String label) {
        Set<Property> props = new HashSet<Property>();
        for (Property p : getProperties()) {
            if (p.hasLabel(label)) {
                props.add(p);
            }
        }
        return props;
    }

    @Override
    public void removeProperties(String label) {
        for (Property p : getProperties()) {
            if (p.hasLabel(label)) {
                removeProperty(p);
            }
        }
    }
}
