package it.polimi.tiw.project.controllers;
import java.io.*;
import java.sql.Connection;

import javax.servlet.*;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.*;

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
        String fileName = request.getParameter("fileName");

        // Construct the file path
        String filePath = getFilePath(getServletContext(), userId.toString(), fileName);
        System.out.println(filePath);
        // Create a file object
        File file = new File(filePath);

        // Check if the file exists
        if (file.exists()) {
            // Set the response content type
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

    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        HttpSession session = request.getSession(false);
        Integer userId = ((User) session.getAttribute("currentUser")).getId();
        String fileName = request.getParameter("fileName");

        // Get the uploaded file part from the request
        Part filePart = request.getPart("file");

        // Save the file
        saveFile(getServletContext(), filePart, userId.toString(), fileName);

        // Send a success response
        response.setStatus(HttpServletResponse.SC_OK);
    }

    private static void saveFile(ServletContext servletContext, Part filePart, String userId, String fileName) throws IOException {
        // Construct the file path
        String filePath = getFilePath(servletContext, userId, fileName);

        // Create a file object
        File file = new File(filePath);

        // Create parent directories if they don't exist
        File parentDir = file.getParentFile();
        if (!parentDir.exists()) {
            parentDir.mkdirs();
        }

        // Create input stream from the uploaded file part
        InputStream inputStream = filePart.getInputStream();

        // Create output stream to write the file content
        OutputStream outputStream = new FileOutputStream(file);

        // Read from the input stream and write to the output stream
        byte[] buffer = new byte[4096];
        int bytesRead;
        while ((bytesRead = inputStream.read(buffer)) != -1) {
            outputStream.write(buffer, 0, bytesRead);
        }

        // Close the streams
        inputStream.close();
        outputStream.close();
    }

    private static String getFilePath(ServletContext servletContext, String userId, String filename) {

        // Get the absolute path of the directory containing the JavaScript file
        String directoryPath = servletContext.getRealPath("/static");

        // Construct the file path
        return directoryPath + File.separator + "user" + File.separator + userId + File.separator + filename;
    }
}