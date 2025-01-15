import java.io.IOException; // Exception handling for IO operations
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.stream.Stream;

public class RDBReader {
    public static void storeRdbKeysValues(String file, Store store){
        Path filePath = Path.of(file);
        KeyValuePair result;
        try {
            System.out.println("File: " + file);
            if (!isTextFile(filePath)) {
                byte[] content = Files.readAllBytes(filePath);
                result = extractKeyValue(content);
                store.addItem(result.getKey(), result.getValue());
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

    }
    public static String readRdbFile(String file) {
        Path filePath = Path.of(file);
        KeyValuePair result;
        try {
            System.out.println("File: " + file);
            if (!isTextFile(filePath)) {
                byte[] content = Files.readAllBytes(filePath);
                result = extractKeyValue(content);
                return result.getKey();
            }
        } catch (IOException e) {
            e.printStackTrace();

        }
        return "-1";
    }

    // check to determine if a file is a text file
    public static boolean isTextFile(Path file) {
        try {
            // Try reading the first few bytes to guess if the file is a text file
            byte[] bytes = Files.readAllBytes(file);
            for (byte b : bytes) {
                if (b == 0) { // Null byte indicates non-text (binary) data
                    return false;
                }
            }
            return true;
        } catch (IOException e) {
            return false;
        }
    }

    public static KeyValuePair extractKeyValue(byte[] content) {
        System.out.println("Extracting key-value pairs");
        int index = 0;
        // skip header (first 9 bytes)
        index += 9;
        // find FE which marks database section
        while (content[index] != (byte) 0xFE) {
            index++;
        }
        // skip the index FE 00
        index += 2;
        // skip hashtable size information FB 01 00
        index += 3;
        // skip type encoding 00 for string
        index += 1;
        // get key string length
        int keyLength = content[index++];
        String key = new String(content, index, keyLength);
        index += keyLength;
        // get value string length
        int valueLength = content[index++];
        String value = new String(content, index, valueLength);
        KeyValuePair result = new KeyValuePair(key, value);
        return result;
    }

    public static void listFilesWithContents(String path) {
        Path dirPath = Paths.get(path); // Replace with your directory path
        System.out.println("Printing files...");
        try (Stream<Path> paths = Files.list(dirPath)) {
            paths.filter(Files::isRegularFile) // Only regular files (exclude directories)
                    .forEach(file -> {
                        try {
                            // Print the file path
                            System.out.println("File: " + file);

                            // Check if the file is a text file (a simple check)
                            if (isTextFile(file)) {
                                // Read and print the content of the text file
                                String content = Files.readString(file);
                                System.out.println("Content: \n" + content);
                            } else {
                                // Read and print a binary file as bytes
                                byte[] content = Files.readAllBytes(file);
                                System.out.println("Content (binary): ");
                                for (byte b : content) {
                                    System.out.print(String.format("%02X ", b)); // Print as hex
                                }
                                System.out.println(); // New line after binary data
                            }
                        } catch (IOException e) {
                            System.err.println("Error reading file " + file + ": " + e.getMessage());
                        }
                    });
        } catch (IOException e) {
            System.err.println("Error reading directory: " + e.getMessage());
        }
        System.out.println("End of printing files.");
    }
}