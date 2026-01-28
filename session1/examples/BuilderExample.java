// Builder Pattern Example — Person class with optional fields
public class Person {
    private final String firstName;
    private final String lastName;
    private final int age;
    private final String email;
    private final String phone;
    private final String address;
    
    // Private constructor only accessible from Builder
    private Person(Builder builder) {
        this.firstName = builder.firstName;
        this.lastName = builder.lastName;
        this.age = builder.age;
        this.email = builder.email;
        this.phone = builder.phone;
        this.address = builder.address;
    }
    
    // Static nested Builder class
    public static class Builder {
        // Required fields
        private final String firstName;
        private final String lastName;
        
        // Optional fields
        private int age = 0;
        private String email = "";
        private String phone = "";
        private String address = "";
        
        // Builder constructor requires mandatory fields
        public Builder(String firstName, String lastName) {
            this.firstName = firstName;
            this.lastName = lastName;
        }
        
        // Fluent setter methods return Builder for chaining
        public Builder age(int age) {
            this.age = age;
            return this;
        }
        
        public Builder email(String email) {
            this.email = email;
            return this;
        }
        
        public Builder phone(String phone) {
            this.phone = phone;
            return this;
        }
        
        public Builder address(String address) {
            this.address = address;
            return this;
        }
        
        // Final build() method creates the Person object
        public Person build() {
            return new Person(this);
        }
    }
    
    @Override
    public String toString() {
        return "Person{" +
                "firstName='" + firstName + '\'' +
                ", lastName='" + lastName + '\'' +
                ", age=" + age +
                ", email='" + email + '\'' +
                ", phone='" + phone + '\'' +
                ", address='" + address + '\'' +
                '}';
    }
}


// Builder Pattern Example — WebDriver configuration
public class WebDriverConfig {
    private final String browser;
    private final String baseUrl;
    private final int timeout;
    private final boolean headless;
    private final String downloadPath;
    private final String logLevel;
    
    private WebDriverConfig(Builder builder) {
        this.browser = builder.browser;
        this.baseUrl = builder.baseUrl;
        this.timeout = builder.timeout;
        this.headless = builder.headless;
        this.downloadPath = builder.downloadPath;
        this.logLevel = builder.logLevel;
    }
    
    public static class Builder {
        private final String browser;
        private final String baseUrl;
        private int timeout = 10;
        private boolean headless = false;
        private String downloadPath = "/downloads";
        private String logLevel = "INFO";
        
        public Builder(String browser, String baseUrl) {
            this.browser = browser;
            this.baseUrl = baseUrl;
        }
        
        public Builder timeout(int timeout) {
            this.timeout = timeout;
            return this;
        }
        
        public Builder headless(boolean headless) {
            this.headless = headless;
            return this;
        }
        
        public Builder downloadPath(String path) {
            this.downloadPath = path;
            return this;
        }
        
        public Builder logLevel(String level) {
            this.logLevel = level;
            return this;
        }
        
        public WebDriverConfig build() {
            return new WebDriverConfig(this);
        }
    }
    
    public String getBrowser() { return browser; }
    public String getBaseUrl() { return baseUrl; }
    public int getTimeout() { return timeout; }
    public boolean isHeadless() { return headless; }
    public String getDownloadPath() { return downloadPath; }
    public String getLogLevel() { return logLevel; }
    
    @Override
    public String toString() {
        return "WebDriverConfig{" +
                "browser='" + browser + '\'' +
                ", baseUrl='" + baseUrl + '\'' +
                ", timeout=" + timeout +
                ", headless=" + headless +
                ", downloadPath='" + downloadPath + '\'' +
                ", logLevel='" + logLevel + '\'' +
                '}';
    }
}


// Builder Pattern Usage Examples
public class BuilderDemo {
    public static void main(String[] args) {
        // Example 1: Simple Person with minimal fields
        Person person1 = new Person.Builder("John", "Doe")
                .build();
        System.out.println("Person 1: " + person1);
        
        // Example 2: Person with all optional fields set
        Person person2 = new Person.Builder("Jane", "Smith")
                .age(28)
                .email("jane.smith@example.com")
                .phone("555-1234")
                .address("123 Main St, City")
                .build();
        System.out.println("Person 2: " + person2);
        
        // Example 3: WebDriver config with fluent API
        WebDriverConfig config1 = new WebDriverConfig.Builder("Chrome", "https://example.com")
                .timeout(30)
                .headless(true)
                .downloadPath("/tmp/downloads")
                .logLevel("DEBUG")
                .build();
        System.out.println("Config 1: " + config1);
        
        // Example 4: WebDriver config with default values
        WebDriverConfig config2 = new WebDriverConfig.Builder("Firefox", "https://test.com")
                .headless(false)
                .build();
        System.out.println("Config 2: " + config2);
    }
}
