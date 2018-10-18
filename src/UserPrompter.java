import java.sql.*;
import java.util.Scanner;

public class UserPrompter {

    private final String URL = "who knows";
    private Connection connection;

    private Scanner scanner;
    private QueryReader reader;
    private QueryProcessor writer;
    private TableParser parser;

    private final int ID_LENGTH = 8;
    private int userID;
    private UserType userType;
    private Person person;

    UserPrompter(){
        this.reader = new QueryReader();
        this.writer = new QueryProcessor();
        this.parser = new TableParser();

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

        while(true) {
            System.out.println("Enter user type: ");
            String userType = this.scanner.nextLine();
            this.userType = UserType.valueOf(userType);

            System.out.println("Enter user ID: ");
            this.userID = this.scanner.nextInt();
            if (String.valueOf(this.userID).length() != ID_LENGTH) {
                System.out.println("Error: user ID must be 8 digits long.");
                continue;
            }

            if(!this.writer.checkUserInTable(this.userID)){
                System.out.println("No " + this.userType + " with given user ID.");
            }
            else{
                break;
            }
        }

        this.person = new Person(this.userID, this.userType);
    }

    boolean prompt() throws SQLException{
        String prompt = this.scanner.nextLine();

        return true;
    }

    void logOut() throws SQLException{
        this.connection.close();
    }

    public static void main(String args[]){
        UserPrompter prompter = new UserPrompter();

        boolean contPrompt = true;
        while(contPrompt){
            try{
                contPrompt = prompter.prompt();
            } catch(SQLException ex){
                System.out.println(ex.getMessage());
            }
        }

        try{
            prompter.logOut();
        } catch(SQLException ex){
            System.out.println(ex.getMessage());
        }

        prompter.scanner.close();
    }
}
