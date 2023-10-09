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
import org.apache.commons.codec.binary.Hex;
import org.glassfish.jersey.media.multipart.FormDataContentDisposition;
import org.glassfish.jersey.media.multipart.FormDataParam;
import org.verapdf.ReleaseDetails;
import org.verapdf.component.ComponentDetails;
import org.verapdf.core.VeraPDFException;
import org.verapdf.exceptions.VeraPDFParserException;
import org.verapdf.gf.foundry.VeraGreenfieldFoundryProvider;
import org.verapdf.io.SeekableInputStream;
import org.verapdf.pdfa.Foundries;
import org.verapdf.pdfa.flavours.PDFAFlavour;
import org.verapdf.pdfa.validation.validators.ValidatorConfig;
import org.verapdf.processor.BatchProcessor;
import org.verapdf.processor.FormatOption;
import org.verapdf.processor.ProcessorConfig;
import org.verapdf.processor.ProcessorFactory;
import org.verapdf.processor.app.ConfigManager;
import org.verapdf.processor.app.ConfigManagerImpl;
import org.verapdf.processor.app.VeraAppConfig;
import org.verapdf.processor.reports.BatchSummary;
import org.verapdf.processor.reports.ItemDetails;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;
import java.io.*;
import java.net.URL;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * @author <a href="mailto:carl@openpreservation.org">Carl Wilson</a>
 */
public class ValidateResource {
	// java.security.digest name for the SHA-1 algorithm
	private static final String SHA1_NAME = "SHA-1"; //$NON-NLS-1$
	private static final String AUTODETECT_PROFILE = "auto";
	private static int maxFileSize;
	private static ValidateResource validateResource;

	public static synchronized ValidateResource getValidateResource() {
		if (validateResource == null) {
			validateResource = new ValidateResource();
		}
		return validateResource;
	}

	private static ConfigManager configManager;

	static {
		VeraGreenfieldFoundryProvider.initialise();
		File root = new File("");
		configManager = ConfigManagerImpl.create(new File(root.getAbsolutePath() + "/config"));
	}

