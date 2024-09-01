import java.sql.*;
import java.util.Scanner;

public class Main {

    public static void main(String[] args) {
        String url = "jdbc:mysql://localhost:3306/library";
        String user = "root";
        String password = "Pradeepkumar21";

        try (Connection conn = DriverManager.getConnection(url, user, password);
             Scanner scanner = new Scanner(System.in)) {

            while (true) {
                System.out.println("Select your role:");
                System.out.println("1. Student");
                System.out.println("2. Librarian");
                System.out.println("3. Exit");

                int role = scanner.nextInt();
                scanner.nextLine();

                switch (role) {
                    case 1:
                        handleStudentActions(conn, scanner);
                        break;
                    case 2:
                        handleLibrarianActions(conn, scanner);
                        break;
                    case 3:
                        System.out.println("Exiting the system.");
                        return;
                    default:
                        System.out.println("Invalid selection. Please try again.");
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private static void handleStudentActions(Connection conn, Scanner scanner) throws SQLException {
        System.out.println("1. View available books");
        System.out.println("2. Borrow a book");
        System.out.println("3. Return a book");
        System.out.println("4. View fines");
        System.out.println("5. Exit");

        int choice = scanner.nextInt();
        scanner.nextLine(); // Consume newline

        switch (choice) {
            case 1:
                viewAvailableBooks(conn);
                break;
            case 2:
                borrowBook(conn, scanner);
                break;
            case 3:
                returnBook(conn, scanner);
                break;
            case 4:
                viewFines(conn, scanner);
                break;
            case 5:
                System.out.println("Exiting to main menu.");
                break;
            default:
                System.out.println("Invalid choice. Please try again.");
        }
    }

    private static void handleLibrarianActions(Connection conn, Scanner scanner) throws SQLException {
        System.out.println("Enter your librarian ID:");
        String librarianId = scanner.nextLine();

        System.out.println("Enter your password:");
        String password = scanner.nextLine();

        if (!validateLibrarian(conn, librarianId, password)) {
            System.out.println("Invalid ID or password. Returning to main menu.");
            return;
        }

        System.out.println("1. Add student");
        System.out.println("2. Add book");
        System.out.println("3. View borrowed books");
        System.out.println("4. View returned books");
        System.out.println("5. View total number of students");
        System.out.println("6. View top borrower of the month");
        System.out.println("7. View most borrowed book");
        System.out.println("8. Exit");

        int choice = scanner.nextInt();
        scanner.nextLine(); // Consume newline

        switch (choice) {
            case 1:
                addNewStudent(conn, scanner);
                break;
            case 2:
                addNewBook(conn, scanner);
                break;
            case 3:
                viewBorrowedBooks(conn);
                break;
            case 4:
                viewReturnedBooks(conn);
                break;
            case 5:
                viewTotalNumberOfStudents(conn);
                break;
            case 6:
                viewTopBorrowersOfTheMonth(conn);
                break;
            case 7:
                viewMostBorrowedBooks(conn);
                break;
            case 8:
                System.out.println("Exiting to main menu.");
                break;
            default:
                System.out.println("Invalid choice. Please try again.");
        }
    }

    private static boolean validateLibrarian(Connection conn, String librarianId, String password) throws SQLException {
        // Implement password validation logic here
        // For simplicity, let's assume the password is validated by the librarian's first name + "123"
        String query = "SELECT first_name FROM librarians WHERE librarian_id = ?";
        try (PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setString(1, librarianId);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    String expectedPassword = rs.getString("first_name") + "123";
                    return expectedPassword.equals(password);
                }
            }
        }
        return false;
    }

    private static void viewAvailableBooks(Connection conn) throws SQLException {
        String query = "SELECT * FROM available_books";
        try (PreparedStatement stmt = conn.prepareStatement(query);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                System.out.println("Book ID: " + rs.getObject("book_id") +
                        " | Title: " + (rs.getString("title") != null ? rs.getString("title") : "null") +
                        " | Author: " + (rs.getString("author") != null ? rs.getString("author") : "null") +
                        " | ISBN: " + (rs.getString("isbn") != null ? rs.getString("isbn") : "null") +
                        " | Publisher: " + (rs.getString("publisher") != null ? rs.getString("publisher") : "null") +
                        " | Year Published: " + (rs.getObject("year_published") != null ? rs.getInt("year_published") : "null") +
                        " | Edition: " + (rs.getString("edition") != null ? rs.getString("edition") : "null"));
            }
        }
    }

    private static void borrowBook(Connection conn, Scanner scanner) throws SQLException {
        System.out.println("Enter your student ID:");
        String studentId = scanner.nextLine();

        System.out.println("Enter the book ID you want to borrow:");
        int bookId = scanner.nextInt();
        scanner.nextLine(); // Consume newline

        // Implement borrow book logic here
        // Check if the book is available and if so, update the `book_borrowed` table and `available_books` table
    }

    private static void returnBook(Connection conn, Scanner scanner) throws SQLException {
        System.out.println("Enter your student ID:");
        String studentId = scanner.nextLine();

        System.out.println("Enter the book ID you want to return:");
        int bookId = scanner.nextInt();
        scanner.nextLine(); // Consume newline

        // Implement return book logic here
        // Update the `book_returned` table and adjust the `available_books` table
    }

    private static void viewFines(Connection conn, Scanner scanner) throws SQLException {
        System.out.println("Enter your student ID:");
        String studentId = scanner.nextLine();

        String query = "SELECT SUM(fine_amount) AS total_fines FROM fines WHERE student_id = ?";
        try (PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setString(1, studentId);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    System.out.println("Total fines: " + (rs.getObject("total_fines") != null ? rs.getDouble("total_fines") : "null") + " rupees");
                }
            }
        }
    }

    private static void addNewStudent(Connection conn, Scanner scanner) throws SQLException {
        System.out.println("Enter student ID:");
        String studentId = scanner.nextLine();

        System.out.println("Enter student name:");
        String name = scanner.nextLine();

        System.out.println("Enter batch:");
        String batch = scanner.nextLine();

        System.out.println("Enter department:");
        String department = scanner.nextLine();

        String query = "INSERT INTO students (student_id, name, batch, department) VALUES (?, ?, ?, ?)";
        try (PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setString(1, studentId);
            stmt.setString(2, name);
            stmt.setString(3, batch);
            stmt.setString(4, department);
            stmt.executeUpdate();
            System.out.println("Student added successfully.");
        }
    }

    private static void addNewBook(Connection conn, Scanner scanner) throws SQLException {
        System.out.println("Enter book title:");
        String title = scanner.nextLine();

        System.out.println("Enter book author:");
        String author = scanner.nextLine();

        System.out.println("Enter book ISBN:");
        String isbn = scanner.nextLine();

        System.out.println("Enter book publisher:");
        String publisher = scanner.nextLine();

        System.out.println("Enter year published:");
        int yearPublished = scanner.nextInt();
        scanner.nextLine(); // Consume newline

        System.out.println("Enter book edition:");
        String edition = scanner.nextLine();

        System.out.println("Enter shelf ID:");
        int shelfId = scanner.nextInt();
        scanner.nextLine(); // Consume newline

        String query = "INSERT INTO books (title, author, isbn, publisher, year_published, edition, shelf_id) VALUES (?, ?, ?, ?, ?, ?, ?)";
        try (PreparedStatement stmt = conn.prepareStatement(query, Statement.RETURN_GENERATED_KEYS)) {
            stmt.setString(1, title);
            stmt.setString(2, author);
            stmt.setString(3, isbn);
            stmt.setString(4, publisher);
            stmt.setInt(5, yearPublished);
            stmt.setString(6, edition);
            stmt.setInt(7, shelfId);
            stmt.executeUpdate();

            try (ResultSet rs = stmt.getGeneratedKeys()) {
                if (rs.next()) {
                    int bookId = rs.getInt(1);
                    System.out.println("Book added with ID: " + bookId);
                }
            }
        }
    }

    private static void viewBorrowedBooks(Connection conn) throws SQLException {
        String query = "SELECT * FROM book_borrowed";
        try (PreparedStatement stmt = conn.prepareStatement(query);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                System.out.println("Student ID: " + (rs.getString("student_id") != null ? rs.getString("student_id") : "null") +
                        " | Book ID: " + (rs.getObject("book_id") != null ? rs.getInt("book_id") : "null") +
                        " | Borrowed Date: " + (rs.getDate("borrowed_date") != null ? rs.getDate("borrowed_date") : "null"));
            }
        }
    }

    private static void viewReturnedBooks(Connection conn) throws SQLException {
        String query = "SELECT * FROM book_returned";
        try (PreparedStatement stmt = conn.prepareStatement(query);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                System.out.println("Student ID: " + (rs.getString("student_id") != null ? rs.getString("student_id") : "null") +
                        " | Book ID: " + (rs.getObject("book_id") != null ? rs.getInt("book_id") : "null") +
                        " | Actual Return Date: " + (rs.getDate("actual_return_date") != null ? rs.getDate("actual_return_date") : "null") +
                        " | Returned Date: " + (rs.getDate("returned_date") != null ? rs.getDate("returned_date") : "null"));
            }
        }
    }

    private static void viewTotalNumberOfStudents(Connection conn) throws SQLException {
        String query = "SELECT COUNT(*) AS total_students FROM students";
        try (PreparedStatement stmt = conn.prepareStatement(query);
             ResultSet rs = stmt.executeQuery()) {

            if (rs.next()) {
                System.out.println("Total number of students: " + rs.getInt("total_students"));
            }
        }
    }


    private static void viewTopBorrowersOfTheMonth(Connection conn) throws SQLException {
        String query = "SELECT student_id, COUNT(book_id) AS num_borrowed " +
                "FROM book_borrowed " +
                "WHERE MONTH(borrowed_date) = MONTH(CURDATE()) " +
                "GROUP BY student_id " +
                "ORDER BY num_borrowed DESC " +
                "LIMIT 1";
        try (PreparedStatement stmt = conn.prepareStatement(query);
             ResultSet rs = stmt.executeQuery()) {

            if (rs.next()) {
                System.out.println("Top borrower of the month: " + (rs.getString("student_id") != null ? rs.getString("student_id") : "null") +
                        " | Books Borrowed: " + (rs.getObject("num_borrowed") != null ? rs.getInt("num_borrowed") : "null"));
            }
        }
    }

    private static void viewMostBorrowedBooks(Connection conn) throws SQLException {
        String query = "SELECT book_id, COUNT(student_id) AS num_borrowed " +
                "FROM book_borrowed " +
                "GROUP BY book_id " +
                "ORDER BY num_borrowed DESC " +
                "LIMIT 1";
        try (PreparedStatement stmt = conn.prepareStatement(query);
             ResultSet rs = stmt.executeQuery()) {

            if (rs.next()) {
                System.out.println("Most borrowed book ID: " + (rs.getObject("book_id") != null ? rs.getInt("book_id") : "null") +
                        " | Number of times borrowed: " + (rs.getObject("num_borrowed") != null ? rs.getInt("num_borrowed") : "null"));
            }
        }
    }
}
