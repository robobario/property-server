package web.view;

import com.google.common.base.Objects;
import web.application.Routes;

public class ApplicationView extends PropertyContainerView{

    private ApplicationView(String name) {
        super(name);
    }

    public static ApplicationView create(String name){
       return new ApplicationView(name);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(name,properties);
    }

    @Override
    public boolean equals(final Object obj) {
        if (obj instanceof ApplicationView) {
            final ApplicationView other = (ApplicationView) obj;
            return Objects.equal(name, other.name)
                    && Objects.equal(properties, other.properties);
        } else {
            return false;
        }
    }

    @Override
    public String toString() {
        return Objects.toStringHelper(this.getClass()).add("name", name).add("properties", properties).toString();
    }

    @Override
    public String getLink(){
        return Routes.to().application().applicationDetails(getName());
    }


}
