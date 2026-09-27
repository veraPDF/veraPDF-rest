/**
 *
 */
package org.verapdf.rest.views;

import io.dropwizard.views.common.View;

/**
 * Swagger UI page. The page itself is this view; the Swagger UI library it
 * loads is served from the swagger-ui webjar, mounted at /swagger-ui by
 * {@link org.verapdf.rest.app.VeraPdfRestApplication}.
 */
public class SwaggerUiView extends View {

    /**
     * Default constructor, simply calls super constructor with mustache template
     */
    public SwaggerUiView() {
        super("swagger.mustache"); //$NON-NLS-1$
    }

}
