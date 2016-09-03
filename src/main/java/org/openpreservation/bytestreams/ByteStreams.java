/**
 * 
 */
package org.openpreservation.bytestreams;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.security.DigestInputStream;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.codec.binary.Hex;

/**
 * A static factory and utility class for {@link ByteStreamId ByteStreamIds}.
 * This is the only way to instantiate ByteStreamIds and provides methods to
 * create them from values, a file and an InputStream.
 * 
 * The class also provides methods to check to see if a Strings pattern matches
 * that of a hex SHA-1 hash String and another to format Byte lengths into
 * readable strings.
 * 
 * @author <a href="mailto:carl@openpreservation.org">Carl Wilson</a> <a
 */

public final class ByteStreams {
    /** The length of a valid hex encoded SHA1 digest string */
    public static final int HEX_SHA1_LENGTH = 40;
    static final String HEX_REGEX_ROOT = "^\\s*([0-9a-fA-F]"; //$NON-NLS-1$
    /** RegEx for a SHA1 digest string */
    public static final String HEX_SHA1_REGEX = HEX_REGEX_ROOT + "{" //$NON-NLS-1$
            + HEX_SHA1_LENGTH + "})\\z"; //$NON-NLS-1$
    /** Hex SHA1 for a null stream */
    public static final String NULL_SHA1 = "da39a3ee5e6b4b0d3255bfef95601890afd80709"; //$NON-NLS-1$
    /** Length given to a byte stream when its length is unknown */
    public static final long UNKNOWN_LENGTH = -1;
    static final NullPointerException DEFAULT_CAUSE = new NullPointerException(
            "[ByteStream] is OK."); //$NON-NLS-1$

    // Buffer size for reading streams
    private static final int BUFFER_SIZE = (32 * 1024);
    // java.security.digest name for the SHA1 algorithm
    private static final String SHA1_NAME = "SHA-1"; //$NON-NLS-1$
    // regex pattern for SHA1 string
    private static final Pattern HEX_SHA1_PATTERN = Pattern
            .compile(HEX_SHA1_REGEX);

    // OK set up the stream for digest calculation, first the digest alg
    private static final MessageDigest SHA1;
    static {
        try {
            // Try for MD5 alg
            SHA1 = MessageDigest.getInstance(SHA1_NAME);
        } catch (NoSuchAlgorithmException excep) {
            // If this happens the Java Digest algorithms aren't present, a
            // faulty Java install??
            throw new IllegalStateException(
                    "No digest algorithm implementation for " + SHA1_NAME //$NON-NLS-1$
                            + ", check you Java installation."); //$NON-NLS-1$
        }
    }

    private ByteStreams() {
        /** Disable default constructor, this should never happen */
        throw new AssertionError("[ByteStreams] In default constructor."); //$NON-NLS-1$
    }

    /**
     * Returns a {@link ByteStreamId} instance for an empty byte stream.
     * 
     * @return the {@link ByteStreamId} of an empty byte stream
     */
    public static final ByteStreamId nullByteStreamId() {
        return ByteStreamIdImpl.NULL_STREAM_ID;
    }

    /**
     * Returns a {@link ByteStreamId} instance for an unidentified byte stream.
     *
     * @return the {@link ByteStreamId} of an unidentified ByteStream
     */
    public static final ByteStreamId unidentifiedByteStreamId() {
        return ByteStreamIdImpl.UNIDENTIFIED_STREAM;
    }

    /**
     * Create a ByteStream instance from the passed values. This method relies
     * on the strong parameter checking in
     * {@link ByteStreamIdImpl#fromValues(long, String)} and does no defensive
     * checking itself.
     * 
     * @param length
     *            the length of the byte stream in bytes
     * @param sha1
     *            a hex-encoded sha1 value for the byte stream
     * @return a new ByteStream instance populated from the passed values
     */
    public static final ByteStreamId idFromValues(final long length,
            final String sha1) {
        return ByteStreamIdImpl.fromValues(length, sha1);
    }

