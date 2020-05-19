package com.lake.banner.net;

import android.os.Environment;
import android.text.TextUtils;
import android.util.Log;

import com.lake.banner.uitls.Constants;
import com.lake.banner.uitls.ParameterizedTypeUtil;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.lang.reflect.Type;
import java.net.HttpURLConnection;
import java.net.URL;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.HashMap;
import java.util.Map;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSession;
import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

public abstract class HttpEvent {
    private HttpCallback.ProgressRequestHttpCallback progressRequestHttpCallback = null;//进度回调
    private HttpCallback.TimeRequestHttpCallback timeRequestHttpCallback = null;//携带服务器时间的回调

    <T> void httpRequest(HttpParam param, HttpCallback.RequestHttpCallback<T> callback) {
        Log.e("lake", param.toString());
        this.progressRequestHttpCallback = null;
        this.timeRequestHttpCallback = null;
        if (callback instanceof HttpCallback.ProgressRequestHttpCallback) {
            this.progressRequestHttpCallback = (HttpCallback.ProgressRequestHttpCallback) callback;
        }
        if (callback instanceof HttpCallback.TimeRequestHttpCallback) {
            this.timeRequestHttpCallback = (HttpCallback.TimeRequestHttpCallback) callback;
        }
        HttpURLConnection conn = null;
        try {
            URL url = new URL(param.getUrl());
            conn = (HttpURLConnection) url.openConnection();
            if (conn instanceof HttpsURLConnection){
                trustAllHosts((HttpsURLConnection) conn);
                ((HttpsURLConnection) conn).setHostnameVerifier(DO_NOT_VERIFY);
            }
            conn.setConnectTimeout(param.timeOut);//设置连接超时时间
            conn.setReadTimeout(param.readTimeOut);//设置读取超时
            HashMap<String, String> map = param.getHeader();
            if (map != null && map.size() > 0) {//设置头信息
                for (Map.Entry<String, String> entry : map.entrySet()) {
                    conn.setRequestProperty(entry.getKey(), entry.getValue());
                }
            }
            if (param.getTypeByInt() != HttpParam.GET) {
                conn.setRequestMethod(param.getType());
            }
            if (param.getTypeByInt() == HttpParam.POST || param.getTypeByInt() == HttpParam.PUT) {
                conn.setDoOutput(true);
                OutputStream out = conn.getOutputStream();
                if (!TextUtils.isEmpty(param.getParam())) {
                    outString(out, param.getParam());
                }
                if (param.getFile() != null) {
                    outFile(out, param.getFile(), progressRequestHttpCallback);
                }
                out.flush();
                out.close();
            }
            long webTime = conn.getDate();
            if (timeRequestHttpCallback != null)
                timeRequestHttpCallback.onTimeInfo(webTime);
//            Log.e("lake", "httpRequest: " + conn.getResponseCode());
            if (conn.getResponseCode() == HttpURLConnection.HTTP_OK) {
                T r = getResultType(conn, param, callback);
                callback.success(r);
            } else {
                callback.failed("HttpRequest failed state:" + conn.getResponseCode() + ";" + conn.getResponseMessage());
            }
        } catch (IOException e) {
            e.printStackTrace();
            Log.e("HttpClient", "httpRequest: " + e.toString());
            callback.failed("HttpRequest failed state:" + e.toString());
        } finally {
            if (conn != null)
                conn.disconnect();
        }
    }

    /**
     * 覆盖java默认的证书验证
     */
    private static final TrustManager[] trustAllCerts = new TrustManager[]{new X509TrustManager() {
        @Override
        public java.security.cert.X509Certificate[] getAcceptedIssuers() {
            return new java.security.cert.X509Certificate[]{};
        }

        @Override
        public void checkClientTrusted(X509Certificate[] chain, String authType)
                throws CertificateException {
        }

        @Override
        public void checkServerTrusted(X509Certificate[] chain, String authType)
                throws CertificateException {
        }
    }};

    /**
     * 设置不验证主机
     */
    private static final HostnameVerifier DO_NOT_VERIFY = new HostnameVerifier() {
        @Override
        public boolean verify(String hostname, SSLSession session) {
            return true;
        }
    };

