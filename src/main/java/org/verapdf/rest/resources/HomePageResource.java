package org.verapdf.rest.resources;

import org.verapdf.rest.views.RestClientView;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

@Path("/")
public class HomePageResource {

    @GET
    @Produces({ MediaType.TEXT_HTML })
    public RestClientView client() {
        return new RestClientView();
    }

}
