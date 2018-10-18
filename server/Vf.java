import javax.crypto.SealedObject;
import java.io.InvalidClassException;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;
import java.net.SocketTimeoutException;
import java.security.KeyPair;
import java.security.PublicKey;
import java.util.ArrayList;
import java.util.HashMap;




public class Vf{
 
    public static void main(String[] inputs) throws IOException {
        int portNum = -1;
        try {
            portNum = Integer.parseInt(inputs[0]);
        } catch (NumberFormatException ex) {
            ServerUtil.handleException(ex, "Invalid port number, not a number");
        }
        ServerSocket serverDomain = new ServerSocket(portNum);
        
        Socket server_socket = null;
        while(true) {
        server_socket = serverDomain.accept();
        server_socket.setSoTimeout(6000000);

        Thread thread = new Thread(new ServerThread(server_socket));
        thread.start();

        
        
        }
    }
}
