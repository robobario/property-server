package web.application;

import service.environment.model.ChildPropertyContainer;
import org.eclipse.jetty.server.Connector;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.nio.SelectChannelConnector;
import org.eclipse.jetty.webapp.WebAppContext;

import java.net.URL;
import java.security.ProtectionDomain;

public class Application {

    public static void main(String[] args) {
        Server server = new Server();
        Connector connector = new SelectChannelConnector();
        connector.setPort(8888);
        connector.setHost("127.0.0.1");
        server.addConnector(connector);
        server.setStopAtShutdown(true);
        WebAppContext context = new WebAppContext();
        context.setServer(server);
        context.setContextPath("/");
        ProtectionDomain protectionDomain = ChildPropertyContainer.class.getProtectionDomain();
        URL location = protectionDomain.getCodeSource().getLocation();
        context.setWar(location.toExternalForm());
        server.setHandler(context);
        try {
            server.start();
            server.join();
        } catch (Exception e) {
            e.printStackTrace();
            System.exit(100);
        }
    }

}
