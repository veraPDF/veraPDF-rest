/**
 * 
 */
package org.verapdf.rest.app;

import io.dropwizard.Application;
import io.dropwizard.assets.AssetsBundle;
import io.dropwizard.forms.MultiPartBundle;
import io.dropwizard.setup.Bootstrap;
import io.dropwizard.setup.Environment;

import java.util.EnumSet;

import javax.servlet.DispatcherType;
import javax.servlet.FilterRegistration;

import io.dropwizard.views.ViewBundle;

import org.eclipse.jetty.servlets.CrossOriginFilter;
import org.verapdf.rest.resources.ApiResource;

import com.yunspace.dropwizard.xml.XmlBundle;

import org.verapdf.rest.resources.HomePageResource;

/**
 * @author <a href="mailto:carl@openpreservation.org">Carl Wilson</a>
 *
 */
public class VeraPdfRestApplication extends Application<VeraPdfRestConfiguration> {

    private static final String NAME = "verapdf-rest"; //$NON-NLS-1$

    /**
     * Main method for Jetty server application. Simply calls the run method
     * with command line args.
     * 
     * @param args
     *            command line arguments as string array.
     * @throws Exception
     *             passes any exception thrown by run
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
        // Dropwizard bundle to handle Jackson XML serialisation
        final XmlBundle xmlBundle = new XmlBundle();
        bootstrap.addBundle(xmlBundle);
        bootstrap.addBundle(new MultiPartBundle());
        bootstrap.addBundle(new ViewBundle<VeraPdfRestConfiguration>());
        bootstrap.addBundle(new AssetsBundle("/assets/css", "/css", null, "css")); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
        bootstrap.addBundle(new AssetsBundle("/assets/js", "/js", null, "js")); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
        bootstrap.addBundle(new AssetsBundle("/assets/img", "/img", null, "img")); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-
    }

    @Override
    public void run(VeraPdfRestConfiguration configuration,
            Environment environment) throws Exception {
        // Create & register our REST resources
        final ApiResource restApi = new ApiResource();
        final HomePageResource homePageResource = new HomePageResource();
        environment.jersey().register(restApi);
        environment.jersey().register(homePageResource);
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
