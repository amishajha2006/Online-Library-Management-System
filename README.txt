ONLINE LIBRARY MANAGEMENT SYSTEM

1. PROJECT DESCRIPTION
This is a Java-based Online Library Management System developed using Java Swing, MySQL, and JDBC.

The system provides separate interfaces for librarians and members.

Main features:
- Librarian Login
- Member Login
- Book Management
- Member Management
- Borrow and Return Books
- Book Search
- Borrowing History
- Member Profile
- Notifications
- Inventory Reports
- MySQL Database Integration


2. REQUIREMENTS
- JDK 8 or above
- MySQL Server
- MySQL Workbench (optional)
- Visual Studio Code or any Java IDE
- MySQL Connector/J


3. DATABASE SETUP

Database Name:
library_db

The project contains:
library_db.sql

To set up the database:
1. Open MySQL Workbench.
2. Connect to your MySQL Server.
3. Open the file library_db.sql.
4. Execute the SQL script.

The script creates the required database and tables.


4. DATABASE CONNECTION

Database connection details are stored in:

DBConnection.java

Before running the project, make sure the MySQL username and password in DBConnection.java match your local MySQL setup.

Do not share your MySQL password with others.


5. MYSQL CONNECTOR

The MySQL Connector/J file is included inside:

lib/


6. COMPILE THE PROJECT

Open the terminal inside the project folder and run:

javac -cp ".;lib/*" *.java


7. RUN THE PROJECT

After successful compilation, run:

java -cp ".;lib/*" Main


8. LIBRARIAN LOGIN

Username:
admin

Password:
admin123


9. SAMPLE MEMBER LOGIN

Email:
anand@gmail.com

Password:
1234


10. PROJECT STRUCTURE

Main.java
LoginFrame.java
LibrarianDashboard.java
MemberDashboard.java
BookManagement.java
MemberManagement.java
TransactionManagement.java
BookSearch.java
DBConnection.java
BookDAO.java
MemberDAO.java
InventoryReports.java
library_db.sql
README.txt
lib/


11. TECHNOLOGIES USED

Frontend:
Java Swing

Backend:
Java

Database:
MySQL

Database Connectivity:
JDBC

Development Environment:
Visual Studio Code


12. NOTE

Make sure MySQL Server is running before starting the application.

The application requires a working MySQL connection to perform database operations.