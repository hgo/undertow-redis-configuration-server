package server.handlers;

import io.undertow.server.HttpHandler;
import io.undertow.server.HttpServerExchange;

import java.util.List;
import java.util.function.Supplier;

public class ConfigReadHandler implements HttpHandler, JsonResponseHttpHandler {

    final Supplier<List<?>> readConfiguration;

    public ConfigReadHandler(Supplier<List<?>> readConfiguration) {

        this.readConfiguration = readConfiguration;
    }

    @Override
    public void handleRequest(HttpServerExchange exchange) {

        if (exchange.isInIoThread()) {
            exchange.dispatch(this);
            return;
        }
        List<?> configurationViews = readConfiguration.get();
        sendJSON(exchange, configurationViews, 200);
    }
}
