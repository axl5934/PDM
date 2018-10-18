public abstract class Person {

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

    private int userId;

    static Person createPerson(int userId, UserType userType){
        switch(userType){
            case AGENT:
                return new Agent(userId);
            case CUSTOMER:
                return new Customer(userId);
            case MANAGER:
                return new Manager(userId);
            default:
                return null;
        }
    }

    int getUserId(){
        return this.userId;
    }

    void setUserId(int userId){
        this.userId = userId;
    }

    abstract void processQuery(String query);

}
