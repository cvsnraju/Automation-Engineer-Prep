// Singleton Pattern Example — Eager Initialization
public class DatabaseConnection {
    // Static instance created at class loading time
    private static final DatabaseConnection instance = new DatabaseConnection();
    
    // Private constructor prevents instantiation from other classes
    private DatabaseConnection() {
        System.out.println("DatabaseConnection initialized");
    }
    
    // Global access point
    public static DatabaseConnection getInstance() {
        return instance;
    }
    
    public void connect() {
        System.out.println("Connected to database");
    }
}


// Singleton Pattern Example — Lazy Initialization (Thread-Safe)
public class ConfigManager {
    private static ConfigManager instance;
    private String config;
    
    private ConfigManager() {
        this.config = "Default Config";
    }
    
    // Thread-safe lazy initialization using synchronized block
    public static synchronized ConfigManager getInstance() {
        if (instance == null) {
            instance = new ConfigManager();
        }
        return instance;
    }
    
    public String getConfig() {
        return config;
    }
    
    public void setConfig(String config) {
        this.config = config;
    }
}


// Singleton Usage Example
public class SingletonDemo {
    public static void main(String[] args) {
        // Get singleton instance
        DatabaseConnection db1 = DatabaseConnection.getInstance();
        DatabaseConnection db2 = DatabaseConnection.getInstance();
        
        // Both references point to the same instance
        System.out.println("Same instance? " + (db1 == db2)); // true
        
        db1.connect();
        
        // ConfigManager lazy initialization
        ConfigManager config1 = ConfigManager.getInstance();
        ConfigManager config2 = ConfigManager.getInstance();
        
        config1.setConfig("Production");
        System.out.println("Config: " + config2.getConfig()); // Production (same instance)
        System.out.println("Same instance? " + (config1 == config2)); // true
    }
}
