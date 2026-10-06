import javax.swing.*;
import java.awt.*;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

public class MemberDashboard extends JFrame {

    int memberId;

    MemberDashboard(int memberId) {

        this.memberId = memberId;

        setTitle("Member Dashboard");
        setSize(850, 600);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setResizable(false);

        // MAIN PANEL
        JPanel mainPanel =
            new JPanel(new BorderLayout(20, 20));

        mainPanel.setBorder(
            BorderFactory.createEmptyBorder(
                25, 35, 30, 35
            )
        );

        // =========================
        // HEADER
        // =========================

        JLabel icon =
            new JLabel("📚");

        icon.setFont(
            new Font(
                "Segoe UI Emoji",
                Font.PLAIN,
                35
            )
        );

        JLabel title =
            new JLabel(
                "MEMBER DASHBOARD"
            );

        title.setFont(
            new Font(
                "Arial",
                Font.BOLD,
                26
            )
        );

        JLabel subtitle =
            new JLabel(
                "Welcome to the Online Library"
            );

        subtitle.setFont(
            new Font(
                "Arial",
                Font.PLAIN,
                14
            )
        );

        JPanel titlePanel =
            new JPanel(
                new GridLayout(2, 1, 0, 4)
            );

        titlePanel.add(title);
        titlePanel.add(subtitle);

        JPanel headerPanel =
            new JPanel(
                new BorderLayout(15, 0)
            );

        headerPanel.add(
            icon,
            BorderLayout.WEST
        );

        headerPanel.add(
            titlePanel,
            BorderLayout.CENTER
        );

        // =========================
        // BUTTONS
        // =========================

        JButton searchBtn =
            new JButton(
                "<html><center>🔍<br>Search Books</center></html>"
            );

        JButton borrowBtn =
            new JButton(
                "<html><center>📖<br>Borrow Book</center></html>"
            );

        JButton returnBtn =
            new JButton(
                "<html><center>↩️<br>Return Book</center></html>"
            );

        JButton historyBtn =
            new JButton(
                "<html><center>📋<br>Borrowing History</center></html>"
            );

        JButton profileBtn =
            new JButton(
                "<html><center>👤<br>My Profile</center></html>"
            );

        JButton logoutBtn =
            new JButton(
                "<html><center>🚪<br>Logout</center></html>"
            );

        JButton[] buttons = {
            searchBtn,
            borrowBtn,
            returnBtn,
            historyBtn,
            profileBtn,
            logoutBtn
        };

        Font buttonFont =
            new Font(
                "Arial",
                Font.BOLD,
                14
            );

        for (JButton button : buttons) {

            button.setFont(buttonFont);

            button.setFocusPainted(false);

            button.setPreferredSize(
                new Dimension(
                    250,
                    110
                )
            );
        }

        JPanel buttonPanel =
            new JPanel(
                new GridLayout(
                    2,
                    3,
                    20,
                    20
                )
            );

        buttonPanel.add(searchBtn);
        buttonPanel.add(borrowBtn);
        buttonPanel.add(returnBtn);
        buttonPanel.add(historyBtn);
        buttonPanel.add(profileBtn);
        buttonPanel.add(logoutBtn);

        // =========================
        // FOOTER
        // =========================

        JLabel footer =
            new JLabel(
                "Member Services Panel"
            );

        footer.setFont(
            new Font(
                "Arial",
                Font.ITALIC,
                12
            )
        );

        footer.setHorizontalAlignment(
            SwingConstants.CENTER
        );

        // =========================
        // SEARCH BOOKS
        // FUNCTIONALITY UNCHANGED
        // =========================

        searchBtn.addActionListener(e ->
            new BookSearch()
        );

        // =========================
        // BORROW BOOK
        // FUNCTIONALITY UNCHANGED
        // =========================

        borrowBtn.addActionListener(e -> {

            JTextField bookIdField =
                new JTextField();

            JPanel panel =
                new JPanel(
                    new GridLayout(
                        1,
                        2,
                        8,
                        8
                    )
                );

            panel.add(
                new JLabel("Book ID:")
            );

            panel.add(bookIdField);

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

                Connection con =
                    DBConnection.getConnection();

                String checkSql =
                    "SELECT status FROM books " +
                    "WHERE book_id=?";

                PreparedStatement checkPst =
                    con.prepareStatement(
                        checkSql
                    );

                checkPst.setInt(
                    1,
                    bookId
                );

                ResultSet rs =
                    checkPst.executeQuery();

                if (!rs.next()) {

                    JOptionPane.showMessageDialog(
                        this,
                        "Book not found!"
                    );

                    con.close();
                    return;
                }

                String status =
                    rs.getString("status");

                if (!status.equalsIgnoreCase(
                        "Available")) {

                    JOptionPane.showMessageDialog(
                        this,
                        "This book is not available!"
                    );

                    con.close();
                    return;
                }

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

                String updateSql =
                    "UPDATE books " +
                    "SET status='Borrowed' " +
                    "WHERE book_id=?";

                PreparedStatement updatePst =
                    con.prepareStatement(
                        updateSql
                    );

                updatePst.setInt(
                    1,
                    bookId
                );

                updatePst.executeUpdate();

                con.close();

                JOptionPane.showMessageDialog(
                    this,
                    "Book borrowed successfully!"
                );

            } catch (
                NumberFormatException ex
            ) {

                JOptionPane.showMessageDialog(
                    this,
                    "Please enter a valid Book ID."
                );

            } catch (Exception ex) {

                JOptionPane.showMessageDialog(
                    this,
                    "Error borrowing book: "
                    + ex.getMessage()
                );
            }
        });

