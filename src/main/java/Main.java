import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.Iterator;

public class Main {
  public static void main(String[] args) {
    try (Selector selector = Selector.open();
        ServerSocketChannel serverSocketChannel = ServerSocketChannel.open()) {

      // Configure the server channel to be non-blocking
      serverSocketChannel.configureBlocking(false);
      serverSocketChannel.bind(new InetSocketAddress(6379)); // Bind to port 6379

      // Register the server channel with the selector for accept events
      serverSocketChannel.register(selector, SelectionKey.OP_ACCEPT);

      System.out.println("Server started on port 6379...");

      // Event loop
      while (true) {
        System.out.print("Waiting for events...");
        selector.select();

        // Process the events
        Iterator<SelectionKey> keyIterator = selector.selectedKeys().iterator();
        while (keyIterator.hasNext()) {
          SelectionKey key = keyIterator.next();
          keyIterator.remove(); // Remove the processed key

          if (key.isAcceptable()) {
            handleAccept(key);
          } else if (key.isReadable()) {
            handleRead(key);
          }
        }
      }
    } catch (IOException e) {
      e.printStackTrace();
    }
  }

  private static void handleAccept(SelectionKey key) throws IOException {
    ServerSocketChannel serverChannel = (ServerSocketChannel) key.channel();
    SocketChannel clientChannel = serverChannel.accept(); // Accept the connection
    clientChannel.configureBlocking(false); // Set the client channel to non-blocking

    // Register the client channel with the selector for read events
    clientChannel.register(key.selector(), SelectionKey.OP_READ);

    System.out.println("Accepted new connection from " + clientChannel.getRemoteAddress());
  }

  private static void handleRead(SelectionKey key) throws IOException {
    SocketChannel clientChannel = (SocketChannel) key.channel();
    ByteBuffer buffer = ByteBuffer.allocate(1024);

    int bytesRead = clientChannel.read(buffer);
    if (bytesRead == -1) {
      // Client disconnected
      System.out.println("Client disconnected: " + clientChannel.getRemoteAddress());
      clientChannel.close();
      return;
    }

    buffer.flip();
    String message = new String(buffer.array(), 0, buffer.limit()).trim();
    System.out.println("Received: " + message);

    // RESP Parsing: Check for '*1 $4 PING'
    if (message.startsWith("*1") && message.contains("$4") && message.endsWith("PING")) {
      clientChannel.write(ByteBuffer.wrap("+PONG\r\n".getBytes())); // Simple string response
    } else {
      clientChannel.write(ByteBuffer.wrap("-ERROR unknown command\r\n".getBytes())); // RESP error response
    }
  }


}
