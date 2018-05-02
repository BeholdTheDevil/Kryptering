import javax.swing.*;
import java.io.IOException;
import java.io.InputStream;
import java.net.ServerSocket;
import java.net.Socket;

/**
 * Created by anton on 2018-05-02.
 */
public class NetworkProtocol implements Runnable {

    private int port;

    NetworkProtocol(int port) {
        this.port = port;
    }

    @Override
    public void run() {
        Socket socket;
        while(true) {
            do {
                socket = acceptConnection();
            } while(socket == null);


        }



    }

    private Socket acceptConnection() {
        Socket socket = null;
        try {
            ServerSocket server = new ServerSocket(port);
            socket = server.accept();
        } catch(IOException e) {
            System.out.println("Error creating ServerSocket\n" + e.getMessage());
        }
        return socket;
    }
}
