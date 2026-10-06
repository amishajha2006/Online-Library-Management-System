import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.*;

public class BookManagement extends JFrame {

    JTextField titleField, authorField, isbnField, genreField;
    JTable table;
    DefaultTableModel model;

    BookManagement() {

        setTitle("Book Management");
        setSize(950, 650);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);
        setResizable(false);

        JPanel mainPanel = new JPanel(new BorderLayout(20, 20));
        mainPanel.setBorder(
            BorderFactory.createEmptyBorder(25, 30, 25, 30)
        );

        // ================= HEADER =================

        JLabel icon = new JLabel("📚");
        icon.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 35));

        JLabel title = new JLabel("BOOK MANAGEMENT");
        title.setFont(new Font("Arial", Font.BOLD, 26));

        JLabel subtitle = new JLabel(
            "Add, update, delete and manage library books"
        );
        subtitle.setFont(new Font("Arial", Font.PLAIN, 14));

        JPanel titlePanel = new JPanel(
            new GridLayout(2, 1, 0, 4)
        );
        titlePanel.add(title);
        titlePanel.add(subtitle);

        JPanel headerPanel = new JPanel(
            new BorderLayout(15, 0)
        );
        headerPanel.add(icon, BorderLayout.WEST);
        headerPanel.add(titlePanel, BorderLayout.CENTER);

        // ================= FORM =================

        JPanel formPanel = new JPanel(
            new GridLayout(2, 4, 12, 10)
        );

        titleField = new JTextField();
        authorField = new JTextField();
        isbnField = new JTextField();
        genreField = new JTextField();

        formPanel.add(new JLabel("Title"));
        formPanel.add(new JLabel("Author"));
        formPanel.add(new JLabel("ISBN"));
        formPanel.add(new JLabel("Genre"));

        formPanel.add(titleField);
        formPanel.add(authorField);
        formPanel.add(isbnField);
        formPanel.add(genreField);

        JPanel formContainer = new JPanel(new BorderLayout(0, 8));
        formContainer.setBorder(
            BorderFactory.createTitledBorder("Book Details")
        );
        formContainer.add(formPanel, BorderLayout.CENTER);

        // ================= BUTTONS =================

        JButton addBtn = new JButton("➕ Add Book");
        JButton updateBtn = new JButton("✏️ Update");
        JButton deleteBtn = new JButton("🗑️ Delete");
        JButton refreshBtn = new JButton("🔄 Refresh");

        JButton[] buttons = {
            addBtn, updateBtn, deleteBtn, refreshBtn
        };

        for (JButton button : buttons) {
            button.setFont(
                new Font("Arial", Font.BOLD, 13)
            );
            button.setFocusPainted(false);
        }

        JPanel buttonPanel = new JPanel(
            new FlowLayout(
                FlowLayout.CENTER,
                15,
                10
            )
        );

        buttonPanel.add(addBtn);
        buttonPanel.add(updateBtn);
        buttonPanel.add(deleteBtn);
        buttonPanel.add(refreshBtn);

        // ================= TABLE =================

        model = new DefaultTableModel(
            new String[]{
                "Book ID",
                "Title",
                "Author",
                "ISBN",
                "Genre",
                "Status"
            },
            0
        );

        table = new JTable(model);
        table.setRowHeight(28);
        table.setFont(
            new Font("Arial", Font.PLAIN, 13)
        );

        table.getTableHeader().setFont(
            new Font("Arial", Font.BOLD, 13)
        );

        JScrollPane scrollPane =
            new JScrollPane(table);

        JPanel tablePanel = new JPanel(
            new BorderLayout()
        );

        tablePanel.setBorder(
            BorderFactory.createTitledBorder(
                "Library Books"
            )
        );

        tablePanel.add(
            scrollPane,
            BorderLayout.CENTER
        );

        // ================= ADD BOOK =================

        addBtn.addActionListener(e -> {

            try {

                Connection con =
                    DBConnection.getConnection();

                String sql =
                    "INSERT INTO books " +
                    "(title, author, isbn, genre) " +
                    "VALUES (?, ?, ?, ?)";

                PreparedStatement pst =
                    con.prepareStatement(sql);

                pst.setString(
                    1,
                    titleField.getText()
                );

                pst.setString(
                    2,
                    authorField.getText()
                );

                pst.setString(
                    3,
                    isbnField.getText()
                );

                pst.setString(
                    4,
                    genreField.getText()
                );

                pst.executeUpdate();

                con.close();

                JOptionPane.showMessageDialog(
                    this,
                    "Book added successfully!"
                );

                clearFields();
                loadBooks();

            } catch (Exception ex) {

                JOptionPane.showMessageDialog(
                    this,
                    "Error adding book: "
                    + ex.getMessage()
                );
            }
        });

        // ================= UPDATE BOOK =================

        updateBtn.addActionListener(e -> {

            int row =
                table.getSelectedRow();

            if (row == -1) {

                JOptionPane.showMessageDialog(
                    this,
                    "Please select a book first."
                );

                return;
            }

            try {

                int bookId =
                    Integer.parseInt(
                        model.getValueAt(
                            row,
                            0
                        ).toString()
                    );

                Connection con =
                    DBConnection.getConnection();

                String sql =
                    "UPDATE books SET " +
                    "title=?, author=?, isbn=?, genre=? " +
                    "WHERE book_id=?";

                PreparedStatement pst =
                    con.prepareStatement(sql);

                pst.setString(
                    1,
                    titleField.getText()
                );

                pst.setString(
                    2,
                    authorField.getText()
                );

                pst.setString(
                    3,
                    isbnField.getText()
                );

                pst.setString(
                    4,
                    genreField.getText()
                );

                pst.setInt(
                    5,
                    bookId
                );

                pst.executeUpdate();

                con.close();

                JOptionPane.showMessageDialog(
                    this,
                    "Book updated successfully!"
                );

                clearFields();
                loadBooks();

            } catch (Exception ex) {

                JOptionPane.showMessageDialog(
                    this,
                    "Error updating book: "
                    + ex.getMessage()
                );
            }
        });

        // ================= DELETE BOOK =================

        deleteBtn.addActionListener(e -> {

            int row =
                table.getSelectedRow();

            if (row == -1) {

                JOptionPane.showMessageDialog(
                    this,
                    "Please select a book first."
                );

                return;
            }

            int confirm =
                JOptionPane.showConfirmDialog(
                    this,
                    "Are you sure you want to delete this book?",
                    "Confirm Delete",
                    JOptionPane.YES_NO_OPTION
                );

            if (
                confirm != JOptionPane.YES_OPTION
            ) {
                return;
            }

            try {

                int bookId =
                    Integer.parseInt(
                        model.getValueAt(
                            row,
                            0
                        ).toString()
                    );

                Connection con =
                    DBConnection.getConnection();

                String sql =
                    "DELETE FROM books " +
                    "WHERE book_id=?";

                PreparedStatement pst =
                    con.prepareStatement(sql);

                pst.setInt(
                    1,
                    bookId
                );

                pst.executeUpdate();

                con.close();

                JOptionPane.showMessageDialog(
                    this,
                    "Book deleted successfully!"
                );

                clearFields();
                loadBooks();

            } catch (Exception ex) {

                JOptionPane.showMessageDialog(
                    this,
                    "Error deleting book: "
                    + ex.getMessage()
                );
            }
        });

        // ================= TABLE SELECTION =================

        table.getSelectionModel()
            .addListSelectionListener(e -> {

                int row =
                    table.getSelectedRow();

                if (row != -1) {

                    titleField.setText(
                        model.getValueAt(
                            row,
                            1
                        ).toString()
                    );

                    authorField.setText(
                        model.getValueAt(
                            row,
                            2
                        ).toString()
                    );

                    isbnField.setText(
                        model.getValueAt(
                            row,
                            3
                        ).toString()
                    );

                    genreField.setText(
                        model.getValueAt(
                            row,
                            4
                        ).toString()
                    );
                }
            });

        // ================= REFRESH =================

        refreshBtn.addActionListener(
            e -> loadBooks()
        );

        // ================= LAYOUT =================

        JPanel topPanel = new JPanel(
            new BorderLayout(0, 15)
        );

        topPanel.add(
            headerPanel,
            BorderLayout.NORTH
        );

        topPanel.add(
            formContainer,
            BorderLayout.CENTER
        );

        mainPanel.add(
            topPanel,
            BorderLayout.NORTH
        );

        mainPanel.add(
            tablePanel,
            BorderLayout.CENTER
        );

        mainPanel.add(
            buttonPanel,
            BorderLayout.SOUTH
        );

        add(mainPanel);

        loadBooks();

        setVisible(true);
    }

    // ================= LOAD BOOKS =================

    void loadBooks() {

        model.setRowCount(0);

        try {

            Connection con =
                DBConnection.getConnection();

            String sql =
                "SELECT * FROM books";

            PreparedStatement pst =
                con.prepareStatement(sql);

            ResultSet rs =
                pst.executeQuery();

            while (rs.next()) {

                model.addRow(
                    new Object[]{
                        rs.getInt("book_id"),
                        rs.getString("title"),
                        rs.getString("author"),
                        rs.getString("isbn"),
                        rs.getString("genre"),
                        rs.getString("status")
                    }
                );
            }

            con.close();

        } catch (Exception ex) {

            JOptionPane.showMessageDialog(
                this,
                "Error loading books: "
                + ex.getMessage()
            );
        }
    }

    void clearFields() {

        titleField.setText("");
        authorField.setText("");
        isbnField.setText("");
        genreField.setText("");
    }
}