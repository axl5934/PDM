import java.sql.*;
import java.util.Arrays;

public class QueryProcessor {

    private final UserType userType;
    private Connection connection;

    //done
    QueryProcessor(Connection connection, UserType userType){
        this.connection = connection;
        this.userType = userType;
    }

    //done
    void processQuery(String prompt) {
        String[] tokens = prompt.split(" ");
        String[] options;
        if(tokens.length > 1) {
            options = Arrays.copyOfRange(tokens, 1, tokens.length);
        }
        else{
            options = null;
        }

        switch(tokens[0]){
            case "dproperty":
                displayProperty(options);
                break;
            case "lproperty":
                listProperty(options);
                break;
            case "dsale":
                displaySale(options);
                break;
            case "close":
                close(options);
                break;
            case "rgsoffer":
                registerOffer(options);
                break;
            case "uoffer":
                updateOffer(options);
                break;
            case "rmvoffer":
                removeOffer(options);
                break;
            case "monthSaleTotal":
                monthSaleTotal();
                break;
            case "agentMostPropertySold":
                agentMostPropertySold();
                break;
            case "mostExpensiveProperty":
                mostExpensiveProperty();
                break;
            case "officeMostSale":
                officeMostSale();
                break;
            case "agentPrimaryOffice":
                agentPrimaryOffice();
                break;
            default:
                System.out.println("Error: unknown command");
                break;
        }

    }


    //done
    boolean checkPermissions(UserType userType, String action){
        if(this.userType != userType){
            System.out.println("Permission denied: " +
                    this.userType.toString() +
                    " cannot use " + action  + ".");
            return false;
        }

        return true;
    }


    void printResultSet(ResultSet rs) throws SQLException{
        ResultSetMetaData meta = rs.getMetaData();
        int cols = meta.getColumnCount();
        while(rs.next()){
            for(int i=1; i<=cols; i++){
                if(i>1){
                    System.out.print(", ");
                }
                String value = rs.getString(i);
                System.out.println(value + " " + meta.getColumnName(i));
            }
            System.out.println("");
        }
    }


    void closeSQL(PreparedStatement prepSt, ResultSet rs) throws SQLException{
        if (prepSt != null) {
            prepSt.close();
        }
        if (rs != null) {
            rs.close();
        }
    }


    //done
    String[] parseConditional(String conditional){
        int attrStartIdx = 0;
        int attrEndIdx = 0;
        int operaterEndIdx = 0;
        for(int i=0; i<conditional.length(); i++){
            char c = conditional.charAt(i);
            if(c == '-' && i<2){
                attrStartIdx++;
                continue;
            }
            else if(c == '=') {
                attrEndIdx = i-1;
                operaterEndIdx = i;
                break;
            }
            else if(c == '<' || c == '>' || c == '!'){
                attrEndIdx = i-1;
                c = conditional.charAt(i+1);
                if(c == '='){
                    operaterEndIdx = i+1;
                    break;
                }
                else{
                    operaterEndIdx = i;
                    break;
                }
            }
        }

        String attribute = conditional.substring(attrStartIdx, attrEndIdx+1);
        String operator = conditional.substring(attrEndIdx+1, operaterEndIdx+1);
        String value = conditional.substring(operaterEndIdx+1, conditional.length());

        String parts[]= {attribute, operator, value};

        return parts;
    }


    //need to print result
    void displayProperty(String conditions[]){
        if(!checkPermissions(UserType.CUSTOMER, "dproperty")){
            return;
        }

        String statement = "SELECT * FROM Property_ForSale";
        if(conditions != null) {
            String[] parts = parseConditional(conditions[0]);
            String attribute = parts[0];
            String operator = parts[1];
            statement += " WHERE " + attribute + " " + operator + " ?";
            for(int i = 1; i < conditions.length; i++){
                parts = parseConditional(conditions[i]);
                attribute = parts[0];
                operator = parts[1];
                statement += " and " + attribute + " " + operator + " ?";
            }
        }
        statement += ";";

        PreparedStatement prepSt = null;
        ResultSet rs = null;

        try{
            prepSt = this.connection.prepareStatement(statement);
            if(conditions != null) {
                for (int i = 0; i < conditions.length; i++) {
                    String parts[] = parseConditional(conditions[i]);

                    String attribute = parts[0];

                    String value = parts[2];
                    int valueIdx = i+1;
                    switch (attribute) {
                        case "price":
                            prepSt.setString(valueIdx, value);
                            break;
                        case "country":
                            prepSt.setString(valueIdx, value);
                            break;
                        case "squareFoot":
                            prepSt.setInt(valueIdx, Integer.parseInt(value));
                            break;
                        case "street":
                            prepSt.setString(valueIdx, value);
                            break;
                        case "zip":
                            prepSt.setString(valueIdx, value);
                            break;
                        case "state":
                            prepSt.setString(valueIdx, value);
                            break;
                    }
                }
            }
            rs = prepSt.executeQuery();

            if(!rs.next()){
                System.out.println("No listings found");
            }
            else {
                printResultSet(rs);
            }

        } catch(SQLException ex1){
            System.out.println(ex1.getMessage());
        } finally{
            try {
                closeSQL(prepSt, rs);
            } catch (SQLException ex3){
                System.out.println(ex3.getMessage());
            }

        }
    }


