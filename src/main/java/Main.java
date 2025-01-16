public class Main { // class containg entry point
  public static void main(String[] args) { // entry point of the application

    Store store = new Store(); // Holds key value pairs using a hashmap
    store.addItem("role", "master");
    store.addItem("master_repl_offset", "0");
    store.addItem("master_replid", "8371b4fb1155b71f4a04d3e1bc3e18c4a990aeeb");
    String fullPath = "";
    int port = 0;

    System.out.println("args.length: " + args.length);
    if (args.length == 2 && args[0].equalsIgnoreCase("--PORT")){
      port = Integer.parseInt(args[1]);
    }
    if (args.length > 2) { // TODO: Generalize the argument handler
      if (ArgumentHandler.containsFlag(args, "--port")){
        int i = 0;
        while (!args[i].equalsIgnoreCase("--port")){
          i++;
        }
        port = Integer.parseInt(args[++i]);
      }
      if (ArgumentHandler.containsFlag(args,"--replicaof")){
        store.addItem("role", "slave");
        store.removeItem("master_replid");
        store.removeItem("master_repl_offset");
      } else {
        DirHandler.handleDirFiles(args, store);
        fullPath = args[1] + "/" + args[3];
      }
    }
    if (port == 0) {
      port = 6379;
    }
    Runner.runService(fullPath, store, port);
  }
}
