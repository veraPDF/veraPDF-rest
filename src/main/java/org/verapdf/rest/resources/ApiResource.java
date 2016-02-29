/**
 * 
 */
package org.verapdf.rest.resources;

import javax.ws.rs.Path;

/**
 * @author <a href="mailto:carl@openpreservation.org">Carl Wilson</a>.</p>
 *
 */
@Path("/api")
public final class ApiResource {
    @Path("/profiles")
    public ProfileResource getProfileResource() {
        return new ProfileResource();
    }
}
