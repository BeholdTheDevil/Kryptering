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
        System.out.println("Server started");

        return socket;
    }

    public void setPort(int port) {
        this.port = port;
    }

    public byte[] readByte(Socket socket, int size) {
        try {
            byte[] input = new byte[size];
            DataInputStream in = new DataInputStream(socket.getInputStream());
            int n = in.read(input);

            if(n == -1) {
                return null;
            }
            return input;
        } catch (IOException e) {
            System.out.println("An error occurred while reading from the stream");
            e.printStackTrace();
        }

        return null;
    }

    public int readInt(Socket socket) {
        try {
            DataInputStream in = new DataInputStream(socket.getInputStream());
            int output = in.readInt();
            return output;
        } catch (IOException e) {
            System.out.println("An error occurred while reading from the stream");
            e.printStackTrace();
        }
        return -1;
    }

    public String readString(Socket socket) {
        try {
            DataInputStream in = new DataInputStream(socket.getInputStream());
            return in.readLine();
        } catch (IOException e) {
            System.out.println("An error occurred while reading from the stream");
            e.printStackTrace();
        }
        return null;
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

    public Socket connectTo(String inetAddress) throws IOException {
        Socket socket = new Socket(inetAddress, port);
        return socket;
    }

    public void connect(Socket socket) {
        try {
            DataOutputStream out = new DataOutputStream(socket.getOutputStream());
            PrintStream ps = new PrintStream(out);
            ps.println("AC");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private Socket acceptConnection() {
        Socket socket = null;
        try {
            ServerSocket server = new ServerSocket(port);
            socket = server.accept();
            server.close();
        } catch(IOException e) {
            //System.out.println("Error creating ServerSocket\n" + e.getMessage());
        }
        return socket;
    }
}
