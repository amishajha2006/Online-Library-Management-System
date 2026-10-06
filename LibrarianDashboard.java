import javax.swing.*;
import java.awt.*;

public class LibrarianDashboard extends JFrame {

    LibrarianDashboard() {

        setTitle("Librarian Dashboard");
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
                "LIBRARIAN DASHBOARD"
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
                "Online Library Management System"
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

        JButton bookBtn =
            new JButton(
                "<html><center>📚<br>Book Management</center></html>"
            );

        JButton memberBtn =
            new JButton(
                "<html><center>👥<br>Member Management</center></html>"
            );

        JButton transactionBtn =
            new JButton(
                "<html><center>🔄<br>Transaction Management</center></html>"
            );

        JButton notificationBtn =
            new JButton(
                "<html><center>🔔<br>Notifications</center></html>"
            );

        JButton reportBtn =
            new JButton(
                "<html><center>📊<br>Inventory Reports</center></html>"
            );

        JButton logoutBtn =
            new JButton(
                "<html><center>🚪<br>Logout</center></html>"
            );

        JButton[] buttons = {
            bookBtn,
            memberBtn,
            transactionBtn,
            notificationBtn,
            reportBtn,
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

        buttonPanel.add(bookBtn);
        buttonPanel.add(memberBtn);
        buttonPanel.add(transactionBtn);
        buttonPanel.add(notificationBtn);
        buttonPanel.add(reportBtn);
        buttonPanel.add(logoutBtn);

        // =========================
        // FOOTER
        // =========================

        JLabel footer =
            new JLabel(
                "Library Administration Panel"
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
        // BOOK MANAGEMENT
        // FUNCTIONALITY UNCHANGED
        // =========================

        bookBtn.addActionListener(e ->
            new BookManagement()
        );

        // =========================
        // MEMBER MANAGEMENT
        // FUNCTIONALITY UNCHANGED
        // =========================

        memberBtn.addActionListener(e ->
            new MemberManagement()
        );

        // =========================
        // TRANSACTION MANAGEMENT
        // FUNCTIONALITY UNCHANGED
        // =========================

        transactionBtn.addActionListener(e ->
            new TransactionManagement()
        );

        // =========================
        // NOTIFICATIONS
        // FUNCTIONALITY UNCHANGED
        // =========================

        notificationBtn.addActionListener(e -> {

            try {

                java.sql.Connection con =
                    DBConnection.getConnection();

                String sql =
                    "SELECT b.title, m.name, t.borrow_date " +
                    "FROM transactions t " +
                    "JOIN books b ON t.book_id = b.book_id " +
                    "JOIN members m ON t.member_id = m.member_id " +
                    "WHERE t.status='Borrowed'";

                java.sql.PreparedStatement pst =
                    con.prepareStatement(sql);

                java.sql.ResultSet rs =
                    pst.executeQuery();

                StringBuilder notifications =
                    new StringBuilder();

                while (rs.next()) {

                    notifications.append(
                        "Book: "
                    );

                    notifications.append(
                        rs.getString("title")
                    );

                    notifications.append(
                        "\nMember: "
                    );

                    notifications.append(
                        rs.getString("name")
                    );

                    notifications.append(
                        "\nBorrowed on: "
                    );

                    notifications.append(
                        rs.getDate("borrow_date")
                    );

                    notifications.append(
                        "\n\n"
                    );
                }

                con.close();

                if (notifications.length() == 0) {

                    JOptionPane.showMessageDialog(
                        this,
                        "No active borrowed books."
                    );

                } else {

                    JTextArea textArea =
                        new JTextArea(
                            notifications.toString()
                        );

                    textArea.setEditable(false);

                    textArea.setFont(
                        new Font(
                            "Arial",
                            Font.PLAIN,
                            14
                        )
                    );

                    JOptionPane.showMessageDialog(
                        this,
                        new JScrollPane(textArea),
                        "Notifications",
                        JOptionPane.INFORMATION_MESSAGE
                    );
                }

            } catch (Exception ex) {

                JOptionPane.showMessageDialog(
                    this,
                    "Error loading notifications: "
                    + ex.getMessage()
                );
            }
        });

        // =========================
        // INVENTORY REPORTS
        // FUNCTIONALITY UNCHANGED
        // =========================

        reportBtn.addActionListener(e ->
            new InventoryReports()
        );

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
}