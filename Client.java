
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetAddress;
import java.net.Socket;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 *
 * @author Дима
 */
public class Client extends Thread {
    private MainForm f;
    private Socket socket;
    private DataInputStream in;
    private DataOutputStream out;
    private int uid;
    public Client(InetAddress addr){
        try{
            socket = new Socket(addr,8080);
        }
        catch(IOException e){
            System.out.println("problems with creting socket");
        }
        try{
            InputStream sin = socket.getInputStream();
            OutputStream sout = socket.getOutputStream();
            in = new DataInputStream(sin);
            out = new DataOutputStream(sout);
        }
        catch(Exception e){
            System.out.println("problems with opening streams");
        }
    }
    
    public boolean auth(String login, String password){
        try{
            if(login.length()==0 || password.length()==0){
                return false;
            }
            out.writeUTF("0 " + login+ " " + password);
            out.flush();
            String resp = in.readUTF();
            if(resp.equals("success")){
                return true; //loading info after
            }
            else{
                return false;
            }
        }
        catch(IOException e){
            System.out.println("problems in action");
            try{
                socket.close();
            }
            catch(Exception e1){
                System.out.println("still opened");
            }
            finally{
                return false;
            }
        }
    }
    public void mainAction(MainForm f){
        this.f = f;
        f.setClient(this);
        try{
            out.writeUTF("2");
            out.flush();
            String resp = in.readUTF();
            String[] friends = resp.split(" ");
            f.addFriends(friends);
        }
        catch(Exception e){
            System.out.println("problems with loading ftiends");
        }
        start();
    }
    public void run(){
        while(true){
            try{
                String line = in.readUTF();
                f.addMessage(line);
            }
            catch(Exception e){
                e.printStackTrace();
            }
            
        }
    }
    
    public void sendMessage(String msg){
        try{
                out.writeUTF("1 "+msg);
                out.flush();
            }
            catch(Exception e){
                e.printStackTrace();
            }
    }
    
    public void openMessages(int to){
        try{
                out.writeUTF("3 "+to);
            }
            catch(Exception e){
                System.out.println("problems with load table of messages");
            }
    }      
}
