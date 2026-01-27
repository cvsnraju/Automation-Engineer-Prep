package session1.mockapi;

import com.google.gson.Gson;
import static spark.Spark.*;

public class MockApiServer {
    private static final Gson gson = new Gson();

    public static void main(String[] args) {
        startServer(4568);
    }

    public static void startServer(int portNumber) {
        port(portNumber);
        get("/api/cse", (req, res) -> {
            res.type("application/json");
            Department d = new Department(
                    "CSE",
                    "Dr. Alice Kumar",
                    320,
                    28,
                    12,
                    new String[]{"Data Structures", "Algorithms", "Operating Systems"}
            );
            return gson.toJson(d);
        });

        get("/api/it", (req, res) -> {
            res.type("application/json");
            Department d = new Department(
                    "IT",
                    "Mr. Bob Singh",
                    210,
                    18,
                    7,
                    new String[]{"Networks", "Security", "Database Systems"}
            );
            return gson.toJson(d);
        });
    }

    public static void stopServer() {
        stop();
        awaitStop();
    }
}
