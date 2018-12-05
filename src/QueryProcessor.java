import java.math.BigDecimal;
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
            case "rgsoffice":
                registerOffice(options);
                break;
            case "uoffice":
                updateOffice(options);
                break;
            case "rmvoffice":
                removeOffice(options);
                break;
            case "rgsaddress":
                registerAddress(options);
                break;
            case "rmvaddress":
                removeAddress(options);
                break;
            case "rgsproperty":
                registerProperty(options);
                break;
            case "uproperty":
                registerProperty(options);
                break;
            case "rmvproperty":
                registerProperty(options);
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
                int type = meta.getColumnType(i);
                if (type == Types.VARCHAR || type == Types.CHAR) {
                    String value = rs.getString(i);
                    System.out.println(value + " " + meta.getColumnName(i));
                } else if(type == Types.INTEGER) {
                    Integer value = rs.getInt(i);
                    System.out.println(value + " " + meta.getColumnName(i));
                } else if(type == Types.BOOLEAN) {
                    Boolean value = rs.getBoolean(i);
                    System.out.println(value + " " + meta.getColumnName(i));
                } else if(type == Types.FLOAT) {
                    Float value = rs.getFloat(i);
                    System.out.println(value + " " + meta.getColumnName(i));
                } else {
                    Long value = rs.getLong(i);
                    System.out.println(value + " " + meta.getColumnName(i));
                }
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


    void closeCall(CallableStatement callSt) throws SQLException {
        if(callSt != null){
            callSt.close();
        }
    }


    //done, needs testing
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


    //done, needs testing
    void displayProperty(String conditions[]){
        if(!checkPermissions(UserType.CUSTOMER, "dproperty")){
            return;
        }

        String statement = "SELECT * FROM property_for_sale";
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
                            prepSt.setObject(valueIdx, value, Types.OTHER);
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
                        default:
                            throw new SQLException("Error: unknown attribute given");
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


    //done, needs testing
    void listProperty(String[] options){
        if(!checkPermissions(UserType.MANAGER, "lproperty")) {
            return;
        }

        String statement = "SELECT * FROM price_Address";
        int numOptions = options.length;
        boolean dOpt = false;
        boolean sOpt = false;
        if(options != null) {
            if (Arrays.asList(options).contains("-d")) {
                dOpt = true;
                numOptions--;
            }

            if(Arrays.asList(options).contains("-s")){
                sOpt = true;
                numOptions--;
            }

            if(dOpt && sOpt){
                statement = "SELECT * FROM for_sale_property_address";
            }
            else if(dOpt){
                statement = "SELECT * FROM Property_Address";
            }
            else if(sOpt){
                statement = "SELECT * FROM for_sale_price_address";
            }

            if(numOptions > 0) {
                statement += " WHERE";
                boolean first = true;
                for(int i = 0; i < options.length; i++) {
                    if(options[i].equals("-d") || options[i].equals("-s")){
                        continue;
                    }
                    String[] parts = parseConditional(options[i]);
                    String attribute = parts[0];
                    String operator = parts[1];
                    if(first) {
                        first = false;
                    }
                    else{
                        statement += " AND";
                    }
                    statement += " " + attribute + " " + operator + " ?";
                }
            }
        }
        statement += ";";

        PreparedStatement prepSt = null;
        ResultSet rs = null;

        try{
            prepSt = this.connection.prepareStatement(statement);
            int valueIdx = 1;
            for(int i=0; i<options.length; i++){
                if(options[i].equals("-s") || options[i].equals("-d")){
                    continue;
                }

                String[] parts = parseConditional(options[i]);
                String attribute = parts[0];
                String value = parts[1];

                switch(attribute){
                    case "country":
                        prepSt.setString(valueIdx, value);
                        break;
                    case "state":
                        prepSt.setString(valueIdx, value);
                        break;
                    case "street":
                        prepSt.setString(valueIdx, value);
                        break;
                    case "zip":
                        prepSt.setString(valueIdx, value);
                        break;
                    default:
                        throw new SQLException("Error: unknown attribute given");
                }

                valueIdx++;
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


    //done, needs testing
    void displaySale(String[] options){
        if(!checkPermissions(UserType.MANAGER, "dsale")){
            return;
        }

        String statement = "SELECT * FROM Sale";
        int numOptions = options.length;

        boolean aOpt = false;
        boolean tOpt = false;
        if(options != null){
            if(Arrays.asList(options).contains("-a")) {
                aOpt = true;
                numOptions--;
            }

            if(Arrays.asList(options).contains("-t")){
                tOpt = true;
                numOptions--;
            }

            if(numOptions > 0){
                statement += " WHERE";
                String option;
                String[] parts;
                String attribute;
                String operator;
                boolean first = true;
                for(int i=0; i<options.length; i++){
                    option = options[i];
                    if(option.equals("-a") || option.equals("-t")){
                        continue;
                    }
                    else{
                        parts = parseConditional(options[i]);
                        attribute = parts[0];
                        operator = parts[1];
                        if(first){
                            first = false;
                        }
                        else {
                            statement += " AND";
                        }
                        statement += " " + attribute + " " + operator + "?";
                    }
                }
            }
        }
        statement += ";";

        PreparedStatement prepSt = null;
        ResultSet rs = null;
        try{
            prepSt = this.connection.prepareStatement(statement);

            String option;
            String[] parts;
            String attribute;
            String value;
            int valueIdx = 1;
            for(int i=0; i<options.length; i++){
                option = options[i];
                if(option.equals("-a") || option.equals("-t")){
                    continue;
                }

                parts = parseConditional(option);
                attribute = parts[0];
                value = parts[2];

                switch(attribute){
                    case "country":
                        prepSt.setString(valueIdx, value);
                        break;
                    case "state":
                        prepSt.setString(valueIdx, value);
                        break;
                    case "street":
                        prepSt.setString(valueIdx, value);
                        break;
                    case "zip":
                        prepSt.setString(valueIdx, value);
                        break;
                    default:
                        throw new SQLException("Error: unknown attribute given");
                }
            }
            rs = prepSt.executeQuery();

            if(aOpt || tOpt){
                int rows = 0;
                BigDecimal total = BigDecimal.ZERO;
                while(rs.next()){
                    total = total.add(rs.getBigDecimal("price"));
                    rows++;
                }
                rs.beforeFirst();

                if(aOpt){
                    BigDecimal averageSale = total.divide(BigDecimal.valueOf(rows));
                    System.out.printf("Average sale of filtered " +
                            "property: %.2f\n", averageSale);
                }

                if(tOpt){
                    System.out.printf("Total sales of filtered " +
                            "sales: %.2f\n", total);
                }
            }
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


    //done, needs testing
    void close(String[] options){
        //check permissions
        if(!checkPermissions(UserType.MANAGER, "close")){
            return;
        }

        String statement = "{CALL close(?)}";

        CallableStatement callSt = null;
        try{
            this.connection.setAutoCommit(false);

            callSt = this.connection.prepareCall(statement);
            int offerID = Integer.parseInt(parseConditional(options[0])[2]);
            callSt.setInt(1, offerID);
            callSt.execute();

            this.connection.commit();

            System.out.println("Close successful.");
        } catch(SQLException ex1){
            System.out.println(ex1.getMessage());
            try {
                this.connection.rollback();
            } catch(SQLException exRoll){
                System.out.println(exRoll.getMessage());
            }
        } finally {
            try {
                closeCall(callSt);
            } catch (SQLException ex2){
                System.out.println(ex2.getMessage());
            }
        }
    }


    //done, needs testing
    void registerOffer(String[] options){
        //check permissions
        if(!checkPermissions(UserType.AGENT, "rgsoffer")){
            return;
        }

        String statement = "{CALL rgsoffer(?, ?, ?, ?, ?)}";

        CallableStatement callSt = null;
        try{
            this.connection.setAutoCommit(false);
            callSt = this.connection.prepareCall(statement);

            int buyerID = Integer.parseInt(parseConditional(options[0])[2]);
            callSt.setInt(1, buyerID);

            int sellerID = Integer.parseInt(parseConditional(options[1])[2]);
            callSt.setInt(2, sellerID);

            int propertyID = Integer.parseInt(parseConditional(options[2])[2]);
            callSt.setInt(3, propertyID);

            int agentID = Integer.parseInt(parseConditional(options[3])[2]);
            callSt.setInt(4, agentID);

            float priceOffer = Float.parseFloat(parseConditional(options[4])[2]);
            callSt.setFloat(5, priceOffer);

            callSt.execute();

            this.connection.commit();

            System.out.println("Successfully registered offer.");
        } catch(SQLException ex1){
            System.out.println(ex1.getMessage());
        } finally {
            try {
                closeCall(callSt);
            } catch (SQLException ex2){
                System.out.println(ex2.getMessage());
            }
        }
    }


    //done, needs testing
    void updateOffer(String[] options){
        //check permissions
        if(!checkPermissions(UserType.AGENT, "uoffer")){
            return;
        }

        String statement = "{CALL uoffer(?, ?))";

        CallableStatement callSt = null;

        try{
            this.connection.setAutoCommit(false);

            callSt = this.connection.prepareCall(statement);

            int offerID = Integer.parseInt(parseConditional(options[0])[2]);
            float priceOffer = Float.parseFloat(parseConditional(options[1])[2]);
            callSt.setInt(1, offerID);
            callSt.setFloat(2, priceOffer);
            callSt.execute();

            this.connection.commit();
            System.out.println("Update successful.");
        } catch(SQLException ex1){
            System.out.println(ex1.getMessage());
        } finally {
            try {
                closeCall(callSt);
            } catch (SQLException ex2){
                System.out.println(ex2.getMessage());
            }
        }
    }


    //done, needs testing
    void removeOffer(String[] options){
        //check permissions
        if(!checkPermissions(UserType.AGENT, "rmvoffer")){
            return;
        }

        String statement = "{CALL rmvoffer(?)}";

        CallableStatement callSt = null;
        try{
            this.connection.setAutoCommit(false);

            callSt = this.connection.prepareCall(statement);
            int offerID = Integer.parseInt(parseConditional(options[0])[2]);
            callSt.setInt(1, offerID);
            callSt.execute();

            this.connection.commit();
            System.out.println("Successfully removed offer.");
        } catch(SQLException ex1){
            System.out.println(ex1.getMessage());
        } finally {
            try {
                closeCall(callSt);
            } catch (SQLException ex2){
                System.out.println(ex2.getMessage());
            }
        }
    }


    void monthSaleTotal(){
        if(!checkPermissions(UserType.MANAGER, "monthSaleTotal")) {
            return;
        }

        String statement = "SELECT * FROM office_monthly_sale;";

        PreparedStatement prepSt = null;
        ResultSet rs = null;
        try{
            prepSt = this.connection.prepareStatement(statement);
            //fill in ? if needed
            rs = prepSt.executeQuery();
            printResultSet(rs);
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


    void agentMostPropertySold(){
        if(!checkPermissions(UserType.MANAGER, "agentMostPropertySold")) {
            return;
        }

        String statement = "SELECT * FROM agent_sold_most_property;";

        PreparedStatement prepSt = null;
        ResultSet rs = null;
        try{
            prepSt = this.connection.prepareStatement(statement);
            //fill in ? if needed
            rs = prepSt.executeQuery();
            printResultSet(rs);
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


    void mostExpensiveProperty(){
        if(!checkPermissions(UserType.MANAGER, "mostExpensiveProperty")) {
            return;
        }

        String statement = "SELECT * FROM most_expensive_property;";

        PreparedStatement prepSt = null;
        ResultSet rs = null;
        try{
            prepSt = this.connection.prepareStatement(statement);
            //fill in ? if needed
            rs = prepSt.executeQuery();
            printResultSet(rs);
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


    void officeMostSale(){
        if(!checkPermissions(UserType.MANAGER, "officeMostSale")) {
            return;
        }

        String statement = "SELECT * FROM office_with_most_sale;";

        PreparedStatement prepSt = null;
        ResultSet rs = null;
        try{
            prepSt = this.connection.prepareStatement(statement);
            //fill in ? if needed
            rs = prepSt.executeQuery();
            printResultSet(rs);
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


    void agentPrimaryOffice(){
        if(!checkPermissions(UserType.MANAGER, "agentPrimaryOffice")) {
            return;
        }

        String statement = "SELECT * FROM agent_primary_office;";

        PreparedStatement prepSt = null;
        ResultSet rs = null;
        try{
            prepSt = this.connection.prepareStatement(statement);
            //fill in ? if needed
            rs = prepSt.executeQuery();
            printResultSet(rs);
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

    void registerOffice(String[] options){
        if(!checkPermissions(UserType.MANAGER, "rgsoffice")) {
            return;
        }

        String statement = "INSERT INTO office (";

        String[] parts1 = parseConditional(options[0]);
        String attribute1 = parts1[0];
        String value1 = parts1[2];
        statement += attribute1 + ", ";

        String[] parts2 = parseConditional(options[1]);
        String attribute2 = parts2[0];
        String value2 = parts2[2];
        statement += attribute2 +") ";

        statement += "VALUES (?,?,?);";

        PreparedStatement prepSt = null;
        ResultSet rs = null;
        try{
            prepSt = this.connection.prepareStatement(statement);
            switch (attribute1) {
                case "phoneNum":
                    prepSt.setString(1, value1);
                    break;
                case "locationID":
                    prepSt.setInt(1, Integer.parseInt(value1));
                    break;
                default:
                    throw new SQLException("Error: unknown attribute given");
            }
            switch (attribute2) {
                case "phoneNum":
                    prepSt.setString(2, value2);
                    break;
                case "locationID":
                    prepSt.setInt(2, Integer.parseInt(value2));
                    break;
                default:
                    throw new SQLException("Error: unknown attribute given");
            }
            prepSt.setBoolean(3, true);
            rs = prepSt.executeQuery();
            printResultSet(rs);
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

    void updateOffice(String[] options){
        if(!checkPermissions(UserType.MANAGER, "uoffice")) {
            return;
        }

        if(options == null){
            System.out.println("Error: not enough parameters");
            return;
        }

        String statement = "UPDATE office SET ";

        String[] parts1 = parseConditional(options[0]);
        String attribute = parts1[0];
        String operator = parts1[1];
        String value = parts1[2];

        String[] parts2;

        for(int i = 1; i < options.length; i++) {

            if(i > 1){
                statement += ", ";
            }
            parts2 = parseConditional(options[i]);
            statement += parts2[0] + parts2[1] + "?";
        }

        statement += " WHERE " + attribute + operator + "?;";

        PreparedStatement prepSt = null;
        ResultSet rs = null;
        try{
            prepSt = this.connection.prepareStatement(statement);
            int j;
            for(j = 1; j < options.length; j++) {
                parts2 = parseConditional(options[j]);
                switch (parts2[0]) {
                    case "phoneNum":
                        prepSt.setString(j, parts2[2]);
                        break;
                    case "locationID":
                        prepSt.setInt(j, Integer.parseInt(parts2[2]));
                        break;
                    case "active":
                        prepSt.setBoolean(j, Boolean.parseBoolean(parts2[2]));
                        break;
                    default:
                        throw new SQLException("Error: unknown attribute given");
                }
            }
            prepSt.setInt(j, Integer.parseInt(value));
            rs = prepSt.executeQuery();
            printResultSet(rs);
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

    void removeOffice(String[] options){
        if(!checkPermissions(UserType.MANAGER, "rmvoffice")) {
            return;
        }

        if(options == null){
            System.out.println("Error: not enough parameters");
            return;
        }

        String statement = "DELETE FROM office WHERE ";

        String[] parts = parseConditional(options[0]);
        String attribute = parts[0];
        String operator = parts[1];
        String value = parts[2];

        statement += attribute + operator + "?;";

        PreparedStatement prepSt = null;
        ResultSet rs = null;
        try{
            prepSt = this.connection.prepareStatement(statement);
            prepSt.setInt(1, Integer.parseInt(value));
            rs = prepSt.executeQuery();
            printResultSet(rs);
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

    void registerAddress(String[] options){
        if(!checkPermissions(UserType.MANAGER, "rgsaddress")) {
            return;
        }

        if(options == null){
            System.out.println("Error: not enough parameters");
            return;
        }

        String statement = "INSERT INTO address (";

        String[] parts;

        for(int i = 0; i < options.length; i++) {

            if(i > 0){
                statement += ", ";
            }
            parts = parseConditional(options[i]);
            statement += parts[0];
        }

        statement += ") VALUES (";

        for(int i = 0; i < options.length; i++) {

            if(i > 0){
                statement += ",";
            }
            statement += "?";
        }

        statement += ");";

        PreparedStatement prepSt = null;
        ResultSet rs = null;
        try{
            prepSt = this.connection.prepareStatement(statement);
            int j;
            for(j = 1; j <= options.length; j++) {
                parts = parseConditional(options[j-1]);
                switch (parts[0]) {
                    case "street":
                        prepSt.setString(j, parts[2]);
                        break;
                    case "zip":
                        prepSt.setString(j, parts[2]);
                        break;
                    case "state":
                        prepSt.setString(j, parts[2]);
                        break;
                    case "aptNum":
                        prepSt.setInt(j, Integer.parseInt(parts[2]));
                        break;
                    case "number":
                        prepSt.setInt(j, Integer.parseInt(parts[2]));
                        break;
                    default:
                        throw new SQLException("Error: unknown attribute given");
                }
            }
            rs = prepSt.executeQuery();
            printResultSet(rs);
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

    void removeAddress(String[] options){
        if(!checkPermissions(UserType.MANAGER, "rmvaddress")) {
            return;
        }

        if(options == null){
            System.out.println("Error: not enough parameters");
            return;
        }

        String statement = "DELETE FROM address WHERE ";

        String[] parts = parseConditional(options[0]);
        String attribute = parts[0];
        String operator = parts[1];
        String value = parts[2];

        statement += attribute + operator + "?;";

        PreparedStatement prepSt = null;
        ResultSet rs = null;
        try{
            prepSt = this.connection.prepareStatement(statement);
            prepSt.setInt(1, Integer.parseInt(value));
            System.out.println(prepSt.toString());
            rs = prepSt.executeQuery();
            printResultSet(rs);
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

    void registerProperty(String[] options){
        if(!checkPermissions(UserType.MANAGER, "rgsproperty")) {
            return;
        }

        if(options == null){
            System.out.println("Error: not enough parameters");
            return;
        }

        String statement = "INSERT INTO property (";

        String[] parts;

        for(int i = 0; i < options.length; i++) {

            if(i > 0){
                statement += ", ";
            }
            parts = parseConditional(options[i]);
            statement += parts[0];
        }

        statement += ") VALUES (";

        for(int i = 0; i < options.length; i++) {

            if(i > 0){
                statement += ",";
            }
            statement += "?";
        }

        statement += ");";

        PreparedStatement prepSt = null;
        ResultSet rs = null;
        try{
            prepSt = this.connection.prepareStatement(statement);
            int j;
            for(j = 1; j <= options.length; j++) {
                parts = parseConditional(options[j-1]);
                switch (parts[0]) {
                    case "price":
                        prepSt.setObject(j, parts[2], Types.OTHER);
                        break;
                    case "locationID":
                        prepSt.setInt(j, Integer.parseInt(parts[2]));
                        break;
                    case "squareFoot":
                        prepSt.setObject(j, parts[2], Types.DECIMAL);
                        break;
                    case "forSale":
                        prepSt.setBoolean(j, Boolean.parseBoolean(parts[2]));
                        break;
                    default:
                        throw new SQLException("Error: unknown attribute given");
                }
            }
            rs = prepSt.executeQuery();
            printResultSet(rs);
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

    void updateProperty(String[] options){
        if(!checkPermissions(UserType.MANAGER, "uproperty")) {
            return;
        }

        if(options == null){
            System.out.println("Error: not enough parameters");
            return;
        }

        String statement = "UPDATE property SET ";

        String[] parts1 = parseConditional(options[0]);
        String attribute = parts1[0];
        String operator = parts1[1];
        String value = parts1[2];

        String[] parts2;

        for(int i = 1; i < options.length; i++) {

            if(i > 1){
                statement += ", ";
            }
            parts2 = parseConditional(options[i]);
            statement += parts2[0] + parts2[1] + "?";
        }

        statement += " WHERE " + attribute + operator + "?;";

        PreparedStatement prepSt = null;
        ResultSet rs = null;
        try{
            prepSt = this.connection.prepareStatement(statement);
            int j;
            for(j = 1; j < options.length; j++) {
                parts2 = parseConditional(options[j]);
                switch (parts2[0]) {
                    case "price":
                        prepSt.setObject(j, parts2[2], Types.OTHER);
                        break;
                    case "locationID":
                        prepSt.setInt(j, Integer.parseInt(parts2[2]));
                        break;
                    case "squareFoot":
                        prepSt.setObject(j, parts2[2], Types.DECIMAL);
                        break;
                    case "forSale":
                        prepSt.setBoolean(j, Boolean.parseBoolean(parts2[2]));
                        break;
                    default:
                        throw new SQLException("Error: unknown attribute given");
                }
            }
            prepSt.setInt(j, Integer.parseInt(value));
            rs = prepSt.executeQuery();
            printResultSet(rs);
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

    void removeProperty(String[] options){
        if(!checkPermissions(UserType.MANAGER, "rmvproperty")) {
            return;
        }

        if(options == null){
            System.out.println("Error: not enough parameters");
            return;
        }

        String statement = "DELETE FROM property WHERE ";

        String[] parts = parseConditional(options[0]);
        String attribute = parts[0];
        String operator = parts[1];
        String value = parts[2];

        statement += attribute + operator + "?;";

        PreparedStatement prepSt = null;
        ResultSet rs = null;
        try{
            prepSt = this.connection.prepareStatement(statement);
            prepSt.setInt(1, Integer.parseInt(value));
            System.out.println(prepSt.toString());
            rs = prepSt.executeQuery();
            printResultSet(rs);
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
        if(!checkPermissions(UserType.AGENT, "rgsoffer")) {
            return;
        }

        if(options == null){
            System.out.println("Error: not enough parameters");
            return;
        }

        String statement = "INSERT INTO offer (";

        String[] parts;

        for(int i = 0; i < options.length; i++) {

            if(i > 0){
                statement += ", ";
            }
            parts = parseConditional(options[i]);
            statement += parts[0];
        }

        statement += ") VALUES (";

        for(int i = 0; i < options.length; i++) {

            if(i > 0){
                statement += ",";
            }
            statement += "?";
        }

        statement += ");";

        PreparedStatement prepSt = null;
        ResultSet rs = null;
        try{
            prepSt = this.connection.prepareStatement(statement);
            int j;
            for(j = 1; j <= options.length; j++) {
                parts = parseConditional(options[j-1]);
                switch (parts[0]) {
                    case "buyerID":
                        prepSt.setInt(j, Integer.parseInt(parts[2]));
                        break;
                    case "sellerID":
                        prepSt.setInt(j, Integer.parseInt(parts[2]));
                        break;
                    case "propertyID":
                        prepSt.setInt(j, Integer.parseInt(parts[2]));
                        break;
                    default:
                        throw new SQLException("Error: unknown attribute given");
                }
            }
            rs = prepSt.executeQuery();
            printResultSet(rs);
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
        if(!checkPermissions(UserType.AGENT, "uoffer")) {
            return;
        }

        if(options == null){
            System.out.println("Error: not enough parameters");
            return;
        }

        String statement = "UPDATE property SET ";

        String[] parts1 = parseConditional(options[0]);
        String attribute = parts1[0];
        String operator = parts1[1];
        String value = parts1[2];

        String[] parts2;

        for(int i = 1; i < options.length; i++) {

            if(i > 1){
                statement += ", ";
            }
            parts2 = parseConditional(options[i]);
            statement += parts2[0] + parts2[1] + "?";
        }

        statement += " WHERE " + attribute + operator + "?;";

        PreparedStatement prepSt = null;
        ResultSet rs = null;
        try{
            prepSt = this.connection.prepareStatement(statement);
            int j;
            for(j = 1; j < options.length; j++) {
                parts2 = parseConditional(options[j]);
                switch (parts2[0]) {
                    case "priceOffer":
                        prepSt.setObject(j, parts2[2], Types.OTHER);
                        break;
                    case "buyerID":
                        prepSt.setInt(j, Integer.parseInt(parts2[2]));
                        break;
                    case "sellerID":
                        prepSt.setInt(j, Integer.parseInt(parts2[2]));
                        break;
                    case "locationID":
                        prepSt.setInt(j, Integer.parseInt(parts2[2]));
                        break;
                    default:
                        throw new SQLException("Error: unknown attribute given");
                }
            }
            prepSt.setInt(j, Integer.parseInt(value));
            rs = prepSt.executeQuery();
            printResultSet(rs);
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
        if(!checkPermissions(UserType.AGENT, "rmvoffer")) {
            return;
        }

        if(options == null){
            System.out.println("Error: not enough parameters");
            return;
        }

        String statement = "DELETE FROM offer WHERE ";

        String[] parts = parseConditional(options[0]);
        String attribute = parts[0];
        String operator = parts[1];
        String value = parts[2];

        statement += attribute + operator + "?;";

        PreparedStatement prepSt = null;
        ResultSet rs = null;
        try{
            prepSt = this.connection.prepareStatement(statement);
            prepSt.setInt(1, Integer.parseInt(value));
            System.out.println(prepSt.toString());
            rs = prepSt.executeQuery();
            printResultSet(rs);
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

}
