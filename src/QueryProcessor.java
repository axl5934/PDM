import java.sql.*;

public class QueryProcessor {

    Connection connection;

    QueryProcessor(Connection connection){
        this.connection = connection;
    }

    enum State{
        AL("Alabama"), AK("Alaska"), AZ("Arizona"), AR("Arkansas"), CA("California"),
        CO("Colorado"), CT("Connecticut"), DE("Delaware"), FL("Florida"), GA("Georgia"),
        HI("Hawaii"), ID("Idaho"), IL("Illinois"), IN("Indiana"), IA("Iowa"),
        KS("Kansas"), KY("Kentucky"), LA("Louisiana"), ME("Maine"), MD("Maryland"),
        MA("Massachussetts"), MI("Michigan"), MN("Minnesota"), MS("Mississippi"), MO("Missouri"),
        MT("Montana"), NE("Nebraska"), NV("Nevada"), NH("New Hampshire"), NJ("New Jersey"),
        NM("New Mexico"), NY("New York"), NC("North Carolina"), ND("North Dakota"), OH("Ohio"),
        OK("Oklahoma"), OR("Oregon"), PA("Pennsylvania"), RI("Rhode Island"), SC("South Carolina"),
        SD("South Dakota"), TN("Tennessee"), TX("Texas"), UT("Utah"), VT("Vermont"),
        VA("Virginia"), WA("Washington"), WV("West Virginia"), WI("Wisconsin"), WY("Wyoming");

        private final String name;

        State(final String name){
            this.name = name;
        }

        public String toString(){
            return this.name;
        }
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

    void processQuery(String prompt){

    }

    void filterByPrice(float price, String operator){
        PreparedStatement prepSt = null;
        ResultSet rs = null;
        try{
            this.connection.setAutoCommit(false);
            prepSt = this.connection.prepareStatement("SELECT price FROM property WHERE " +
                    "price " + operator + " " + price + ";"); //update later for multiple operators
            rs = prepSt.executeQuery();
            this.connection.commit();

            if(!rs.next()){
                System.out.println("No listings found");
            }
            while(rs.next()){
                System.out.println(rs.getString("price"));
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
    }

    void displayProperty(String conditions[]){
        String statement = "SELECT * FROM Property Where ";
        for(int i = 0; i < conditions.length; i++){
            statement += conditions[i] = " and ";
        }
        statement += ";";
        PreparedStatement prepSt = null;
        ResultSet rs = null;
        try{
            this.connection.setAutoCommit(false);
            prepSt = this.connection.prepareStatement(statement);

            rs = prepSt.executeQuery();
            this.connection.commit();
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
    }

    void filterByState(State state, String operator){
        PreparedStatement prepSt = null;
        ResultSet rs = null;
        try{
            this.connection.setAutoCommit(false);
            prepSt = this.connection.prepareStatement("SELECT state FROM address WHERE " +
                    "state " + operator + " " + state + ";"); //update later for multiple operators or states
            rs = prepSt.executeQuery();
            this.connection.commit();

            if(!rs.next()){
                System.out.println("No listings found");
            }
            while(rs.next()){
                System.out.println(rs.getString("state"));
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
    }

}
