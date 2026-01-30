package session1.examples;

public class SingletonExample {
    private static volatile SingletonExample instance;

    private SingletonExample() {
        // private constructor
    }

    public static SingletonExample getInstance() {
        if (instance == null) {
            synchronized (SingletonExample.class) {
                if (instance == null) {
                    instance = new SingletonExample();
                }
            }
        }
        return instance;
    }
}
