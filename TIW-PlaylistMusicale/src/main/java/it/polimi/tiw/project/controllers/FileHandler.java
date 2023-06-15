package it.polimi.tiw.project.controllers;
import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.sql.Connection;

import javax.servlet.*;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.*;

import org.apache.commons.lang.StringEscapeUtils;
import org.thymeleaf.TemplateEngine;

import it.polimi.tiw.project.beans.User;

@WebServlet("/FileHandler")
public class FileHandler extends HttpServlet {
	private static final long serialVersionUID = 1L;
	
    /**
     * @see HttpServlet#HttpServlet()
     */
    public FileHandler() {
        super();
        // TODO Auto-generated constructor stub
    }

    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		HttpSession session = request.getSession(false);
		Integer userId = ((User) session.getAttribute("currentUser")).getId();
        String fileName = StringEscapeUtils.escapeJava(request.getParameter("fileName"));

        // Construct the file path
        String filePath = getFilePath(getServletContext(), userId.toString(), fileName);
        // Create a file object
        File file = new File(filePath);

        // Check if the file exists
        if (file.exists()) {
            // Set the response content type (used to indicate that a body contains arbitrary binary data.)
            response.setContentType("application/octet-stream");

            // Set the Content-Disposition header to force download
            response.setHeader("Content-Disposition", "attachment; fileName=\"" + fileName + "\"");

            // Create input stream from the file
            InputStream inputStream = new FileInputStream(file);

            // Create output stream to write the response
            OutputStream outputStream = response.getOutputStream();

            // Read from the input stream and write to the output stream
            byte[] buffer = new byte[4096];
            int bytesRead;
            while ((bytesRead = inputStream.read(buffer)) != -1) {
                outputStream.write(buffer, 0, bytesRead);
            }

            // Close the streams
            inputStream.close();
            outputStream.close();
        } else {
            // File not found, send an error response
            response.sendError(HttpServletResponse.SC_NOT_FOUND, "File: " + fileName + " not found");
        }
    }

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

    private static String getFilePath(ServletContext servletContext, String userId, String filename) {

        // Get the absolute path of the directory containing the JavaScript file
		String baseDirectory = servletContext.getInitParameter("userFilesDirectory");

        // Construct the file path
        return baseDirectory + File.separator + userId + File.separator + filename;
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

    private static String getFileName(Part part) {
        for (String contentDisposition : part.getHeader("content-disposition").split(";")) {
            if (contentDisposition.trim().startsWith("filename")) {
                return contentDisposition.substring(contentDisposition.indexOf('=') + 1).trim().replace("\"", "");
            }
        }
        return null;
    }
}