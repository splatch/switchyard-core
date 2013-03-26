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
package org.switchyard;

import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.switchyard.label.BehaviorLabel;

/**
 * MockContext.
 *
 * @author David Ward &lt;<a href="mailto:dward@jboss.org">dward@jboss.org</a>&gt; (C) 2011 Red Hat Inc.
 */
public class MockContext implements Context {

    private final Map<String,Property> _properties = Collections.synchronizedMap(new HashMap<String,Property>());
	private Scope _scope;

    public MockContext() {}

    private MockContext(Map<String, Property> properties) {
        _properties.putAll(properties);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Property getProperty(String name) {
        return _properties.get(name);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Object getPropertyValue(String name) {
        Property property = getProperty(name);
        return property != null ? property.getValue() : null;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Set<Property> getProperties() {
        return new HashSet<Property>(_properties.values());
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void removeProperty(Property property) {
        _properties.remove(property.getName());
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void removeProperties() {
        _properties.clear();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Property setProperty(String name, Object val) {
        MockProperty property = new MockProperty(name, val, _scope);
        _properties.put(name, property);
        return property;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Context setProperties(Set<Property> properties) {
        for (Property property : properties) {
            setProperty(property.getName(), property.getValue());
        }
        return this;
    }

    @Override
    public Context copy() {
        MockContext ctx = new MockContext(_properties);
        ctx.removeProperties(BehaviorLabel.TRANSIENT.label());
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
