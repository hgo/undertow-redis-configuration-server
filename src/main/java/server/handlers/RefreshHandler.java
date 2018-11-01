package server.handlers;

import io.undertow.server.HttpHandler;
import io.undertow.server.HttpServerExchange;
import org.json.JSONWriter;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;

import java.util.List;
import java.util.function.Supplier;

public class RefreshHandler implements HttpHandler, JsonResponseHttpHandler {

    final JedisPool jedisPool;
    final Supplier<List<?>> readConfiguration;

    public RefreshHandler(JedisPool jedisPool, Supplier<List<?>> readConfiguration) {
        this.jedisPool = jedisPool;
        this.readConfiguration = readConfiguration;
    }

    @Override
    public void handleRequest(HttpServerExchange exchange) {

        if (exchange.isInIoThread()) {
            exchange.dispatch(this);
            return;
        }
        try (Jedis jedis = jedisPool.getResource()) {
            jedis.publish("configurations", JSONWriter.valueToString(readConfiguration.get()));
        } catch (Exception e) {
            sendJSON(exchange, e.getMessage(), 500);
        }

        sendJSON(exchange, "OK", 200);
    }
}
