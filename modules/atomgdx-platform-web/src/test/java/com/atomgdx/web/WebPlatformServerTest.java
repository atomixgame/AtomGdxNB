package com.atomgdx.web;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.file.Files;

import static org.assertj.core.api.Assertions.assertThat;

class WebPlatformServerTest {

    @Test
    void testWebServerServingStaticFiles(@TempDir File tempDir) throws IOException, InterruptedException {
        Files.writeString(new File(tempDir, "index.html").toPath(), "<h1>AtomGdx Web Game</h1>");

        WebPlatformServer server = new WebPlatformServer(9123, tempDir);
        server.start();

        try {
            HttpClient client = HttpClient.newHttpClient();
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create("http://localhost:9123/index.html"))
                    .GET()
                    .build();

            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
            assertThat(response.statusCode()).isEqualTo(200);
            assertThat(response.body()).contains("AtomGdx Web Game");
        } finally {
            server.stop();
        }
    }
}
