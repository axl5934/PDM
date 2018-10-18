import java.sql.*;

public class QueryProcessor {

    Connection connection;

    QueryProcessor(Connection connection){
        this.connection = connection;
    }

    boolean checkUserInTable(int userId, Person.UserType userType){
        PreparedStatement prepSt = null;
        ResultSet rs = null;
        try{
            this.connection.setAutoCommit(false);
            prepSt = this.connection.prepareStatement("SELECT userID FROM "
                    + userType + " WHERE userID = " + userId + ";");
            rs = prepSt.executeQuery();
            this.connection.commit();

            if(rs.next()){
                return true;
            }
            else{
                return false;
            }


        } catch(SQLException ex1){
            try {
                this.connection.rollback();
            } catch (SQLException ex2){
                //squash
            }
        } finally{
            try {
                if (prepSt != null) {
                    prepSt.close();
                }
                if (rs != null) {
                    rs.close();
                }
            } catch (SQLException ex3){
                //squash
            }

        }

        return false;
    }

}
