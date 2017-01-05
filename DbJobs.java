
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 *
 * @author Дима
 */
public class DbJobs {
    Connection connection;
    private final String host = "jdbc:mysql://localhost:3306/test";
    private final String user = "root";
    private final String dbPassword = "";
    private final String insertMsg = "INSERT INTO messages (fromWho,toWho,message) VALUES(?,?,?)";
    private final String authQuery = "SELECT COUNT(*),id FROM users WHERE login=? AND password=?";
    private final String friendsQuery = "SELECT friendId FROM friends WHERE id=?";
    private final String loadMsgQuery = "SELECT * FROM messages WHERE (fromWho=? OR fromWho=?) AND (toWho=? OR toWho=?)";
    
    public DbJobs(){
        try{
            connection = DriverManager.getConnection(host, user, dbPassword);
        }
        catch (SQLException sqlEx) {
            sqlEx.printStackTrace();
        }
    }
    
    public boolean close(){
        try{
            connection.close();
        }
        catch (SQLException sqlEx) {
            sqlEx.printStackTrace();
            return false;
        }
        return true;
    }
    
    public void insertMsg(int from, int to, String msg){
        try{
            PreparedStatement stmt = connection.prepareStatement(insertMsg);
            stmt.setInt(1,from);
            stmt.setInt(2,to);
            stmt.setString(3,msg);
            stmt.executeUpdate();
        }
        catch (SQLException sqlEx) {
            sqlEx.printStackTrace();
        }
    }
    
    public String getFriendsList(OneConnection o){
        String result = "";
        try {
            PreparedStatement stmt = connection.prepareStatement(friendsQuery);
            stmt.setInt(1, o.getUid());
            ResultSet rs = stmt.executeQuery();
            while(rs.next()){
                result+=rs.getInt("friendId")+" ";
            }
        }
        catch (SQLException sqlEx) {
            sqlEx.printStackTrace();
            return result;
        }
        return result;
    }
    
    public String getMessages(OneConnection o, int to){
        String result = "";
        try {
            PreparedStatement stmt = connection.prepareStatement(loadMsgQuery);
            stmt.setInt(1, o.getUid());
            stmt.setInt(2, to);
            stmt.setInt(3, o.getUid());
            stmt.setInt(4, to);
            ResultSet rs = stmt.executeQuery();
            while(rs.next()){
                result+=rs.getInt("fromWho")+" "+rs.getString("message")+"!!!!!";
            }
        }
        catch (SQLException sqlEx) {
            sqlEx.printStackTrace();
            return result;
        }
        return result;
    }

    public boolean connect(OneConnection o, String login, String password){
        try {
            PreparedStatement stmt = connection.prepareStatement(authQuery);
            stmt.setString(1, login);
            stmt.setString(2, password);
            ResultSet rs = stmt.executeQuery();
            rs.next();
            if(rs.getInt(1)==0){
                return false;
            }
            o.setUid(rs.getInt(2));
        }
        catch (SQLException sqlEx) {
            sqlEx.printStackTrace();
            return false;
        }
        return true;
    }
}
