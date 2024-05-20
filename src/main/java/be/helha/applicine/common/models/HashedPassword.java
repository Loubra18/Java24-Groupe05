package be.helha.applicine.common.models;

import org.springframework.security.crypto.bcrypt.BCrypt;

import java.io.IOException;

/**
 * This class is used to hash passwords and check if a password matches a hashed password.
 */
public class HashedPassword {
    /**
     * Hashes a password using BCrypt
     * @param password The password to hash
     * @return The hashed password
     * @throws IOException If an error occurs while hashing the password
     */
    public static String getHashedPassword(String password) throws IOException {
        if (password == null || password.isEmpty()) {
            throw new IllegalArgumentException("Password cannot be null or empty");
        }
        try {
            return BCrypt.hashpw(password, BCrypt.gensalt());
        } catch (IllegalArgumentException e) {
            throw new IOException("Error while hashing password", e);
        }
    }

    /**
     * Checks if a password matches a hashed password
     * @param password The password to check
     * @param hashedPassword The hashed password to check against
     * @return True if the password matches the hashed password, false otherwise
     * @throws IllegalArgumentException If an error occurs while checking the password
     */
    public static boolean checkPassword(String password, String hashedPassword) {
        if (password == null || hashedPassword == null) {
            throw new IllegalArgumentException("Password and hashed password cannot be null");
        }
        try {
            return BCrypt.checkpw(password, hashedPassword);
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("Error while checking password", e);
        }
    }
}
