public class Record {
    private String value;
    private long createdTime;
    private long expiry = -1;

    public Record(String value) {
        this.value = value;
        createdTime = System.currentTimeMillis();
    }

    public Record(String value, String expiry) {
        this.value = value;
        createdTime = System.currentTimeMillis();

        try {
            this.expiry = Long.parseLong(expiry);
        } catch (NumberFormatException e) {
            System.out.println("Unable to parse expiry to long value");
        }
    }

    public String getValue() {
        return value;
    }

    public long getExpiry() {
        return expiry;
    }

    public long getCreatedTime() {
        return createdTime;
    }

    public boolean hasExpired() {
        if (expiry < 0)
            return false;
        long currentTimeMillis = System.currentTimeMillis();
        boolean result = (currentTimeMillis - createdTime) >= expiry;
        System.out.println("expiry: " + expiry + " currentTimeMillis: " + currentTimeMillis + " createdTime: " + createdTime + " result: " + result);
        long elapsedTime = currentTimeMillis - createdTime;
        System.out.println("Elapsed time: " + elapsedTime);
        return result;
    }
}
