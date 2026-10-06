import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

public class BookSearch extends JFrame {

    DefaultTableModel model;
    JTable table;
    JTextField searchField;

    BookSearch() {

        setTitle("Search Books");
        setSize(900, 550);
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
            new JLabel("SEARCH BOOKS");

        title.setFont(
            new Font("Arial", Font.BOLD, 22)
        );

        title.setHorizontalAlignment(
            SwingConstants.CENTER
        );

        // SEARCH AREA
        searchField =
            new JTextField();

        JButton searchBtn =
            new JButton("Search");

        JButton refreshBtn =
            new JButton("Show All");

        searchBtn.setFont(
            new Font("Arial", Font.BOLD, 13)
        );

        refreshBtn.setFont(
            new Font("Arial", Font.BOLD, 13)
        );

        JPanel searchPanel =
            new JPanel(
                new BorderLayout(10, 10)
            );

        searchPanel.add(
            new JLabel("Search by Title, Author or Genre:"),
            BorderLayout.WEST
        );

        searchPanel.add(
            searchField,
            BorderLayout.CENTER
        );

        searchPanel.add(
            searchBtn,
            BorderLayout.EAST
        );

        // TABLE
        String[] columns = {
            "Book ID",
            "Title",
            "Author",
            "ISBN",
            "Genre",
            "Status"
        };

        model =
            new DefaultTableModel(
                columns,
                0
            );

        table =
            new JTable(model);

        table.setRowHeight(25);

        JScrollPane scrollPane =
            new JScrollPane(table);

        // BOTTOM PANEL
        JPanel bottomPanel =
            new JPanel(
                new FlowLayout(
                    FlowLayout.CENTER
                )
            );

        bottomPanel.add(refreshBtn);

        // MAIN LAYOUT
        mainPanel.add(
            title,
            BorderLayout.NORTH
        );

        JPanel topPanel =
            new JPanel(
                new BorderLayout(10, 10)
            );

        topPanel.add(
            searchPanel,
            BorderLayout.CENTER
        );

        mainPanel.add(
            topPanel,
            BorderLayout.BEFORE_FIRST_LINE
        );

        mainPanel.add(
            scrollPane,
            BorderLayout.CENTER
        );

        mainPanel.add(
            bottomPanel,
            BorderLayout.SOUTH
        );

        add(mainPanel);

        // LOAD ALL BOOKS
        loadBooks("");

        // SEARCH
        searchBtn.addActionListener(e -> {

            String keyword =
                searchField.getText();

            loadBooks(keyword);
        });

        // SHOW ALL
        refreshBtn.addActionListener(e -> {

            searchField.setText("");

            loadBooks("");
        });

        // ENTER KEY FOR SEARCH
        searchField.addActionListener(e -> {

            String keyword =
                searchField.getText();

            loadBooks(keyword);
        });

        setVisible(true);
    }

    void loadBooks(String keyword) {

        model.setRowCount(0);

        String sql =
            "SELECT * FROM books " +
            "WHERE title LIKE ? " +
            "OR author LIKE ? " +
            "OR genre LIKE ?";

        try {

            Connection con =
                DBConnection.getConnection();

            PreparedStatement pst =
                con.prepareStatement(sql);

            String searchKeyword =
                "%" + keyword + "%";

            pst.setString(
                1,
                searchKeyword
            );

            pst.setString(
                2,
                searchKeyword
            );

            pst.setString(
                3,
                searchKeyword
            );

            ResultSet rs =
                pst.executeQuery();

            while (rs.next()) {

                model.addRow(
                    new Object[] {

                        rs.getInt(
                            "book_id"
                        ),

                        rs.getString(
                            "title"
                        ),

                        rs.getString(
                            "author"
                        ),

                        rs.getString(
                            "isbn"
                        ),

                        rs.getString(
                            "genre"
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
                "Error searching books: " +
                e.getMessage()
            );
        }
    }
}