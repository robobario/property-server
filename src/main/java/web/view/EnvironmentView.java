package web.view;

import web.application.Routes;
import com.google.common.base.Function;
import com.google.common.base.Objects;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Ordering;

import java.util.List;
import java.util.Set;

public class EnvironmentView extends PropertyContainerView{
    private Set<ApplicationView> applications = ImmutableSet.of();
    private Set<EnvironmentView> subEnvironments = ImmutableSet.of();

    private EnvironmentView(String name) {
        super(name);
    }

    public static EnvironmentView create(String name){
        return new EnvironmentView(name);
    }

    public EnvironmentView addApplication(ApplicationView applicationView){
        applications = ImmutableSet.<ApplicationView>builder().addAll(applications).add(applicationView).build();
        return this;
    }

    public EnvironmentView addSubEnvironment(EnvironmentView environmentView){
        subEnvironments = ImmutableSet.<EnvironmentView>builder().addAll(subEnvironments).add(environmentView).build();
        return this;
    }

    public List<ApplicationView> getApplications() {
        return Ordering.<String>natural().onResultOf(new Function<ApplicationView, String>() {
            @Override
            public String apply(ApplicationView input) {
                return input.getName();
            }
        }).sortedCopy(applications);
    }

    public List<EnvironmentView> getSubEnvironmentViews() {
        return Ordering.<String>natural().onResultOf(new Function<EnvironmentView, String>() {
            @Override
            public String apply(EnvironmentView input) {
                return input.getName();
            }
        }).sortedCopy(subEnvironments);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(applications, subEnvironments, name,properties);
    }

    @Override
    public boolean equals(final Object obj) {
        if (obj instanceof EnvironmentView) {
            final EnvironmentView other = (EnvironmentView) obj;
            return Objects.equal(applications, other.applications)
                    && Objects.equal(subEnvironments, other.subEnvironments)
                    && Objects.equal(properties, other.properties)
                    && Objects.equal(name, other.name);
        } else {
            return false;
        }
    }

    @Override
    public String toString() {
        return Objects.toStringHelper(this.getClass()).add("applications", applications).add("name",name).add("subEnvironments", subEnvironments).add("properties", properties).toString();
    }

    public String getLink(){
        return Routes.to().environment().environmentDetails(getName());
    }
}
