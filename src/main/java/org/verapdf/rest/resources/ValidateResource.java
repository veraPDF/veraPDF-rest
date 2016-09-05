/**
 * 
 */
package org.verapdf.rest.resources;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.security.DigestInputStream;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Date;

import javax.ws.rs.Consumes;
import javax.ws.rs.NotSupportedException;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;
import javax.xml.bind.JAXBException;
import javax.xml.transform.TransformerException;

import org.apache.commons.codec.binary.Hex;
import org.apache.commons.io.output.ByteArrayOutputStream;
import org.glassfish.jersey.media.multipart.FormDataContentDisposition;
import org.glassfish.jersey.media.multipart.FormDataParam;
import org.verapdf.core.ModelParsingException;
import org.verapdf.core.VeraPDFException;
import org.verapdf.model.ModelParser;
import org.verapdf.pdfa.PDFAValidator;
import org.verapdf.pdfa.flavours.PDFAFlavour;
import org.verapdf.pdfa.results.ValidationResult;
import org.verapdf.pdfa.validation.ProfileDirectory;
import org.verapdf.pdfa.validation.Profiles;
import org.verapdf.pdfa.validators.Validators;
import org.verapdf.report.HTMLReport;
import org.verapdf.report.ItemDetails;
import org.verapdf.report.MachineReadableReport;

/**
 * @author <a href="mailto:carl@openpreservation.org">Carl Wilson</a>
 */
public class ValidateResource {
	private static final ProfileDirectory DIRECTORY = Profiles.getVeraProfileDirectory();
	// java.security.digest name for the MD5 algorithm
	private static final String SHA1_NAME = "SHA-1";

	/**
	 * @param profileId
	 *            the String id of the Validation profile (1b, 1a, 2b, 2a, 2u,
	 *            3b, 3a, or 3u)
	 * @param sha1Hex
	 *            the hex String representation of the file's SHA-1 hash
	 * @param uploadedInputStream
	 *            a {@link java.io.InputStream} to the PDF to be validated
	 * @param contentDispositionHeader
	 * @return the {@link org.verapdf.pdfa.results.ValidationResult} obtained
	 *         when validating the uploaded stream against the selected profile.
	 * @throws VeraPDFException
	 */
	@POST
	@Path("/{profileid}")
	@Consumes(MediaType.MULTIPART_FORM_DATA)
	@Produces({ MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML })
	public static ValidationResult validate(@PathParam("profileid") String profileId,
			@FormDataParam("sha1Hex") String sha1Hex, @FormDataParam("file") InputStream uploadedInputStream,
			@FormDataParam("file") final FormDataContentDisposition contentDispositionHeader) throws VeraPDFException {
		PDFAFlavour flavour = PDFAFlavour.byFlavourId(profileId);
		MessageDigest sha1 = getDigest();
		DigestInputStream dis = new DigestInputStream(uploadedInputStream, sha1);
		try (ModelParser toValidate = ModelParser.createModelWithFlavour(dis, flavour)) {
			PDFAValidator validator = Validators.createValidator(flavour, false);
			ValidationResult result = validator.validate(toValidate);
			return result;
		} catch (ModelParsingException mpExcep) {
			// If we have the same sha-1 then it's a PDF Box parse error, so
			// treat as non PDF.
			if (sha1Hex.equalsIgnoreCase(Hex.encodeHexString(sha1.digest()))) {
				throw new NotSupportedException(Response.status(Status.UNSUPPORTED_MEDIA_TYPE)
						.type(MediaType.TEXT_PLAIN).entity("File does not appear to be a PDF.").build(), mpExcep);
			}
			throw mpExcep;
		}
	}

	/**
	 * @param profileId
	 *            the String id of the Validation profile (1b, 1a, 2b, 2a, 2u,
	 *            3b, 3a, or 3u)
	 * @param sha1Hex
	 *            the hex String representation of the file's SHA-1 hash
	 * @param uploadedInputStream
	 *            a {@link java.io.InputStream} to the PDF to be validated
	 * @param contentDispositionHeader
	 * @return the {@link org.verapdf.pdfa.results.ValidationResult} obtained
	 *         when validating the uploaded stream against the selected profile.
	 * @throws VeraPDFException
	 */
	@POST
	@Path("/{profileid}")
	@Consumes(MediaType.MULTIPART_FORM_DATA)
	@Produces({ MediaType.TEXT_HTML })
	public static InputStream validateHtml(@PathParam("profileid") String profileId,
			@FormDataParam("sha1Hex") String sha1Hex, @FormDataParam("file") InputStream uploadedInputStream,
			@FormDataParam("file") final FormDataContentDisposition contentDispositionHeader) throws VeraPDFException {
		long start = new Date().getTime();
		ValidationResult result = validate(profileId, sha1Hex, uploadedInputStream, contentDispositionHeader);
		MachineReadableReport mrr = MachineReadableReport.fromValues(
				ItemDetails.fromValues(contentDispositionHeader.getFileName(), contentDispositionHeader.getSize()),
				DIRECTORY.getValidationProfileByFlavour(PDFAFlavour.byFlavourId(profileId)), result, false, 0, null,
				null, new Date().getTime() - start);
		byte[] htmlBytes = new byte[0];
		try (ByteArrayOutputStream xmlBos = new ByteArrayOutputStream()) {
			MachineReadableReport.toXml(mrr, xmlBos, Boolean.FALSE);
			htmlBytes = getHtmlBytes(xmlBos.toByteArray());
		} catch (IOException | JAXBException | TransformerException excep) {
			excep.printStackTrace();
			throw new VeraPDFException("Some Java Exception while validating", excep);
			// TODO Auto-generated catch block
		}
		return new ByteArrayInputStream(htmlBytes);
	}

	private static MessageDigest getDigest() {
		try {
			return MessageDigest.getInstance(SHA1_NAME);
		} catch (NoSuchAlgorithmException nsaExcep) {
			// If this happens the Java Digest algorithms aren't present, a
			// faulty Java install??
			throw new IllegalStateException(
					"No digest algorithm implementation for " + SHA1_NAME + ", check you Java installation.", nsaExcep);
		}
	}

	private static byte[] getHtmlBytes(byte[] xmlBytes) throws IOException, TransformerException {
		try (ByteArrayInputStream xmlBis = new ByteArrayInputStream(xmlBytes);
				ByteArrayOutputStream htmlBos = new ByteArrayOutputStream()) {
			HTMLReport.writeHTMLReport(xmlBis, htmlBos, null, false);
			return htmlBos.toByteArray();
		}

	}
}
