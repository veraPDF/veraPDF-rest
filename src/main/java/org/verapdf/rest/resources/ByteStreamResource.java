/**
 *
 */
package org.verapdf.rest.resources;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.ParameterStyle;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import org.glassfish.jersey.media.multipart.FormDataContentDisposition;
import org.glassfish.jersey.media.multipart.FormDataParam;
import org.openpreservation.bytestreams.ByteStreamId;
import org.openpreservation.bytestreams.ByteStreams;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;


/**
 * The REST resource definition for byte stream identification services, these
 * are JERSEY REST services and it's the annotations that perform the magic of
 * handling content types and serialisation.
 *
 * @author <a href="mailto:carl@openpreservation.org">Carl Wilson</a>.</p>
 */
public class ByteStreamResource {
	/**
	 * Default public constructor required by Jersey / Dropwizard
	 */
	public ByteStreamResource() {
		/** Intentionally blank */
	}

	/**
	 * @param uploadedInputStream
	 *            InputStream for the uploaded file
	 * @param contentDispositionHeader
	 *            extra info about the uploaded file, currently unused.
	 * @return the {@link org.openpreservation.bytestreams.ByteStreamId} of
	 *         the uploaded file's byte stream serialised according to requested
	 *         content type.
	 */
	@POST
	@Operation(summary = "Upload file's byte stream and serialise according to requested content type")
	@ApiResponses(value = {
			@ApiResponse(responseCode = "200", description = "OK", content = {
					@Content(mediaType = "application/xml", schema =
					@Schema(implementation = ByteStreamId.class)),
					@Content(mediaType = "application/json", schema =
					@Schema(implementation = ByteStreamId.class)),
					@Content(mediaType = "text/xml", schema =
					@Schema(implementation = ByteStreamId.class))
			})})
	@Consumes(MediaType.MULTIPART_FORM_DATA)
	@Produces({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML,
	           MediaType.TEXT_XML})
	public static ByteStreamId getSha1(
			@Parameter(name = "file", schema = @Schema(implementation = File.class), style = ParameterStyle.FORM,
			           description = "InputStream for the uploaded file")
			@FormDataParam("file") InputStream uploadedInputStream,
			@Parameter(hidden = true) @FormDataParam("file") final FormDataContentDisposition contentDispositionHeader) {
		try {
			ByteStreamId id = ByteStreams.idFromStream(uploadedInputStream);
			uploadedInputStream.close();
			return id;// return
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return ByteStreams.nullByteStreamId();
	}

	/**
	 * @return the {@link org.openpreservation.bytestreams} of
	 *         an empty (0 byte) byte stream serialised according to requested
	 *         content type.
	 */
	@GET
	@Path("/null")
	@Operation(summary = "Get byte streams of an empty byte stream serialised according to requested content type")
	@ApiResponses(value = {
			@ApiResponse(responseCode = "200", description = "OK", content = {
					@Content(mediaType = "application/xml", schema =
					@Schema(implementation = ByteStreamId.class)),
					@Content(mediaType = "application/json", schema =
					@Schema(implementation = ByteStreamId.class)),
					@Content(mediaType = "text/xml", schema =
					@Schema(implementation = ByteStreamId.class))
			})})
	@Produces({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML,
	           MediaType.TEXT_XML})
	public static ByteStreamId getEmptySha1() {
		return ByteStreams.nullByteStreamId();
	}
}
