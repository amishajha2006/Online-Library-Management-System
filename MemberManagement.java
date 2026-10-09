
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.*;

public class MemberManagement extends JFrame {

    private JTable table;
    private DefaultTableModel model;

    public MemberManagement() {
        setTitle("Member Management");
        setSize(900, 620);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);
        setResizable(false);

        JPanel mainPanel = new JPanel(new BorderLayout(20, 20));
        mainPanel.setBorder(
            BorderFactory.createEmptyBorder(25, 30, 25, 30)
        );

        // HEADER
        JLabel icon = new JLabel("👥");
        icon.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 35));

        JLabel title = new JLabel("MEMBER MANAGEMENT");
        title.setFont(new Font("Arial", Font.BOLD, 26));

        JLabel subtitle = new JLabel(
            "Create, update and manage library members"
        );
        subtitle.setFont(new Font("Arial", Font.PLAIN, 14));

        JPanel titlePanel = new JPanel(new GridLayout(2, 1, 0, 4));
        titlePanel.add(title);
        titlePanel.add(subtitle);

        JPanel headerPanel = new JPanel(new BorderLayout(15, 0));
        headerPanel.add(icon, BorderLayout.WEST);
        headerPanel.add(titlePanel, BorderLayout.CENTER);

        // BUTTONS
        JButton addBtn = new JButton("➕ Add Member");
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

        // TABLE
        model = new DefaultTableModel(
            new String[]{"Member ID", "Name", "Email", "Password"}, 0
        ) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        table = new JTable(model);
        table.setRowHeight(28);
        table.setFont(new Font("Arial", Font.PLAIN, 13));
        table.getTableHeader().setFont(
            new Font("Arial", Font.BOLD, 13)
        );

        JScrollPane scrollPane = new JScrollPane(table);

        JPanel tablePanel = new JPanel(new BorderLayout());
        tablePanel.setBorder(
            BorderFactory.createTitledBorder("Registered Members")
        );
        tablePanel.add(scrollPane, BorderLayout.CENTER);

        // ADD MEMBER: OPEN SEPARATE FORM
        addBtn.addActionListener(e ->
            openMemberForm(null, "Add Member")
        );

        // UPDATE MEMBER: OPEN SEPARATE FORM
        updateBtn.addActionListener(e -> {
            int row = table.getSelectedRow();

            if (row == -1) {
                JOptionPane.showMessageDialog(
                    this, "Please select a member first."
                );
                return;
            }

            int modelRow = table.convertRowIndexToModel(row);

            Object[] member = {
                model.getValueAt(modelRow, 0),
                model.getValueAt(modelRow, 1),
                model.getValueAt(modelRow, 2),
                model.getValueAt(modelRow, 3)
            };

            openMemberForm(member, "Update Member");
        });

        // DELETE AND REFRESH
        deleteBtn.addActionListener(e -> deleteSelectedMember());
        refreshBtn.addActionListener(e -> loadMembers());

        // LAYOUT
        JPanel topPanel = new JPanel(new BorderLayout(0, 15));
        topPanel.add(headerPanel, BorderLayout.NORTH);

        mainPanel.add(topPanel, BorderLayout.NORTH);
        mainPanel.add(tablePanel, BorderLayout.CENTER);
        mainPanel.add(buttonPanel, BorderLayout.SOUTH);

        add(mainPanel);

        loadMembers();
        setVisible(true);
    }

    // SEPARATE ADD / UPDATE FORM
    private void openMemberForm(Object[] member, String dialogTitle) {

        boolean isUpdate = member != null;

        JDialog dialog = new JDialog(this, dialogTitle, true);
        dialog.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
        dialog.setSize(430, 300);
        dialog.setResizable(false);
        dialog.setLocationRelativeTo(this);

        JTextField nameField = new JTextField(22);
        JTextField emailField = new JTextField(22);
        JPasswordField passwordField = new JPasswordField(22);

        JPanel fieldsPanel = new JPanel(
            new GridLayout(3, 2, 12, 12)
        );
        fieldsPanel.setBorder(
            BorderFactory.createEmptyBorder(20, 20, 10, 20)
        );

        fieldsPanel.add(new JLabel("Name:"));
        fieldsPanel.add(nameField);

        fieldsPanel.add(new JLabel("Email:"));
        fieldsPanel.add(emailField);

        fieldsPanel.add(new JLabel("Password:"));
        fieldsPanel.add(passwordField);

        // Fill existing values when updating
        if (isUpdate) {
            nameField.setText(String.valueOf(member[1]));
            emailField.setText(String.valueOf(member[2]));
            passwordField.setText(String.valueOf(member[3]));
        }

        JButton saveBtn = new JButton("Save");
        JButton cancelBtn = new JButton("Cancel");

        JPanel actionsPanel = new JPanel(
            new FlowLayout(FlowLayout.CENTER, 12, 10)
        );
        actionsPanel.add(saveBtn);
        actionsPanel.add(cancelBtn);

        // SAVE BUTTON
        saveBtn.addActionListener(e -> {

            String name = nameField.getText().trim();
            String email = emailField.getText().trim();
            String password = new String(passwordField.getPassword());

            if (name.isEmpty() || email.isEmpty() || password.isEmpty()) {
                JOptionPane.showMessageDialog(
                    dialog,
                    "Please fill in Name, Email, and Password."
                );
                return;
            }

            String sql = isUpdate
                ? "UPDATE members SET name=?, email=?, password=? WHERE member_id=?"
                : "INSERT INTO members (name, email, password) VALUES (?, ?, ?)";

            try (Connection con = DBConnection.getConnection()) {

                if (con == null) {
                    JOptionPane.showMessageDialog(
                        dialog,
                        "Database connection failed. Check your database configuration."
                    );
                    return;
                }

                try (PreparedStatement pst = con.prepareStatement(sql)) {

                    pst.setString(1, name);
                    pst.setString(2, email);
                    pst.setString(3, password);

                    if (isUpdate) {
                        pst.setInt(
                            4,
                            Integer.parseInt(String.valueOf(member[0]))
                        );
                    }

                    pst.executeUpdate();
                }

                JOptionPane.showMessageDialog(
                    dialog,
                    isUpdate
                        ? "Member updated successfully!"
                        : "Member added successfully!"
                );

                dialog.dispose();
                loadMembers();

            } catch (Exception ex) {
                JOptionPane.showMessageDialog(
                    dialog,
                    "Error " + (isUpdate ? "updating" : "adding")
                        + " member: " + ex.getMessage()
                );
            }
        });

        cancelBtn.addActionListener(e -> dialog.dispose());

        dialog.add(fieldsPanel, BorderLayout.CENTER);
        dialog.add(actionsPanel, BorderLayout.SOUTH);
        dialog.getRootPane().setDefaultButton(saveBtn);
        dialog.setVisible(true);
    }

    // DELETE MEMBER
    private void deleteSelectedMember() {

        int row = table.getSelectedRow();

        if (row == -1) {
            JOptionPane.showMessageDialog(
                this, "Please select a member first."
            );
            return;
        }

        int confirm = JOptionPane.showConfirmDialog(
            this,
            "Are you sure you want to delete this member?",
            "Confirm Delete",
            JOptionPane.YES_NO_OPTION
        );

        if (confirm != JOptionPane.YES_OPTION) {
            return;
        }

        int modelRow = table.convertRowIndexToModel(row);
        int memberId = Integer.parseInt(
            model.getValueAt(modelRow, 0).toString()
        );

        try (Connection con = DBConnection.getConnection()) {

            if (con == null) {
                JOptionPane.showMessageDialog(
                    this,
                    "Database connection failed. Check your database configuration."
                );
                return;
            }

            try (PreparedStatement pst = con.prepareStatement(
                "DELETE FROM members WHERE member_id=?"
            )) {
                pst.setInt(1, memberId);
                pst.executeUpdate();
            }

            JOptionPane.showMessageDialog(
                this, "Member deleted successfully!"
            );

            loadMembers();

        } catch (Exception ex) {
            JOptionPane.showMessageDialog(
                this, "Error deleting member: " + ex.getMessage()
            );
        }
    }

    // LOAD MEMBERS INTO TABLE
    void loadMembers() {

        model.setRowCount(0);

        String sql = "SELECT * FROM members";

        try (Connection con = DBConnection.getConnection()) {

            if (con == null) {
                JOptionPane.showMessageDialog(
                    this,
                    "Database connection failed. Check your database configuration."
                );
                return;
            }

            try (PreparedStatement pst = con.prepareStatement(sql);
                 ResultSet rs = pst.executeQuery()) {

                while (rs.next()) {
                    model.addRow(new Object[]{
                        rs.getInt("member_id"),
                        rs.getString("name"),
                        rs.getString("email"),
                        rs.getString("password")
                    });
                }
            }

        } catch (Exception ex) {
            JOptionPane.showMessageDialog(
                this, "Error loading members: " + ex.getMessage()
            );
        }
    }
}