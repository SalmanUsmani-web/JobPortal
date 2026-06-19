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

public class UpdateProfilePic extends HttpServlet
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

                    String uploadPath = getServletContext().getRealPath("/profile_pics");

                    File folder = new File(uploadPath);
                    if (!folder.exists())
                    {
                        folder.mkdirs();
                    }

                    File uploadedFile = new File(folder, fileName);
                    item.write(uploadedFile);

                    System.out.println("Profile pic uploaded at: " + uploadedFile.getAbsolutePath());

                    break;
                }
            }

            if (fileName == null)
            {
                System.out.println("No profile pic selected");
                resp.sendRedirect("edit-profile-pic.jsp");
                return;
            }
        }
        catch(Exception e)
        {
            e.printStackTrace();

            RequestDispatcher rd1 = req.getRequestDispatcher("error.jsp");
            rd1.include(req, resp);

            RequestDispatcher rd2 = req.getRequestDispatcher("edit-profile-pic.jsp");
            rd2.include(req, resp);

            return;
        }

        Connection con = null;

        try
        {
            con = DbConnection.getConnect();
            con.setAutoCommit(false);

            PreparedStatement ps = con.prepareStatement("update profile_pics set path=? where email=?");
            ps.setString(1, fileName);
            ps.setString(2, email);

            int i = ps.executeUpdate();

            if (i == 0)
            {
                PreparedStatement ps2 = con.prepareStatement("insert into profile_pics(email, path) values(?, ?)");
                ps2.setString(1, email);
                ps2.setString(2, fileName);

                i = ps2.executeUpdate();
            }

            if (i > 0)
            {
                session.setAttribute("session_profilepic", fileName);
                con.commit();
                resp.sendRedirect("edit-profile-pic.jsp");
            }
            else
            {
                con.rollback();

                RequestDispatcher rd1 = req.getRequestDispatcher("error.jsp");
                rd1.include(req, resp);

                RequestDispatcher rd2 = req.getRequestDispatcher("edit-profile-pic.jsp");
                rd2.include(req, resp);
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

            RequestDispatcher rd2 = req.getRequestDispatcher("edit-profile-pic.jsp");
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