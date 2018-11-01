package server;


import io.undertow.Undertow;
import org.jdbi.v3.core.Jdbi;
import redis.clients.jedis.*;
import server.handlers.ConfigReadHandler;
import server.handlers.RefreshHandler;

import java.util.Optional;

import static io.undertow.Handlers.routing;

public class App {

    public static void main(String[] args) {
        JedisPoolConfig poolConfig = new JedisPoolConfig();
        JedisPool pool = new JedisPool(poolConfig, redisHost(), redisPort(), Protocol.DEFAULT_TIMEOUT);
        subscribe(pool, "configurations");

        DB db = new DB(Jdbi.create(jdbcUrl()));
        Undertow.builder()
                .addHttpListener(port(), host())
                .setHandler(routing()
                        .post("/refresh", new RefreshHandler(pool, db::readConfiguration))
                        .get("/config", new ConfigReadHandler(db::readConfiguration)))
                .build()
                .start();
    }

    private static void subscribe(JedisPool pool, String channel) {
        new Thread(() -> {
            try (Jedis jedis = pool.getResource()) {
                jedis.subscribe(jedisPubSub, channel);
            }
        }).start();
    }

    static JedisPubSub jedisPubSub = new JedisPubSub() {


        @Override
        public void onMessage(String channel, String message) {
            System.err.println(channel + " " + message);
        }
    };


    private static String redisPassword() {
        return Optional.ofNullable(System.getProperty("redisPassword")).orElse("");
    }

    private static int redisPort() {
        return Integer.parseInt(Optional.ofNullable(System.getProperty("redisPort")).orElse("" + Protocol.DEFAULT_PORT));
    }

    private static String redisHost() {
        return Optional.ofNullable(System.getProperty("redisHost")).orElse("0.0.0.0");
    }

    private static String jdbcUrl() {
        return Optional.ofNullable(System.getProperty("jdbcUrl")).orElse("jdbc:h2:mem:test");
    }

    private static String host() {
        return Optional.ofNullable(System.getProperty("host")).orElse("localhost");
    }

    private static int port() {
        return Integer.parseInt(Optional.ofNullable(System.getProperty("port")).orElse("9090"));
    }
}
