/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.deepak.backend;

import com.deepak.connection.DbConnection;
import java.io.File;
import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.List;
import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.disk.DiskFileItemFactory;
import org.apache.commons.fileupload.servlet.ServletFileUpload;

public class UpdateResume extends HttpServlet
{
    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException
    {
        HttpSession session = req.getSession();
        String email = (String) session.getAttribute("session_email");

        if (email == null || email.trim().equals(""))
        {
            resp.sendRedirect("login.jsp");
            return;
        }

        String fileName = null;

        try
        {
            DiskFileItemFactory factory = new DiskFileItemFactory();
            ServletFileUpload sfu = new ServletFileUpload(factory);
            List<FileItem> items = sfu.parseRequest(req);

            for (FileItem item : items)
            {
                if (!item.isFormField() && item.getSize() > 0)
                {
                    String originalName = new File(item.getName()).getName();
                    fileName = System.currentTimeMillis() + "_" + originalName;

                    String uploadPath = getServletContext().getRealPath("/resumes");

                    File folder = new File(uploadPath);
                    if (!folder.exists())
                    {
                        folder.mkdirs();
                    }

                    File uploadedFile = new File(folder, fileName);
                    item.write(uploadedFile);

                    System.out.println("Resume uploaded at: " + uploadedFile.getAbsolutePath());

                    break;
                }
            }

            if (fileName == null)
            {
                System.out.println("No resume selected");
                resp.sendRedirect("upload-resume.jsp");
                return;
            }
        }
        catch(Exception e)
        {
            e.printStackTrace();

            RequestDispatcher rd1 = req.getRequestDispatcher("error.jsp");
            rd1.include(req, resp);

            RequestDispatcher rd2 = req.getRequestDispatcher("upload-resume.jsp");
            rd2.include(req, resp);

            return;
        }

        Connection con = null;

        try
        {
            con = DbConnection.getConnect();
            con.setAutoCommit(false);

            PreparedStatement ps1 = con.prepareStatement("select * from resumes where email=?");
            ps1.setString(1, email);

            ResultSet rs = ps1.executeQuery();

            if (rs.next())
            {
                PreparedStatement ps2 = con.prepareStatement("update resumes set path=? where email=?");
                ps2.setString(1, fileName);
                ps2.setString(2, email);

                int i = ps2.executeUpdate();

                if (i > 0)
                {
                    con.commit();
                    resp.sendRedirect("profile.jsp");
                }
                else
                {
                    con.rollback();

                    RequestDispatcher rd1 = req.getRequestDispatcher("error.jsp");
                    rd1.include(req, resp);

                    RequestDispatcher rd2 = req.getRequestDispatcher("upload-resume.jsp");
                    rd2.include(req, resp);
                }
            }
            else
            {
                PreparedStatement ps3 = con.prepareStatement("insert into resumes(email, path) values(?,?)");
                ps3.setString(1, email);
                ps3.setString(2, fileName);

                int i = ps3.executeUpdate();

                if (i > 0)
                {
                    con.commit();
                    resp.sendRedirect("profile.jsp");
                }
                else
                {
                    con.rollback();

                    RequestDispatcher rd1 = req.getRequestDispatcher("error.jsp");
                    rd1.include(req, resp);

                    RequestDispatcher rd2 = req.getRequestDispatcher("upload-resume.jsp");
                    rd2.include(req, resp);
                }
            }
        }
        catch(Exception e)
        {
            e.printStackTrace();

            try
            {
                if (con != null)
                {
                    con.rollback();
                }
            }
            catch(Exception ee)
            {
                ee.printStackTrace();
            }

            RequestDispatcher rd1 = req.getRequestDispatcher("error.jsp");
            rd1.include(req, resp);

            RequestDispatcher rd2 = req.getRequestDispatcher("upload-resume.jsp");
            rd2.include(req, resp);
        }
        finally
        {
            try
            {
                if (con != null)
                {
                    con.close();
                }
            }
            catch(Exception e)
            {
                e.printStackTrace();
            }
        }
    }
}