public class ArgumentHandler {
    public static boolean containsFlag(String[] args, String flag){
        for (String arg : args){
            if (arg.equalsIgnoreCase(flag)){
                return true;
            }
        }
        return false;
    }
}
