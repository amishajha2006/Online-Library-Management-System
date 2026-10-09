
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.*;

public class BookManagement extends JFrame {

    private JTable table;
    private DefaultTableModel model;

    public BookManagement() {
        setTitle("Book Management");
        setSize(950, 650);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);
        setResizable(false);

        JPanel mainPanel = new JPanel(new BorderLayout(20, 20));
        mainPanel.setBorder(
            BorderFactory.createEmptyBorder(25, 30, 25, 30)
        );

        // Header
        JLabel icon = new JLabel("📚");
        icon.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 35));

        JLabel heading = new JLabel("BOOK MANAGEMENT");
        heading.setFont(new Font("Arial", Font.BOLD, 26));

        JLabel subtitle = new JLabel(
            "Add, update, delete and manage library books"
        );
        subtitle.setFont(new Font("Arial", Font.PLAIN, 14));

        JPanel titlePanel = new JPanel(new GridLayout(2, 1, 0, 4));
        titlePanel.add(heading);
        titlePanel.add(subtitle);

        JPanel headerPanel = new JPanel(new BorderLayout(15, 0));
        headerPanel.add(icon, BorderLayout.WEST);
        headerPanel.add(titlePanel, BorderLayout.CENTER);

        // Buttons
        JButton addBtn = new JButton("➕ Add Book");
        JButton updateBtn = new JButton("✏️ Update");
        JButton deleteBtn = new JButton("🗑️ Delete");
        JButton refreshBtn = new JButton("🔄 Refresh");

        JButton[] buttons = {
            addBtn, updateBtn, deleteBtn, refreshBtn
        };

        for (JButton button : buttons) {
            button.setFont(new Font("Arial", Font.BOLD, 13));
            button.setFocusPainted(false);
        }

        JPanel buttonPanel = new JPanel(
            new FlowLayout(FlowLayout.CENTER, 15, 10)
        );

        buttonPanel.add(addBtn);
        buttonPanel.add(updateBtn);
        buttonPanel.add(deleteBtn);
        buttonPanel.add(refreshBtn);

        // Books table
        model = new DefaultTableModel(
            new String[]{
                "Book ID", "Title", "Author",
                "ISBN", "Genre", "Status"
            }, 0
        ) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        table = new JTable(model);
        table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        table.setRowHeight(28);
        table.setFont(new Font("Arial", Font.PLAIN, 13));
        table.getTableHeader().setFont(
            new Font("Arial", Font.BOLD, 13)
        );

        JPanel tablePanel = new JPanel(new BorderLayout());
        tablePanel.setBorder(
            BorderFactory.createTitledBorder("Library Books")
        );
        tablePanel.add(new JScrollPane(table), BorderLayout.CENTER);

        // Layout
        JPanel topPanel = new JPanel(new BorderLayout(0, 15));
        topPanel.add(headerPanel, BorderLayout.NORTH);

        mainPanel.add(topPanel, BorderLayout.NORTH);
        mainPanel.add(tablePanel, BorderLayout.CENTER);
        mainPanel.add(buttonPanel, BorderLayout.SOUTH);

        add(mainPanel);

        // Add book opens a separate form
        addBtn.addActionListener(e ->
            openBookForm(false, -1, "", "", "", "")
        );

        // Update selected book
        updateBtn.addActionListener(e -> {
            int row = table.getSelectedRow();

            if (row == -1) {
                JOptionPane.showMessageDialog(
                    this, "Please select a book first."
                );
                return;
            }

            int modelRow = table.convertRowIndexToModel(row);

            int bookId = Integer.parseInt(
                model.getValueAt(modelRow, 0).toString()
            );

            String title = valueAt(modelRow, 1);
            String author = valueAt(modelRow, 2);
            String isbn = valueAt(modelRow, 3);
            String genre = valueAt(modelRow, 4);

            openBookForm(
                true, bookId, title, author, isbn, genre
            );
        });

        deleteBtn.addActionListener(e -> deleteSelectedBook());
        refreshBtn.addActionListener(e -> loadBooks());

        loadBooks();
        setVisible(true);
    }

    private String valueAt(int row, int column) {
        Object value = model.getValueAt(row, column);
        return value == null ? "" : value.toString();
    }

    // Add Book and Update Book form
    private void openBookForm(
        boolean editMode,
        int bookId,
        String currentTitle,
        String currentAuthor,
        String currentIsbn,
        String currentGenre
    ) {
        JDialog dialog = new JDialog(
            this,
            editMode ? "Update Book" : "Add New Book",
            true
        );

        dialog.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
        dialog.setResizable(false);

        JTextField titleField = new JTextField(currentTitle, 22);
        JTextField authorField = new JTextField(currentAuthor, 22);
        JTextField isbnField = new JTextField(currentIsbn, 22);
        JTextField genreField = new JTextField(currentGenre, 22);

        JPanel fieldsPanel = new JPanel(
            new GridLayout(4, 2, 12, 14)
        );

        fieldsPanel.setBorder(
            BorderFactory.createEmptyBorder(20, 22, 12, 22)
        );

        fieldsPanel.add(new JLabel("Title:"));
        fieldsPanel.add(titleField);

        fieldsPanel.add(new JLabel("Author:"));
        fieldsPanel.add(authorField);

        fieldsPanel.add(new JLabel("ISBN:"));
        fieldsPanel.add(isbnField);

        fieldsPanel.add(new JLabel("Genre:"));
        fieldsPanel.add(genreField);

        JButton saveButton = new JButton(
            editMode ? "Save Changes" : "Save Book"
        );

        JButton cancelButton = new JButton("Cancel");

        saveButton.setFont(new Font("Arial", Font.BOLD, 13));
        cancelButton.setFont(new Font("Arial", Font.PLAIN, 13));

        JPanel buttonPanel = new JPanel(
            new FlowLayout(FlowLayout.RIGHT, 10, 12)
        );

        buttonPanel.setBorder(
            BorderFactory.createEmptyBorder(0, 12, 8, 12)
        );

        buttonPanel.add(cancelButton);
        buttonPanel.add(saveButton);

        JPanel content = new JPanel(new BorderLayout());
        content.add(fieldsPanel, BorderLayout.CENTER);
        content.add(buttonPanel, BorderLayout.SOUTH);

        dialog.setContentPane(content);

        // Save book or update existing book
        saveButton.addActionListener(e -> {
            String title = titleField.getText().trim();
            String author = authorField.getText().trim();
            String isbn = isbnField.getText().trim();
            String genre = genreField.getText().trim();

            if (title.isEmpty() || author.isEmpty()
                    || isbn.isEmpty() || genre.isEmpty()) {
                JOptionPane.showMessageDialog(
                    dialog,
                    "Please fill in all four fields.",
                    "Missing Information",
                    JOptionPane.WARNING_MESSAGE
                );
                return;
            }

            String sql = editMode
                ? "UPDATE books SET title=?, author=?, isbn=?, genre=? WHERE book_id=?"
                : "INSERT INTO books (title, author, isbn, genre) VALUES (?, ?, ?, ?)";

            try (Connection con = DBConnection.getConnection()) {
                if (con == null) {
                    JOptionPane.showMessageDialog(
                        dialog,
                        "Could not connect to the database. Check your database settings.",
                        "Database Connection Error",
                        JOptionPane.ERROR_MESSAGE
                    );
                    return;
                }

                try (PreparedStatement pst = con.prepareStatement(sql)) {
                    pst.setString(1, title);
                    pst.setString(2, author);
                    pst.setString(3, isbn);
                    pst.setString(4, genre);

                    if (editMode) {
                        pst.setInt(5, bookId);
                    }

                    pst.executeUpdate();
                }

                JOptionPane.showMessageDialog(
                    dialog,
                    editMode
                        ? "Book updated successfully!"
                        : "Book added successfully!"
                );

                dialog.dispose();
                loadBooks();

            } catch (SQLException ex) {
                JOptionPane.showMessageDialog(
                    dialog,
                    "Error " + (editMode ? "updating" : "adding")
                        + " book: " + ex.getMessage(),
                    "Database Error",
                    JOptionPane.ERROR_MESSAGE
                );
            }
        });

        cancelButton.addActionListener(e -> dialog.dispose());

        dialog.getRootPane().setDefaultButton(saveButton);
        dialog.pack();
        dialog.setMinimumSize(new Dimension(430, 280));
        dialog.setLocationRelativeTo(this);
        dialog.setVisible(true);
    }

    // Delete selected book
    private void deleteSelectedBook() {
        int row = table.getSelectedRow();

        if (row == -1) {
            JOptionPane.showMessageDialog(
                this, "Please select a book first."
            );
            return;
        }

        int confirm = JOptionPane.showConfirmDialog(
            this,
            "Are you sure you want to delete this book?",
            "Confirm Delete",
            JOptionPane.YES_NO_OPTION
        );

        if (confirm != JOptionPane.YES_OPTION) {
            return;
        }

        int modelRow = table.convertRowIndexToModel(row);

        int bookId = Integer.parseInt(
            model.getValueAt(modelRow, 0).toString()
        );

        try (Connection con = DBConnection.getConnection()) {
            if (con == null) {
                JOptionPane.showMessageDialog(
                    this,
                    "Could not connect to the database.",
                    "Database Connection Error",
                    JOptionPane.ERROR_MESSAGE
                );
                return;
            }

            try (PreparedStatement pst = con.prepareStatement(
                    "DELETE FROM books WHERE book_id=?")) {
                pst.setInt(1, bookId);
                pst.executeUpdate();
            }

            JOptionPane.showMessageDialog(
                this, "Book deleted successfully!"
            );

            loadBooks();

        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(
                this,
                "Error deleting book: " + ex.getMessage(),
                "Database Error",
                JOptionPane.ERROR_MESSAGE
            );
        }
    }

    // Load books into the table
    private void loadBooks() {
        model.setRowCount(0);

        String sql = "SELECT * FROM books";

        try (Connection con = DBConnection.getConnection()) {
            if (con == null) {
                JOptionPane.showMessageDialog(
                    this,
                    "Could not connect to the database.",
                    "Database Connection Error",
                    JOptionPane.ERROR_MESSAGE
                );
                return;
            }

            try (PreparedStatement pst = con.prepareStatement(sql);
                 ResultSet rs = pst.executeQuery()) {

                while (rs.next()) {
                    model.addRow(new Object[]{
                        rs.getInt("book_id"),
                        rs.getString("title"),
                        rs.getString("author"),
                        rs.getString("isbn"),
                        rs.getString("genre"),
                        rs.getString("status")
                    });
                }
            }

        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(
                this,
                "Error loading books: " + ex.getMessage(),
                "Database Error",
                JOptionPane.ERROR_MESSAGE
            );
        }
    }
}