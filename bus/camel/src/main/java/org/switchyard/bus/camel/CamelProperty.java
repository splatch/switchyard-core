package org.switchyard.bus.camel;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.switchyard.Property;
import org.switchyard.Scope;

public abstract class CamelProperty implements Property {

    public final static String LABELS = "org.switchyardbus.camel.labels";

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
        Map<String, Set<String>> labels = getLabelsBag();
        if (!labels.containsKey(getName())) {
            labels.put(getName(), new HashSet<String>());
        }
        return labels.get(getName());
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

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((_name == null) ? 0 : _name.hashCode());
        result = prime * result + ((_scope == null) ? 0 : _scope.hashCode());
        result = prime * result + ((getValue() == null) ? 0 : getValue().hashCode());
        return result;
    }

    /* (non-Javadoc)
     * @see java.lang.Object#equals(java.lang.Object)
     */
    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        CamelProperty other = (CamelProperty) obj;
        if (_name == null) {
            if (other._name != null)
                return false;
        } else if (!_name.equals(other._name))
            return false;
        if (_scope != other._scope)
            return false;
        if (getValue() == null) {
            if (other.getValue() != null)
                return false;
        } else if (!getValue().equals(other.getValue())) {
            return false;
        }
        return true;
    }

}
