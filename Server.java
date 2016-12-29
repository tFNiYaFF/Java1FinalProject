import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;

public class Server {
    
    static final int PORT = 8080;
    
    public static void main(String[] args) throws IOException {
        ServerSocket s = new ServerSocket(PORT);
        System.out.println("Wait...");
        try {
            while (true) {
                Socket socket = s.accept();
                System.out.println(socket.getInetAddress());
                try {
                    new OneConnection(socket);
                }
                catch (IOException e) {
                    socket.close();
                }
            }
        }
        finally {
            s.close();
        }
    }
}