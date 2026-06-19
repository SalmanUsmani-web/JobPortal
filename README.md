# JobPortal

A Java-based Job Portal web application built using JSP, Servlets, MySQL, HTML, CSS, Bootstrap, and Apache Tomcat.

## Features

* User registration and login
* Profile creation and editing
* Profile picture upload
* Resume upload and download
* Education and experience management
* Job search by location and technology
* Apply for jobs
* View applied jobs
* Resume builder
* Contact form
* Session and cookie handling

## Technologies Used

* Java
* JSP
* Servlets
* JDBC
* MySQL
* HTML5
* CSS3
* Bootstrap
* Apache Tomcat
* NetBeans IDE
* Git and GitHub

## Project Structure

* `src` – Java backend files and servlets
* `web` – JSP pages, CSS, images, and configuration files
* `nbproject` – NetBeans project configuration
* `build.xml` – Ant build configuration

## Database Configuration

The project uses MySQL.

Create a `db.properties` file inside:

```text
src/java/com/deepak/connection/
```

Add your database details:

```properties
jdbc-url=jdbc:mysql://localhost:3306/jobportal
username=your_mysql_username
password=your_mysql_password
```

The `db.properties` file is excluded from GitHub for security.

## How to Run

1. Clone the repository:

```bash
git clone https://github.com/SalmanUsmani-web/JobPortal.git
```

2. Open the project in NetBeans.

3. Configure Apache Tomcat or TomEE server.

4. Create the MySQL database and required tables.

5. Add the `db.properties` file with your database credentials.

6. Clean and Build the project.

7. Run the project.

## Security

Sensitive files, database credentials, uploaded resumes, profile pictures, and build files are excluded using `.gitignore`.

## Author

**Salman Usmani**

GitHub: https://github.com/SalmanUsmani-web
