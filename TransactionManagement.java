
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class TransactionManagement extends JFrame {

    private DefaultTableModel model;
    private JTable table;

    public TransactionManagement() {
        setTitle("Transaction Management");
        setSize(950, 550);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

        JPanel mainPanel = new JPanel(new BorderLayout(15, 15));
        mainPanel.setBorder(
            BorderFactory.createEmptyBorder(20, 20, 20, 20)
        );

        JLabel title = new JLabel("TRANSACTION MANAGEMENT");
        title.setFont(new Font("Arial", Font.BOLD, 22));
        title.setHorizontalAlignment(SwingConstants.CENTER);

        String[] columns = {
            "Transaction ID",
            "Book ID",
            "Member ID",
            "Borrow Date",
            "Return Date",
            "Status"
        };

        model = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        table = new JTable(model);
        table.setRowHeight(25);
        table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

        JScrollPane scrollPane = new JScrollPane(table);

        JButton borrowBtn = new JButton("Borrow Book");
        JButton returnBtn = new JButton("Return Book");
        JButton refreshBtn = new JButton("Refresh");

        Font buttonFont = new Font("Arial", Font.BOLD, 13);
        borrowBtn.setFont(buttonFont);
        returnBtn.setFont(buttonFont);
        refreshBtn.setFont(buttonFont);

        JPanel buttonPanel = new JPanel(
            new FlowLayout(FlowLayout.CENTER, 20, 10)
        );

        buttonPanel.add(borrowBtn);
        buttonPanel.add(returnBtn);
        buttonPanel.add(refreshBtn);

        mainPanel.add(title, BorderLayout.NORTH);
        mainPanel.add(scrollPane, BorderLayout.CENTER);
        mainPanel.add(buttonPanel, BorderLayout.SOUTH);

        add(mainPanel);

        borrowBtn.addActionListener(e -> showBorrowDialog());
        returnBtn.addActionListener(e -> returnSelectedBook());
        refreshBtn.addActionListener(e -> loadTransactions());

        loadTransactions();
        setVisible(true);
    }

    // Separate window for borrowing a book
    private void showBorrowDialog() {

        JDialog dialog = new JDialog(this, "Borrow Book", true);
        dialog.setSize(420, 230);
        dialog.setLocationRelativeTo(this);
        dialog.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);

        JPanel formPanel = new JPanel(new GridLayout(2, 2, 10, 12));
        formPanel.setBorder(
            BorderFactory.createEmptyBorder(20, 20, 10, 20)
        );

        JTextField bookIdField = new JTextField();
        JTextField memberIdField = new JTextField();

        formPanel.add(new JLabel("Book ID:"));
        formPanel.add(bookIdField);

        formPanel.add(new JLabel("Member ID:"));
        formPanel.add(memberIdField);

        JButton borrowButton = new JButton("Borrow");
        JButton cancelButton = new JButton("Cancel");

        JPanel buttonPanel = new JPanel(
            new FlowLayout(FlowLayout.CENTER, 12, 8)
        );

        buttonPanel.add(borrowButton);
        buttonPanel.add(cancelButton);

        dialog.setLayout(new BorderLayout());
        dialog.add(formPanel, BorderLayout.CENTER);
        dialog.add(buttonPanel, BorderLayout.SOUTH);

        borrowButton.addActionListener(e -> {

            String bookText = bookIdField.getText().trim();
            String memberText = memberIdField.getText().trim();

            if (bookText.isEmpty() || memberText.isEmpty()) {
                JOptionPane.showMessageDialog(
                    dialog,
                    "Please enter both Book ID and Member ID."
                );
                return;
            }

            try {
                int bookId = Integer.parseInt(bookText);
                int memberId = Integer.parseInt(memberText);

                if (bookId <= 0 || memberId <= 0) {
                    JOptionPane.showMessageDialog(
                        dialog,
                        "IDs must be positive numbers."
                    );
                    return;
                }

                if (borrowBook(bookId, memberId)) {
                    dialog.dispose();
                    loadTransactions();
                }

            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(
                    dialog,
                    "Please enter valid numeric IDs."
                );
            }
        });

        cancelButton.addActionListener(e -> dialog.dispose());

        dialog.getRootPane().setDefaultButton(borrowButton);
        dialog.setVisible(true);
    }

    // Borrow book and save transaction
    private boolean borrowBook(int bookId, int memberId) {

        Connection con = null;

        try {
            con = DBConnection.getConnection();

            if (con == null) {
                JOptionPane.showMessageDialog(
                    this,
                    "Database connection failed. Check DBConnection settings."
                );
                return false;
            }

            con.setAutoCommit(false);

            // Check whether book exists and is available
            try (PreparedStatement bookPst = con.prepareStatement(
                    "SELECT status FROM books WHERE book_id=?")) {

                bookPst.setInt(1, bookId);

                try (ResultSet bookRs = bookPst.executeQuery()) {

                    if (!bookRs.next()) {
                        con.rollback();
                        JOptionPane.showMessageDialog(this, "Book not found!");
                        return false;
                    }

                    String status = bookRs.getString("status");

                    if (status == null ||
                        !status.equalsIgnoreCase("Available")) {

                        con.rollback();

                        JOptionPane.showMessageDialog(
                            this,
                            "This book is not available!"
                        );
                        return false;
                    }
                }
            }

            // Check whether member exists
            try (PreparedStatement memberPst = con.prepareStatement(
                    "SELECT member_id FROM members WHERE member_id=?")) {

                memberPst.setInt(1, memberId);

                try (ResultSet memberRs = memberPst.executeQuery()) {

                    if (!memberRs.next()) {
                        con.rollback();

                        JOptionPane.showMessageDialog(
                            this,
                            "Member not found!"
                        );
                        return false;
                    }
                }
            }

            // Insert transaction
            try (PreparedStatement transactionPst = con.prepareStatement(
                    "INSERT INTO transactions " +
                    "(book_id, member_id, borrow_date, status) " +
                    "VALUES (?, ?, ?, ?)")) {

                transactionPst.setInt(1, bookId);
                transactionPst.setInt(2, memberId);

                transactionPst.setDate(
                    3,
                    new java.sql.Date(System.currentTimeMillis())
                );

                transactionPst.setString(4, "Borrowed");
                transactionPst.executeUpdate();
            }

            // Update book status
            try (PreparedStatement updateBookPst = con.prepareStatement(
                    "UPDATE books SET status='Borrowed' WHERE book_id=?")) {

                updateBookPst.setInt(1, bookId);
                updateBookPst.executeUpdate();
            }

            con.commit();

            JOptionPane.showMessageDialog(
                this,
                "Book borrowed successfully!"
            );

            return true;

        } catch (Exception ex) {

            if (con != null) {
                try {
                    con.rollback();
                } catch (SQLException ignored) {
                    // Keep the original error message.
                }
            }

            JOptionPane.showMessageDialog(
                this,
                "Error borrowing book: " + ex.getMessage()
            );

            return false;

        } finally {

            if (con != null) {
                try {
                    con.setAutoCommit(true);
                    con.close();
                } catch (SQLException ignored) {
                    // Connection cleanup.
                }
            }
        }
    }

    // Return selected borrowed book
    private void returnSelectedBook() {

        int selectedRow = table.getSelectedRow();

        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(
                this,
                "Please select a borrowed transaction."
            );
            return;
        }

        selectedRow = table.convertRowIndexToModel(selectedRow);

        int transactionId =
            ((Number) model.getValueAt(selectedRow, 0)).intValue();

        int bookId =
            ((Number) model.getValueAt(selectedRow, 1)).intValue();

        String status =
            String.valueOf(model.getValueAt(selectedRow, 5));

        if (!status.equalsIgnoreCase("Borrowed")) {
            JOptionPane.showMessageDialog(
                this,
                "This transaction is already returned."
            );
            return;
        }

        int confirm = JOptionPane.showConfirmDialog(
            this,
            "Return this book?",
            "Confirm Return",
            JOptionPane.YES_NO_OPTION
        );

        if (confirm != JOptionPane.YES_OPTION) {
            return;
        }

        Connection con = null;

        try {
            con = DBConnection.getConnection();

            if (con == null) {
                JOptionPane.showMessageDialog(
                    this,
                    "Database connection failed. Check DBConnection settings."
                );
                return;
            }

            con.setAutoCommit(false);

            // Update transaction status and return date
            try (PreparedStatement transactionPst = con.prepareStatement(
                    "UPDATE transactions " +
                    "SET return_date=?, status='Returned' " +
                    "WHERE transaction_id=? AND status='Borrowed'")) {

                transactionPst.setDate(
                    1,
                    new java.sql.Date(System.currentTimeMillis())
                );

                transactionPst.setInt(2, transactionId);

                if (transactionPst.executeUpdate() == 0) {
                    con.rollback();

                    JOptionPane.showMessageDialog(
                        this,
                        "This transaction is no longer marked as borrowed."
                    );

                    loadTransactions();
                    return;
                }
            }

            // Make book available again
            try (PreparedStatement bookPst = con.prepareStatement(
                    "UPDATE books SET status='Available' WHERE book_id=?")) {

                bookPst.setInt(1, bookId);
                bookPst.executeUpdate();
            }

            con.commit();

            JOptionPane.showMessageDialog(
                this,
                "Book returned successfully!"
            );

            loadTransactions();

        } catch (Exception ex) {

            if (con != null) {
                try {
                    con.rollback();
                } catch (SQLException ignored) {
                    // Keep the original error message.
                }
            }

            JOptionPane.showMessageDialog(
                this,
                "Error returning book: " + ex.getMessage()
            );

        } finally {

            if (con != null) {
                try {
                    con.setAutoCommit(true);
                    con.close();
                } catch (SQLException ignored) {
                    // Connection cleanup.
                }
            }
        }
    }

    // Load transactions into the table
    public void loadTransactions() {

        model.setRowCount(0);

        String sql =
            "SELECT * FROM transactions ORDER BY transaction_id DESC";

        try (Connection con = DBConnection.getConnection()) {

            if (con == null) {
                JOptionPane.showMessageDialog(
                    this,
                    "Database connection failed. Check DBConnection settings."
                );
                return;
            }

            try (PreparedStatement pst = con.prepareStatement(sql);
                 ResultSet rs = pst.executeQuery()) {

                while (rs.next()) {
                    model.addRow(new Object[] {
                        rs.getInt("transaction_id"),
                        rs.getInt("book_id"),
                        rs.getInt("member_id"),
                        rs.getDate("borrow_date"),
                        rs.getDate("return_date"),
                        rs.getString("status")
                    });
                }
            }

        } catch (Exception e) {
            JOptionPane.showMessageDialog(
                this,
                "Error loading transactions: " + e.getMessage()
            );
        }
    }
}