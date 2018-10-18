import java.sql.*;
import java.util.Scanner;

public class UserPrompter {
    private final String URL = "who knows";
    private Connection connection;

    private final int ID_LENGTH = 8;
    private int userID;

    UserPrompter(){
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
        //get user credentials
        Scanner sc = new Scanner(System.in);

        System.out.println("Enter user ID: ");
        String username = sc.nextLine();

        System.out.println("Enter password: ");
        String password = sc.nextLine();

        //try to connect to sql
        this.connection = DriverManager.getConnection(this.URL,
                            username, password);

        //close scanner
        sc.close();
    }

    boolean prompt(){
        return true;
    }

    boolean logOut() {
        return true;
    }

    public static void main(String args[]){
        UserPrompter prompter = new UserPrompter();

        while(prompter.prompt()){
            //keep prompting until the user
            //asks to stop prompting
        }

        if(!prompter.logOut()){
            //something went wrong while logging out
        }
    }
}
