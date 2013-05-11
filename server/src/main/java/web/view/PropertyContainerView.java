package web.view;

import com.google.common.base.Function;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Ordering;

import java.util.List;
import java.util.Set;

public abstract class PropertyContainerView {
    protected Set<PropertyView> properties = ImmutableSet.of();
    protected String name;

    public PropertyContainerView(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public List<PropertyView> getProperties() {
        return Ordering.<String>natural().onResultOf(new Function<PropertyView, String>() {
            @Override
            public String apply(PropertyView input) {
                return input.getKey();
            }
        }).sortedCopy(properties);
    }

    public void addLocalProp(String key, String value, String derivedValue) {
        addProp(PropertyView.local(key, value, derivedValue,this));
    }

    public void addInheritedProp(String key, String value, String derivedValue) {
        addProp(PropertyView.inherited(key, value, derivedValue));
    }

    private void addProp(PropertyView propertyView) {
        properties = ImmutableSet.<PropertyView>builder().addAll(properties).add(propertyView).build();
    }


    public abstract String getLink();
}
