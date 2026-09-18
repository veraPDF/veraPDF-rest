package org.verapdf.rest.resources;

import io.swagger.v3.oas.annotations.Hidden;
import org.verapdf.rest.views.SwaggerUiView;

import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;

/**
 * Serves the Swagger UI page, which reads the OpenAPI description published at
 * /openapi.json.
 */
@Hidden
@Path("/swagger")
public class SwaggerUiResource {

	/**
	 * @return a new {@link org.verapdf.rest.views.SwaggerUiView} for the
	 *         Swagger UI page.
	 */
	@GET
	@Produces({ MediaType.TEXT_HTML })
	public static SwaggerUiView swaggerUi() {
		return new SwaggerUiView();
	}

}
