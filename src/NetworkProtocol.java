import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;

/**
 * Created by anton on 2018-05-02.
 */
public class NetworkProtocol {

    private int port;

    NetworkProtocol(int port) {
        this.port = port;
    }

    public Socket getConnection() {
        Socket socket;
        do {
            socket = acceptConnection();
        } while(socket == null);

        return socket;
    }

    public byte[] readByte(Socket socket) {
        byte[] input = new byte[1];
        try {
            DataInputStream in = new DataInputStream(socket.getInputStream());
            int n = in.read(input);

            if(n == -1) {
                return null;
            }
        } catch (IOException e) {
            System.out.println("An error occurred while reading from the stream");
            e.printStackTrace();
        }

        return input;
    }

    public void disconnect(Socket socket) {
        try {
            DataOutputStream out = new DataOutputStream(socket.getOutputStream());
            PrintStream ps = new PrintStream(out);
            ps.println("DC");

            ps.close();
            out.close();
            socket.close();
        } catch (IOException e) {
            System.out.println("An error occurred while disconnecting");
            e.printStackTrace();
        }
    }

    public void connect(Socket socket) {
        try {
            DataOutputStream out = new DataOutputStream(socket.getOutputStream());
            PrintStream ps = new PrintStream(out);
            ps.println("AC");

            ps.close();
            out.close();
        } catch (IOException e) {
            e.printStackTrace();
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
