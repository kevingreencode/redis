import java.io.IOException; // Exception handling for IO operations
import java.net.InetSocketAddress; // Represents an IP address and port pair
import java.nio.ByteBuffer; //  A buffer for handling byte data during read/write operations
import java.nio.channels.SelectionKey; // Represents a selectable channel's registration with a Selector
import java.nio.channels.Selector; //  Manages multiple channels, enabling multiplexed I/O operations
import java.nio.channels.ServerSocketChannel; // A non-blocking server-side socket channel
import java.nio.channels.SocketChannel; // A non-blocking client-side channe
import java.util.Iterator; // Used to iterate over collections (e.g., selected keys in the Selector)

public class Main { // class containg entry point
  public static void main(String[] args) { // entry point of the application
    Store store = new Store(); // Holds key value pairs using a hashmap

    try (Selector selector = Selector.open(); // creates selector for managing multiple channels
        ServerSocketChannel serverSocketChannel = ServerSocketChannel.open()) { // opens server-side socket channel

      // Configure the server channel to be non-blocking
      serverSocketChannel.configureBlocking(false); // sets socket to non-blocking mode
      serverSocketChannel.bind(new InetSocketAddress(6379)); // binds server socket to port 6379
      serverSocketChannel.register(selector, SelectionKey.OP_ACCEPT); // registers to connection-accept events
      System.out.println("Server started on port 6379...");

      // Event loop
      while (true) {

        selector.select(); // blocks until at least one event is ready

        // Process events
        Iterator<SelectionKey> keyIterator = selector.selectedKeys().iterator(); // gets keys from selector
        while (keyIterator.hasNext()) {
          SelectionKey key = keyIterator.next();
          keyIterator.remove(); // removes the key to avoid processing again

          if (key.isAcceptable()) { // checks if event is a new client connection
            handleAccept(key);
          } else if (key.isReadable()) { // checks if event is data ready to be read
            handleRead(key, store);
          }
        }
      }
    } catch (IOException e) {
      e.printStackTrace();
    }
  }

  private static void handleAccept(SelectionKey key) throws IOException {
    ServerSocketChannel serverChannel = (ServerSocketChannel) key.channel();
    SocketChannel clientChannel = serverChannel.accept(); // accepts a new client connection
    clientChannel.configureBlocking(false); // configure as non-blocking
    clientChannel.register(key.selector(), SelectionKey.OP_READ); // registers the connection for read events
    System.out.println("Accepted new connection from " + clientChannel.getRemoteAddress());
  }

  private static void handleRead(SelectionKey key, Store store) throws IOException {
    SocketChannel clientChannel = (SocketChannel) key.channel(); // retrieves client channel from key
    ByteBuffer buffer = ByteBuffer.allocate(1024); // allocates 1024 bytes to read data

    int bytesRead = clientChannel.read(buffer); // reads data from client into buffer
    if (bytesRead == -1) { // if read returns -1, the client has disconnected
      System.out.println("Client disconnected: " + clientChannel.getRemoteAddress());
      clientChannel.close();
      return;
    }

    buffer.flip(); // flips the buffer to prepare for reading
    String message2 = new String(buffer.array());
    String message = new String(buffer.array(), 0, buffer.limit()).trim(); // conversts buffer content to string
    System.out.println("Received: " + message2);

    String response = processCommand(message, store); // process client message
    clientChannel.write(ByteBuffer.wrap(response.getBytes())); // send response back to client
  }

  private static String processCommand(String message, Store store) {
    // Parse RESP message
    String[] lines = message.split("\r\n"); // split message using RESP \r\n delimiter
    System.out.println("lines size is: " + lines.length);
    if (lines.length < 2 || !lines[0].startsWith("*")) { // check message follows RESP format
      return "-ERROR invalid RESP format\r\n";
    }

    int numElements = Integer.parseInt(lines[0].substring(1)); // Parse array length
    if (numElements == 1 && lines.length >= 3 && "$4".equals(lines[1]) && "PING".equalsIgnoreCase(lines[2])) {
      return "+PONG\r\n"; // Respond to PING
    }

    if (numElements == 2 && lines.length >= 5) {
      if ("ECHO".equalsIgnoreCase(lines[2])) { // handle ECHO command
        String argument = lines[4];
        return "$" + argument.length() + "\r\n" + argument + "\r\n"; // Respond to ECHO
      }

      if ("GET".equalsIgnoreCase(lines[2])) { // handle GET command
        String key = lines[4];
        if (!store.containsItem(key)) { // if the store doesn't contain the item
          return "$-1\r\n";
        } else {
          String value = store.getItem(key);
          return "$" + value.length() + "\r\n" + value + "\r\n"; // return the value RESP formatted
        }
      }
    }

    if (numElements == 3 && lines.length >= 7 && "SET".equalsIgnoreCase(lines[2])) { // Handle SET command
      String key = lines[4];
      String value = lines[6];
      store.addItem(key, value);
      return "+OK\r\n"; // Added the value to the Store
    }

    return "-ERROR unknown command\r\n";
  }

}
