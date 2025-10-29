package pw.wunderlich.lightbeat.audio;

/**
 * Exception thrown when audio operations fail.
 */
public class AudioException extends Exception {

    /**
     * Creates a new AudioException with the specified message.
     *
     * @param message the detail message
     */
    public AudioException(String message) {
        super(message);
    }

    /**
     * Creates a new AudioException with the specified message and cause.
     *
     * @param message the detail message
     * @param cause   the cause of the exception
     */
    public AudioException(String message, Throwable cause) {
        super(message, cause);
    }
}
