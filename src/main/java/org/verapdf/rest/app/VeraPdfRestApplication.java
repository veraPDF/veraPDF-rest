/**
 *
 */
package org.verapdf.rest.app;

import java.util.EnumSet;

import jakarta.servlet.DispatcherType;
import jakarta.servlet.FilterRegistration;

import io.dropwizard.configuration.EnvironmentVariableSubstitutor;
import io.dropwizard.configuration.SubstitutingSourceProvider;
import org.eclipse.jetty.ee10.servlets.CrossOriginFilter;
import org.verapdf.rest.resources.ApiResource;
import org.verapdf.rest.resources.HomePageResource;
import org.verapdf.rest.resources.SwaggerUiResource;
import org.verapdf.rest.resources.ValidateResource;
import org.verapdf.rest.resources.ValidationExceptionMapper;

import io.dropwizard.core.Application;
import io.dropwizard.assets.AssetsBundle;
import io.dropwizard.forms.MultiPartBundle;
import io.dropwizard.core.setup.Bootstrap;
import io.dropwizard.core.setup.Environment;
import io.dropwizard.views.common.ViewBundle;
import io.swagger.v3.jaxrs2.integration.resources.OpenApiResource;

import java.util.Collections;

/**
 * @author <a href="mailto:carl@openpreservation.org">Carl Wilson</a>
 *
 */
public class VeraPdfRestApplication extends Application<VeraPdfRestConfiguration> {

    private static final String NAME = "verapdf-rest"; //$NON-NLS-1$

    /**
     * Classpath root of the swagger-ui webjar. The version segment is part of
     * the path, so this must be kept in step with the swagger.ui.version
     * property in pom.xml.
     */
    private static final String SWAGGER_UI_WEBJAR_PATH = "/META-INF/resources/webjars/swagger-ui/5.32.14"; //$NON-NLS-1$

    /**
     * Main method for Jetty server application. Simply calls the run method
     * with command line args.
     *
     * @param args
     *             command line arguments as string array.
     * @throws Exception
     *                   passes any exception thrown by run
     */
    public static void main(String[] args) throws Exception {
        new VeraPdfRestApplication().run(args);
    }

    @Override
    public String getName() {
        return NAME;
    }

    @Override
    public void initialize(Bootstrap<VeraPdfRestConfiguration> bootstrap) {
        bootstrap.addBundle(new MultiPartBundle());
        bootstrap.addBundle(new ViewBundle<>());
        bootstrap.setConfigurationSourceProvider(
                new SubstitutingSourceProvider(bootstrap.getConfigurationSourceProvider(),
                                               new EnvironmentVariableSubstitutor(false)
                ));
        bootstrap.addBundle(new AssetsBundle("/assets/css", "/css", null, "css")); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
        bootstrap.addBundle(new AssetsBundle("/assets/js", "/js", null, "js")); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
        bootstrap.addBundle(new AssetsBundle("/assets/img", "/img", null, "img")); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
                                                                                   // //$NON-NLS-
        // The Swagger UI library, served from its webjar. The page that loads
        // it is SwaggerUiResource at /swagger.
        bootstrap.addBundle(new AssetsBundle(SWAGGER_UI_WEBJAR_PATH, "/swagger-ui", null, "swagger-ui")); //$NON-NLS-1$ //$NON-NLS-2$
    }

    @Override
    public void run(VeraPdfRestConfiguration configuration,
            Environment environment) {
        // Create & register our REST resources
        final ValidationExceptionMapper vem = new ValidationExceptionMapper();
        ValidateResource validateResource = new ValidateResource();
        ValidateResource.setMaxFileSize(configuration.getMaxFileSize());
        environment.jersey().register(validateResource);
        environment.jersey().register(new ApiResource());
        environment.jersey().register(new HomePageResource());
        environment.jersey().register(new SwaggerUiResource());
        environment.jersey().register(vem);
        // Serves the OpenAPI description at /openapi.json and /openapi.yaml.
        environment.jersey().register(new OpenApiResource()
                .resourcePackages(Collections.singleton("org.verapdf.rest.resources"))); //$NON-NLS-1$
        // Set up cross domain REST
        setupCORS(environment);
    }

    private static void setupCORS(Environment environment) {
        // Enable CORS headers
        final FilterRegistration.Dynamic cors = environment.servlets()
                .addFilter("CORS", CrossOriginFilter.class); //$NON-NLS-1$

        // Configure CORS parameters
        cors.setInitParameter("allowedOrigins", "*"); //$NON-NLS-1$ //$NON-NLS-2$
        cors.setInitParameter(
                "allowedHeaders", "X-Requested-With,Content-Type,Accept,Origin"); //$NON-NLS-1$ //$NON-NLS-2$
        cors.setInitParameter(
                "allowedMethods", "OPTIONS,GET,PUT,POST,DELETE,HEAD"); //$NON-NLS-1$ //$NON-NLS-2$

        // Add URL mapping
        cors.addMappingForUrlPatterns(EnumSet.allOf(DispatcherType.class),
                true, "/*"); //$NON-NLS-1$
    }

}
