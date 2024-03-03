package io.algopreporg.jokes;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.LambdaLogger;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import org.json.JSONObject;
import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.URL;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.Objects;

public class RandomJoke implements RequestHandler<Void, Void> {
    private static final String BOT_TOKEN = "TELEGRAM_BOT_TOKEN";
    private static final String CHAT_ID = "TELEGRAM_CHAT_ID";
    public static final String TELEGRAM_SEND_MESSAGE_API_URL = "https://api.telegram.org/bot%s/sendMessage";
    public static final String POST = "POST";
    public static final String CONTENT_TYPE = "Content-Type";
    public static final String APPLICATION_X_WWW_FORM_URLENCODED = "application/x-www-form-urlencoded";
    private static final String JOKE_OF_THE_DAY = "Joke of the day: \n%s";
    public static final String JOKES_RANDOM_URL = "https://api.chucknorris.io/jokes/random";
    public static final String VALUE = "value";

    @Override
    public Void handleRequest(Void input, Context context) {
        var logger = context.getLogger();
        HttpRequest httpRequest = HttpRequest.newBuilder()
                .uri(URI.create(JOKES_RANDOM_URL))
                .GET()
                .build();
        try {
            HttpResponse<String> response = HttpClient.newHttpClient().send(httpRequest, HttpResponse.BodyHandlers.ofString());
            String body = response.body();
            JSONObject jsonObject = new JSONObject(body);
            String joke = jsonObject.getString(VALUE);
            sendTelegramMessage(joke, logger);
        } catch (Exception e) {
            throw new RuntimeException("Can't send joke to Telegram", e);
        }
        return null;
    }

    public void sendTelegramMessage(String joke, LambdaLogger logger) throws Exception {
        String botToken = System.getenv(BOT_TOKEN);
        String chatId = System.getenv(CHAT_ID);

        if (Objects.nonNull(botToken) && Objects.nonNull(chatId)) {
            String telegramMessageApiUrl = String.format(TELEGRAM_SEND_MESSAGE_API_URL, botToken);

            String parameters = "chat_id=" + chatId +
                    "&text=" + JOKE_OF_THE_DAY.formatted(joke);

            HttpURLConnection connection = null;
            try {
                var url = new URL(telegramMessageApiUrl);
                connection = (HttpURLConnection) url.openConnection();
                connection.setRequestMethod(POST);
                connection.setDoOutput(true);
                connection.setRequestProperty(CONTENT_TYPE, APPLICATION_X_WWW_FORM_URLENCODED);

                try (var outputStream = new DataOutputStream(connection.getOutputStream())) {
                    outputStream.writeBytes(parameters);
                    outputStream.flush();
                }

                int responseCode = connection.getResponseCode();
                if (responseCode == HttpURLConnection.HTTP_OK) {
                    try (var reader = new BufferedReader(new InputStreamReader(connection.getInputStream()))) {
                        String response = reader.lines()
                                .reduce(new StringBuilder(), StringBuilder::append, StringBuilder::append).toString();
                        logger.log("Message sent successfully. Response: " + response);
                    }
                } else {
                    logger.log("Failed to send message. Response code: " + responseCode);
                }
            } finally {
                if (connection != null) {
                    connection.disconnect();
                }
            }
        } else {
            logger.log("Skip sendMessage to telegram  botToken & chatId is null.");
        }
    }
}
