public class User {
    // A class to create new users with username and password,
    private String password;
    public  String username;

    public void User(String username, String password){
        this.username = username;
        this.password = password;
    }

    public void setPassword(String password){
        this.password = password;
    }
    public String getUsername(){
        return this.username;
    }
}