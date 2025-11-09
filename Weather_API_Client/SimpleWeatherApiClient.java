import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.Scanner;

public class SimpleWeatherApiClient {

    private static final String API_KEY = "76e8ea98d312d26404fb81af82474bcb";
    private static final String BASE_URL = "https://api.openweathermap.org/data/2.5/weather";

    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);
        System.out.println("=== Weather Info ===");
        System.out.print("Enter city name: ");
        String city = sc.nextLine();
        sc.close();

        String url = BASE_URL + "?q=" + city + "&appid=" + API_KEY + "&units=metric";

        try {
            HttpClient client = HttpClient.newHttpClient();
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(url))
                    .build();

            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

            if (response.statusCode() == 200) {
                String json = response.body();
                showWeatherInfo(city, json);
            } else {
                System.out.println("❌ Failed to fetch data! Status Code: " + response.statusCode());
            }

        } catch (IOException | InterruptedException e) {
            System.out.println("⚠️ Error: " + e.getMessage());
        }
    }

    private static void showWeatherInfo(String city, String json) {
        String temp = getValue(json, "\"temp\":");
        String humidity = getValue(json, "\"humidity\":");
        String desc = getValue(json, "\"description\":\"");

        if (desc != null) {
            desc = desc.replace("\"", "").trim();
        }

        System.out.println("\n🌍 City: " + city);
        System.out.println("🌡️ Temperature: " + temp + " °C");
        System.out.println("💧 Humidity: " + humidity + "%");
        System.out.println("☁️ Condition: " + capitalize(desc));
    }

    private static String getValue(String json, String key) {
        int start = json.indexOf(key);
        if (start == -1) return "N/A";
        start += key.length();
        int end = json.indexOf(",", start);
        if (end == -1) end = json.indexOf("}", start);
        return json.substring(start, end).replace("\"", "").trim();
    }

    private static String capitalize(String text) {
        if (text == null || text.isEmpty()) return text;
        String[] words = text.split(" ");
        StringBuilder sb = new StringBuilder();
        for (String w : words) {
            sb.append(Character.toUpperCase(w.charAt(0)))
              .append(w.substring(1).toLowerCase())
              .append(" ");
        }
        return sb.toString().trim();
    }
}
