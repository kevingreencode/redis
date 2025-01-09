import java.io.IOException;
import java.io.OutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Arrays;

public class Main {
  public static void main(String[] args) {

    System.out.println("Logs from your program will appear here!");

    ServerSocket serverSocket = null;
    Socket clientSocket = null;
    int port = 6379;

    try {

      // String response = "+PONG\r\n";
      // byte[] byteArray = response.getBytes();

      // // Print raw byte array (default `toString()` won't show the values)
      // System.out.println(byteArray);

      // // Print byte array values using Arrays.toString()
      // System.out.println(Arrays.toString(byteArray));

      // // Print each byte individually
      // for (byte b : byteArray) {
      //     System.out.println(b);
      // }

      serverSocket = new ServerSocket(port);
      serverSocket.setReuseAddress(true);
      clientSocket = serverSocket.accept();
      OutputStream out = clientSocket.getOutputStream();
      out.write("+PONG\r\n".getBytes());

    } catch (IOException e) {

      System.out.println("IOException: " + e.getMessage());

    } finally {
      try {

        if (clientSocket != null) {
          clientSocket.close();
        }

      } catch (IOException e) {

        System.out.println("IOException: " + e.getMessage());
      }

    }
  }
}
