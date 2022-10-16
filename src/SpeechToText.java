import com.google.gson.Gson;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.concurrent.TimeUnit;

public class SpeechToText {
    public static void main(String[] args) throws URISyntaxException, IOException, InterruptedException {
        // The first argument is the location of the audio file
        if (args.length < 1) {
            System.out.println("You must provide an audio resource.");
            System.exit(1);
        }

        var transcript = new Transcript();
        transcript.setAudio_url(args[0]);
        var gson = new Gson();
        var jsonRequest = gson.toJson(transcript);

        // Build the post request
        var postRequest = HttpRequest.newBuilder()
                .uri(new URI("https://api.assemblyai.com/v2/transcript"))
                .header("Authorization", Authorization.key)
                .POST(HttpRequest.BodyPublishers.ofString(jsonRequest))
                .build();

        // Send the post request to the web server and get the response
        HttpClient httpClient = HttpClient.newHttpClient();
        var postResponse = httpClient.send(postRequest, HttpResponse.BodyHandlers.ofString());

        if (postResponse.statusCode() != 200) {
            System.out.println("Provided audio file resource invalid or server is down.");
            System.exit(1);
        }

        transcript = gson.fromJson(postResponse.body(), Transcript.class);

        // Prepare the get request to send over and over again until we get the final transcript
        var getRequest = HttpRequest.newBuilder()
                .uri(new URI("https://api.assemblyai.com/v2/transcript/" + transcript.getId()))
                .header("Authorization", Authorization.key)
                .GET()
                .build();

        // Send the get requests periodically until audio processing has finished or an error has occurred
        while (transcript.getStatus().equals("queued") || transcript.getStatus().equals("processing")) {

            var getResponse = httpClient.send(getRequest, HttpResponse.BodyHandlers.ofString());
            transcript = gson.fromJson(getResponse.body(), Transcript.class);

            System.out.println("Processing...");

            // Wait for a second
            TimeUnit.SECONDS.sleep(1);
        }

        // Provide the result or information about the conclusion of speech to text
        if (transcript.getStatus().equals("completed")) {
            System.out.println("Processing has finished, audio to text is: \n" + transcript.getText());
        } else if (transcript.getStatus().equals("error")) {
            System.out.println("Error occurred when processing audio file! Check if the provided audio resource is valid");
        } else {
            System.out.println("Unknown state.");
        }
    }
}