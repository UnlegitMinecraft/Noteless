package net.ccbluex.liquidbounce.utils;

import antiskidderobfuscator.NativeMethod;
import org.apache.commons.io.Charsets;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.Validate;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.*;
import java.util.List;
import java.util.Map;

public class HttpUtil {
    public static String sendGet(String url, String param) {
        String result = "";
        BufferedReader in = null;
        try {
            String urlNameString = url/* + "?" + param*/;
            URL realUrl = new URL(urlNameString);
            URLConnection connection = realUrl.openConnection();
            connection.setDoOutput(true);
            connection.setReadTimeout(981);
            connection.setRequestProperty("accept", "*/*");
            connection.setRequestProperty("connection", "Keep-Alive");
            connection.setRequestProperty("user-agent", "Mozilla/4.0 (compatible; MSIE 6.0; Windows NT 5.1;SV1)");
            connection.connect();
            Map<String, List<String>> map = connection.getHeaderFields();
            for (String key : map.keySet()) { }
            in = new BufferedReader(new InputStreamReader(
                    connection.getInputStream()));
            String line;
            while ((line = in.readLine()) != null) {
                result += line+"\n";
            }
        } catch (Exception e) {
        }
        finally {
            try {
                if (in != null) {
                    in.close();
                }
            } catch (Exception e2) {
            }
        }
        return result;
    }
    @NativeMethod
    public String performGetRequestWithoutStatic(URL url, boolean withKey) throws IOException {
        Validate.notNull(url);



        HttpURLConnection connection = createUrlConnection(url);
        InputStream inputStream = null;
        connection.setRequestProperty("user-agent", "Mozilla/5.0 AppIeWebKit");

        if (withKey) {
            connection.setRequestProperty("xf-api-key", "LnM-qSeQqtJlJmJnVt76GhU-SoiolWs9");
        }

        String var6;
        try {
            String result;
            try {
                inputStream = connection.getInputStream();
                return IOUtils.toString(inputStream, Charsets.UTF_8);
            } catch (IOException var10) {
                IOUtils.closeQuietly(inputStream);
                inputStream = connection.getErrorStream();
                if (inputStream == null) {
                    throw var10;
                }
            }

            result = IOUtils.toString(inputStream, Charsets.UTF_8);
            var6 = result;
        } finally {
            IOUtils.closeQuietly(inputStream);
        }

        return var6;
    }

    public static String performGetRequest(URL url) throws IOException {
        return new HttpUtil().performGetRequestWithoutStatic(url, false);
    }
    public static HttpURLConnection createUrlConnection(URL url) throws IOException {
        Validate.notNull(url);
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        connection.setConnectTimeout(15000);
        connection.setReadTimeout(15000);
        connection.setUseCaches(false);
        return connection;
    }
    public static String webget(String url) throws IOException {
        HttpURLConnection con = (HttpURLConnection) new URL(url).openConnection();
        con.setRequestMethod("GET");
        con.setRequestProperty("User-Agent", "Mozilla/5.0");
        BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream()));
        String inputLine;
        StringBuilder response = new StringBuilder();
        while((inputLine = in.readLine())!= null) {
            response.append(inputLine);
            response.append("\n");
        }
        in.close();
        return response.toString();
    }
}