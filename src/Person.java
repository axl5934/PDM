public class Person {

    private int userId;
    UserType userType;

    enum UserType{
        AGENT("agent"),
        CUSTOMER("customer"),
        MANAGER("manager");

        private final String type;

        UserType(final String type){
            this.type = type;
        }

        public String toString(){
            return this.type;
        }
    }

    Person(int userId, UserType userType){
        this.userId = userId;
        this.userType = userType;
    }

    public int getUserId(){
        return this.userId;
    }

    public UserType getUserType(){
        return this.userType;
    }
}