    /**
     * 信任所有
     *
     * @param connection
     * @return
     */
    private SSLSocketFactory trustAllHosts(HttpsURLConnection connection) {
        SSLSocketFactory oldFactory = connection.getSSLSocketFactory();
        try {
            SSLContext sc = SSLContext.getInstance("TLS");
            sc.init(null, trustAllCerts, new java.security.SecureRandom());
            SSLSocketFactory newFactory = sc.getSocketFactory();
            connection.setSSLSocketFactory(newFactory);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return oldFactory;
    }

    //获取泛型的具体类型的结果
    @SuppressWarnings("unchecked")
    private <T> T getResultType(HttpURLConnection conn, HttpParam param, HttpCallback.RequestHttpCallback<T> callback) throws IOException {
        Type type = resolveLoadType(callback);
        InputStream inputStream = conn.getInputStream();
        if (File.class == type) {//文件
            String fileName = "";
            if (!TextUtils.isEmpty(param.getFileName())) {//获取设置的文件名
                fileName = param.getFileName();
            } else {//使用默认的文件名
                String urlFilePath = conn.getURL().getFile();
                fileName = urlFilePath.substring(urlFilePath.lastIndexOf(File.separatorChar) + 1);
            }
            File file = new File(getFilesDir(param.getFilePath()), fileName);
            if (!file.exists()) {
                file.createNewFile();
            }
            int contentLength = conn.getContentLength();

            inFile(inputStream, contentLength, file, progressRequestHttpCallback);
            inputStream.close();
            return (T) file;
        } else if (String.class == type) {//字符串
            inputStream = conn.getInputStream();
            String r = inString(inputStream);
            inputStream.close();
            return (T) r;
        } else {//返回实体类
            inputStream = conn.getInputStream();
            String r = inString(inputStream);
            inputStream.close();
            return (T) r;
        }
    }

    /**
     * 获取保存文件地址
     *
     * @param path
     * @return
     */
    private String getFilesDir(String path) {
        if (TextUtils.isEmpty(path)) {//如果保存文件地址为空，默认保存在固定的一个文件夹内，且文件后缀从返回头信息里读取
            path = Constants.DEFAULT_DOWNLOAD_DIR;
        }
        if (path.equals(Environment.getExternalStorageDirectory().getAbsolutePath())) {
            return path;
        }
        File file = new File(path);
        if (!file.exists()) {
            file.mkdirs();
        }
        return path;
    }

    /**
     * 检测传入的T类型
     *
     * @param callback
     * @param <T>
     * @return
     */
    private <T> Type resolveLoadType(HttpCallback.RequestHttpCallback<T> callback) {
        Class<?> callBackType = callback.getClass();
        Type loadType = ParameterizedTypeUtil.getParameterizedType(callBackType, HttpCallback.RequestHttpCallback.class, 0);
        Log.e("lake", "传入的返回值类型=" + loadType);
        return loadType;
    }

    /**
     * 写入字符
     *
     * @param out
     * @param param
     */
    private void outString(OutputStream out, String param) throws IOException {
        DataOutputStream os = new DataOutputStream(out);
        os.write(param.getBytes());
        os.flush();
        os.close();
    }

    /**
     * 写入文件
     *
     * @param out
     * @param file
     */
    private void outFile(OutputStream out, File file, HttpCallback.ProgressRequestHttpCallback callback) throws IOException {
        DataOutputStream os = new DataOutputStream(out);
        FileInputStream ins = new FileInputStream(file);
        byte[] bytes = new byte[1024];
        long length = file.length();
        int len;
        int totale = 0;
        while ((len = ins.read(bytes)) != -1) {
            totale += len;
            if (callback != null) {
                callback.progress((float) totale, (float) length);
            }
            os.write(bytes, 0, len);
        }
        ins.close();
        os.flush();
        os.close();
    }

    /**
     * 读入字符
     *
     * @throws IOException
     */
    private String inString(InputStream ins) throws IOException {
        InputStreamReader in = new InputStreamReader(ins);
        BufferedReader bf = new BufferedReader(in);
        String recieveData = null;
        String r = "";
        while ((recieveData = bf.readLine()) != null) {
            r += recieveData + "\n";
        }
        in.close();
        return r;
    }


    /**
     * 读入文件 需要检测冲突 并发同时写同一文件会有问题
     *
     * @throws IOException
     */
    private void inFile(InputStream ins, int length, File file, HttpCallback.ProgressRequestHttpCallback callback) throws IOException {
        FileOutputStream os = new FileOutputStream(file);
        Log.e("lake", "数据总长度=" + length);
        BufferedInputStream bfi = new BufferedInputStream(ins);
        int len;
        int totale = 0;
        byte[] bytes = new byte[1024];
        while ((len = bfi.read(bytes)) != -1) {
            totale += len;
            if (callback != null) {
                callback.progress((float) totale, (float) length);
            }
            os.write(bytes, 0, len);
        }
        os.close();
        bfi.close();
    }
}
