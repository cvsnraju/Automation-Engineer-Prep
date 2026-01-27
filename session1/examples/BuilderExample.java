package session1.examples;

public class BuilderExample {
    private final String host;
    private final int port;
    private final boolean useSsl;

    private BuilderExample(Builder b) {
        this.host = b.host;
        this.port = b.port;
        this.useSsl = b.useSsl;
    }

    public static class Builder {
        private String host = "localhost";
        private int port = 80;
        private boolean useSsl = false;

        public Builder host(String host) { this.host = host; return this; }
        public Builder port(int port) { this.port = port; return this; }
        public Builder useSsl(boolean useSsl) { this.useSsl = useSsl; return this; }
        public BuilderExample build() { return new BuilderExample(this); }
    }
}
