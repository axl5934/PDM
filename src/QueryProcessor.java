import java.sql.*;

public class QueryProcessor {

    Connection connection;

    QueryProcessor(Connection connection){
        this.connection = connection;
    }

    ResultSet checkUserInTable(int userId, Person.UserType userType){
        PreparedStatement prepSt = null;
        ResultSet rs = null;
        try{
            this.connection.setAutoCommit(false);
            prepSt = this.connection.prepareStatement("SELECT userID FROM "
                    + userType + " WHERE userID = " + userId + ";");
            rs = prepSt.executeQuery();

        } catch(SQLException ex){
            //squash
        } finally{
            try {
                if (prepSt != null) {
                    prepSt.close();
                }
                if (rs != null) {
                    rs.close();
                }
            } catch (SQLException ex){
                //squash
            }

        }
    }
}
