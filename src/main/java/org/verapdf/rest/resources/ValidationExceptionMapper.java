/**
 * 
 */
package org.verapdf.rest.resources;

import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;
import javax.ws.rs.ext.ExceptionMapper;

import org.verapdf.core.ValidationException;

/**
 * @author  <a href="mailto:carl@openpreservation.org">Carl Wilson</a>.
 *
 */
public final class ValidationExceptionMapper implements ExceptionMapper<ValidationException> {

    @Override
    public Response toResponse(ValidationException exception) {
        StringBuilder builder = new StringBuilder();
        builder.append(exception.getMessage());
        if (exception.getStackTrace().length > 0) {
            StackTraceElement trace = exception.getStackTrace()[0];            
            builder.append(" at " + trace.getClassName() + "." + trace.getMethodName() + "(" + trace.getFileName() + ":" + trace.getLineNumber() + ")");
        }
        return Response.status(Status.INTERNAL_SERVER_ERROR).entity(builder.toString()).type(MediaType.TEXT_PLAIN).build();
    }

}
