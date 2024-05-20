package be.helha.applicine.server;

import java.io.File;
import java.io.IOException;
import java.nio.file.*;

public class FileManager {

    /**
     * Creates the data folder in the Appdata folder.
     */
    public static void createDataFolder() {
        String getAppdata = System.getenv("APPDATA");
        Path path = Paths.get(getAppdata + "/Applicine/images/");
        try {
            Files.createDirectories(path);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static String createPath(String fileName) {
        return "file:" + System.getenv("APPDATA") + "/Applicine/images/" + fileName;
    }

    public static byte[] fileToByteArray(File imageFile) {
        try {
            return Files.readAllBytes(imageFile.toPath());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public static byte[] getImageAsBytes(String imagePath) throws IOException {
        if (imagePath.startsWith("file:")) {
            imagePath = imagePath.substring(5); // Remove the "file:" scheme
        }
        return Files.readAllBytes(Paths.get(imagePath));
    }

    public static void createImageFromBytes(byte[] imageBytes, String imagePath) throws IOException {
        imagePath = imagePath.substring(5); // Remove the "file:" scheme
        Files.write(Paths.get(imagePath), imageBytes);
    }
}