        // =========================
        // RETURN BOOK
        // FUNCTIONALITY UNCHANGED
        // =========================

        returnBtn.addActionListener(e -> {

            try {

                Connection con =
                    DBConnection.getConnection();

                String sql =
                    "SELECT t.transaction_id, " +
                    "t.book_id, b.title " +
                    "FROM transactions t " +
                    "JOIN books b " +
                    "ON t.book_id=b.book_id " +
                    "WHERE t.member_id=? " +
                    "AND t.status='Borrowed'";

                PreparedStatement pst =
                    con.prepareStatement(sql);

                pst.setInt(
                    1,
                    memberId
                );

                ResultSet rs =
                    pst.executeQuery();

                DefaultListModel<String> listModel =
                    new DefaultListModel<>();

                while (rs.next()) {

                    listModel.addElement(
                        rs.getInt(
                            "transaction_id"
                        )
                        + " - "
                        + rs.getString(
                            "title"
                        )
                    );
                }

                con.close();

                if (listModel.isEmpty()) {

                    JOptionPane.showMessageDialog(
                        this,
                        "You have no borrowed books."
                    );

                    return;
                }

                JList<String> list =
                    new JList<>(listModel);

                int result =
                    JOptionPane.showConfirmDialog(
                        this,
                        new JScrollPane(list),
                        "Select Book to Return",
                        JOptionPane.OK_CANCEL_OPTION
                    );

                if (result !=
                    JOptionPane.OK_OPTION) {

                    return;
                }

                int selectedIndex =
                    list.getSelectedIndex();

                if (selectedIndex == -1) {

                    JOptionPane.showMessageDialog(
                        this,
                        "Please select a book."
                    );

                    return;
                }

                String selected =
                    listModel.getElementAt(
                        selectedIndex
                    );

                int transactionId =
                    Integer.parseInt(
                        selected.split(" - ")[0]
                    );

                returnBook(transactionId);

            } catch (Exception ex) {

                JOptionPane.showMessageDialog(
                    this,
                    "Error loading borrowed books: "
                    + ex.getMessage()
                );
            }
        });

        // =========================
        // HISTORY
        // FUNCTIONALITY UNCHANGED
        // =========================

        historyBtn.addActionListener(e -> {

            try {

                Connection con =
                    DBConnection.getConnection();

                String sql =
                    "SELECT t.transaction_id, " +
                    "t.book_id, b.title, " +
                    "t.borrow_date, " +
                    "t.return_date, t.status " +
                    "FROM transactions t " +
                    "JOIN books b " +
                    "ON t.book_id=b.book_id " +
                    "WHERE t.member_id=? " +
                    "ORDER BY t.transaction_id DESC";

                PreparedStatement pst =
                    con.prepareStatement(sql);

                pst.setInt(
                    1,
                    memberId
                );

                ResultSet rs =
                    pst.executeQuery();

                StringBuilder history =
                    new StringBuilder();

                while (rs.next()) {

                    history.append(
                        "Transaction ID: "
                    );

                    history.append(
                        rs.getInt(
                            "transaction_id"
                        )
                    );

                    history.append(
                        "\nBook: "
                    );

                    history.append(
                        rs.getString(
                            "title"
                        )
                    );

                    history.append(
                        "\nBorrow Date: "
                    );

                    history.append(
                        rs.getDate(
                            "borrow_date"
                        )
                    );

                    history.append(
                        "\nReturn Date: "
                    );

                    history.append(
                        rs.getDate(
                            "return_date"
                        )
                    );

                    history.append(
                        "\nStatus: "
                    );

                    history.append(
                        rs.getString(
                            "status"
                        )
                    );

                    history.append(
                        "\n\n"
                    );
                }

                con.close();

                if (history.length() == 0) {

                    history.append(
                        "No borrowing history found."
                    );
                }

                JTextArea area =
                    new JTextArea(
                        history.toString()
                    );

                area.setEditable(false);

                area.setFont(
                    new Font(
                        "Arial",
                        Font.PLAIN,
                        14
                    )
                );

                JOptionPane.showMessageDialog(
                    this,
                    new JScrollPane(area),
                    "Borrowing History",
                    JOptionPane.INFORMATION_MESSAGE
                );

            } catch (Exception ex) {

                JOptionPane.showMessageDialog(
                    this,
                    "Error loading history: "
                    + ex.getMessage()
                );
            }
        });

