import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

public class Communicator {

    public static void connectToPeer(String host, int port) {
        try (Socket socket = new Socket(host, port);
             PrintWriter writer = new PrintWriter(socket.getOutputStream(), true);
             BufferedReader reader = new BufferedReader(new InputStreamReader(socket.getInputStream()))) {

            System.out.println("Connected to peer at " + host + ":" + port);
            writer.print("*1\r\n$4\r\nPING\r\n");
            writer.flush();
            // Read and print response
            String response = reader.readLine();
            System.out.println("Response from peer: " + response);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void sendHandshake(String message){

    }
}
