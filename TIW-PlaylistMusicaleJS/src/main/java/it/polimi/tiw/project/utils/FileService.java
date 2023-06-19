package it.polimi.tiw.project.utils;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;

import javax.servlet.ServletContext;
import javax.servlet.http.Part;

public class FileService {
    public static void saveFile(ServletContext servletContext, Part filePart, String userId, String fileName) throws IOException {
        // Construct the file path
        String filePath = getFilePath(servletContext, userId, fileName);
        
        // Create a file object
        Path path = Path.of(filePath);

        // Create parent directories if they don't exist
        Files.createDirectories(path.getParent());

        // Create input stream from the uploaded file part
        InputStream inputStream = filePart.getInputStream();

        // Create output stream to write the file content
        try (OutputStream outputStream = Files.newOutputStream(path);
             BufferedWriter writer = new BufferedWriter(new FileWriter(path.toFile()))) {
            // Read from the input stream and write to the output stream
            byte[] buffer = new byte[4096];
            int bytesRead;
            while ((bytesRead = inputStream.read(buffer)) != -1) {
                outputStream.write(buffer, 0, bytesRead);
            }
        } catch (IOException e) {
            System.out.println("An error occurred while saving the file: " + e.getMessage());
            throw e;
        } finally {
            // Close the input stream
            inputStream.close();
        }
    }

    public static String getFilePath(ServletContext servletContext, String userId, String filename) {

        // Get the absolute path of the directory containing the JavaScript file
		String baseDirectory = servletContext.getInitParameter("userFilesDirectory");

        // Construct the file path
        return baseDirectory + File.separator + userId + File.separator + URLEncoder.encode(filename, StandardCharsets.UTF_8);
    }
    
    public static String getFileExtension(Part part) {
        String fileName = getFileName(part);
        if (fileName != null) {
            int dotIndex = fileName.lastIndexOf(".");
            if (dotIndex >= 0 && dotIndex < fileName.length() - 1) {
                return fileName.substring(dotIndex + 1);
            }
        }
        return null;
    }

    public static String getFileName(Part part) {
        for (String contentDisposition : part.getHeader("content-disposition").split(";")) {
            if (contentDisposition.trim().startsWith("filename")) {
                return contentDisposition.substring(contentDisposition.indexOf('=') + 1).trim().replace("\"", "");
            }
        }
        return null;
    }
}
