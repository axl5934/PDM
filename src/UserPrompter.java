import java.sql.*;
import java.util.Scanner;

public class UserPrompter {

    private final String URL = "jdbc:postgresql://reddwarf.cs.rit.edu/?currentSchema=public";

    private Connection connection;
    private Scanner scanner;
    private QueryProcessor processor;

    UserPrompter(){
        this.scanner = new Scanner(System.in);

        this.connection = null;
        while(this.connection == null) {
            System.out.print("Enter username: ");
            String username = this.scanner.nextLine();

            System.out.print("Enter password: ");
            String password = this.scanner.nextLine();

            //try to connect to sql
            try {
                this.connection = DriverManager.getConnection(this.URL,
                        username, password);
                break;
            } catch (SQLException ex) {
                System.out.println(ex.getMessage());
                this.connection = null;
            }
        }

        UserType userType;
        while(true) {
            System.out.print("Enter user type (\"AGENT\", " +
                    "\"CUSTOMER\", or \"MANAGER\"): ");
            String type = this.scanner.nextLine();
            try {
                userType = UserType.valueOf(type);
                break;
            } catch(Exception ex){
                System.out.println("Error: invalid user type.");
            }
        }

        this.processor = new QueryProcessor(this.connection, userType);
    }


    void prompt(){
        String query;
        do {
            System.out.print(">> ");
            query = this.scanner.nextLine();
            if(query.compareTo("quit") == 0){
                break;
            }
            this.processor.processQuery(query);
        } while(true);
    }


    void logOut() throws SQLException{
        this.connection.close();
    }


    public static void main(String args[]){
        UserPrompter prompter = new UserPrompter();
        prompter.prompt();

        try{
            prompter.logOut();
        } catch(SQLException ex){
            System.out.println(ex.getMessage());
        }

        prompter.scanner.close();
    }
}
