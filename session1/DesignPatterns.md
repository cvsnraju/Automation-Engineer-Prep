# Design Patterns — Quick Notes

## Singleton

**Purpose:** Ensure a class has only one instance and provide a global point of access.

**Java approach:** Private constructor, static field, and `getInstance()`.

**Eager Initialization (Simple, Thread-Safe):**
```java
public class DatabaseConnection {
    private static final DatabaseConnection instance = new DatabaseConnection();
    
    private DatabaseConnection() {
        // initialization code
    }
    
    public static DatabaseConnection getInstance() {
        return instance;
    }
}
```

**Lazy Initialization (Thread-Safe with synchronized):**
```java
public class ConfigManager {
    private static ConfigManager instance;
    
    private ConfigManager() {
        // initialization code
    }
    
    public static synchronized ConfigManager getInstance() {
        if (instance == null) {
            instance = new ConfigManager();
        }
        return instance;
    }
}
```

**Usage:**
```java
DatabaseConnection db1 = DatabaseConnection.getInstance();
DatabaseConnection db2 = DatabaseConnection.getInstance();
// db1 == db2 (same instance)
```

---

## Builder Pattern

**Purpose:** Construct complex objects step-by-step and provide readable code for object creation.

**Java approach:** Static nested `Builder` class with fluent setters and a `build()` method.

**Basic Structure:**
```java
public class Person {
    private final String firstName;
    private final String lastName;
    private final int age;
    private final String email;
    
    private Person(Builder builder) {
        this.firstName = builder.firstName;
        this.lastName = builder.lastName;
        this.age = builder.age;
        this.email = builder.email;
    }
    
    public static class Builder {
        private final String firstName;
        private final String lastName;
        private int age = 0;
        private String email = "";
        
        public Builder(String firstName, String lastName) {
            this.firstName = firstName;
            this.lastName = lastName;
        }
        
        public Builder age(int age) {
            this.age = age;
            return this;
        }
        
        public Builder email(String email) {
            this.email = email;
            return this;
        }
        
        public Person build() {
            return new Person(this);
        }
    }
}
```

**Usage:**
```java
Person person = new Person.Builder("John", "Doe")
        .age(30)
        .email("john@example.com")
        .build();
```

**Benefits:**
- Clean, readable object creation
- Required vs optional fields clearly distinguished
- Avoids telescoping constructors
- Immutable objects possible
- Easier to add new optional fields

See [examples/SingletonExample.java](examples/SingletonExample.java) and [examples/BuilderExample.java](examples/BuilderExample.java) for comprehensive examples.
