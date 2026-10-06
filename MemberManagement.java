import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.*;

public class MemberManagement extends JFrame {

    JTextField nameField, emailField;
    JPasswordField passwordField;

    JTable table;
    DefaultTableModel model;

    MemberManagement() {

        setTitle("Member Management");
        setSize(900, 620);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);
        setResizable(false);

        JPanel mainPanel =
            new JPanel(new BorderLayout(20, 20));

        mainPanel.setBorder(
            BorderFactory.createEmptyBorder(
                25, 30, 25, 30
            )
        );

        // ================= HEADER =================

        JLabel icon =
            new JLabel("👥");

        icon.setFont(
            new Font(
                "Segoe UI Emoji",
                Font.PLAIN,
                35
            )
        );

        JLabel title =
            new JLabel("MEMBER MANAGEMENT");

        title.setFont(
            new Font(
                "Arial",
                Font.BOLD,
                26
            )
        );

        JLabel subtitle =
            new JLabel(
                "Create, update and manage library members"
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

        // ================= FORM =================

        JPanel formPanel =
            new JPanel(
                new GridLayout(
                    2,
                    3,
                    12,
                    10
                )
            );

        nameField =
            new JTextField();

        emailField =
            new JTextField();

        passwordField =
            new JPasswordField();

        formPanel.add(
            new JLabel("Name")
        );

        formPanel.add(
            new JLabel("Email")
        );

        formPanel.add(
            new JLabel("Password")
        );

        formPanel.add(nameField);
        formPanel.add(emailField);
        formPanel.add(passwordField);

        JPanel formContainer =
            new JPanel(
                new BorderLayout(0, 8)
            );

        formContainer.setBorder(
            BorderFactory.createTitledBorder(
                "Member Details"
            )
        );

        formContainer.add(
            formPanel,
            BorderLayout.CENTER
        );

        // ================= BUTTONS =================

        JButton addBtn =
            new JButton("➕ Add Member");

        JButton updateBtn =
            new JButton("✏️ Update");

        JButton deleteBtn =
            new JButton("🗑️ Delete");

        JButton refreshBtn =
            new JButton("🔄 Refresh");

        JButton[] buttons = {
            addBtn,
            updateBtn,
            deleteBtn,
            refreshBtn
        };

        for (JButton button : buttons) {

            button.setFont(
                new Font(
                    "Arial",
                    Font.BOLD,
                    13
                )
            );

            button.setFocusPainted(false);
        }

        JPanel buttonPanel =
            new JPanel(
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

        model =
            new DefaultTableModel(
                new String[]{
                    "Member ID",
                    "Name",
                    "Email",
                    "Password"
                },
                0
            );

        table =
            new JTable(model);

        table.setRowHeight(28);

        table.setFont(
            new Font(
                "Arial",
                Font.PLAIN,
                13
            )
        );

        table.getTableHeader()
            .setFont(
                new Font(
                    "Arial",
                    Font.BOLD,
                    13
                )
            );

        JScrollPane scrollPane =
            new JScrollPane(table);

        JPanel tablePanel =
            new JPanel(
                new BorderLayout()
            );

        tablePanel.setBorder(
            BorderFactory.createTitledBorder(
                "Registered Members"
            )
        );

        tablePanel.add(
            scrollPane,
            BorderLayout.CENTER
        );

        // ================= ADD MEMBER =================

        addBtn.addActionListener(e -> {

            try {

                Connection con =
                    DBConnection.getConnection();

                String sql =
                    "INSERT INTO members " +
                    "(name, email, password) " +
                    "VALUES (?, ?, ?)";

                PreparedStatement pst =
                    con.prepareStatement(sql);

                pst.setString(
                    1,
                    nameField.getText()
                );

                pst.setString(
                    2,
                    emailField.getText()
                );

                pst.setString(
                    3,
                    new String(
                        passwordField.getPassword()
                    )
                );

                pst.executeUpdate();

                con.close();

                JOptionPane.showMessageDialog(
                    this,
                    "Member added successfully!"
                );

                clearFields();
                loadMembers();

            } catch (Exception ex) {

                JOptionPane.showMessageDialog(
                    this,
                    "Error adding member: "
                    + ex.getMessage()
                );
            }
        });

        // ================= UPDATE MEMBER =================

        updateBtn.addActionListener(e -> {

            int row =
                table.getSelectedRow();

            if (row == -1) {

                JOptionPane.showMessageDialog(
                    this,
                    "Please select a member first."
                );

                return;
            }

            try {

                int memberId =
                    Integer.parseInt(
                        model.getValueAt(
                            row,
                            0
                        ).toString()
                    );

                Connection con =
                    DBConnection.getConnection();

                String sql =
                    "UPDATE members SET " +
                    "name=?, email=?, password=? " +
                    "WHERE member_id=?";

                PreparedStatement pst =
                    con.prepareStatement(sql);

                pst.setString(
                    1,
                    nameField.getText()
                );

                pst.setString(
                    2,
                    emailField.getText()
                );

                pst.setString(
                    3,
                    new String(
                        passwordField.getPassword()
                    )
                );

                pst.setInt(
                    4,
                    memberId
                );

                pst.executeUpdate();

                con.close();

                JOptionPane.showMessageDialog(
                    this,
                    "Member updated successfully!"
                );

                clearFields();
                loadMembers();

            } catch (Exception ex) {

                JOptionPane.showMessageDialog(
                    this,
                    "Error updating member: "
                    + ex.getMessage()
                );
            }
        });

        // ================= DELETE MEMBER =================

        deleteBtn.addActionListener(e -> {

            int row =
                table.getSelectedRow();

            if (row == -1) {

                JOptionPane.showMessageDialog(
                    this,
                    "Please select a member first."
                );

                return;
            }

            int confirm =
                JOptionPane.showConfirmDialog(
                    this,
                    "Are you sure you want to delete this member?",
                    "Confirm Delete",
                    JOptionPane.YES_NO_OPTION
                );

            if (
                confirm != JOptionPane.YES_OPTION
            ) {
                return;
            }

            try {

                int memberId =
                    Integer.parseInt(
                        model.getValueAt(
                            row,
                            0
                        ).toString()
                    );

                Connection con =
                    DBConnection.getConnection();

                String sql =
                    "DELETE FROM members " +
                    "WHERE member_id=?";

                PreparedStatement pst =
                    con.prepareStatement(sql);

                pst.setInt(
                    1,
                    memberId
                );

                pst.executeUpdate();

                con.close();

                JOptionPane.showMessageDialog(
                    this,
                    "Member deleted successfully!"
                );

                clearFields();
                loadMembers();

            } catch (Exception ex) {

                JOptionPane.showMessageDialog(
                    this,
                    "Error deleting member: "
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

                    nameField.setText(
                        model.getValueAt(
                            row,
                            1
                        ).toString()
                    );

                    emailField.setText(
                        model.getValueAt(
                            row,
                            2
                        ).toString()
                    );

                    passwordField.setText(
                        model.getValueAt(
                            row,
                            3
                        ).toString()
                    );
                }
            });

        // ================= REFRESH =================

        refreshBtn.addActionListener(
            e -> loadMembers()
        );

        // ================= LAYOUT =================

        JPanel topPanel =
            new JPanel(
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

        loadMembers();

        setVisible(true);
    }

    // ================= LOAD MEMBERS =================

    void loadMembers() {

        model.setRowCount(0);

        try {

            Connection con =
                DBConnection.getConnection();

            String sql =
                "SELECT * FROM members";

            PreparedStatement pst =
                con.prepareStatement(sql);

            ResultSet rs =
                pst.executeQuery();

            while (rs.next()) {

                model.addRow(
                    new Object[]{
                        rs.getInt("member_id"),
                        rs.getString("name"),
                        rs.getString("email"),
                        rs.getString("password")
                    }
                );
            }

            con.close();

        } catch (Exception ex) {

            JOptionPane.showMessageDialog(
                this,
                "Error loading members: "
                + ex.getMessage()
            );
        }
    }

    void clearFields() {

        nameField.setText("");
        emailField.setText("");
        passwordField.setText("");
    }
}