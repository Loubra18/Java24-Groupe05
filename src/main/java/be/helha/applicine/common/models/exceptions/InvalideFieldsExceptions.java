package be.helha.applicine.common.models.exceptions;

public class InvalideFieldsExceptions extends Exception {
    /**
     * Constructor for the exception.
     * InvalidFieldsExceptions is thrown when the fields are invalid ( empty or bad format ).
     * @param message the message to display.
     */
    public InvalideFieldsExceptions(String message) {
        super(message);
    }

}
