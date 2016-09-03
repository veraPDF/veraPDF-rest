/**
 * 
 */
package org.openpreservation.bytestreams;


/**
 * Straightforward immutable implementation of the {@link ByteStreamId} interface. 
 * 
 * @author <a href="mailto:carl@openpreservation.org">Carl Wilson</a> <a
 */
final class ByteStreamIdImpl implements ByteStreamId, Comparable<ByteStreamId> {
	static final ByteStreamIdImpl UNIDENTIFIED_STREAM = new ByteStreamIdImpl();
	static final ByteStreamIdImpl NULL_STREAM_ID = new ByteStreamIdImpl(
			0L, ByteStreams.NULL_SHA1.toLowerCase());

	private final String hexSHA1;
	private final long length;

	private ByteStreamIdImpl() {
		this(ByteStreams.UNKNOWN_LENGTH, ByteStreams.NULL_SHA1.toLowerCase());
	}

	private ByteStreamIdImpl(final long length, final String sha1) {
		this.length = length;
		this.hexSHA1 = sha1;
	}

	static final ByteStreamIdImpl fromValues(final long length,
			final String sha1) {
		if (length < 1L)
			throw new IllegalArgumentException("(length " + length //$NON-NLS-1$
					+ " < 1) == true"); //$NON-NLS-1$
        if ((sha1 == null) || (sha1.isEmpty()))
            throw new IllegalArgumentException(
                    "((sha1 == null) || (sha1.isEmpty())) == true"); //$NON-NLS-1$
		if (!ByteStreams.isHexSHA1(sha1))
			throw new IllegalArgumentException(
					"ByteStreams.isHexSHA1(sha1) != true, regex used: " //$NON-NLS-1$
							+ ByteStreams.HEX_SHA1_REGEX);
		// This is the only route to the constructor, lower case the hash values
		// to prevent
		// case sensitivity ruining equals and hashCode. Upper and lower case
		// hex digest strings
		// are equivalent.
		return new ByteStreamIdImpl(length, sha1.toLowerCase());
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public final long getLength() {
		return this.length;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public final String getHexSHA1() {
		return this.hexSHA1;
	}

    /**
     * {@inheritDoc}
     */
	@Override
	public String toString() {
		return "{\"ByteStreamId\":{\"hexSHA1\":\"" + this.hexSHA1 + "\",\"length\":" + this.length + " bytes}}"; //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
	}

    /**
     * {@inheritDoc}
     */
	@Override
	public final boolean equals(final Object obj) {
		if (this == obj)
			return true;
		if (!(obj instanceof ByteStreamId))
			return false;
		ByteStreamId other = (ByteStreamId) obj;
		if (this.length != other.getLength())
			return false;
		if (this.hexSHA1 == null) {
			if (other.getHexSHA1() != null)
				return false;
		} else if (!this.hexSHA1.equals(other.getHexSHA1()))
			return false;
		return true;
	}

    /**
     * {@inheritDoc}
     */
	@Override
	public final int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + (int) (this.length ^ (this.length >>> 32));
		result = prime * result
				+ ((this.hexSHA1 == null) ? 0 : this.hexSHA1.hashCode());
		return result;
	}

    /**
     * {@inheritDoc}
     */
	@Override
	public final int compareTo(final ByteStreamId other) {
		long lengthDiff = this.length - other.getLength();
		if (lengthDiff != 0)
			return (lengthDiff > 0) ? 1 : -1;
		// Sort by 256 hash
		return this.hexSHA1.compareToIgnoreCase(other
				.getHexSHA1());
	}
}
