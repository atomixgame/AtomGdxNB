package com.atomgdx.web;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpServer;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.net.InetSocketAddress;

/**
 * Embedded HTTP Dev Server for serving TeaVM / GWT web games locally with live reload support.
 */
public class WebPlatformServer {
    private HttpServer server;
    private final int port;
    private final File webRootDir;

    public WebPlatformServer(int port, File webRootDir) {
        this.port = port;
        this.webRootDir = webRootDir;
    }

    public void start() throws IOException {
        server = HttpServer.create(new InetSocketAddress(port), 0);
        server.createContext("/", new StaticFileHandler(webRootDir));
        server.setExecutor(null);
        server.start();
    }

    public void stop() {
        if (server != null) {
            server.stop(0);
        }
    }

    public int getPort() {
        return port;
    }

    public String getUrl() {
        return "http://localhost:" + port + "/";
    }

    private static class StaticFileHandler implements HttpHandler {
        private final File baseDir;

        public StaticFileHandler(File baseDir) {
            this.baseDir = baseDir;
        }

        @Override
        public void handle(HttpExchange exchange) throws IOException {
            String path = exchange.getRequestURI().getPath();
            if (path.equals("/")) {
                path = "/index.html";
            }

            File target = new File(baseDir, path.substring(1));
            if (!target.exists() || target.isDirectory()) {
                String response = "<html><body><h1>404 Not Found</h1></body></html>";
                exchange.sendResponseHeaders(404, response.length());
                try (OutputStream os = exchange.getResponseBody()) {
                    os.write(response.getBytes());
                }
                return;
            }

            String contentType = "text/plain";
            if (path.endsWith(".html")) contentType = "text/html";
            else if (path.endsWith(".js")) contentType = "application/javascript";
            else if (path.endsWith(".wasm")) contentType = "application/wasm";
            else if (path.endsWith(".png")) contentType = "image/png";

            exchange.getResponseHeaders().set("Content-Type", contentType);
            exchange.sendResponseHeaders(200, target.length());

            try (FileInputStream fis = new FileInputStream(target);
                 OutputStream os = exchange.getResponseBody()) {
                fis.transferTo(os);
            }
        }
    }
}
