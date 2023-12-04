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
import java.io.*;
import java.net.URL;
import java.security.DigestInputStream;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * @author <a href="mailto:carl@openpreservation.org">Carl Wilson</a>
 */
public class ValidateResource {
	// java.security.digest name for the SHA-1 algorithm
	private static final String SHA1_NAME = "SHA-1"; //$NON-NLS-1$
	private static final String FILE_SIZE_HEADER = "X-File-Size";
	private static final int CONVERTER_MB_TO_B = 1_048_576;
	private static int maxFileSize;
	private static final String MAX_ALLOWED_FILE_SIZE = "Maximum allowed file size exceeded: %d MB";
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
					@Schema(implementation = ComponentDetails.class)
					), @Content(mediaType = "application/xml", schema =
			@Schema(implementation = ComponentDetails.class)
			)})})
	@Produces({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
	public static ComponentDetails getDetails() {
		return Foundries.defaultInstance().getDetails();
	}

	/**
	 * @param profileId
	 *                                 the String id of the Validation profile
	 *                                 (auto, 1b, 1a, 2b, 2a, 2u,
	 *                                 3b, 3a, 3u, 4, 4e, 4f, ua1 or ua2)
	 * @param uploadedInputStream
	 *                                 a {@link java.io.InputStream} to the PDF to
	 *                                 be validated
	 * @param contentDispositionHeader
	 * @return the {@link org.verapdf.pdfa.results.ValidationResult} obtained
	 *         when validating the uploaded stream against the selected profile.
	 */
	@POST
	@Path("/{profileId}")
	@Operation(summary = "Validate the uploaded stream against the selected profile and return validation result")
	@ApiResponses(value = {
			@ApiResponse(responseCode = "200", description = "OK", content = {
					@Content(mediaType = "application/xml", schema =
					@Schema(implementation = InputStream.class)),
					@Content(mediaType = "application/json", schema =
					@Schema(implementation = InputStream.class)),
					@Content(mediaType = "text/html", schema =
					@Schema(implementation = InputStream.class)
					)})})
	@Consumes(MediaType.MULTIPART_FORM_DATA)
	@Produces({MediaType.APPLICATION_XML})
	public static InputStream validateXml(@Parameter(description = "the String id of the Validation profile " +
	                                                               "(auto, 1b, 1a, 2b, 2a, 2u, 3b, 3a, 3u, 4, 4e, 4f, ua1 or ua2)")
	                                      @PathParam("profileId") String profileId,
	                                      @Parameter(name = "file", schema = @Schema(implementation = File.class),
	                                                 style = ParameterStyle.FORM, description = "an InputStream of the PDF to be validated")
	                                      @FormDataParam("file") InputStream uploadedInputStream,
	                                      @Parameter(hidden = true) @FormDataParam("file") final FormDataContentDisposition contentDispositionHeader,
	                                      @Parameter(name = FILE_SIZE_HEADER, description = "a size of the PDF to be validated")
	                                      @HeaderParam(FILE_SIZE_HEADER) Integer fileSize) {
		return validateFile(uploadedInputStream, contentDispositionHeader, profileId, null, FormatOption.XML, fileSize);
	}

	/**
	 * @param profileId
	 *                                 the String id of the Validation profile
	 *                                 (auto, 1b, 1a, 2b, 2a, 2u,
	 *                                 3b, 3a, 3u, 4, 4e, 4f, ua1 or ua2)
	 * @param sha1Hex
	 *                                 the hex String representation of the file's
	 *                                 SHA-1 hash
	 * @param uploadedInputStream
	 *                                 a {@link java.io.InputStream} to the PDF to
	 *                                 be validated
	 * @param contentDispositionHeader
	 * @return the {@link org.verapdf.pdfa.results.ValidationResult} obtained
	 *         when validating the uploaded stream against the selected profile.
	 */
	@POST
	@Path("/sha/{profileId}")
	@Operation(summary = "Validate the uploaded stream against the selected profile and return validation result")
	@ApiResponses(value = {
			@ApiResponse(responseCode = "200", description = "OK", content = {
					@Content(mediaType = "application/xml", schema =
					@Schema(implementation = InputStream.class)),
					@Content(mediaType = "application/json", schema =
					@Schema(implementation = InputStream.class)),
					@Content(mediaType = "text/html", schema =
					@Schema(implementation = InputStream.class)
					)})})
	@Consumes(MediaType.MULTIPART_FORM_DATA)
	@Produces({MediaType.APPLICATION_XML})
	public static InputStream validateXml(@Parameter(description = "the String id of the Validation profile " +
	                                                               "(auto, 1b, 1a, 2b, 2a, 2u, 3b, 3a, 3u, 4, 4e, 4f, ua1 or ua2)")
	                                      @PathParam("profileId") String profileId,
	                                      @Parameter(description = "the hex String representation of the file's SHA-1 hash")
	                                      @FormDataParam("sha1Hex") String sha1Hex,
	                                      @Parameter(name = "file", schema = @Schema(implementation = File.class),
	                                                 style = ParameterStyle.FORM, description = "an InputStream of the PDF to be validated")
	                                      @FormDataParam("file") InputStream uploadedInputStream,
	                                      @Parameter(hidden = true) @FormDataParam("file") final FormDataContentDisposition contentDispositionHeader,
	                                      @Parameter(name = FILE_SIZE_HEADER, description = "a size of the PDF to be validated")
	                                      @HeaderParam(FILE_SIZE_HEADER) Integer fileSize) {
		return validateFile(uploadedInputStream, contentDispositionHeader, profileId, sha1Hex, FormatOption.XML, fileSize);
	}

	@POST
	@Path("/url/{profileId}")
	@Operation(summary = "Validate PDF given by URL against the selected profile and return validation result")
	@ApiResponses(value = {
			@ApiResponse(responseCode = "200", description = "OK", content = {
					@Content(mediaType = "application/xml", schema =
					@Schema(implementation = InputStream.class)),
					@Content(mediaType = "application/json", schema =
					@Schema(implementation = InputStream.class)),
					@Content(mediaType = "text/html", schema =
					@Schema(implementation = InputStream.class)
					)})})
	@Consumes(MediaType.MULTIPART_FORM_DATA)
	@Produces({MediaType.APPLICATION_XML})
	public static InputStream validateXml(@Parameter(description = "the String id of the Validation profile " +
	                                                               "(auto, 1b, 1a, 2b, 2a, 2u, 3b, 3a, 3u, 4, 4e, 4f, ua1 or ua2)")
	                                      @PathParam("profileId") String profileId,
	                                      @Parameter(description = "a URL of PDF to be validated")
	                                      @FormDataParam("url") String urlLink,
	                                      @Parameter(name = FILE_SIZE_HEADER, description = "a size of the PDF to be validated")
	                                      @HeaderParam(FILE_SIZE_HEADER) Integer fileSize) {
		return validateUrl(urlLink, profileId, FormatOption.XML, fileSize);
	}

	/**
	 * @param profileId
	 *                                 the String id of the Validation profile
	 *                                 (auto, 1b, 1a, 2b, 2a, 2u,
	 *                                 3b, 3a, 3u, 4, 4e, 4f, ua1 or ua2)
	 * @param uploadedInputStream
	 *                                 a {@link java.io.InputStream} to the PDF to
	 *                                 be validated
	 * @param contentDispositionHeader
	 * @return the {@link org.verapdf.pdfa.results.ValidationResult} obtained
	 *         when validating the uploaded stream against the selected profile.
	 */
	@POST
	@Path("/{profileId}")
	@Consumes(MediaType.MULTIPART_FORM_DATA)
	@Produces({MediaType.APPLICATION_JSON})
	public static InputStream validateJson(@Parameter(description = "the String id of the Validation profile " +
	                                                                "(auto, 1b, 1a, 2b, 2a, 2u, 3b, 3a, 3u, 4, 4e, 4f, ua1 or ua2)")
	                                       @PathParam("profileId") String profileId,
	                                       @Parameter(name = "file", schema = @Schema(implementation = File.class),
	                                                  style = ParameterStyle.FORM, description = "an InputStream of the PDF to be validated")
	                                       @FormDataParam("file") InputStream uploadedInputStream,
	                                       @Parameter(hidden = true) @FormDataParam("file") final FormDataContentDisposition contentDispositionHeader,
	                                       @Parameter(name = FILE_SIZE_HEADER, description = "a size of the PDF to be validated")
	                                       @HeaderParam(FILE_SIZE_HEADER) Integer fileSize) {
		return validateFile(uploadedInputStream, contentDispositionHeader, profileId, null, FormatOption.JSON, fileSize);
	}

	/**
	 * @param profileId
	 *                                 the String id of the Validation profile
	 *                                 (auto, 1b, 1a, 2b, 2a, 2u,
	 *                                 3b, 3a, 3u, 4, 4e, 4f, ua1 or ua2)
	 * @param sha1Hex
	 *                                 the hex String representation of the file's
	 *                                 SHA-1 hash
	 * @param uploadedInputStream
	 *                                 a {@link java.io.InputStream} to the PDF to
	 *                                 be validated
	 * @param contentDispositionHeader
	 * @return the {@link org.verapdf.pdfa.results.ValidationResult} obtained
	 *         when validating the uploaded stream against the selected profile.
	 */
	@POST
	@Path("/sha/{profileId}")
	@Consumes(MediaType.MULTIPART_FORM_DATA)
	@Produces({MediaType.APPLICATION_JSON})
	public static InputStream validateJson(@Parameter(description = "the String id of the Validation profile " +
	                                                                "(auto, 1b, 1a, 2b, 2a, 2u, 3b, 3a, 3u, 4, 4e, 4f, ua1 or ua2)")
	                                       @PathParam("profileId") String profileId,
	                                       @Parameter(description = "the hex String representation of the file's SHA-1 hash")
	                                       @FormDataParam("sha1Hex") String sha1Hex,
	                                       @Parameter(name = "file", schema = @Schema(implementation = File.class),
	                                                  style = ParameterStyle.FORM, description = "an InputStream of the PDF to be validated")
	                                       @FormDataParam("file") InputStream uploadedInputStream,
	                                       @Parameter(hidden = true) @FormDataParam("file") final FormDataContentDisposition contentDispositionHeader,
	                                       @Parameter(name = FILE_SIZE_HEADER, description = "a size of the PDF to be validated")
	                                       @HeaderParam(FILE_SIZE_HEADER) Integer fileSize) {

		return validateFile(uploadedInputStream, contentDispositionHeader, profileId, sha1Hex, FormatOption.JSON, fileSize);
	}

	@POST
	@Path("/url/{profileId}")
	@Consumes(MediaType.MULTIPART_FORM_DATA)
	@Produces({MediaType.APPLICATION_JSON})
	public static InputStream validateJson(@Parameter(description = "the String id of the Validation profile " +
	                                                                "(auto, 1b, 1a, 2b, 2a, 2u, 3b, 3a, 3u, 4, 4e, 4f, ua1 or ua2)")
	                                       @PathParam("profileId") String profileId,
	                                       @Parameter(description = "a URL of PDF to be validated")
	                                       @FormDataParam("url") String urlLink,
	                                       @Parameter(name = FILE_SIZE_HEADER, description = "a size of the PDF to be validated")
	                                       @HeaderParam(FILE_SIZE_HEADER) Integer fileSize) {
		return validateUrl(urlLink, profileId, FormatOption.JSON, fileSize);
	}

	/**
	 * @param profileId
	 *                                 the String id of the Validation profile
	 *                                 (auto, 1b, 1a, 2b, 2a, 2u,
	 *                                 3b, 3a, 3u, 4, 4e, 4f, ua1 or ua2)
	 * @param uploadedInputStream
	 *                                 a {@link java.io.InputStream} to the PDF to
	 *                                 be validated
	 * @param contentDispositionHeader
	 * @return
	 */
	@POST
	@Path("/{profileId}")
	@Consumes(MediaType.MULTIPART_FORM_DATA)
	@Produces({MediaType.TEXT_HTML})
	public static InputStream validateHtml(@PathParam("profileId") String profileId,
	                                       @Parameter(name = "file", schema = @Schema(implementation = File.class),
	                                                  style = ParameterStyle.FORM, description = "an InputStream of the PDF to be validated")
	                                       @FormDataParam("file") InputStream uploadedInputStream,
	                                       @Parameter(hidden = true) @FormDataParam("file") final FormDataContentDisposition contentDispositionHeader,
	                                       @Parameter(name = FILE_SIZE_HEADER, description = "a size of the PDF to be validated")
	                                       @HeaderParam(FILE_SIZE_HEADER) Integer fileSize) {
		return validateFile(uploadedInputStream, contentDispositionHeader, profileId, null, FormatOption.HTML, fileSize);
	}

	/**
	 * @param profileId
	 *                                 the String id of the Validation profile
	 *                                 (auto, 1b, 1a, 2b, 2a, 2u,
	 *                                 3b, 3a, 3u, 4, 4e, 4f, ua1 or ua2)
	 * @param sha1Hex
	 *                                 the hex String representation of the file's
	 *                                 SHA-1 hash
	 * @param uploadedInputStream
	 *                                 a {@link java.io.InputStream} to the PDF to
	 *                                 be validated
	 * @param contentDispositionHeader
	 * @return
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
	                                       @Parameter(hidden = true) @FormDataParam("file") final FormDataContentDisposition contentDispositionHeader,
	                                       @Parameter(name = FILE_SIZE_HEADER, description = "a size of the PDF to be validated")
	                                       @HeaderParam(FILE_SIZE_HEADER) Integer fileSize) {
		return validateFile(uploadedInputStream, contentDispositionHeader, profileId, sha1Hex, FormatOption.HTML, fileSize);
	}

	@POST
	@Path("/url/{profileId}")
	@Consumes(MediaType.MULTIPART_FORM_DATA)
	@Produces({MediaType.TEXT_HTML})
	public static InputStream validateHtml(@Parameter(description = "the String id of the Validation profile " +
	                                                                "(auto, 1b, 1a, 2b, 2a, 2u, 3b, 3a, 3u, 4, 4e, 4f, ua1 or ua2)")
	                                       @PathParam("profileId") String profileId,
	                                       @Parameter(description = "a URL of PDF to be validated")
	                                       @FormDataParam("url") String urlLink,
	                                       @Parameter(name = FILE_SIZE_HEADER, description = "a size of the PDF to be validated")
	                                       @HeaderParam(FILE_SIZE_HEADER) Integer fileSize) {
		return validateUrl(urlLink, profileId, FormatOption.HTML, fileSize);
	}

	public static void setMaxFileSize(Integer maxFileSize) {
		ValidateResource.maxFileSize = maxFileSize;
	}

	private static InputStream validateFile(InputStream uploadedInputStream,
	                                        FormDataContentDisposition contentDispositionHeader, String profileId,
	                                        String sha1Hex, FormatOption formatOption, Integer fileSize) {
		if (contentDispositionHeader == null) {
			throw new BadRequestException("File is empty");
		}

		return validate(uploadedInputStream, contentDispositionHeader.getFileName(), profileId, sha1Hex, formatOption, fileSize);
	}

	private static InputStream validateUrl(String urlLink, String profileId, FormatOption formatOption, Integer fileSize) {
		InputStream uploadedInputStream = getInputStreamByUrlLink(urlLink);

		return validate(uploadedInputStream, urlLink, profileId, null, formatOption, fileSize);
	}

	private static InputStream validate(InputStream uploadedInputStream, String fileName, String profileId,
	                                    String sha1Hex, FormatOption formatOption, Integer fileSize) {
		if (fileName == null) {
			throw new BadRequestException("File name is empty");
		}

		SeekableInputStream seekableInputStream = createInputStream(uploadedInputStream, sha1Hex, fileSize);
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
			throw new InternalServerErrorException("Some Java Exception while validating", excep);
		}
		return new ByteArrayInputStream(outputBytes);
	}

	private static SeekableInputStream createInputStream(InputStream uploadedInputStream, String sha1Hex, Integer fileSize) {
		InputStream inputStream = uploadedInputStream;
		if (sha1Hex != null) {
			MessageDigest sha1 = getDigest();
			inputStream = new DigestInputStream(uploadedInputStream, sha1);
		}
		if (fileSize != null && fileSize > maxFileSize * CONVERTER_MB_TO_B) {
			throw new BadRequestException(String.format(MAX_ALLOWED_FILE_SIZE, maxFileSize));
		}

		try {
			SeekableInputStream seekableInputStream = SeekableInputStream.getSeekableStream(inputStream, CONVERTER_MB_TO_B * maxFileSize);
			if (sha1Hex != null && !sha1Hex.equalsIgnoreCase(Hex.encodeHexString(((DigestInputStream) inputStream).getMessageDigest().digest()))) {
				throw new BadRequestException("Incorrect sha1 value");
			}
			return seekableInputStream;
		} catch (VeraPDFParserException e) {
			throw new BadRequestException(String.format(MAX_ALLOWED_FILE_SIZE, maxFileSize), e);
		} catch (IOException e) {
			throw new InternalServerErrorException(e.getMessage(), e);
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

	private static InputStream getInputStreamByUrlLink(String urlLink) {
		try {
			return new URL(urlLink).openStream();
		} catch (IOException e) {
			if (urlLink.isEmpty()) {
				throw new BadRequestException("URL is empty", e);
			} else {
				throw new BadRequestException("URL is incorrect: " + urlLink, e);
			}
		}
	}

	private static BatchSummary processStream(SeekableInputStream inputStream, String fileName, ProcessorConfig config, OutputStream stream,
	                                          VeraAppConfig appConfig, FormatOption formatOption)
			throws IOException {
		BatchSummary summary;
		try (BatchProcessor processor = ProcessorFactory.fileBatchProcessor(config)) {
			summary = processor.process(ItemDetails.fromValues(fileName, inputStream.getStreamLength()), inputStream,
			                            ProcessorFactory.getHandler(formatOption, appConfig.isVerbose(), stream,
			                                                        config.getValidatorConfig().isRecordPasses(), appConfig.getWikiPath()));
		} catch (VeraPDFException e) {
			throw new InternalServerErrorException(e.getMessage(), e);
		} finally {
			if (inputStream != null) {
				inputStream.close();
			}
		}
		return summary;
	}
}