    //working on
    void listProperty(String[] options){
        if(!checkPermissions(UserType.MANAGER, "lproperty")) {
            return;
        }

        String statement = "SELECT * FROM price_Address";
        int numOptions = options.length;
        if(options != null) {
            if (Arrays.asList(options).contains("d")) {
                statement = "SELECT * Property_Address";
                numOptions--;
            }

            if(numOptions > 0) {
                statement += " WHERE";
                for (int i = 0; i < numOptions; i++) {
                    String option = options[i];
                    if (option.equals("s")) {
                        statement += " forSale = true";
                    }
                    else{
                        String operator = parseConditional(options[i])[1];
                        statement += " ? " + operator + " ?";
                    }
                }
            }
        }
        statement += ";";

        PreparedStatement prepSt = null;
        ResultSet rs = null;

        try{
            prepSt = this.connection.prepareStatement(statement);
            for(int i=0; i<numOptions; i++){
                String[] parts = parseConditional(options[i]);
            }
            rs = prepSt.executeQuery();

            if(!rs.next()){
                System.out.println("No listings found");
            }
            else {
                printResultSet(rs);
            }

        } catch(SQLException ex1){
            System.out.println(ex1.getMessage());
        } finally{
            try {
                closeSQL(prepSt, rs);
            } catch (SQLException ex3){
                System.out.println(ex3.getMessage());
            }

        }

    }


    //working on
    void displaySale(String[] options){
        if(!checkPermissions(UserType.MANAGER, "dsale")){
            return;
        }

        String statement = "SELECT * FROM Sale";
        boolean aOpt = false;
        boolean tOpt = false;
        if(options != null){
            aOpt = Arrays.asList(options).contains("a");
            tOpt = Arrays.asList(options).contains("t");
        }

        PreparedStatement prepSt = null;
        ResultSet rs = null;
        try{
            prepSt = this.connection.prepareStatement(statement);
            //fill in ? if needed
            rs = prepSt.executeQuery();
            //do something with rs
        } catch(SQLException ex1){
            System.out.println(ex1.getMessage());
        } finally {
            try {
                closeSQL(prepSt, rs);
            } catch (SQLException ex2){
                System.out.println(ex2.getMessage());
            }
        }
    }


    void close(String[] options){
        //check permissions
        if(!checkPermissions(UserType.MANAGER, "close")){
            return;
        }

        String statement = "";
        //create the statment

        PreparedStatement prepSt = null;
        ResultSet rs = null;
        try{
            prepSt = this.connection.prepareStatement(statement);
            //fill in ? if needed
            rs = prepSt.executeQuery();
            //do something with rs
        } catch(SQLException ex1){
            System.out.println(ex1.getMessage());
        } finally {
            try {
                closeSQL(prepSt, rs);
            } catch (SQLException ex2){
                System.out.println(ex2.getMessage());
            }
        }
    }


    void registerOffer(String[] options){
        //check permissions
        if(!checkPermissions(UserType.AGENT, "rgsoffer")){
            return;
        }

        String statement = "";
        //create the statment

        PreparedStatement prepSt = null;
        ResultSet rs = null;
        try{
            prepSt = this.connection.prepareStatement(statement);
            //fill in ? if needed
            rs = prepSt.executeQuery();
            //do something with rs
        } catch(SQLException ex1){
            System.out.println(ex1.getMessage());
        } finally {
            try {
                closeSQL(prepSt, rs);
            } catch (SQLException ex2){
                System.out.println(ex2.getMessage());
            }
        }
    }


    void updateOffer(String[] options){
        //check permissions
        if(!checkPermissions(UserType.AGENT, "uoffer")){
            return;
        }

        String statement = "";
        //create the statment

        PreparedStatement prepSt = null;
        ResultSet rs = null;
        try{
            prepSt = this.connection.prepareStatement(statement);
            //fill in ? if needed
            rs = prepSt.executeQuery();
            //do something with rs
        } catch(SQLException ex1){
            System.out.println(ex1.getMessage());
        } finally {
            try {
                closeSQL(prepSt, rs);
            } catch (SQLException ex2){
                System.out.println(ex2.getMessage());
            }
        }
    }


    void removeOffer(String[] options){
        //check permissions
        if(!checkPermissions(UserType.AGENT, "rmvoffer")){
            return;
        }

        String statement = "";
        //create the statment

        PreparedStatement prepSt = null;
        ResultSet rs = null;
        try{
            prepSt = this.connection.prepareStatement(statement);
            //fill in ? if needed
            rs = prepSt.executeQuery();
            //do something with rs
        } catch(SQLException ex1){
            System.out.println(ex1.getMessage());
        } finally {
            try {
                closeSQL(prepSt, rs);
            } catch (SQLException ex2){
                System.out.println(ex2.getMessage());
            }
        }
    }


    void monthSaleTotal(){}


    void agentMostPropertySold(){}


    void mostExpensiveProperty(){}


    void officeMostSale(){}


    void agentPrimaryOffice(){}

}
