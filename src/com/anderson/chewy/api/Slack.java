package com.anderson.chewy.api;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

public class Slack {

    private final String TOKEN = "****-********-********-********";

    public static PayloadBuilder payload() {
        return new PayloadBuilder();
    }

    public void chatPostMessage(Payload payload) {
        chatPostMessage(payload.data());
    }

    private void chatPostMessage(String text) {
        try {
            var uri = new URI("https://slack.com/api/chat.postMessage");
            var request = HttpRequest.newBuilder()
                    .uri(uri)
                    .header("Authorization", "Bearer " + TOKEN)
                    .header("Content-Type", "application/json; utf-8")
                    .POST(HttpRequest.BodyPublishers.ofString(text))
                    .build();

            HttpResponse<String> response = getResponse(request);
            if (response.statusCode() != 200 || response.body().contains("\"ok\":false")) {
                System.out.println("Failed to send Slack message.");
                printErrorMessage(response);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public String lookupByEmail(String email) {
        try {
            var uri = new URI("https://slack.com/api/users.lookupByEmail?email=" + email);
            var request = HttpRequest.newBuilder()
                    .uri(uri)
                    .header("Authorization", "Bearer " + TOKEN)
                    .header("Content-Type", "application/json; utf-8")
                    .GET()
                    .build();

            HttpResponse<String> response = getResponse(request);
            if (response.statusCode() != 200) {
                System.out.println("Failed to find Slack user.");
                printErrorMessage(response);
                return null;
            }
            if (response.body().contains("\"ok\":false")) {
                return null;
            }

            return getDisplayNameFromJSON(response.body());

        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    private String getDisplayNameFromJSON(String json) {
        final String DISPLAY_NAME_KEY = "\"display_name\":\"";
        int displayNameIndex = json.indexOf(DISPLAY_NAME_KEY);

        if (displayNameIndex == -1) {
            return null;
        }

        var startOfDisplayName = displayNameIndex + DISPLAY_NAME_KEY.length();
        var endOfDisplayName = json.indexOf("\"", startOfDisplayName);

        return (endOfDisplayName == -1) ? null : json.substring(startOfDisplayName, endOfDisplayName);
    }

    private void printErrorMessage(HttpResponse<String> response) {
        System.out.println("Error: " + response.statusCode());
        System.out.println("Response: \n" + response.body());
    }

    private HttpResponse<String> getResponse(HttpRequest request) throws IOException, InterruptedException {
        return HttpClient.newHttpClient()
                .send(request, HttpResponse.BodyHandlers.ofString());
    }

    public static class PayloadBuilder {

        private final StringBuilder data;

        public PayloadBuilder() {
            String DEFAULT_CHANNEL = "fll7-astro-hub-alerts-test-channel";
            data = new StringBuilder();

            data.append("{");
            data.append("\"channel\":\"").append(DEFAULT_CHANNEL).append("\",");
            data.append("\"blocks\":[");
        }

        public PayloadBuilder channel(String channel) {
            if (channel == null) {
                throw new NullPointerException("Payload's channel can not be null.");
            }

            int index = data.indexOf("\"channel\":\"");
            int endIndex = data.indexOf("\",", index);
            data.replace(index + 10, endIndex, channel);
            return this;
        }

        public PayloadBuilder header(String header) {
            if (header == null) {
                throw new NullPointerException("Payload's header can not be null.");
            }

            data.append("{\"type\":\"header\",\"text\":{");
            data.append("\"type\":\"plain_text\",");
            data.append("\"text\":\"").append(header).append("\"}");
            data.append("},");
            return this;
        }

        public PayloadBuilder text(String text) {
            if (text == null) {
                throw new NullPointerException("Payload's text can not be null.");
            }

            data.append("{\"type\":\"section\",\"text\":{");
            data.append("\"type\":\"mrkdwn\",");
            data.append("\"text\":\"").append(text).append("\"}");
            data.append("}");
            return this;
        }

        public Payload build() {
            data.append("]}");
            return new Payload(data.toString());
        }
    }
}
