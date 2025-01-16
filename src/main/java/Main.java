public class Main { // class containg entry point
  public static void main(String[] args) { // entry point of the application

    Store store = new Store(); // Holds key value pairs using a hashmap
    String fullPath = "";
    int port = 0;

    System.out.println("args.length: " + args.length);
    if (args.length == 2 && args[0].equalsIgnoreCase("--PORT")){
      port = Integer.parseInt(args[1]);
    }
    if (args.length > 2) { // TODO: Generalize the argument handler
      DirHandler.handleDirFiles(args, store);
      fullPath = args[1] + "/" + args[3];
    }
    if (port == 0) {
      port = 6379;
    }
    Runner.runService(fullPath, store, port);
  }
}
