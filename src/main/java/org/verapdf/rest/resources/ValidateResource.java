/**
 * 
 */
package org.verapdf.rest.resources;

import java.io.IOException;
import java.io.InputStream;
import java.security.DigestInputStream;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import javax.ws.rs.Consumes;
import javax.ws.rs.NotSupportedException;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

import org.apache.commons.codec.binary.Hex;
import org.glassfish.jersey.media.multipart.FormDataContentDisposition;
import org.glassfish.jersey.media.multipart.FormDataParam;
import org.verapdf.core.ValidationException;
import org.verapdf.model.ModelParser;
import org.verapdf.pdfa.PDFAValidator;
import org.verapdf.pdfa.flavours.PDFAFlavour;
import org.verapdf.pdfa.results.ValidationResult;
import org.verapdf.pdfa.validators.Validators;

/**
 * @author <a href="mailto:carl@openpreservation.org">Carl Wilson</a>
 *
 */
public class ValidateResource {
    // java.security.digest name for the MD5 algorithm
    private static final String SHA1_NAME = "SHA-1";

    /**
     * @return a validation profile selected by id
     * @throws ValidationException
     * 
     */
    @POST
    @Path("/{profileid}")
    @Consumes(MediaType.MULTIPART_FORM_DATA)
    @Produces({ MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML })
    public ValidationResult validate(
            @PathParam("profileid") String profileId,
            @FormDataParam("sha1Hex") String sha1Hex,
            @FormDataParam("file") InputStream uploadedInputStream,
            @FormDataParam("file") final FormDataContentDisposition contentDispositionHeader)
            throws ValidationException {
        PDFAFlavour flavour = PDFAFlavour.byFlavourId(profileId);
        MessageDigest sha1 = getDigest();
        DigestInputStream dis = new DigestInputStream(uploadedInputStream, sha1);
        try (ModelParser toValidate = ModelParser.createModelWithFlavour(dis,
                flavour)) {
            PDFAValidator validator = Validators
                    .createValidator(flavour, false);
            ValidationResult result = validator.validate(toValidate);
            return result;
        } catch (IOException e) {
            // If we have the same sha-1 then it's a PDF Box parse error, so
            // treat as non PDF.
            if (sha1Hex.equalsIgnoreCase(Hex.encodeHexString(sha1.digest()))) {
                throw new NotSupportedException(Response
                        .status(Status.UNSUPPORTED_MEDIA_TYPE)
                        .type(MediaType.TEXT_PLAIN).entity("File does not appear to be a PDF.").build());
            }
            throw (new ValidationException(e.getClass() + ":" + e.getMessage()
                    + " thrown during validation.", e));
        } catch (Exception e) {
            e.printStackTrace();
            throw (new ValidationException(e.getClass() + ":" + e.getMessage()
                    + " thrown during validation.", e));
        }
    }

    private static MessageDigest getDigest() {
        try {
            return MessageDigest.getInstance(SHA1_NAME);
        } catch (NoSuchAlgorithmException excep) {
            // If this happens the Java Digest algorithms aren't present, a
            // faulty Java install??
            throw new IllegalStateException(
                    "No digest algorithm implementation for " + SHA1_NAME
                            + ", check you Java installation.");
        }
    }
}
