
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import javax.swing.JOptionPane;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 *
 * @author Никита
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
    private final String findUsers = "SELECT login FROM users WHERE login LIKE ?";
    private final String insertFriend = "INSERT INTO friends (friendId,id) VALUES(?,?)";
    private final String getNickById = "SELECT login FROM users WHERE id = ?";
    private final String checkFriends = "SELECT COUNT(*) FROM friends WHERE id = ? AND friendId = ?";
    
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
    
    public String addFriend(String me, String login){
        try{           
            PreparedStatement stmt = connection.prepareStatement(checkFriends);
            stmt.setString(1, me);
            stmt.setString(2, login);
            ResultSet rs = stmt.executeQuery();
            rs.next();
            if(rs.getInt(1)!=0 || login.equals(me)){
                return "error";
            }
            stmt = connection.prepareStatement(insertFriend);
            stmt.setString(1, login);
            stmt.setString(2, me);
            stmt.executeUpdate();
            stmt = connection.prepareStatement(insertFriend);
            stmt.setString(1, me);
            stmt.setString(2, login);
            stmt.executeUpdate();    
        }
        catch (SQLException sqlEx) {
            sqlEx.printStackTrace();
        }
        return login;
    }
    
    public String getUsers(String pattern){
        String result = "";
        try {
            PreparedStatement stmt = connection.prepareStatement(findUsers);
            stmt.setString(1,"%"+pattern+"%");
            ResultSet rs = stmt.executeQuery();
            while(rs.next()){
                result+=rs.getString("login")+" ";
            }
        }
        catch (SQLException sqlEx) {
            sqlEx.printStackTrace();
            return result;
        }
        return result;
    }
    
    public void insertMsg(String from, String to, String msg){
        try{
            PreparedStatement stmt = connection.prepareStatement(insertMsg);
            stmt.setString(1,from);
            stmt.setString(2,to);
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
            stmt.setString(1, o.getUid());
            ResultSet rs = stmt.executeQuery();
            while(rs.next()){
                result+=rs.getString("friendId")+" ";
            }
        }
        catch (SQLException sqlEx) {
            sqlEx.printStackTrace();
            return result;
        }
        return result;
    }
    
    public String getMessages(OneConnection o, String to){
        String result = "";
        try {
            
            PreparedStatement stmt = connection.prepareStatement(loadMsgQuery);
            stmt.setString(1, o.getUid());
            stmt.setString(2, to);
            stmt.setString(3, o.getUid());
            stmt.setString(4, to);
            ResultSet rs = stmt.executeQuery();
            while(rs.next()){
                result+=rs.getString("fromWho")+" "+rs.getString("message")+"!!!!!"; //Поменять!
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
            o.setUid(login);
        }
        catch (SQLException sqlEx) {
            sqlEx.printStackTrace();
            return false;
        }
        return true;
    }
}
