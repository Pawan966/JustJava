
import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.net.http.HttpTimeoutException;
import java.time.Duration;

public class ApiClient {

    private static final HttpClient httpClient = HttpClient.newBuilder()
            .connectTimeout(Duration.ofSeconds(5))
            .build();

    //private static final ObjectMapper objectMapper = new ObjectMapper();

    public static String sendPostRequest(String url, String requestBody) {

        try {
            // Convert Java object to JSON
            //String requestBody = objectMapper.writeValueAsString(requestObject);

            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(url))
                    .timeout(Duration.ofSeconds(10))
                    .header("Content-Type", "application/json")
                    .header("Accept", "application/json")
                    .header("Authorization", "Bearer YOUR_ACCESS_TOKEN")
                    .POST(HttpRequest.BodyPublishers.ofString(requestBody))
//                    .PUT(HttpRequest.BodyPublishers.ofString(requestBody))
//                    .GET()
                    .build();

            HttpResponse<String> response = httpClient.send(
                    request,
                    HttpResponse.BodyHandlers.ofString()
            );

            if (response.statusCode() >= 200 && response.statusCode() < 300) {
                return response.body();
            }

            throw new ApiException("Error making api call");

        } catch (HttpTimeoutException e) {
            throw new ApiException("API request timed out", e);

        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new ApiException("API request was interrupted", e);

        } catch (IOException e) {
            throw new ApiException("Failed to serialize request or communicate with API", e);
        }
    }
}
