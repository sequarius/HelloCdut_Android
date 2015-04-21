package com.emptypointer.hellocdut.service;

import java.io.InputStream;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.conn.ClientConnectionManager;
import org.apache.http.conn.scheme.PlainSocketFactory;
import org.apache.http.conn.scheme.Scheme;
import org.apache.http.conn.scheme.SchemeRegistry;

import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.impl.conn.tsccm.ThreadSafeClientConnManager;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpProtocolParams;
import org.apache.http.protocol.HTTP;
import org.apache.http.util.EntityUtils;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;

import internal.org.apache.http.entity.mime.MultipartEntity;
import internal.org.apache.http.entity.mime.content.InputStreamBody;
import internal.org.apache.http.entity.mime.content.StringBody;

/**
 * emptypointer http请求的类封装
 *
 * @author Sequarius
 */

public class EPHttpService {

    private static HttpClient client;

    // 连接超时 ConnectionTimeoutException
    private static final int REQUEST_TIMEOUT = 10000;

    // 响应超时 SocketTimeoutException
    private static final int RESPONSE_TIMEOUT = 20000;

    // 从连接池中取出连接超时 ConnectionPoolTimeoutException
    // private static final int POOL_TIMEOUT = 5000;

    private static final String TAG = "CustomerHttpClient";

    private EPHttpService() {
    }

    public static synchronized HttpClient getInstance() {
        if (client == null) {
            BasicHttpParams params = new BasicHttpParams();

            HttpProtocolParams.setContentCharset(params, HTTP.UTF_8);

            HttpConnectionParams.setConnectionTimeout(params, REQUEST_TIMEOUT);
            HttpConnectionParams.setSoTimeout(params, RESPONSE_TIMEOUT);

            SchemeRegistry schReg = new SchemeRegistry();
            schReg.register(new Scheme("http", PlainSocketFactory
                    .getSocketFactory(), 80));

            ClientConnectionManager conMgr = new ThreadSafeClientConnManager(
                    params, schReg);

            client = new DefaultHttpClient(conMgr, params);
        }

        return client;
    }

    /**
     * 复杂类型的上传：文件 ＋ 字符串，需要使用MultipartEntity对象来封装数据。
     *
     * @return
     * @throws Exception
     */
    public static String customerPostUpdateString(String uri,
                                                  Map<String, String> params, InputStream file) throws Exception {

        Log.d(TAG, "File: " + file.available());
        Log.d(TAG, "URI: " + uri);
        Log.d(TAG, "Params: " + params.size());

        HttpPost post = new HttpPost(uri);

        MultipartEntity entity = new MultipartEntity();
        for (Map.Entry<String, String> entry : params.entrySet()) {
            String key = entry.getKey();
            String value = entry.getValue();
            entity.addPart(key, new StringBody(value, Charset.forName("UTF-8")));
        }

        entity.addPart("file", new InputStreamBody(file, "multipart/form-data",
                "test.jpg"));

        post.setEntity(entity);

        HttpClient client = getInstance();

        HttpResponse response = client.execute(post);

        int responseStatusCode = response.getStatusLine().getStatusCode();

        if (responseStatusCode != HttpStatus.SC_OK) {
            throw new EPRulesException("访问服务器出错。");
        }

        String results = EntityUtils.toString(response.getEntity(), HTTP.UTF_8);

        return results;
    }

    /**
     * 使用post的方式获取url的content
     *
     * @return
     * @throws Exception
     */
    public static String customerPostString(String uri,
                                            List<? extends NameValuePair> nameValuePairs) throws Exception {

        HttpPost post = new HttpPost(uri);

        if (nameValuePairs != null) {
            post.setEntity(new UrlEncodedFormEntity(nameValuePairs, HTTP.UTF_8));
        }
        HttpClient client = getInstance();

        HttpResponse response = client.execute(post);

        int responseStatusCode = response.getStatusLine().getStatusCode();

        if (responseStatusCode != HttpStatus.SC_OK) {
            Log.i(TAG, "errocode" + responseStatusCode);
            throw new EPRulesException("访问服务器出错。");
        }
        String results = EntityUtils.toString(response.getEntity(), HTTP.UTF_8);

        return results;
    }

    /**
     * 使用post的方式获取url的content
     *
     * @param uri
     * @param params
     * @return
     * @throws Exception
     */
    public static String oldCustomerPostString(String uri,
                                               NameValuePair... params) throws Exception {

        List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();
        if (params != null) {
            for (NameValuePair param : params) {
                nameValuePairs.add(param);
            }
        }
        HttpPost post = new HttpPost(uri);
        post.setEntity(new UrlEncodedFormEntity(nameValuePairs, HTTP.UTF_8));

        HttpClient client = getInstance();

        HttpResponse response = client.execute(post);

        int responseStatusCode = response.getStatusLine().getStatusCode();

        if (responseStatusCode != HttpStatus.SC_OK) {
            throw new EPRulesException("访问服务器出错。");
        }

        String results = EntityUtils.toString(response.getEntity(), HTTP.UTF_8);

        return results;
    }

    /**
     * 使用get的方式获取url的内容
     *
     * @param uri
     * @param params
     * @return
     * @throws Exception
     */
    public static String customerGetString(String uri,
                                           Map<String, String> params) throws Exception {
        int i = 0;
        if (params != null) {
            for (Map.Entry<String, String> entry : params.entrySet()) {
                if (i == 0) {
                    uri += "?" + entry.getKey() + "=" + entry.getValue();
                } else {
                    uri += "&" + entry.getKey() + "=" + entry.getValue();
                }
                i++;
            }
        }
        Log.d(TAG, uri);

        HttpGet get = new HttpGet(uri);

        HttpClient client = getInstance();

        HttpResponse response = client.execute(get);

        int responseStatusCode = response.getStatusLine().getStatusCode();

        if (responseStatusCode != HttpStatus.SC_OK) {
            throw new EPRulesException("访问服务器出错。");
        }

        String results = EntityUtils.toString(response.getEntity(), HTTP.UTF_8);

        return results;

    }

    /**
     * 使用get的方式获取url的图片
     *
     * @param uri
     * @param params
     * @return
     * @throws Exception
     */
    public static Bitmap customerGetBitmap(String uri,
                                           Map<String, String> params) throws Exception {
        int i = 0;
        if (params != null) {
            for (Map.Entry<String, String> entry : params.entrySet()) {
                if (i == 0) {
                    uri += "?" + entry.getKey() + "=" + entry.getValue();
                } else {
                    uri += "&" + entry.getKey() + "=" + entry.getValue();
                }
                i++;
            }
        }
        Log.d(TAG, uri);

        HttpGet get = new HttpGet(uri);

        HttpClient client = getInstance();

        HttpResponse response = client.execute(get);

        int responseStatusCode = response.getStatusLine().getStatusCode();

        Log.d(TAG, "响应状态码：" + String.valueOf(responseStatusCode));

        if (responseStatusCode != HttpStatus.SC_OK) {
            throw new EPRulesException("访问服务器出错。");
        }

        Bitmap bitmap = null;
        InputStream in = null;
        try {
            in = response.getEntity().getContent();
            bitmap = BitmapFactory.decodeStream(in);
        } finally {
            if (in != null) {
                in.close();
            }
        }
        return bitmap;

    }
}
