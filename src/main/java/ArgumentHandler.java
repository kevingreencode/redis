public class ArgumentHandler {
    public static boolean containsReplicaof(String[] args){
        for (String arg : args){
            if (arg.equals("--replicaof")){
                return true;
            }
        }
        return false;
    }
}
