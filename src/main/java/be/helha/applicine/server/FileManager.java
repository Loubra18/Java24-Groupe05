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

    /**
     * Copies an image to the Appdata folder.
     * @param imagePath The path of the image to copy.
     * @return The path of the copied image.
     */

    public static String copyImageToAppdata(String imagePath) {
        Path source = Paths.get(imagePath);
        String fileSeparator = FileSystems.getDefault().getSeparator();
        String imageName = imagePath.substring(imagePath.lastIndexOf(fileSeparator) + 1);
        System.out.println(imageName);
        Path destination = Paths.get(System.getenv("APPDATA") + "/Applicine/images/" + imageName);
        try {
            Files.copy(source, destination);
            return destination.toString();
        } catch ( FileAlreadyExistsException e) {
            return destination.toString();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
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
}
