import java.io.IOException;
import java.io.OutputStream;
import java.io.InputStreamReader;
import java.io.BufferedReader;

import java.net.ServerSocket;
import java.net.Socket;

import java.util.Arrays;

public class Main {
  public static void main(String[] args) {

    System.out.println("Redis server is running!");

    ServerSocket serverSocket = null;
    Socket clientSocket = null;
    int port = 6379;

    try {

      serverSocket = new ServerSocket(port);
      serverSocket.setReuseAddress(true);

      // Wait for a connection
      clientSocket = serverSocket.accept();
      // Connection made
      System.out.println("Client connected!");

      byte[] response = "+PONG\r\n".getBytes(); // temporary hard coded response
      InputStreamReader inputStreamReader = new InputStreamReader(clientSocket.getInputStream());
      BufferedReader bufferedReader = new BufferedReader(inputStreamReader); // For handling text
      String command;

      OutputStream out = clientSocket.getOutputStream(); // to write back to the client
      
      // process one command at a time
      while ((command = bufferedReader.readLine().trim()) != null) {
        System.out.println("Received command: " + command);

        if ("PING".equalsIgnoreCase(command)) {
          out.write(response);
          out.flush();
        }
      }

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
