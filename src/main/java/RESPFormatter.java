public class RESPFormatter {
    public static String formatArrayRESP(String[] array) {

        StringBuilder sb = new StringBuilder();
        sb.append("*");
        sb.append(array.length);
        sb.append("\r\n");
        sb.append("$");
        sb.append(array[0].length());
        sb.append("\r\n");
        sb.append(array[0]);
        sb.append("\r\n");
        sb.append(array[1]);

        return sb.toString();
    }

    public static String formatSingleRESP(String value) {
        StringBuilder sb = new StringBuilder();
        sb.append("*1\r\n");
        sb.append("$");
        sb.append(value.length());
        sb.append("\r\n");
        sb.append(value);
        sb.append("\r\n");
        return sb.toString();
    }

    public static String formatLongArray(String[] strings){
        StringBuilder sb = new StringBuilder();
        sb.append("*");
        sb.append(strings.length);
        sb.append("\r\n");
        for (int i = 0; i < strings.length; i++){
            sb.append("$");
            sb.append(strings[i].length());
            sb.append("\r\n");
            sb.append(strings[i]);
            sb.append("\r\n");
        }
        return sb.toString();
    }
}
