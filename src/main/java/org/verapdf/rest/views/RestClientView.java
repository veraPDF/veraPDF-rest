/**
 * 
 */
package org.verapdf.rest.views;

import io.dropwizard.views.View;

/**
 * @author  <a href="mailto:carl@openpreservation.org">Carl Wilson</a>
 *          <a href="https://github.com/carlwilson">carlwilson AT github</a>
 *
 * @version 0.1
 * 
 * Created 3 Sep 2016:17:05:03
 */
public class RestClientView extends View {
    
    /**
     * Default constructor, simply calls super constructor with mustache template
     */
    public RestClientView() {
        super("restclient.mustache"); //$NON-NLS-1$
    }

}
