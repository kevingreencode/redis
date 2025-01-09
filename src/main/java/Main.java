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
      clientSocket = serverSocket.accept();

      System.out.println("Client connected!");

      byte[] response = "+PONG\r\n".getBytes();
      InputStreamReader inputStreamReader = new InputStreamReader(clientSocket.getInputStream());
      BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
      String command;

      OutputStream out = clientSocket.getOutputStream();

      while ((command = bufferedReader.readLine()) != null) {
        System.out.println("Received command: " + command.trim());

        if ("PING".equalsIgnoreCase(command.trim())) {
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
