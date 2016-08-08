package com.wangjunneil.schedule.utility;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;

/**
 * Created by wangjun on 8/1/16.
 */
public final class HttpUtil {
    private HttpUtil() {}
    public static String get(String urlPath) {
        StringBuffer sb = new StringBuffer();
        try {
            URL url = new URL(urlPath);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestProperty("Accept-Charset","utf-8");
            conn.setRequestMethod("POST");
            int responseCode = conn.getResponseCode();
            if (responseCode != 200) {

            }

            InputStream is = conn.getInputStream();

            byte[] buff = new byte[1024];
            int len;
            while ((len = is.read(buff)) != -1) {
                sb.append(new String(buff, 0, len));
            }
            is.close();
            conn.disconnect();
        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (ProtocolException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return sb.toString();
    }
}
