public enum UserType {
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
