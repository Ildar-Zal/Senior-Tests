package common.context;

public class ApiVersionContext {

    private static final ThreadLocal<String> CURRENT_VERSION = new ThreadLocal<>();

    public static void setVersion(String version) {
        CURRENT_VERSION.set(version);
    }

    public static String getVersion() {
        return CURRENT_VERSION.get();
    }

    public static void clear() {
        CURRENT_VERSION.remove();
    }}
