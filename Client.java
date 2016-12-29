
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetAddress;
import java.net.Socket;


public class Client extends Thread {
    private MainForm f;
    private Socket socket;
    private DataInputStream in;
    private DataOutputStream out;
    
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
                out.writeUTF(msg);
                out.flush();
            }
            catch(Exception e){
                e.printStackTrace();
            }
    }
    
}
