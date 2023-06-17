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
import it.polimi.tiw.project.utils.FileService;

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
        String filePath = FileService.getFilePath(getServletContext(), userId.toString(), fileName);
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
}