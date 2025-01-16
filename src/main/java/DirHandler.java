public class DirHandler {
    public static void handleDirFiles(String[] args, Store store) {
        String fullPath = "";
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
            fullPath = args[1] + "/" + args[3];
            System.out.println("***********************START************************");
            RDBReader.listFilesWithContents(args[1]);
            System.out.println("*************************END**********************");
        }
        if (fullPath.length() != 0){
            RDBReader.readRdbFile(fullPath,store);
        }
    }
}
