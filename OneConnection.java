
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.sql.Connection;
import java.util.HashMap;
import java.util.Map;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 *
 * @author Дима
 */
public class OneConnection extends Thread {
    private static DbJobs db;
    private boolean loggedIn = false;
    private static Map<String, DataOutputStream> online = new HashMap<>(); //тут список пользователей онлайн
    private String uid; // Уникальный ид из бд для каждого клиента записывается при инициализации
    private Socket socket;
    private DataInputStream in;
    private DataOutputStream out;
    
    public void setUid(String value){
        uid = value;
    }
    
    public String getUid(){
        return uid;
    }
    
    public OneConnection(Socket s) throws IOException {
        socket = s;
        InputStream sin = socket.getInputStream();
        OutputStream sout = socket.getOutputStream();
        in = new DataInputStream(sin);
        out = new DataOutputStream(sout);
        db = new DbJobs();
        start();
    }

    public void run()  {
        while(true){
            try {
                String line = in.readUTF();
                switch (line.charAt(0)){
                    case '0':{
                        if(loggedIn){
                            //Гневное сообщение
                            break;
                        }
                        String[] res = line.split(" ");
                        String login = res[1];
                        String password = res[2];
                        if (db.connect(this, login, password)) {
                            online.put(login,out);
                            out.writeUTF("success "+login);
                            out.flush();
                            loggedIn = true;
                        }
                        else{
                            out.writeUTF("fail");
                            out.flush();
                        }
                        break;
                    }
                    case '1':{
                        if(!loggedIn){
                            //Гневное сообщение
                            break;
                        }
                        String[] res = line.split(" ");
                        String to = res[1];
                        String msg = "";
                        for(int i=2; i<res.length;i++){
                            msg+=res[i]+" ";
                        }
                        if(online.containsKey(to)) {
                            DataOutputStream send = online.get(to);
                            send.writeUTF(uid + " " + msg);
                            send.flush();
                        }
                        db.insertMsg(uid,to,msg);
                        break;
                    }
                    case '2':{
                        if(!loggedIn){
                            //Гневное сообщение
                            break;
                        }
                        out.writeUTF(db.getFriendsList(this));
                        out.flush();
                        break;
                    }
                    case '3':{
                        if(!loggedIn){
                            //Гневное сообщение
                            break;
                        }
                        String[] res = line.split(" ");
                        out.writeUTF(db.getMessages(this,res[1]));
                        out.flush();
                        break;
                    }
                    case'4':{
                        if(!loggedIn){
                            break;
                        }
                        String login = line.substring(2);
                        out.writeUTF("*//FIND//* "+db.getUsers(login));
                        out.flush();
                        break;
                    }
                    case '5':{
                        if(!loggedIn){
                            break;
                        }
                        String nick = db.addFriend(uid, line.substring(2));
                        if(online.containsKey(nick)){
                            online.get(nick).writeUTF("*//FRIENDUPDATE//*");
                            online.get(nick).flush();
                        }
                        out.writeUTF("*//FRIENDUPDATE//*");
                        out.flush();
                    }
                }
            }
            catch(Exception e){
                e.printStackTrace();
                try {
                    socket.close();
                    System.out.println("client die");
                    online.remove(uid);
                }
                catch(Exception ex){
                    System.out.println("still is not close");
                }
                finally {
                    break;
                }
            }
        }
    }
}
