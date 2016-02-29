/**
 * 
 */
package org.verapdf.rest.resources;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import org.verapdf.rest.environment.Environment;
import org.verapdf.rest.environment.Environments;

/**
 * @author <a href="mailto:carl@openpreservation.org">Carl Wilson</a>.</p>
 *
 */
@Path("/api")
public final class ApiResource {
    /**
     * @return
     */
    @GET
    @Produces({ MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML })
    public static Environment getEnvironent() {
        return Environments.getEnvironment();
    }
    
    /**
     * @return
     */
    @Path("/profiles")
    public static ProfileResource getProfileResource() {
        return new ProfileResource();
    }

    /**
     * @return
     */
    @Path("/validate")
    public static ValidateResource getValidateResource() {
        return new ValidateResource();
    }
}
