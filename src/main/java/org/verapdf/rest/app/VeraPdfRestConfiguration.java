/**
 *
 */
package org.verapdf.rest.app;

import io.dropwizard.Configuration;
import io.federecio.dropwizard.swagger.SwaggerBundleConfiguration;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Configuration object for the Dropwizard app. Reads defaults from
 * configuration YAML file. This class has to be "mutable" due to Dropwizard
 * requirements.
 *
 * @author <a href="mailto:carl@openpreservation.org">Carl Wilson</a>.</p>
 */
public class VeraPdfRestConfiguration extends Configuration {
    //
    // FIXME: This isn't mapping to the port option from the config
    //
    private int port;


    @JsonProperty("swagger")
	  public SwaggerBundleConfiguration swaggerBundleConfiguration = new SwaggerBundleConfiguration();

    /**
     * @return the TCP/IP port used
     */
    @JsonProperty
    public int getPort() {
        return this.port;
    }

    /**
     * @param port
     *            numeric value of TCP/IP port to listen to
     */
    @JsonProperty
    public void setPort(int port) {
        this.port = port;
    }

}
