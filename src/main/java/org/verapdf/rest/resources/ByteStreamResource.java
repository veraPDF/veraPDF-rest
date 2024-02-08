/**
 *
 */
package org.verapdf.rest.resources;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import org.glassfish.jersey.media.multipart.FormDataContentDisposition;
import org.glassfish.jersey.media.multipart.FormDataParam;
import org.openpreservation.bytestreams.ByteStreamId;
import org.openpreservation.bytestreams.ByteStreams;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.ParameterStyle;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;

/**
 * The REST resource definition for byte stream identification services, these
 * are JERSEY REST services and it's the annotations that perform the magic of
 * handling content types and serialisation.
 *
 * @author <a href="mailto:carl@openpreservation.org">Carl Wilson</a>.
 *         </p>
 */
public class ByteStreamResource {
    private static final String EMPTY_SHA_XML = "<ByteStreamId>\n" + //
            "  <hexSHA1>da39a3ee5e6b4b0d3255bfef95601890afd80709</hexSHA1>\n" + //
            "  <length>0</length>\n" + //
            "</ByteStreamId>" + //
            "";
    private static final String EMPTY_SHA_JSON = "{\n" + //
            "  \"hexSHA1\": \"da39a3ee5e6b4b0d3255bfef95601890afd80709\",\n" + //
            "  \"length\": 0\n" + //
            "}";
    private static final String TEST_SHA_XML = "<ByteStreamId>\n" + //
            "  <hexSHA1>a94a8fe5ccb19ba61c4c0873d391e987982fbbd3</hexSHA1>\n" + //
            "  <length>4</length>\n" + //
            "</ByteStreamId>" + //
            "";
    private static final String TEST_SHA_JSON = "{\n" + //
            "  \"hexSHA1\": \"a94a8fe5ccb19ba61c4c0873d391e987982fbbd3\",\n" + //
            "  \"length\": 4\n" + //
            "}";

    /**
     * Default public constructor required by Jersey / Dropwizard
     */
    public ByteStreamResource() {
        /** Intentionally blank */
    }

    /**
     * @param uploadedInputStream
     *                                 InputStream for the uploaded file
     * @param contentDispositionHeader
     *                                 extra info about the uploaded file, currently
     *                                 unused.
     * @return the {@link org.openpreservation.bytestreams.ByteStreamId} of
     *         the uploaded file's byte stream serialised according to requested
     *         content type.
     * @throws IOException
     */
    @POST
    @Operation(summary = "Calculates and returns the SHA1 and length of the uploaded file.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "OK", content = {
                    @Content(mediaType = "application/xml", schema = @Schema(implementation = ByteStreamId.class), examples = @ExampleObject(name = "Test file.", value = TEST_SHA_XML, summary = "Test file SHA1 as XML", externalValue = "/examples/test.txt")),
                    @Content(mediaType = "application/json", schema = @Schema(implementation = ByteStreamId.class), examples = @ExampleObject(name = "Test file.", value = TEST_SHA_JSON, summary = "Test file SHA1 as JSON", externalValue = "/examples/test.txt")),
                    @Content(mediaType = "text/xml", schema = @Schema(implementation = ByteStreamId.class), examples = @ExampleObject(name = "Test file.", value = TEST_SHA_XML, summary = "Test file SHA1 as XML", externalValue = "/examples/test.txt"))
            }),
            @ApiResponse(responseCode = "500", description = "Server error when processing the request.")
        })
    @Consumes(MediaType.MULTIPART_FORM_DATA)
    @Produces({ MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML,
            MediaType.TEXT_XML })
    public static ByteStreamId getSha1(
            @Parameter(name = "file", schema = @Schema(implementation = File.class), style = ParameterStyle.FORM, description = "File uploaded for SHA1 calculation.") @FormDataParam("file") InputStream uploadedInputStream,
            @Parameter(hidden = true) @FormDataParam("file") final FormDataContentDisposition contentDispositionHeader)
            throws IOException {
        ByteStreamId id = ByteStreams.idFromStream(uploadedInputStream);
        uploadedInputStream.close();
        return id;// return
    }

    /**
     * @return the {@link org.openpreservation.bytestreams} of
     *         an empty (0 byte) byte stream serialised according to requested
     *         content type.
     */
    @GET
    @Path("/null")
    @Operation(summary = "Returns the SHA1 and zero length of a null byte stream.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "OK", content = {
                    @Content(mediaType = "application/xml", schema = @Schema(implementation = ByteStreamId.class), examples = @ExampleObject(name = "Empty SHA1", value = EMPTY_SHA_XML, summary = "Empty SHA1 as XML")),
                    @Content(mediaType = "application/json", schema = @Schema(implementation = ByteStreamId.class), examples = @ExampleObject(name = "Empty SHA1", value = EMPTY_SHA_JSON, summary = "Empty SHA1 as JSON")),
                    @Content(mediaType = "text/xml", schema = @Schema(implementation = ByteStreamId.class), examples = @ExampleObject(name = "Empty SHA1", value = EMPTY_SHA_XML, summary = "Empty SHA1 as XML"))
            }) })
    @Produces({ MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML,
            MediaType.TEXT_XML })
    public static ByteStreamId getEmptySha1() {
        return ByteStreams.nullByteStreamId();
    }
}
