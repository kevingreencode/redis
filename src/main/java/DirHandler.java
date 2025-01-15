public class DirHandler {
    public static void handleDirFiles(String[] args, Store store) {
        for (int i = 0; i < args.length; i++) {
            String arg = args[i];
            if (arg.startsWith("--")) {
                if (i + 1 >= args.length)
                    return;
                String key = arg.substring(2);
                String value = args[i + 1];
                i++;
                System.out.println("Added key: " + key + " Value: " + value);
                store.addItem(key, value);
            }
        }
    }
}