        // =========================
        // PROFILE
        // FUNCTIONALITY UNCHANGED
        // =========================

        profileBtn.addActionListener(e -> {

            try {

                Connection con =
                    DBConnection.getConnection();

                String sql =
                    "SELECT * FROM members " +
                    "WHERE member_id=?";

                PreparedStatement pst =
                    con.prepareStatement(sql);

                pst.setInt(
                    1,
                    memberId
                );

                ResultSet rs =
                    pst.executeQuery();

                if (rs.next()) {

                    JTextField nameField =
                        new JTextField(
                            rs.getString("name")
                        );

                    JTextField emailField =
                        new JTextField(
                            rs.getString("email")
                        );

                    JPasswordField passwordField =
                        new JPasswordField(
                            rs.getString("password")
                        );

                    JPanel panel =
                        new JPanel(
                            new GridLayout(
                                3,
                                2,
                                8,
                                8
                            )
                        );

                    panel.add(
                        new JLabel("Name:")
                    );

                    panel.add(nameField);

                    panel.add(
                        new JLabel("Email:")
                    );

                    panel.add(emailField);

                    panel.add(
                        new JLabel("Password:")
                    );

                    panel.add(passwordField);

                    int result =
                        JOptionPane.showConfirmDialog(
                            this,
                            panel,
                            "My Profile",
                            JOptionPane.OK_CANCEL_OPTION
                        );

                    if (result ==
                        JOptionPane.OK_OPTION) {

                        String updateSql =
                            "UPDATE members " +
                            "SET name=?, email=?, " +
                            "password=? " +
                            "WHERE member_id=?";

                        PreparedStatement updatePst =
                            con.prepareStatement(
                                updateSql
                            );

                        updatePst.setString(
                            1,
                            nameField.getText()
                        );

                        updatePst.setString(
                            2,
                            emailField.getText()
                        );

                        updatePst.setString(
                            3,
                            new String(
                                passwordField
                                    .getPassword()
                            )
                        );

                        updatePst.setInt(
                            4,
                            memberId
                        );

                        updatePst.executeUpdate();

                        JOptionPane.showMessageDialog(
                            this,
                            "Profile updated successfully!"
                        );
                    }
                }

                con.close();

            } catch (Exception ex) {

                JOptionPane.showMessageDialog(
                    this,
                    "Error loading profile: "
                    + ex.getMessage()
                );
            }
        });

        // =========================
        // LOGOUT
        // FUNCTIONALITY UNCHANGED
        // =========================

        logoutBtn.addActionListener(e -> {

            new LoginFrame();

            dispose();
        });

        // =========================
        // ADD COMPONENTS
        // =========================

        mainPanel.add(
            headerPanel,
            BorderLayout.NORTH
        );

        mainPanel.add(
            buttonPanel,
            BorderLayout.CENTER
        );

        mainPanel.add(
            footer,
            BorderLayout.SOUTH
        );

        add(mainPanel);

        setVisible(true);
    }

    // =========================
    // RETURN BOOK METHOD
    // FUNCTIONALITY UNCHANGED
    // =========================

    void returnBook(int transactionId) {

        try {

            Connection con =
                DBConnection.getConnection();

            String bookIdSql =
                "SELECT book_id FROM transactions " +
                "WHERE transaction_id=?";

            PreparedStatement bookIdPst =
                con.prepareStatement(bookIdSql);

            bookIdPst.setInt(
                1,
                transactionId
            );

            ResultSet rs =
                bookIdPst.executeQuery();

            int bookId = 0;

            if (rs.next()) {

                bookId =
                    rs.getInt("book_id");
            }

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

            String bookSql =
                "UPDATE books " +
                "SET status='Available' " +
                "WHERE book_id=?";

            PreparedStatement bookPst =
                con.prepareStatement(bookSql);

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

        } catch (Exception ex) {

            JOptionPane.showMessageDialog(
                this,
                "Error returning book: "
                + ex.getMessage()
            );
        }
    }
}