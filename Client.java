
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetAddress;
import java.net.Socket;
import javax.swing.JOptionPane;

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
    private Find findForm;
    private Socket socket;
    private DataInputStream in;
    private DataOutputStream out;
    private InetAddress address;
    
    public Client(InetAddress addr){
        address = addr;
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
            if(!resp.equals("fail")){
                return true; 
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
            catch(IOException e1){
                System.out.println("still opened");
            }
            finally{
                return false;
            }
        }
    }
    
    public void loadFriends(){
        try{
            out.writeUTF("2");
            out.flush();
            String resp = in.readUTF();
            String[] friends = resp.split(" ");
            if(friends.length==1 && friends[0].equals("")){
                return;
            }
            f.addFriends(friends);
        }
        catch(IOException e){
            System.out.println("problems with loading ftiends");
        }
    }
    
    public void addFriend(String login){
        try{
            out.writeUTF("5 "+ login);
        }
        catch(IOException e){
            System.out.println("problems with adding friend");
        }
    }
    
    public void mainAction(MainForm f){
        this.f = f;
        f.setClient(this);
        loadFriends();
        start();
    }
    
    @Override
    public void run(){
        while(true){
            try{
                String line = in.readUTF();
                if(line.equals("*//FRIENDUPDATE//*")){
                    loadFriends();
                    continue;
                }
                if(line.equals("error")){
                    JOptionPane.showMessageDialog(null, "Вы уже добавили этого собеседника!");
                }
                if(line.length()>=10){
                    String temp = line.substring(0,10);
                    if(temp.equals("*//FIND//*")){
                        temp = line.substring(10);
                        findForm.setResult(temp.split(" "));
                        continue;
                    }
                }
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
    
    public void openMessages(String to){
        try{
                out.writeUTF("3 "+to);
            }
            catch(Exception e){
                System.out.println("problems with load table of messages");
            }
    }
    
    public void findPeoples(Find findForm,String pattern){
        this.findForm = findForm;
        try{
            out.writeUTF("4 "+pattern);
        }
        catch(IOException e){
            System.out.println("some prblems with finding");
        }
    }
}
