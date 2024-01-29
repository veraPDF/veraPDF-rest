/**
 *
 */
package org.verapdf.rest.resources;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.info.Info;
import io.swagger.v3.oas.annotations.info.License;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.servers.Server;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.verapdf.ReleaseDetails;
import org.verapdf.rest.environment.Environment;
import org.verapdf.rest.environment.Environments;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

/**
 * API wrapper resource, provides routing for child API resources.
 *
 * @author <a href="mailto:carl@openpreservation.org">Carl Wilson</a>.
 *         </p>
 *
 */
@Path("/api")
@Tag(name = "veraPDF")
@OpenAPIDefinition(info = @Info(title = "veraPDF API", description = "A REST service API for veraPDF", version = "V0.2.0", license = @License(name = "Apache 2.0", url = "https://www.apache.org/licenses/LICENSE-2.0")), servers = {
        @Server(url = "https://demo.verapdf.org", description = "default"),
        @Server(url = "https://dev.verapdf-rest.duallab.com", description = "dev"),
        @Server(url = "http://localhost:8080", description = "local") })

public final class ApiResource {
    private static ReleaseDetails buildDetails = ReleaseDetails.addDetailsFromResource(
            ReleaseDetails.APPLICATION_PROPERTIES_ROOT + "rest." + ReleaseDetails.PROPERTIES_EXT);

    @GET
    @Path("/")
    @Operation(summary = "Returns the release details of the veraPDF library used by the server.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Release details successfully returned.", content = {
                    @Content(mediaType = "application/json", schema = @Schema(implementation = ReleaseDetails.class)),
                    @Content(mediaType = "application/xml", schema = @Schema(implementation = ReleaseDetails.class)) }) })
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
    @Operation(summary = "Returns relavent server information, such as JDK version, OS, etc.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Server environment details successfully returned.", content = {
                    @Content(mediaType = "application/json", schema = @Schema(implementation = Environment.class)),
                    @Content(mediaType = "application/xml", schema = @Schema(implementation = Environment.class)) }) })
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
