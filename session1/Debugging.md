# Debugging Test Failures — Quick Guide

1. Read the stack trace carefully — the topmost test failure shows the assertion or exception.
2. Identify failure type:
   - Assertion failures: expected vs actual mismatch.
   - Exceptions: NullPointerException, TimeoutException, etc.
3. Reproduce locally and run the failing test only (use TestNG groups or `-Dtest=` depending on runner).
4. Add logging or use debugger breakpoints to inspect state before the failure.
5. Check test data, environment differences (config, endpoints, credentials), and test order dependencies.
6. For flaky tests, isolate timing or synchronization issues; add waits or retry logic carefully.

Tips
- Keep assertions small and focused.
- Use meaningful assertion messages to speed root-cause discovery.
- When hitting unknown exceptions, add minimal reproducer code and run with a debugger.
