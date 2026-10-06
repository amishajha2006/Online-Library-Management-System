import javax.swing.*;
import java.awt.*;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

public class LoginFrame extends JFrame {

    JTextField username;
    JPasswordField password;

    LoginFrame() {

        setTitle("Online Library Management System");
        setSize(850, 520);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setResizable(false);

        // MAIN PANEL
        JPanel mainPanel =
            new JPanel(new GridLayout(1, 2));

        // =========================
        // LEFT SIDE
        // =========================

        JPanel leftPanel =
            new JPanel();

        leftPanel.setLayout(
            new BoxLayout(
                leftPanel,
                BoxLayout.Y_AXIS
            )
        );

        leftPanel.setBorder(
            BorderFactory.createEmptyBorder(
                60, 40, 60, 40
            )
        );

        JLabel libraryIcon =
            new JLabel("📚");

        libraryIcon.setFont(
            new Font("Segoe UI Emoji", Font.PLAIN, 55)
        );

        libraryIcon.setAlignmentX(
            Component.CENTER_ALIGNMENT
        );

        JLabel welcome =
            new JLabel(
                "<html><div style='text-align:center;'>"
                + "ONLINE LIBRARY<br>"
                + "MANAGEMENT SYSTEM"
                + "</div></html>"
            );

        welcome.setFont(
            new Font("Arial", Font.BOLD, 25)
        );

        welcome.setAlignmentX(
            Component.CENTER_ALIGNMENT
        );

        JLabel description =
            new JLabel(
                "<html><div style='text-align:center;'>"
                + "Manage books, members and<br>"
                + "library transactions easily."
                + "</div></html>"
            );

        description.setFont(
            new Font("Arial", Font.PLAIN, 15)
        );

        description.setAlignmentX(
            Component.CENTER_ALIGNMENT
        );

        leftPanel.add(libraryIcon);
        leftPanel.add(Box.createVerticalStrut(25));
        leftPanel.add(welcome);
        leftPanel.add(Box.createVerticalStrut(20));
        leftPanel.add(description);

        // =========================
        // RIGHT SIDE
        // =========================

        JPanel rightPanel =
            new JPanel();

        rightPanel.setLayout(
            new BoxLayout(
                rightPanel,
                BoxLayout.Y_AXIS
            )
        );

        rightPanel.setBorder(
            BorderFactory.createEmptyBorder(
                55, 55, 55, 55
            )
        );

        JLabel loginTitle =
            new JLabel("Welcome Back!");

        loginTitle.setFont(
            new Font("Arial", Font.BOLD, 27)
        );

        loginTitle.setAlignmentX(
            Component.CENTER_ALIGNMENT
        );

        JLabel loginSubtitle =
            new JLabel(
                "Login to continue"
            );

        loginSubtitle.setFont(
            new Font("Arial", Font.PLAIN, 14)
        );

        loginSubtitle.setAlignmentX(
            Component.CENTER_ALIGNMENT
        );

        // USERNAME
        JLabel userLabel =
            new JLabel("Username / Email");

        userLabel.setFont(
            new Font("Arial", Font.BOLD, 13)
        );

        userLabel.setAlignmentX(
            Component.LEFT_ALIGNMENT
        );

        username =
            new JTextField();

        username.setMaximumSize(
            new Dimension(
                Integer.MAX_VALUE,
                38
            )
        );

        username.setFont(
            new Font("Arial", Font.PLAIN, 14)
        );

        // PASSWORD
        JLabel passLabel =
            new JLabel("Password");

        passLabel.setFont(
            new Font("Arial", Font.BOLD, 13)
        );

        passLabel.setAlignmentX(
            Component.LEFT_ALIGNMENT
        );

        password =
            new JPasswordField();

        password.setMaximumSize(
            new Dimension(
                Integer.MAX_VALUE,
                38
            )
        );

        password.setFont(
            new Font("Arial", Font.PLAIN, 14)
        );

        // BUTTONS
        JButton librarianBtn =
            new JButton("Librarian Login");

        JButton memberBtn =
            new JButton("Member Login");

        Font buttonFont =
            new Font(
                "Arial",
                Font.BOLD,
                13
            );

        librarianBtn.setFont(buttonFont);
        memberBtn.setFont(buttonFont);

        librarianBtn.setAlignmentX(
            Component.CENTER_ALIGNMENT
        );

        memberBtn.setAlignmentX(
            Component.CENTER_ALIGNMENT
        );

        librarianBtn.setMaximumSize(
            new Dimension(
                Integer.MAX_VALUE,
                42
            )
        );

        memberBtn.setMaximumSize(
            new Dimension(
                Integer.MAX_VALUE,
                42
            )
        );

        // INFORMATION
        JLabel info =
            new JLabel(
                "<html><div style='text-align:center;'>"
                + "Librarian: admin<br>"
                + "Member: registered email"
                + "</div></html>"
            );

        info.setFont(
            new Font(
                "Arial",
                Font.ITALIC,
                12
            )
        );

        info.setAlignmentX(
            Component.CENTER_ALIGNMENT
        );

        // =========================
        // LIBRARIAN LOGIN
        // FUNCTIONALITY UNCHANGED
        // =========================

        librarianBtn.addActionListener(e -> {

            String user =
                username.getText();

            String pass =
                new String(
                    password.getPassword()
                );

            if (user.equals("admin") &&
                pass.equals("admin123")) {

                new LibrarianDashboard();
                dispose();

            } else {

                JOptionPane.showMessageDialog(
                    this,
                    "Invalid librarian username or password!"
                );
            }
        });

        // =========================
        // MEMBER LOGIN
        // FUNCTIONALITY UNCHANGED
        // =========================

        memberBtn.addActionListener(e -> {

            String email =
                username.getText();

            String pass =
                new String(
                    password.getPassword()
                );

            String sql =
                "SELECT * FROM members " +
                "WHERE email=? AND password=?";

            try {

                Connection con =
                    DBConnection.getConnection();

                PreparedStatement pst =
                    con.prepareStatement(sql);

                pst.setString(1, email);
                pst.setString(2, pass);

                ResultSet rs =
                    pst.executeQuery();

                if (rs.next()) {

                    JOptionPane.showMessageDialog(
                        this,
                        "Login successful! Welcome "
                        + rs.getString("name")
                    );

                    new MemberDashboard(
                        rs.getInt("member_id")
                    );

                    dispose();

                } else {

                    JOptionPane.showMessageDialog(
                        this,
                        "Invalid email or password!"
                    );
                }

                con.close();

            } catch (Exception ex) {

                JOptionPane.showMessageDialog(
                    this,
                    "Login error: "
                    + ex.getMessage()
                );
            }
        });

        // =========================
        // ADD COMPONENTS
        // =========================

        rightPanel.add(loginTitle);
        rightPanel.add(
            Box.createVerticalStrut(8)
        );

        rightPanel.add(loginSubtitle);
        rightPanel.add(
            Box.createVerticalStrut(30)
        );

        rightPanel.add(userLabel);
        rightPanel.add(
            Box.createVerticalStrut(7)
        );

        rightPanel.add(username);
        rightPanel.add(
            Box.createVerticalStrut(18)
        );

        rightPanel.add(passLabel);
        rightPanel.add(
            Box.createVerticalStrut(7)
        );

        rightPanel.add(password);
        rightPanel.add(
            Box.createVerticalStrut(28)
        );

        rightPanel.add(librarianBtn);
        rightPanel.add(
            Box.createVerticalStrut(12)
        );

        rightPanel.add(memberBtn);
        rightPanel.add(
            Box.createVerticalStrut(25)
        );

        rightPanel.add(info);

        mainPanel.add(leftPanel);
        mainPanel.add(rightPanel);

        add(mainPanel);

        setVisible(true);
    }
}