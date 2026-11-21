
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

    public static String sendRequest(String url, String httpMethod, String requestBody){

        try {
            // Convert Java object to JSON
            //String requestBody = objectMapper.writeValueAsString(requestObject);

            HttpRequest.BodyPublisher bodyPublisher = (requestBody == null)
                    ? HttpRequest.BodyPublishers.noBody()
                    : HttpRequest.BodyPublishers.ofString(requestBody);

            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(url))
                    .timeout(Duration.ofSeconds(10))
                    .header("Content-Type", "application/json")
                    .header("Accept", "application/json")
                    .header("Authorization", "Bearer YOUR_ACCESS_TOKEN")
                    .method(httpMethod, bodyPublisher)
                    .build();

            HttpResponse<String> response = httpClient.send(
                    request,
                    HttpResponse.BodyHandlers.ofString()
            );

            if (response.statusCode() >= 200 && response.statusCode() < 300) {
                //  objectMapper.readValue(response.body(), responseType);
                return response.body();
            }

            throw new ApiException(
                    response.statusCode(),
                    response.body(),
                    "API call failed with status " + response.statusCode()
            );

        } catch (HttpTimeoutException e) {
            throw new ApiException("API request timed out", e);

        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new ApiException("API request was interrupted", e);

        } catch (Exception e) {
            throw new ApiException("Generic exception caught", e);
        }
    }
}
