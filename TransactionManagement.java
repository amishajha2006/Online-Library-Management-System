import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

public class TransactionManagement extends JFrame {

    DefaultTableModel model;
    JTable table;

    TransactionManagement() {

        setTitle("Transaction Management");
        setSize(950, 550);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

        JPanel mainPanel =
            new JPanel(new BorderLayout(15, 15));

        mainPanel.setBorder(
            BorderFactory.createEmptyBorder(
                20, 20, 20, 20
            )
        );

        // TITLE
        JLabel title =
            new JLabel("TRANSACTION MANAGEMENT");

        title.setFont(
            new Font("Arial", Font.BOLD, 22)
        );

        title.setHorizontalAlignment(
            SwingConstants.CENTER
        );

        // TABLE
        String[] columns = {
            "Transaction ID",
            "Book ID",
            "Member ID",
            "Borrow Date",
            "Return Date",
            "Status"
        };

        model =
            new DefaultTableModel(columns, 0);

        table = new JTable(model);

        table.setRowHeight(25);

        JScrollPane scrollPane =
            new JScrollPane(table);

        // BUTTONS
        JButton borrowBtn =
            new JButton("Borrow Book");

        JButton returnBtn =
            new JButton("Return Book");

        JButton refreshBtn =
            new JButton("Refresh");

        Font buttonFont =
            new Font("Arial", Font.BOLD, 13);

        borrowBtn.setFont(buttonFont);
        returnBtn.setFont(buttonFont);
        refreshBtn.setFont(buttonFont);

        JPanel buttonPanel =
            new JPanel(
                new FlowLayout(
                    FlowLayout.CENTER,
                    20,
                    10
                )
            );

        buttonPanel.add(borrowBtn);
        buttonPanel.add(returnBtn);
        buttonPanel.add(refreshBtn);

        mainPanel.add(
            title,
            BorderLayout.NORTH
        );

        mainPanel.add(
            scrollPane,
            BorderLayout.CENTER
        );

        mainPanel.add(
            buttonPanel,
            BorderLayout.SOUTH
        );

        add(mainPanel);

        loadTransactions();

        // REFRESH
        refreshBtn.addActionListener(e ->
            loadTransactions()
        );

        // BORROW BOOK
        borrowBtn.addActionListener(e -> {

            JTextField bookIdField =
                new JTextField();

            JTextField memberIdField =
                new JTextField();

            JPanel panel =
                new JPanel(
                    new GridLayout(
                        2,
                        2,
                        8,
                        8
                    )
                );

            panel.add(
                new JLabel("Book ID:")
            );

            panel.add(bookIdField);

            panel.add(
                new JLabel("Member ID:")
            );

            panel.add(memberIdField);

            int result =
                JOptionPane.showConfirmDialog(
                    this,
                    panel,
                    "Borrow Book",
                    JOptionPane.OK_CANCEL_OPTION
                );

            if (result !=
                JOptionPane.OK_OPTION) {

                return;
            }

            try {

                int bookId =
                    Integer.parseInt(
                        bookIdField.getText()
                    );

                int memberId =
                    Integer.parseInt(
                        memberIdField.getText()
                    );

                Connection con =
                    DBConnection.getConnection();

                // Check book
                String bookSql =
                    "SELECT status FROM books " +
                    "WHERE book_id=?";

                PreparedStatement bookPst =
                    con.prepareStatement(
                        bookSql
                    );

                bookPst.setInt(
                    1,
                    bookId
                );

                ResultSet bookRs =
                    bookPst.executeQuery();

                if (!bookRs.next()) {

                    JOptionPane.showMessageDialog(
                        this,
                        "Book not found!"
                    );

                    con.close();
                    return;
                }

                String status =
                    bookRs.getString("status");

                if (!status.equalsIgnoreCase(
                        "Available")) {

                    JOptionPane.showMessageDialog(
                        this,
                        "This book is not available!"
                    );

                    con.close();
                    return;
                }

                // Check member
                String memberSql =
                    "SELECT member_id FROM members " +
                    "WHERE member_id=?";

                PreparedStatement memberPst =
                    con.prepareStatement(
                        memberSql
                    );

                memberPst.setInt(
                    1,
                    memberId
                );

                ResultSet memberRs =
                    memberPst.executeQuery();

                if (!memberRs.next()) {

                    JOptionPane.showMessageDialog(
                        this,
                        "Member not found!"
                    );

                    con.close();
                    return;
                }

                // Insert transaction
                String transactionSql =
                    "INSERT INTO transactions " +
                    "(book_id, member_id, " +
                    "borrow_date, status) " +
                    "VALUES (?, ?, ?, ?)";

                PreparedStatement transactionPst =
                    con.prepareStatement(
                        transactionSql
                    );

                transactionPst.setInt(
                    1,
                    bookId
                );

                transactionPst.setInt(
                    2,
                    memberId
                );

                transactionPst.setDate(
                    3,
                    new java.sql.Date(
                        System.currentTimeMillis()
                    )
                );

                transactionPst.setString(
                    4,
                    "Borrowed"
                );

                transactionPst.executeUpdate();

                // Update book status
                String updateBookSql =
                    "UPDATE books " +
                    "SET status='Borrowed' " +
                    "WHERE book_id=?";

                PreparedStatement updateBookPst =
                    con.prepareStatement(
                        updateBookSql
                    );

                updateBookPst.setInt(
                    1,
                    bookId
                );

                updateBookPst.executeUpdate();

                con.close();

                JOptionPane.showMessageDialog(
                    this,
                    "Book borrowed successfully!"
                );

                loadTransactions();

            } catch (
                NumberFormatException ex
            ) {

                JOptionPane.showMessageDialog(
                    this,
                    "Please enter valid numeric IDs."
                );

            } catch (Exception ex) {

                JOptionPane.showMessageDialog(
                    this,
                    "Error borrowing book: " +
                    ex.getMessage()
                );
            }
        });

        // RETURN BOOK
        returnBtn.addActionListener(e -> {

            int selectedRow =
                table.getSelectedRow();

            if (selectedRow == -1) {

                JOptionPane.showMessageDialog(
                    this,
                    "Please select a borrowed transaction."
                );

                return;
            }

            int transactionId =
                (int) model.getValueAt(
                    selectedRow,
                    0
                );

            int bookId =
                (int) model.getValueAt(
                    selectedRow,
                    1
                );

            String status =
                model.getValueAt(
                    selectedRow,
                    5
                ).toString();

            if (!status.equalsIgnoreCase(
                    "Borrowed")) {

                JOptionPane.showMessageDialog(
                    this,
                    "This transaction is already returned."
                );

                return;
            }

            int confirm =
                JOptionPane.showConfirmDialog(
                    this,
                    "Return this book?",
                    "Confirm Return",
                    JOptionPane.YES_NO_OPTION
                );

            if (confirm !=
                JOptionPane.YES_OPTION) {

                return;
            }

            try {

                Connection con =
                    DBConnection.getConnection();

                // Update transaction
                String transactionSql =
                    "UPDATE transactions " +
                    "SET return_date=?, " +
                    "status='Returned' " +
                    "WHERE transaction_id=?";

                PreparedStatement transactionPst =
                    con.prepareStatement(
                        transactionSql
                    );

                transactionPst.setDate(
                    1,
                    new java.sql.Date(
                        System.currentTimeMillis()
                    )
                );

                transactionPst.setInt(
                    2,
                    transactionId
                );

                transactionPst.executeUpdate();

                // Make book available
                String bookSql =
                    "UPDATE books " +
                    "SET status='Available' " +
                    "WHERE book_id=?";

                PreparedStatement bookPst =
                    con.prepareStatement(
                        bookSql
                    );

                bookPst.setInt(
                    1,
                    bookId
                );

                bookPst.executeUpdate();

                con.close();

                JOptionPane.showMessageDialog(
                    this,
                    "Book returned successfully!"
                );

                loadTransactions();

            } catch (Exception ex) {

                JOptionPane.showMessageDialog(
                    this,
                    "Error returning book: " +
                    ex.getMessage()
                );
            }
        });

        setVisible(true);
    }

    void loadTransactions() {

        model.setRowCount(0);

        String sql =
            "SELECT * FROM transactions " +
            "ORDER BY transaction_id DESC";

        try {

            Connection con =
                DBConnection.getConnection();

            PreparedStatement pst =
                con.prepareStatement(sql);

            ResultSet rs =
                pst.executeQuery();

            while (rs.next()) {

                model.addRow(
                    new Object[] {

                        rs.getInt(
                            "transaction_id"
                        ),

                        rs.getInt(
                            "book_id"
                        ),

                        rs.getInt(
                            "member_id"
                        ),

                        rs.getDate(
                            "borrow_date"
                        ),

                        rs.getDate(
                            "return_date"
                        ),

                        rs.getString(
                            "status"
                        )
                    }
                );
            }

            con.close();

        } catch (Exception e) {

            JOptionPane.showMessageDialog(
                this,
                "Error loading transactions: " +
                e.getMessage()
            );
        }
    }
}