/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.deepak.backend;

import com.deepak.connection.DbConnection;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.PreparedStatement;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

public class Register extends HttpServlet {
    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        resp.setContentType("text/html");
        PrintWriter out = resp.getWriter();

        String name2 = req.getParameter("name1");
        String email2 = req.getParameter("email1");
        String pass2 = req.getParameter("pass1");
        String gender2 = req.getParameter("gender1");
        String[] field2 = req.getParameterValues("field1");
        String city2 = req.getParameter("city1");

        // Array ko String me convert karna
        String fields2 = "";
        if (field2 != null && field2.length > 0) {
            fields2 = String.join(",", field2);
        }

        Connection con = null;
        try {
            con = DbConnection.getConnect();
            con.setAutoCommit(false);

            PreparedStatement ps = con.prepareStatement("insert into register(name, email, password, gender, field, city) values(?,?,?,?,?,?)");
            ps.setString(1, name2);
            ps.setString(2, email2);
            ps.setString(3, pass2);
            ps.setString(4, gender2);
            ps.setString(5, fields2);
            ps.setString(6, city2);
            int i1 = ps.executeUpdate();

            PreparedStatement ps2 = con.prepareStatement("insert into about_user(email, about, skills) values(?,?,?)");
            ps2.setString(1, email2);
            ps2.setString(2, "");
            ps2.setString(3, "");
            int i2 = ps2.executeUpdate();

            PreparedStatement ps3 = con.prepareStatement("insert into profile_pics(email, path) values(?,?)");
            ps3.setString(1, email2);
            ps3.setString(2, "profilepic.png");
            int i3 = ps3.executeUpdate();

            if (i1 > 0 && i2 > 0 && i3 > 0) {
                con.commit();
                HttpSession session = req.getSession();
                session.setAttribute("session_name", name2);
                session.setAttribute("session_email", email2);
                session.setAttribute("session_gender", gender2);
                session.setAttribute("session_city", city2);
                session.setAttribute("session_fields", fields2); // ab ye String hai
                session.setAttribute("session_title", "");
                session.setAttribute("session_skills", "");
                session.setAttribute("session_profilepic", "profilepic.png");

                resp.sendRedirect("profile.jsp");
            } else {
                con.rollback();
                out.println("Failed");
            }
        } catch (Exception e) {
            try {
                if (con != null) con.rollback();
            } catch (Exception ee) {
                ee.printStackTrace();
            }
            out.print(e);
        } finally {
            try {
                if (con != null) con.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}

//create table register(id INT AUTO_INCREMENT, name VARCHAR(30) NOT NULL, email VARCHAR(100) NOT NULL, password VARCHAR(16) NOT NULL, gender VARCHAR(6) NOT NULL, field VARCHAR(200) NOT NULL, city VARCHAR(20) NOT NULL, PRIMARY KEY(id));