	@GET
	@Path("/details")
	@Operation(summary = "Get component details")
	@ApiResponses(value = {
			@ApiResponse(responseCode = "200", description = "OK", content = {
					@Content(mediaType = "application/json", schema =
					@Schema(implementation = ReleaseDetails.class)
					), @Content(mediaType = "application/xml", schema =
			@Schema(implementation = ReleaseDetails.class)
			)})})
	@Produces({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
	public static ComponentDetails getDetails() {
		return Foundries.defaultInstance().getDetails();
	}

	/**
	 * @param profileId
	 *                                 the String id of the Validation profile
	 *                                 (auto, 1b, 1a, 2b, 2a, 2u,
	 *                                 3b, 3a, 3u, 4, 4e, 4f or ua1)
	 * @param uploadedInputStream
	 *                                 a {@link java.io.InputStream} to the PDF to
	 *                                 be validated
	 * @param contentDispositionHeader
	 * @return the {@link org.verapdf.pdfa.results.ValidationResult} obtained
	 *         when validating the uploaded stream against the selected profile.
	 * @throws VeraPDFException
	 */
	@POST
	@Path("/{profileId}")
	@Operation(summary = "Validate the uploaded stream against the selected profile and return validation result")
	@ApiResponses(value = {
			@ApiResponse(responseCode = "200", description = "OK", content = {
					@Content(mediaType = "application/xml", schema =
					@Schema(implementation = ReleaseDetails.class)),
					@Content(mediaType = "application/json", schema =
					@Schema(implementation = ReleaseDetails.class)),
					@Content(mediaType = "text/html", schema =
					@Schema(implementation = ReleaseDetails.class)
					)})})
	@Consumes(MediaType.MULTIPART_FORM_DATA)
	@Produces({MediaType.APPLICATION_XML})
	public static InputStream validateXml(@Parameter(description = "the String id of the Validation profile" +
	                                                               "(auto, 1b, 1a, 2b, 2a, 2u, 3b, 3a, 3u, 4, 4e, 4f or ua1)")
	                                      @PathParam("profileId") String profileId,
	                                      @Parameter(name = "file", schema = @Schema(implementation = File.class),
	                                                 style = ParameterStyle.FORM, description = "an InputStream of the PDF to be validated")
	                                      @FormDataParam("file") InputStream uploadedInputStream,
	                                      @Parameter(hidden = true) @FormDataParam("file") final FormDataContentDisposition contentDispositionHeader)
			throws VeraPDFException {

		return validate(uploadedInputStream, contentDispositionHeader.getFileName(), profileId, FormatOption.XML);
	}

	/**
	 * @param profileId
	 *                                 the String id of the Validation profile
	 *                                 (auto, 1b, 1a, 2b, 2a, 2u,
	 *                                 3b, 3a, 3u, 4, 4e, 4f or ua1)
	 * @param sha1Hex
	 *                                 the hex String representation of the file's
	 *                                 SHA-1 hash
	 * @param uploadedInputStream
	 *                                 a {@link java.io.InputStream} to the PDF to
	 *                                 be validated
	 * @param contentDispositionHeader
	 * @return the {@link org.verapdf.pdfa.results.ValidationResult} obtained
	 *         when validating the uploaded stream against the selected profile.
	 * @throws VeraPDFException
	 */
	@POST
	@Path("/sha/{profileId}")
	@Operation(summary = "Validate the uploaded stream against the selected profile and return validation result")
	@ApiResponses(value = {
			@ApiResponse(responseCode = "200", description = "OK", content = {
					@Content(mediaType = "application/xml", schema =
					@Schema(implementation = ReleaseDetails.class)),
					@Content(mediaType = "application/json", schema =
					@Schema(implementation = ReleaseDetails.class)),
					@Content(mediaType = "text/html", schema =
					@Schema(implementation = ReleaseDetails.class)
					)})})
	@Consumes(MediaType.MULTIPART_FORM_DATA)
	@Produces({MediaType.APPLICATION_XML})
	public static InputStream validateXml(@Parameter(description = "the String id of the Validation profile" +
	                                                               "(auto, 1b, 1a, 2b, 2a, 2u, 3b, 3a, 3u, 4, 4e, 4f or ua1)")
	                                      @PathParam("profileId") String profileId,
	                                      @Parameter(description = "the hex String representation of the file's SHA-1 hash")
	                                      @FormDataParam("sha1Hex") String sha1Hex,
	                                      @Parameter(name = "file", schema = @Schema(implementation = File.class),
	                                                 style = ParameterStyle.FORM, description = "an InputStream of the PDF to be validated")
	                                      @FormDataParam("file") InputStream uploadedInputStream,
	                                      @Parameter(hidden = true) @FormDataParam("file") final FormDataContentDisposition contentDispositionHeader)
			throws VeraPDFException {
		checkSha1Hex(sha1Hex);
		return validate(uploadedInputStream, contentDispositionHeader.getFileName(), profileId, FormatOption.XML);
	}

	@POST
	@Path("/url/{profileId}")
	@Operation(summary = "Validate PDF given by URL against the selected profile and return validation result")
	@ApiResponses(value = {
			@ApiResponse(responseCode = "200", description = "OK", content = {
					@Content(mediaType = "application/xml", schema =
					@Schema(implementation = ReleaseDetails.class)),
					@Content(mediaType = "application/json", schema =
					@Schema(implementation = ReleaseDetails.class)),
					@Content(mediaType = "text/html", schema =
					@Schema(implementation = ReleaseDetails.class)
					)})})
	@Consumes(MediaType.MULTIPART_FORM_DATA)
	@Produces({MediaType.APPLICATION_XML})
	public static InputStream validateXml(@Parameter(description = "the String id of the Validation profile" +
	                                                               "(auto, 1b, 1a, 2b, 2a, 2u, 3b, 3a, 3u, 4, 4e, 4f or ua1)")
	                                      @PathParam("profileId") String profileId,
	                                      @Parameter(description = "a URL of PDF to be validated")
	                                      @FormDataParam("url") String urlLink) throws VeraPDFException {
		InputStream uploadedInputStream = getInputStreamByUrlLink(urlLink);

		return validate(uploadedInputStream, urlLink, profileId, FormatOption.XML);
	}

	/**
	 * @param profileId
	 *                                 the String id of the Validation profile
	 *                                 (auto, 1b, 1a, 2b, 2a, 2u,
	 *                                 3b, 3a, 3u, 4, 4e, 4f or ua1)
	 * @param uploadedInputStream
	 *                                 a {@link java.io.InputStream} to the PDF to
	 *                                 be validated
	 * @param contentDispositionHeader
	 * @return the {@link org.verapdf.pdfa.results.ValidationResult} obtained
	 *         when validating the uploaded stream against the selected profile.
	 * @throws VeraPDFException
	 */
	@POST
	@Path("/{profileId}")
	@Consumes(MediaType.MULTIPART_FORM_DATA)
	@Produces({MediaType.APPLICATION_JSON})
	public static InputStream validateJson(@Parameter(description = "the String id of the Validation profile" +
	                                                                "(auto, 1b, 1a, 2b, 2a, 2u, 3b, 3a, 3u, 4, 4e, 4f or ua1)")
	                                       @PathParam("profileId") String profileId,
	                                       @Parameter(name = "file", schema = @Schema(implementation = File.class),
	                                                  style = ParameterStyle.FORM, description = "an InputStream of the PDF to be validated")
	                                       @FormDataParam("file") InputStream uploadedInputStream,
	                                       @Parameter(hidden = true) @FormDataParam("file") final FormDataContentDisposition contentDispositionHeader)
			throws VeraPDFException {

		return validate(uploadedInputStream, contentDispositionHeader.getFileName(), profileId, FormatOption.JSON);
	}

	/**
	 * @param profileId
	 *                                 the String id of the Validation profile
	 *                                 (auto, 1b, 1a, 2b, 2a, 2u,
	 *                                 3b, 3a, 3u, 4, 4e, 4f or ua1)
	 * @param sha1Hex
	 *                                 the hex String representation of the file's
	 *                                 SHA-1 hash
	 * @param uploadedInputStream
	 *                                 a {@link java.io.InputStream} to the PDF to
	 *                                 be validated
	 * @param contentDispositionHeader
	 * @return the {@link org.verapdf.pdfa.results.ValidationResult} obtained
	 *         when validating the uploaded stream against the selected profile.
	 * @throws VeraPDFException
	 */
	@POST
	@Path("/sha/{profileId}")
	@Consumes(MediaType.MULTIPART_FORM_DATA)
	@Produces({MediaType.APPLICATION_JSON})
	public static InputStream validateJson(@Parameter(description = "the String id of the Validation profile" +
	                                                                "(auto, 1b, 1a, 2b, 2a, 2u, 3b, 3a, 3u, 4, 4e, 4f or ua1)")
	                                       @PathParam("profileId") String profileId,
	                                       @Parameter(description = "the hex String representation of the file's SHA-1 hash")
	                                       @FormDataParam("sha1Hex") String sha1Hex,
	                                       @Parameter(name = "file", schema = @Schema(implementation = File.class),
	                                                  style = ParameterStyle.FORM, description = "an InputStream of the PDF to be validated")
	                                       @FormDataParam("file") InputStream uploadedInputStream,
	                                       @Parameter(hidden = true) @FormDataParam("file") final FormDataContentDisposition contentDispositionHeader)
			throws VeraPDFException {
		checkSha1Hex(sha1Hex);
		return validate(uploadedInputStream, contentDispositionHeader.getFileName(), profileId, FormatOption.JSON);
	}

	@POST
	@Path("/url/{profileId}")
	@Consumes(MediaType.MULTIPART_FORM_DATA)
	@Produces({MediaType.APPLICATION_JSON})
	public static InputStream validateJson(@Parameter(description = "the String id of the Validation profile" +
	                                                                "(auto, 1b, 1a, 2b, 2a, 2u, 3b, 3a, 3u, 4, 4e, 4f or ua1)")
	                                       @PathParam("profileId") String profileId,
	                                       @Parameter(description = "a URL of PDF to be validated")
	                                       @FormDataParam("url") String urlLink) throws VeraPDFException {
		InputStream uploadedInputStream = getInputStreamByUrlLink(urlLink);

		return validate(uploadedInputStream, urlLink, profileId, FormatOption.JSON);
	}

	/**
	 * @param profileId
	 *                                 the String id of the Validation profile
	 *                                 (auto, 1b, 1a, 2b, 2a, 2u,
	 *                                 3b, 3a, 3u, 4, 4e, 4f or ua1)
	 * @param uploadedInputStream
	 *                                 a {@link java.io.InputStream} to the PDF to
	 *                                 be validated
	 * @param contentDispositionHeader
	 * @return
	 * @throws VeraPDFException
	 */
	@POST
	@Path("/{profileId}")
	@Consumes(MediaType.MULTIPART_FORM_DATA)
	@Produces({MediaType.TEXT_HTML})
	public static InputStream validateHtml(@PathParam("profileId") String profileId,
	                                       @Parameter(name = "file", schema = @Schema(implementation = File.class),
	                                                  style = ParameterStyle.FORM, description = "an InputStream of the PDF to be validated")
	                                       @FormDataParam("file") InputStream uploadedInputStream,
	                                       @Parameter(hidden = true) @FormDataParam("file") final FormDataContentDisposition contentDispositionHeader)
			throws VeraPDFException {
		return validate(uploadedInputStream, contentDispositionHeader.getFileName(), profileId, FormatOption.HTML);
	}

	/**
	 * @param profileId
	 *                                 the String id of the Validation profile
	 *                                 (auto, 1b, 1a, 2b, 2a, 2u,
	 *                                 3b, 3a, 3u, 4, 4e, 4f or ua1)
	 * @param sha1Hex
	 *                                 the hex String representation of the file's
	 *                                 SHA-1 hash
	 * @param uploadedInputStream
	 *                                 a {@link java.io.InputStream} to the PDF to
	 *                                 be validated
	 * @param contentDispositionHeader
	 * @return
	 * @throws VeraPDFException
	 */
	@POST
	@Path("/sha/{profileId}")
	@Consumes(MediaType.MULTIPART_FORM_DATA)
	@Produces({MediaType.TEXT_HTML})
	public static InputStream validateHtml(@PathParam("profileId") String profileId,
	                                       @Parameter(description = "the hex String representation of the file's SHA-1 hash")
	                                       @FormDataParam("sha1Hex") String sha1Hex,
	                                       @Parameter(name = "file", schema = @Schema(implementation = File.class),
	                                                  style = ParameterStyle.FORM, description = "an InputStream of the PDF to be validated")
	                                       @FormDataParam("file") InputStream uploadedInputStream,
	                                       @Parameter(hidden = true) @FormDataParam("file") final FormDataContentDisposition contentDispositionHeader)
			throws VeraPDFException {
		checkSha1Hex(sha1Hex);
		return validate(uploadedInputStream, contentDispositionHeader.getFileName(), profileId, FormatOption.HTML);
	}

	@POST
	@Path("/url/{profileId}")
	@Consumes(MediaType.MULTIPART_FORM_DATA)
	@Produces({MediaType.TEXT_HTML})
	public static InputStream validateHtml(@Parameter(description = "the String id of the Validation profile" +
	                                                                "(auto, 1b, 1a, 2b, 2a, 2u, 3b, 3a, 3u, 4, 4e, 4f or ua1)")
	                                       @PathParam("profileId") String profileId,
	                                       @Parameter(description = "a URL of PDF to be validated")
	                                       @FormDataParam("url") String urlLink) throws VeraPDFException {
		InputStream uploadedInputStream = getInputStreamByUrlLink(urlLink);

		return validate(uploadedInputStream, urlLink, profileId, FormatOption.HTML);
	}

	public static void setMaxFileSize(Integer maxFileSize) {
		ValidateResource.maxFileSize = maxFileSize;
	}

	private static InputStream validate(InputStream uploadedInputStream, String fileName, String profileId, FormatOption formatOption) throws VeraPDFException {
		SeekableInputStream seekableInputStream;
		try {
			seekableInputStream = SeekableInputStream.getSeekableStream(uploadedInputStream, 1000000 * maxFileSize);
		} catch (VeraPDFParserException e) {
			throw new VeraPDFException("Maximum allowed file size exceeded: " + maxFileSize + " MB");
		} catch (IOException e) {
			throw new VeraPDFException(e.getMessage());
		}
		PDFAFlavour flavour = PDFAFlavour.byFlavourId(profileId);
		ValidatorConfig validatorConfig = configManager.getValidatorConfig();
		validatorConfig.setFlavour(flavour);
		ProcessorConfig config = createProcessorConfig(validatorConfig);
		byte[] outputBytes;
		try (ByteArrayOutputStream outputStream = new ByteArrayOutputStream()) {
			VeraAppConfig appConfig = configManager.getApplicationConfig();
			processStream(seekableInputStream, fileName, config, outputStream, appConfig, formatOption);
			outputBytes = outputStream.toByteArray();
		} catch (IOException excep) {
			throw new VeraPDFException("Some Java Exception while validating", excep); //$NON-NLS-1$
		}
		return new ByteArrayInputStream(outputBytes);
	}

	private static void checkSha1Hex(String sha1Hex) {
		MessageDigest sha1 = getDigest();
		if (sha1Hex != null && sha1Hex.equalsIgnoreCase(Hex.encodeHexString(sha1.digest()))) {
			throw new NotSupportedException(Response.status(Status.UNSUPPORTED_MEDIA_TYPE)
			                                        .type(MediaType.TEXT_PLAIN).entity("File does not appear " +
			                                                                           "to be a PDF.") //$NON-NLS-1$
			                                        .build());
		}
	}

	private static MessageDigest getDigest() {
		try {
			return MessageDigest.getInstance(SHA1_NAME);
		} catch (NoSuchAlgorithmException nsaExcep) {
			// If this happens the Java Digest algorithms aren't present, a
			// faulty Java install??
			throw new IllegalStateException(
					"No digest algorithm implementation for " +
					SHA1_NAME + ", check you Java installation.", //$NON-NLS-1$
					nsaExcep); //$NON-NLS-2$
		}
	}

	private static ProcessorConfig createProcessorConfig(ValidatorConfig validatorConfig) {
		VeraAppConfig veraAppConfig = configManager.getApplicationConfig();
		return ProcessorFactory.fromValues(validatorConfig, configManager.getFeaturesConfig(),
		                                   configManager.getPluginsCollectionConfig(), configManager.getFixerConfig(),
		                                   veraAppConfig.getProcessType().getTasks());
	}

	private static InputStream getInputStreamByUrlLink(String urlLink) throws VeraPDFException {
		try {
			return new URL(urlLink).openStream();
		} catch (IOException e) {
			if (urlLink.isEmpty()) {
				throw new VeraPDFException("URL is empty");
			} else {
				throw new VeraPDFException("URL is incorrect: " + urlLink);
			}
		}
	}

	private static BatchSummary processStream(SeekableInputStream inputStream, String fileName, ProcessorConfig config, OutputStream stream,
	                                          VeraAppConfig appConfig, FormatOption formatOption)
			throws VeraPDFException, IOException {
		BatchSummary summary;
		try (BatchProcessor processor = ProcessorFactory.fileBatchProcessor(config)) {
			summary = processor.process(ItemDetails.fromValues(fileName, inputStream.getStreamLength()), inputStream,
			                            ProcessorFactory.getHandler(formatOption, appConfig.isVerbose(), stream,
			                                                        config.getValidatorConfig().isRecordPasses(), appConfig.getWikiPath()));
		} finally {
			if (inputStream != null) {
				inputStream.close();
			}
		}
		return summary;
	}
}
