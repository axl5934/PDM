import java.sql.*;

public class QueryProcessor {

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

    private Connection connection;

    QueryProcessor(Connection connection){
        this.connection = connection;
    }

    void processQuery(String prompt){

    }


    boolean checkUserInTable(int userId, Person.UserType userType){
        PreparedStatement prepSt = null;
        ResultSet rs = null;
        try{
            prepSt = this.connection.prepareStatement("SELECT ?ID FROM "
                    + userType + " WHERE userID = ?;");
            prepSt.setString(1, userType.toString());
            prepSt.setString(2, Integer.toString(userId));
            rs = prepSt.executeQuery();

            if(rs.next()){
                return true;
            }
            else{
                return false;
            }
        } catch(SQLException ex1){
            System.out.println(ex1.getMessage());
        } finally{
            try {
                if (prepSt != null) {
                    prepSt.close();
                }
                if (rs != null) {
                    rs.close();
                }
            } catch (SQLException ex3){
                System.out.println(ex3.getMessage());
            }
        }

        return false;
    }


    void displayProperty(String conditions[]){
        String statement = "SELECT * FROM Property Where ?";
        for(int i = 1; i < conditions.length; i++){
            statement += " and ?";
        }
        statement += ";";
        PreparedStatement prepSt = null;
        ResultSet rs = null;
        try{
            prepSt = this.connection.prepareStatement(statement);
            for(int i=0; i<conditions.length; i++){
                prepSt.setString(i, conditions[i]);
            }

            rs = prepSt.executeQuery();

            if(!rs.next()){
                System.out.println("No listings found");
            }
            while(rs.next()){
                //print row
            }

        } catch(SQLException ex1){
            //catch
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
