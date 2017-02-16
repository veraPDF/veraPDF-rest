/**
 * 
 */
package org.openpreservation.bytestreams;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;


/**
 * Encapsulates two byte stream properties:
 * <ul>
 * <li>The length of the ByteStream in bytes, will be >= 0.</li>
 * <li>The hex encoded SHA1 digest calculated from the ByteStream, a 40
 * character hex string, i.e. [0-9a-f]{40}.</li>
 * </ul>
 * Used as an identifier for tagging richer metadata and associating it with a
 * ByteStream.
 * 
 * @author <a href="mailto:carl@openpreservation.org">Carl Wilson</a>
 */
@JsonSerialize(as = ByteStreamIdImpl.class)
@JsonDeserialize(as = ByteStreamIdImpl.class)
public interface ByteStreamId {
	/**
	 * @return The length of the ByteStream in bytes, will be >= 0.
	 */
	public long getLength();

	/**
	 * @return The hex encoded SHA1 digest calculated from the ByteStream, a
	 *         40 character hex string, i.e. [0-9a-f]{40}.
	 */
	public String getHexSHA1();

	/**
	 * @param id
	 *            id for comparison
	 * @return -1 if the item less than id, 0 if equal, 1 if greater
	 */
	public int compareTo(final ByteStreamId id);
}
