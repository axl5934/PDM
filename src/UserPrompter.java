import java.sql.*;
import java.util.Scanner;

public class UserPrompter {

    private final String URL = "jdbc:postgresql://reddwarf/?currentSchema=";//WHAT IS THE ADDRESS OF THE POSTGRESQL DATABASE???????
    private Connection connection;

    private Scanner scanner;
    private QueryProcessor processor;

    private final int ID_LENGTH = 8;
    private Person person;

    UserPrompter(){
        this.processor = new QueryProcessor(this.connection);

        this.scanner = new Scanner(System.in);

        boolean logInSuccess = false;
        while(!logInSuccess){
            try{
                this.logIn();
                logInSuccess = true;
            } catch(SQLException ex){
                System.out.println(ex.getMessage());
            }
        }
    }

    void logIn() throws SQLException{
        System.out.println("Enter user ID: ");
        String username = this.scanner.nextLine();

        System.out.println("Enter password: ");
        String password = this.scanner.nextLine();

        //try to connect to sql
        this.connection = DriverManager.getConnection(this.URL,
                            username, password);

        Person.UserType userType;
        int userID;
        while(true) {
            System.out.println("Enter user type (\"agent\", " +
                    "\"customer\", or \"agent\": ");
            String type = this.scanner.nextLine();
            userType = Person.UserType.valueOf(type);

            System.out.println("Enter user ID: ");
            userID = this.scanner.nextInt();
            if (String.valueOf(userID).length() != ID_LENGTH) {
                System.out.println("Error: user ID must be 8 digits long.");
                continue;
            }

            if(this.processor.checkUserInTable(userID, userType)){
                System.out.println("No " + userType + " with given user ID.");
            }
            else{
                break;
            }
        }

        this.person = Person.createPerson(userID, userType);
    }

    void prompt() throws SQLException{
        String query;
        do {
            System.out.println(">> ");
            query = this.scanner.nextLine();
            this.person.processQuery(query);
        } while(query.compareTo("quit") != 0);
    }

    void logOut() throws SQLException{
        this.connection.close();
    }

    public static void main(String args[]){
        UserPrompter prompter = new UserPrompter();

        try{
            prompter.prompt();
        } catch(SQLException ex){
            System.out.println(ex.getMessage());
        }

        try{
            prompter.logOut();
        } catch(SQLException ex){
            System.out.println(ex.getMessage());
        }

        prompter.scanner.close();
    }
}
