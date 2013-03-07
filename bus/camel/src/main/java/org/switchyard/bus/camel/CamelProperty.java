package org.switchyard.bus.camel;

import java.util.Arrays;
import java.util.Map;
import java.util.Set;

import org.switchyard.Property;
import org.switchyard.Scope;

public abstract class CamelProperty implements Property {

    public final static String LABELS = "org.switchyard.message.property.labels";

    private final Scope _scope;
    private final String _name;

    protected CamelProperty(Scope scope, String name) {
        this._scope = scope;
        this._name = name;
    }

    public abstract void remove();
    public abstract Property setValue(Object val);
    protected abstract Map<String, Set<String>> getLabelsBag();

    @Override
    public Scope getScope() {
        return _scope;
    }

    @Override
    public String getName() {
        return _name;
    }

    @Override
    public Set<String> getLabels() {
        return getLabelsBag().get(getName());
    }

    @Override
    public Property addLabels(String... labels) {
        getLabels().addAll(Arrays.asList(labels));
        return this;
    }

    @Override
    public Property removeLabels(String... labels) {
        getLabels().removeAll(Arrays.asList(labels));
        return this;
    }

    @Override
    public boolean hasLabel(String label) {
        return getLabels().contains(label);
    }

}
