package net.ccbluex.liquidbounce;

import java.io.*;
import java.net.*;

public class WebUtils {
    public static String get(String url) throws IOException {
        HttpURLConnection con = (HttpURLConnection) new URL(url).openConnection();

        con.setRequestMethod("GET");
        con.setRequestProperty("User-Agent", "Mozilla/5.0");

        BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream()));
        String inputLine;
        StringBuilder response = new StringBuilder();

        while ((inputLine = in.readLine()) != null) {
            response.append(inputLine);
            response.append("\n");
        }

        in.close();

        return response.toString();
    }

    public static String readContent(final String stringURL) throws IOException {
        final HttpURLConnection httpConnection = stringURL.toLowerCase().startsWith("https://") ? (HttpURLConnection) new URL(stringURL).openConnection() : ((HttpURLConnection) new URL(stringURL).openConnection());
        httpConnection.setConnectTimeout(10000);
        httpConnection.setReadTimeout(10000);
        httpConnection.setRequestMethod("GET");
        httpConnection.setRequestProperty("User-Agent", "Mozilla/5.0 (Windows NT 6.1; WOW64; rv:25.0) Gecko/20100101 Firefox/25.0");
        HttpURLConnection.setFollowRedirects(true);
        httpConnection.setDoOutput(true);
        final BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(httpConnection.getInputStream()));
        final StringBuilder stringBuilder = new StringBuilder();
        String line;
        while ((line = bufferedReader.readLine()) != null) {
            stringBuilder.append(line).append("\n");
        }
        bufferedReader.close();
        return stringBuilder.toString();
    }
}
