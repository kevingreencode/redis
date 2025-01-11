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
      serverSocketChannel.bind(new InetSocketAddress(6379));
      serverSocketChannel.register(selector, SelectionKey.OP_ACCEPT);
      System.out.println("Server started on port 6379...");

      // Event loop
      while (true) {
        selector.select();

        // Process events
        Iterator<SelectionKey> keyIterator = selector.selectedKeys().iterator();
        while (keyIterator.hasNext()) {
          SelectionKey key = keyIterator.next();
          keyIterator.remove();

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
    SocketChannel clientChannel = serverChannel.accept();
    clientChannel.configureBlocking(false);
    clientChannel.register(key.selector(), SelectionKey.OP_READ);
    System.out.println("Accepted new connection from " + clientChannel.getRemoteAddress());
  }

  private static void handleRead(SelectionKey key) throws IOException {
    SocketChannel clientChannel = (SocketChannel) key.channel();
    ByteBuffer buffer = ByteBuffer.allocate(1024);

    int bytesRead = clientChannel.read(buffer);
    if (bytesRead == -1) {
      System.out.println("Client disconnected: " + clientChannel.getRemoteAddress());
      clientChannel.close();
      return;
    }

    buffer.flip();
    String message = new String(buffer.array(), 0, buffer.limit()).trim();
    System.out.println("Received: " + message);

    String response = processCommand(message);
    clientChannel.write(ByteBuffer.wrap(response.getBytes()));
  }

  private static String processCommand(String message) {
    // Parse RESP message
    String[] lines = message.split("\r\n");
    if (lines.length < 2 || !lines[0].startsWith("*")) {
      return "-ERROR invalid RESP format\r\n";
    }

    int numElements = Integer.parseInt(lines[0].substring(1)); // Parse array length
    if (numElements == 1 && lines.length >= 3 && "$4".equals(lines[1]) && "PING".equalsIgnoreCase(lines[2])) {
      return "+PONG\r\n"; // Respond to PING
    }

    if (numElements == 2 && lines.length >= 5 && "ECHO".equalsIgnoreCase(lines[2])) {
      String argument = lines[4];
      return "$" + argument.length() + "\r\n" + argument + "\r\n"; // Respond to ECHO
    }

    return "-ERROR unknown command\r\n";
  }
}
