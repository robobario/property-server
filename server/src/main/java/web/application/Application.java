package web.application;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.GnuParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;
import org.eclipse.jetty.server.Connector;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.nio.SelectChannelConnector;
import org.eclipse.jetty.webapp.WebAppContext;
import service.environment.model.ChildPropertyContainer;

import java.net.URL;
import java.security.ProtectionDomain;

public class Application {

    public static final String DIR_OPT = "d";

    public static final String PORT_OPT = "p";

    public static final String HOST_OPT = "host";

    public static final String HELP_OPT = "h";


    public static void main(String[] args) {
        Options options = getOpts(args);
        CommandLine commandLine = getOpts(args, options);
        if (commandLine.hasOption(HELP_OPT)) {
            HelpFormatter formatter = new HelpFormatter();
            formatter.printHelp( "java -jar server.war", options);
        } else{
            startServer(commandLine);
        }
    }


    private static void startServer(CommandLine commandLine) {
        String host = "127.0.0.1";
        int port = 8888;
        if (commandLine.hasOption(DIR_OPT)) {
            String optionValue = commandLine.getOptionValue(DIR_OPT);
            System.getProperties().put(Context.ROOT_DIR, optionValue);
        }
        if (commandLine.hasOption(PORT_OPT)) {
            port = Integer.parseInt(commandLine.getOptionValue(PORT_OPT));
        }
        if (commandLine.hasOption(HOST_OPT)) {
            host = commandLine.getOptionValue(HOST_OPT);
        }

        Server server = new Server();
        Connector connector = new SelectChannelConnector();
        connector.setPort(port);
        connector.setHost(host);
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
        }
        catch (Exception e) {
            e.printStackTrace();
            System.exit(100);
        }
    }


    private static CommandLine getOpts(String[] args, Options commandLine) {
        CommandLineParser parser = new GnuParser();
        try {
             return parser.parse(commandLine, args);
        }
        catch (ParseException e) {
            throw new RuntimeException(e);
        }
    }


    private static Options getOpts(String[] args) {
        Option dir =
                new Option(DIR_OPT, "history-directory", true, "directory where properties files will be persisted");
        dir.setArgName("dir");
        Option port = new Option(PORT_OPT, "port", true, "port the server will run on : 8888 default");
        port.setArgName("port");
        Option host = new Option(HOST_OPT, "host", true, "host the server will bind to : 127.0.0.1 default");
        port.setArgName("host");
        Option help = new Option(HELP_OPT, "help",false, "help yo");
        Options options = new Options();
        options.addOption(dir);
        options.addOption(port);
        options.addOption(host);
        options.addOption(help);
        return options;
    }

}
