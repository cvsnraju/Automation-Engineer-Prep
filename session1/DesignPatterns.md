# Design Patterns — Quick Notes

Singleton
- Purpose: ensure a class has only one instance and provide a global point of access.
- Java approach: private constructor, static field, and `getInstance()`.

Builder Pattern
- Purpose: construct complex objects step-by-step and provide readable code for object creation.
- Java approach: static nested `Builder` class with fluent setters and a `build()` method.

See `examples/SingletonExample.java` and `examples/BuilderExample.java` for minimal examples.
