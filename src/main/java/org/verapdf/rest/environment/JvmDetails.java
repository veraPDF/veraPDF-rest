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
public class JvmDetails {
	private final static JvmDetails INSTANCE = new JvmDetails();
	private final String vendor;
	private final String version;
	private final String architecture;
	private final String home;
	
	private JvmDetails() {
		this.vendor = Environments.getJavaVendor();
		this.version = Environments.getJavaVersion();
		this.architecture = Environments.getJavaArch();
		this.home = Environments.getJavaHome();
	}

	/**
	 * @return the JVM details instance
	 */
	public static final JvmDetails getInstance() {
		return INSTANCE;
	}

	/**
	 * @return the Java vendor
	 */
	@JsonProperty
	public String getVendor() {
		return this.vendor;
	}
	
	/**
	 * @return the Java version
	 */
	@JsonProperty
	public String getVersion() {
		return this.version;
	}
	
	/**
	 * @return the Java system architecture
	 */
	@JsonProperty
	public String getArchitecture() {
		return this.architecture;
	}
	
	/**
	 * @return the value of JAVA_HOME
	 */
	@JsonProperty
	public String getHome() {
		return this.home;
	}
}
