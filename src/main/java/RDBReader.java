import java.io.IOException; // Exception handling for IO operations
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.stream.Stream;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;

public class RDBReader {
    public static String readRdbFile(String file, Store store) {
        Path filePath = Path.of(file);
        String[] resultArray;
        try {
            System.out.println("File: " + file);
            if (!isTextFile(filePath)) {
                byte[] content = Files.readAllBytes(filePath);
                resultArray = extractKeyValue2(content, store);
                System.out.println("resultArray.length: " + resultArray.length);
                String result = RESPFormatter.formatLongArray(resultArray);
                System.out.println("Return from readRdbFile: " + result);
                return result;
            }
        } catch (IOException e) {
            System.out.println("Couldn't open file, it may not exist");
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

    public static String[] extractKeyValue(byte[] content, Store store) {
        ArrayList<String> keyList = new ArrayList<>();
        System.out.println("Extracting key-value pairs");
        int index = 0;
        // skip header (first 9 bytes)
        index += 9;
        // find FE which marks database section
        while (index < content.length && content[index] != (byte) 0xFE) {
            index++;
        }
        // skip the index FE 00
        index += 2;
        // skip hashtable size information FB 01 00
        index += 3;
        // skip type encoding 00 for string
        index += 1;
        // get key string length

        while (index < content.length && content[index] != (byte) 0xFF) {
            int keyLength = content[index++];
            System.out.println("Content length: " + content.length + " index: " + index + " keyLength: " + keyLength);
            String key = new String(content, index, keyLength);
            index += keyLength;
            if (key.length() == 0)
                continue;
            // get value string length
            int valueLength = content[index++];
            String value = new String(content, index, valueLength);
            index += valueLength;
            keyList.add(key);
            store.addItem(key, value);
        }
        System.out.println("Number of keys keyList.size(): " + keyList.size());
        return keyList.toArray(new String[0]);
    }

    public static String[] extractKeyValue2(byte[] content, Store store) {
        ArrayList<String> keyList = new ArrayList<>();
        System.out.println("Extracting key-value pairs #2");

        int index = 0;

        while (index < content.length && content[index] != (byte) 0xFE) {
            index++;
        }

        // skip the index FE 00
        index += 2;
        // skip hashtable size information FB 01 00
        index += 3;

        while (index < content.length && content[index] != (byte) 0xFF) {
            long expiry = -1;
            if (content[index] == (byte) 0xFC) {
                byte[] bigEndianBytes = convertLittleEndianToBigEndian(readNBytes(content, index, Long.BYTES));
                expiry = byteArrayToDecimal(bigEndianBytes);
                System.out.println("#######################expiry: " + expiry);
                index += Long.BYTES;
                index += 2; // value type
            }
            int keyLength = content[index++];
            System.out
                    .println("Content length: " + content.length + " index: " + index + " keyLength: " + keyLength);
            String key = new String(content, index, keyLength);
            index += keyLength;
            if (key.length() == 0)
                continue;
            // get value string length
            int valueLength = content[index++];
            String value = new String(content, index, valueLength);
            index += valueLength;
            keyList.add(key);
            store.addItem(key, value, String.valueOf(expiry));
        }

        return keyList.toArray(new String[0]);
    }

    public static void listFilesWithContents(String path) {
        Path dirPath = Paths.get(path);
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

    // Method to convert little-endian byte array to big-endian
    public static byte[] convertLittleEndianToBigEndian(byte[] littleEndianBytes) {
        // Create a ByteBuffer to wrap the little-endian byte array
        ByteBuffer buffer = ByteBuffer.wrap(littleEndianBytes);

        // Set the byte order to LITTLE_ENDIAN (for the input bytes)
        buffer.order(ByteOrder.LITTLE_ENDIAN);

        // Read the value as a long or any other type (e.g., int, short)
        long value = buffer.getLong();

        // create a ByteBuffer with big-endian byte order
        ByteBuffer bigEndianBuffer = ByteBuffer.allocate(Long.BYTES);
        bigEndianBuffer.order(ByteOrder.BIG_ENDIAN); // Set to BIG_ENDIAN
        bigEndianBuffer.putLong(value); // Put the value in big-endian order

        return bigEndianBuffer.array();
    }

    public static byte[] readNBytes(byte[] content, int index, int N) {
        byte[] result = new byte[N];

        for (int i = 0; i < N; i++) {
            result[i] = content[index + i + 1];
        }

        return result;
    }

    // Method to convert byte array to decimal
    public static long byteArrayToDecimal(byte[] byteArray) {
        // Create a ByteBuffer to interpret the byte array as a long
        ByteBuffer buffer = ByteBuffer.wrap(byteArray);

        // Set the byte order to BIG_ENDIAN, assuming the byte array is in big-endian
        // order
        buffer.order(ByteOrder.BIG_ENDIAN);

        // Read the long value from the buffer
        return buffer.getLong();
    }
}