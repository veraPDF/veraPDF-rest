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
public class ServerDetails {
	private static final ServerDetails INSTANCE = new ServerDetails();
	private final String ipAddress;
	private final String hostName;
	private final String machAddress;
	
	private ServerDetails() {
		this.ipAddress=Environments.getHostAddress();
		this.hostName=Environments.getHostName();
		this.machAddress=Environments.getMachAddress();
	}
	
	/**
	 * @return the server details instance
	 */
	public static final ServerDetails getInstance() {
		return INSTANCE;
	}
	
	/**
	 * @return the JVM's host's IP address
	 */
	@JsonProperty
	public String getIpAddress() {
		return this.ipAddress;
	}
	
	/**
	 * @return the JVM's host's name
	 */
	@JsonProperty
	public String getHostName() {
		return this.hostName;
	}
	
	/**
	 * @return the JVM's host's MACH address
	 */
	@JsonProperty
	public String getMachAddress() {
		return this.machAddress;
	}
}
