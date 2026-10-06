import javax.swing.*;
import java.awt.*;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

public class InventoryReports extends JFrame {

    JLabel totalBooksValue;
    JLabel availableBooksValue;
    JLabel borrowedBooksValue;
    JLabel totalMembersValue;
    JLabel totalTransactionsValue;

    InventoryReports() {

        setTitle("Inventory Reports");
        setSize(650, 500);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

        JPanel mainPanel =
            new JPanel(new BorderLayout(20, 20));

        mainPanel.setBorder(
            BorderFactory.createEmptyBorder(
                25, 30, 25, 30
            )
        );

        // TITLE
        JLabel title =
            new JLabel("INVENTORY REPORTS");

        title.setFont(
            new Font("Arial", Font.BOLD, 24)
        );

        title.setHorizontalAlignment(
            SwingConstants.CENTER
        );

        JLabel subtitle =
            new JLabel(
                "Library Statistics and Inventory Summary"
            );

        subtitle.setFont(
            new Font("Arial", Font.PLAIN, 14)
        );

        subtitle.setHorizontalAlignment(
            SwingConstants.CENTER
        );

        JPanel headerPanel =
            new JPanel(new GridLayout(2, 1, 5, 5));

        headerPanel.add(title);
        headerPanel.add(subtitle);

        // REPORT PANEL
        JPanel reportPanel =
            new JPanel(
                new GridLayout(
                    5,
                    2,
                    15,
                    15
                )
            );

        reportPanel.setBorder(
            BorderFactory.createTitledBorder(
                "Library Summary"
            )
        );

        JLabel totalBooksLabel =
            new JLabel("Total Books:");

        JLabel availableBooksLabel =
            new JLabel("Available Books:");

        JLabel borrowedBooksLabel =
            new JLabel("Borrowed Books:");

        JLabel totalMembersLabel =
            new JLabel("Total Members:");

        JLabel totalTransactionsLabel =
            new JLabel("Total Transactions:");

        totalBooksValue =
            new JLabel("0");

        availableBooksValue =
            new JLabel("0");

        borrowedBooksValue =
            new JLabel("0");

        totalMembersValue =
            new JLabel("0");

        totalTransactionsValue =
            new JLabel("0");

        Font labelFont =
            new Font("Arial", Font.BOLD, 14);

        Font valueFont =
            new Font("Arial", Font.BOLD, 16);

        totalBooksLabel.setFont(labelFont);
        availableBooksLabel.setFont(labelFont);
        borrowedBooksLabel.setFont(labelFont);
        totalMembersLabel.setFont(labelFont);
        totalTransactionsLabel.setFont(labelFont);

        totalBooksValue.setFont(valueFont);
        availableBooksValue.setFont(valueFont);
        borrowedBooksValue.setFont(valueFont);
        totalMembersValue.setFont(valueFont);
        totalTransactionsValue.setFont(valueFont);

        reportPanel.add(totalBooksLabel);
        reportPanel.add(totalBooksValue);

        reportPanel.add(availableBooksLabel);
        reportPanel.add(availableBooksValue);

        reportPanel.add(borrowedBooksLabel);
        reportPanel.add(borrowedBooksValue);

        reportPanel.add(totalMembersLabel);
        reportPanel.add(totalMembersValue);

        reportPanel.add(totalTransactionsLabel);
        reportPanel.add(totalTransactionsValue);

        // REFRESH BUTTON
        JButton refreshBtn =
            new JButton("Refresh Report");

        refreshBtn.setFont(
            new Font("Arial", Font.BOLD, 14)
        );

        JPanel bottomPanel =
            new JPanel(
                new FlowLayout(
                    FlowLayout.CENTER
                )
            );

        bottomPanel.add(refreshBtn);

        // ADD TO FRAME
        mainPanel.add(
            headerPanel,
            BorderLayout.NORTH
        );

        mainPanel.add(
            reportPanel,
            BorderLayout.CENTER
        );

        mainPanel.add(
            bottomPanel,
            BorderLayout.SOUTH
        );

        add(mainPanel);

        // LOAD REPORT
        loadReport();

        // REFRESH
        refreshBtn.addActionListener(e ->
            loadReport()
        );

        setVisible(true);
    }

    void loadReport() {

        try {

            Connection con =
                DBConnection.getConnection();

            // TOTAL BOOKS
            String bookSql =
                "SELECT COUNT(*) FROM books";

            PreparedStatement bookPst =
                con.prepareStatement(bookSql);

            ResultSet bookRs =
                bookPst.executeQuery();

            if (bookRs.next()) {

                totalBooksValue.setText(
                    String.valueOf(
                        bookRs.getInt(1)
                    )
                );
            }

            // AVAILABLE BOOKS
            String availableSql =
                "SELECT COUNT(*) FROM books " +
                "WHERE status='Available'";

            PreparedStatement availablePst =
                con.prepareStatement(
                    availableSql
                );

            ResultSet availableRs =
                availablePst.executeQuery();

            if (availableRs.next()) {

                availableBooksValue.setText(
                    String.valueOf(
                        availableRs.getInt(1)
                    )
                );
            }

            // BORROWED BOOKS
            String borrowedSql =
                "SELECT COUNT(*) FROM books " +
                "WHERE status='Borrowed'";

            PreparedStatement borrowedPst =
                con.prepareStatement(
                    borrowedSql
                );

            ResultSet borrowedRs =
                borrowedPst.executeQuery();

            if (borrowedRs.next()) {

                borrowedBooksValue.setText(
                    String.valueOf(
                        borrowedRs.getInt(1)
                    )
                );
            }

            // TOTAL MEMBERS
            String memberSql =
                "SELECT COUNT(*) FROM members";

            PreparedStatement memberPst =
                con.prepareStatement(memberSql);

            ResultSet memberRs =
                memberPst.executeQuery();

            if (memberRs.next()) {

                totalMembersValue.setText(
                    String.valueOf(
                        memberRs.getInt(1)
                    )
                );
            }

            // TOTAL TRANSACTIONS
            String transactionSql =
                "SELECT COUNT(*) FROM transactions";

            PreparedStatement transactionPst =
                con.prepareStatement(
                    transactionSql
                );

            ResultSet transactionRs =
                transactionPst.executeQuery();

            if (transactionRs.next()) {

                totalTransactionsValue.setText(
                    String.valueOf(
                        transactionRs.getInt(1)
                    )
                );
            }

            con.close();

        } catch (Exception e) {

            JOptionPane.showMessageDialog(
                this,
                "Error loading report: " +
                e.getMessage()
            );
        }
    }
}