import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

public class MemberDAO {

    public void displayMembers() {

        String sql = "SELECT * FROM members";

        try {

            Connection con = DBConnection.getConnection();

            PreparedStatement pst =
                con.prepareStatement(sql);

            ResultSet rs = pst.executeQuery();

            while (rs.next()) {

                System.out.println(
                    rs.getInt("member_id") + " | " +
                    rs.getString("name") + " | " +
                    rs.getString("email")
                );
            }

            con.close();

        } catch (Exception e) {

            e.printStackTrace();
        }
    }
}