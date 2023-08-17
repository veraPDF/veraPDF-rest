/**
 * 
 */
package org.verapdf.rest.resources;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import io.swagger.annotations.Api;
import io.swagger.annotations.Info;
import io.swagger.annotations.License;
import io.swagger.annotations.SwaggerDefinition;
import org.verapdf.ReleaseDetails;
import org.verapdf.rest.environment.Environment;
import org.verapdf.rest.environment.Environments;

/**
 * API wrapper resource, provides routing for child API resources.
 * 
 * @author <a href="mailto:carl@openpreservation.org">Carl Wilson</a>.
 *         </p>
 *
 */
@Path("/api")
@Api(value = "VeraPDF")
@SwaggerDefinition(info = @Info(
        description = "Rest API for VeraPDF",
        version = "V0.2.0",
        title = "VeraPDF API",
        license = @License(name = "Apache 2.0", url = "https://www.apache.org/licenses/LICENSE-2.0")),
                   schemes = {SwaggerDefinition.Scheme.HTTP}
)

public final class ApiResource {
    private static ReleaseDetails buildDetails = ReleaseDetails.addDetailsFromResource(
            ReleaseDetails.APPLICATION_PROPERTIES_ROOT + "rest." + ReleaseDetails.PROPERTIES_EXT);

    @GET
    @Path("/")
    @Produces({ MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML })
    public static ReleaseDetails getReleaseDetails() {
        return buildDetails;
    }

    /**
     * @return the server environment information as a
     *         {@link org.verapdf.rest.environment.Environment}.
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
