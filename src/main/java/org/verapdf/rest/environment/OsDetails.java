/**
 * 
 */
package org.verapdf.rest.environment;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlRootElement;



/**
 * @author <a href="mailto:carl@openpreservation.org">Carl Wilson</a>.</p>
 */
@JacksonXmlRootElement
public class OsDetails {
	private final static OsDetails INSTANCE = new OsDetails();
	private final String name;
	private final String version;
	private final String architecture;
	
	private OsDetails() {
		this.name = Environments.getOSName();
		this.version = Environments.getOSVersion();
		this.architecture = Environments.getOSArch();
	}
	
	/**
	 * @return the OS Details instance
	 */
	public final static OsDetails getInstance() {
		return INSTANCE;
	}
	
	/**
	 * @return the os name
	 */
	@JsonProperty
	public String getName() {
		return this.name;
	}
	
	/**
	 * @return the os version
	 */
	@JsonProperty
	public String getVersion() {
		return this.version;
	}
	
	/**
	 * @return the os architecture
	 */
	@JsonProperty
	public String getArchitecture() {
		return this.architecture;
	}
}
