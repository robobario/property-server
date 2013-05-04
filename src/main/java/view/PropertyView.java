package view;

import com.google.common.base.Objects;

public class PropertyView {
    private final boolean inherited;
    private String key;
    private String derivedValue;
    private String value;

    private PropertyView(boolean isInherited, String key, String valu, String derivedValue) {
        inherited = isInherited;
        this.key = key;
        this.derivedValue = derivedValue;
        this.value = value;
    }

    public static PropertyView inherited(String key, String value, String derivedValue) {
        return new PropertyView(true, key, value, derivedValue);
    }

    public static PropertyView local(String key, String value, String derivedValue) {
        return new PropertyView(false, key, value, derivedValue);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(inherited, key, value,derivedValue);
    }

    @Override
    public boolean equals(final Object obj) {
        if (obj instanceof PropertyView) {
            final PropertyView other = (PropertyView) obj;
            return Objects.equal(inherited, other.inherited)
                    && Objects.equal(key, other.key)
                    && Objects.equal(derivedValue, other.derivedValue)
                    && Objects.equal(value, other.value);
        } else {
            return false;
        }
    }

    @Override
    public String toString() {
        return Objects.toStringHelper(this.getClass()).add("inherited", inherited).add("derivedValue", derivedValue).add("key", key).add("value", value).toString();
    }

    public String getKey() {
        return key;
    }
}
