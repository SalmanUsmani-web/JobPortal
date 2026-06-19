/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.deepak.backend;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.net.URLEncoder;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class DownloadResume extends HttpServlet
{
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException
    {
        String fileName = req.getParameter("fn");

        if (fileName == null || fileName.trim().equals(""))
        {
            resp.sendError(HttpServletResponse.SC_BAD_REQUEST, "File name missing");
            return;
        }

        File file = new File(PathDetails.RESUME_PATH, fileName);

        if (!file.exists())
        {
            resp.sendError(HttpServletResponse.SC_NOT_FOUND, "Resume file not found");
            return;
        }

        String encodedFileName = URLEncoder.encode(file.getName(), "UTF-8").replace("+", "%20");

        resp.setContentType("application/octet-stream");
        resp.setHeader("Content-Disposition", "attachment; filename=\"" + encodedFileName + "\"");
        resp.setContentLengthLong(file.length());

        FileInputStream fis = null;
        OutputStream os = null;

        try
        {
            fis = new FileInputStream(file);
            os = resp.getOutputStream();

            byte[] buffer = new byte[4096];
            int bytesRead;

            while ((bytesRead = fis.read(buffer)) != -1)
            {
                os.write(buffer, 0, bytesRead);
            }

            os.flush();
        }
        finally
        {
            if (fis != null)
            {
                fis.close();
            }
        }
    }
}