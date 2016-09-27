/**
 * 
 */
package org.verapdf.rest.resources;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.xml.bind.JAXBException;

import org.glassfish.jersey.media.multipart.FormDataContentDisposition;
import org.glassfish.jersey.media.multipart.FormDataParam;
import org.verapdf.PdfBoxFoundry;
import org.verapdf.pdfa.BatchValidator;
import org.verapdf.pdfa.flavours.PDFAFlavour;
import org.verapdf.pdfa.validation.Profiles;
import org.verapdf.pdfa.validation.ValidationProfile;
import org.verapdf.pdfa.validators.ReferenceBatchValidator;
import org.verapdf.report.ValidationBatchReport;

/**
 * @author  <a href="mailto:carl@openpreservation.org">Carl Wilson</a>
 *          <a href="https://github.com/carlwilson">carlwilson AT github</a>
 *
 * @version 0.1
 * 
 * Created 22 Sep 2016:11:44:26
 */
public class ZipProcessingResource {
	@POST
	@Path("/{profileid}")
	@Consumes(MediaType.MULTIPART_FORM_DATA)
	@Produces({ MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML })
	public static ValidationProfile validate(@PathParam("profileid") String profileId,
			@FormDataParam("file") InputStream uploadedInputStream,
			@FormDataParam("file") final FormDataContentDisposition contentDispositionHeader) throws IOException {
		PDFAFlavour flavour = PDFAFlavour.byFlavourId(profileId);
		PdfBoxFoundry.initialise();
		File tempFile = createTempFileStream(uploadedInputStream, "vera");
		BatchValidator validator = new ReferenceBatchValidator(contentDispositionHeader.getFileName(), flavour, true);
		ValidationBatchReport report = validator.processArchive(tempFile);
        OutputStream out = System.out;
        try {
			ValidationBatchReport.toXml(report, out, Boolean.TRUE);
		} catch (JAXBException excep) {
			// TODO Auto-generated catch block
			excep.printStackTrace();
		}
		return Profiles.getVeraProfileDirectory().getValidationProfileById("1b");
	}

    private static File createTempFileStream(final InputStream corpusInput,
            final String tempPrefix) throws IOException {
        File tempFile = File.createTempFile(tempPrefix, "zip");
        try (OutputStream output = new FileOutputStream(tempFile)) {
            byte[] buffer = new byte[8 * 1024];
            int bytesRead;
            while ((bytesRead = corpusInput.read(buffer)) != -1) {
                output.write(buffer, 0, bytesRead);
            }
        }
        return tempFile;
    }

}
