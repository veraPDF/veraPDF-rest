package org.verapdf.rest.resources;

import io.swagger.v3.oas.annotations.Hidden;
import org.verapdf.rest.views.RestClientView;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

/**
 * @author <a href="mailto:carl@openpreservation.org">Carl Wilson</a>
 *         <a href="https://github.com/carlwilson">carlwilson AT github</a>
 * @version 0.1 Created 3 Sep 2016:17:00:32
 */
@Hidden
@Path("/")
public class HomePageResource {

	/**
	 * @return a new
	 *         {@link org.verapdf.rest.views.RestClientView} for
	 *         the home page.
	 */
	@GET
	@Produces({ MediaType.TEXT_HTML })
	public static RestClientView client() {
		return new RestClientView();
	}

}