    /**
     * Factory method that creates a new ByteStream instance from an
     * InputStream. Note that the caller is responsible for closing the passed
     * stream.
     * 
     * @param inStream
     *            a java.io.InputStream from which to create the ByteStreamId,
     *            must be closed by the caller
     * @return a new ByteStreamId instance created from the input Stream
     * @throws IOException
     *             when the InputStream cannot be read
     */
    public static final ByteStreamId idFromStream(final InputStream inStream)
            throws IOException {
        if (inStream == null)
            throw new IllegalArgumentException("inStream == null"); //$NON-NLS-1$
        SHA1.reset();
        // Create input streams for digest calculation
        DigestInputStream SHA1Stream = new DigestInputStream(inStream, SHA1);
        // Wrap them all in a buffered stream for efficiency
        BufferedInputStream bis = new BufferedInputStream(SHA1Stream);
        byte[] buff = new byte[BUFFER_SIZE];
        long totalBytes = 0L;
        int bytesRead = 0;
        // Read the entire stream while calculating the length
        while ((bytesRead = bis.read(buff, 0, BUFFER_SIZE)) > -1) {
            totalBytes += bytesRead;
        }
        // Return the new instance from the calulated details
        return (totalBytes == 0L) ? ByteStreams.nullByteStreamId()
                : ByteStreamIdImpl.fromValues(totalBytes,
                        Hex.encodeHexString(SHA1.digest()));
    }

    /**
     * Factory method to create a byte sequence from the contents of a file.
     * 
     * @param file
     *            a java.io.File from which to create the ByteStreamId
     * @return a new ByteStreamId instance created from the file
     * @throws FileNotFoundException
     *             when the file cannot be found
     * @throws IOException
     *             when the InputStream opened from the file cannot be read
     */
    public static final ByteStreamId idFromFile(final File file)
            throws FileNotFoundException, IOException {
        if (file == null)
            throw new IllegalArgumentException("file == null"); //$NON-NLS-1$
        if (file.isDirectory())
            throw new IllegalArgumentException("file.isDirectory() == true"); //$NON-NLS-1$
        try (InputStream inStream = new FileInputStream(file)) {
            ByteStreamId bs = idFromStream(inStream);
            return bs;
        }
    }

    /**
     * Static method to test whether a string matches the pattern for a hex
     * SHA-1 digest, that is 40 hex characters, i.e. only 0-9 or A-F case
     * insensitive. Effectively runs a Java RegEx on the string.
     * 
     * @param toTest
     *            java.lang.String to test to see if it's a hex SHA-1 string
     * @return true if the String matches the hex SHA-1 RegEx, false if not.
     */
    public static final boolean isHexSHA1(final String toTest) {
        if (toTest == null)
            throw new IllegalArgumentException("toTest == null"); //$NON-NLS-1$
        Matcher matcher = HEX_SHA1_PATTERN.matcher(toTest);
        return matcher.find();
    }

    /**
     * Returns a human readable Byte count with units, safe up to ExaBytes
     * @param bytes
     *            the number of bytes as a long
     * @param si
     *            set true to use SI units and 1000 bytes == 1K, false 1024
     *            bytes == 1K
     * @return a human readable byte count derived from bytes
     */
    public static String humanReadableByteCount(final long bytes,
            final boolean si) {
        int unit = si ? 1000 : 1024;
        if (bytes < unit)
            return bytes + " B"; //$NON-NLS-1$
        int exp = (int) (Math.log(bytes) / Math.log(unit));
        String pre = (si ? "kMGTPE" : "KMGTPE").charAt(exp - 1) //$NON-NLS-1$ //$NON-NLS-2$
                + (si ? "" : "i"); //$NON-NLS-1$ //$NON-NLS-2$
        return String.format(
                "%.1f %sB", Double.valueOf(bytes / Math.pow(unit, exp)), pre); //$NON-NLS-1$
    }
}
