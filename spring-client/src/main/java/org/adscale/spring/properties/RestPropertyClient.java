package org.adscale.spring.properties;

import com.google.common.base.Charsets;
import com.google.common.io.CharStreams;
import com.google.common.io.InputSupplier;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.utils.URIBuilder;
import org.apache.http.impl.client.DefaultHttpClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.net.URISyntaxException;

public class RestPropertyClient implements PropertyClient {

    private final String propertyServerUrl;

    private final String appName;

    protected final HttpClient client = new DefaultHttpClient();

    private static Logger log = LoggerFactory.getLogger(RestPropertyClient.class);


    public RestPropertyClient(String propertyServerUrl, String appName) {
        this.propertyServerUrl = propertyServerUrl;
        this.appName = appName;
    }


    @Override
    public Object getProperty(String name) {
        try {
            URI uri = getPropertyUri(name, appName, propertyServerUrl);
            HttpGet request = new HttpGet(uri);
            final HttpResponse response = client.execute(request);
            if (response.getStatusLine().getStatusCode() == 200) {
                return CharStreams.toString(CharStreams.newReaderSupplier(new InputSupplier<InputStream>() {
                    @Override
                    public InputStream getInput() throws IOException {
                        return response.getEntity().getContent();
                    }
                }, Charsets.UTF_8));
            }
        }
        catch (Exception e) {
            log.error("error requesting property " + name, e);
        }
        return null;
    }


    protected URI getPropertyUri(String propertyServerUrl, String appName, String name) throws URISyntaxException {
        return new URIBuilder(propertyServerUrl + "/" + appName + "/" + name).build();
    }
}
