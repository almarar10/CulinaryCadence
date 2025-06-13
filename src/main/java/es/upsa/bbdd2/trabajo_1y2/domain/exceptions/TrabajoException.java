package es.upsa.bbdd2.trabajo_1y2.domain.exceptions;

public class TrabajoException extends Exception
{
    public TrabajoException(Throwable cause) {
        super(cause);
    }

    public TrabajoException() {
    }

    public TrabajoException(String message) {
        super(message);
    }

    public TrabajoException(String message, Throwable cause) {
        super(message, cause);
    }

    public TrabajoException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
