/**
 *
 */
package org.verapdf.rest.app;

import io.dropwizard.Configuration;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Configuration object for the Dropwizard app. Reads defaults from
 * configuration YAML file. This class has to be "mutable" due to Dropwizard
 * requirements.
 *
 * @author <a href="mailto:carl@openpreservation.org">Carl Wilson</a>.</p>
 */
public class VeraPdfRestConfiguration extends Configuration {
    private int port;
    private int maxFileSize;

    @JsonProperty
    public int getMaxFileSize() {
        return maxFileSize;
    }

    @JsonProperty
    public void setMaxFileSize(int maxFileSize) {
        this.maxFileSize = maxFileSize;
    }

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
