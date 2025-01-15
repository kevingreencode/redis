public class Main { // class containg entry point
  public static void main(String[] args) { // entry point of the application

    Store store = new Store(); // Holds key value pairs using a hashmap
    String fullPath = "";

    System.out.println("args.length: " + args.length);
    if (args.length > 0) {
      DirHandler.handleDirFiles(args, store);
      fullPath = args[1] + "/" + args[3];
    }
    Runner.runService(fullPath, store);
  }
}
