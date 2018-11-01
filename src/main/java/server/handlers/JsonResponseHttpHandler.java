package server.handlers;

import io.undertow.server.HttpServerExchange;
import io.undertow.util.Headers;
import org.json.JSONWriter;

public interface JsonResponseHttpHandler {

    default void sendJSON(HttpServerExchange exchange, Object toWrite, int status) {
        String result = JSONWriter.valueToString(toWrite);
        exchange.getResponseHeaders().add(Headers.CONTENT_TYPE, "application/json");
        exchange.setStatusCode(status);
        exchange.getResponseSender().send(result);
    }
}
