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
 * API wrapper resource, provides routing for child API resources.
 * 
 * @author <a href="mailto:carl@openpreservation.org">Carl Wilson</a>.</p>
 *
 */
@Path("/api")
public final class ApiResource {

    /**
     * @return the server environment information as a {@link org.verapdf.rest.environment.Environment}.
     */
    @GET
    @Path("/info")
    @Produces({ MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML })
    public static Environment getEnvironment() {
        return Environments.getEnvironment();
    }
    
    /**
     * @return a new {@link org.verapdf.rest.resources.ProfileResource}
     */
    @Path("/profiles")
    public static ProfileResource getProfileResource() {
        return new ProfileResource();
    }

    /**
     * @return a new {@link org.verapdf.rest.resources.ValidateResource}
     */
    @Path("/validate")
    public static ValidateResource getValidateResource() {
        return new ValidateResource();
    }


    /**
     * @return a new {@link ByteStreamResource}
     */
    @Path("/sha1")
    public static ByteStreamResource getBytestreamResource() {
        return new ByteStreamResource();
    }
}
