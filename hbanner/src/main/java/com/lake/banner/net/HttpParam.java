package com.lake.banner.net;

import java.util.HashMap;

public class HttpParam {
    public static final int GET = 1;

    private String url = null;//请求地址
    private String param = null;//请求参数
    private String filePath = null;//返回的文件保存地址
    private String fileName = null;//返回的文件保存名称
    public int timeOut = 60 * 1000;//超时时间
    public int readTimeOut = 60 * 1000;//读取时间
    private HashMap<String, String> requestHeader = new HashMap<>();

    public HttpParam() {
        requestHeader.put("Content-type", "multipart/form-data");
    }

    public HttpParam(String url) {
        this.url = url;
        requestHeader.put("Content-type", "multipart/form-data");
    }

    public String getUrl() {
        return url;
    }

    public void Url(String url) {
        this.url = url;
    }

    public void setHeader(String head, String value) {//设置头信息
        requestHeader.put(head, value);
    }

    public void setParam(String param) {//设置请求参数
        this.param = param;
    }

    public HashMap<String, String> getHeader() {//获取设置的头信息
        if (requestHeader != null && requestHeader.size() > 0) {
            return requestHeader;
        }
        return null;
    }

    public String getParam() {//获取参数
        return param;
    }

    public void setSavePath(String path) {// 设置文件保存地址
        filePath = path;
    }

    public String getFilePath() {
        return filePath;
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {//设置文件保存名称
        this.fileName = fileName;
    }

    @Override
    public String toString() {
        return "HttpParam{" +
                "url='" + url + '\'' +
                ", param='" + param + '\'' +
                ", filePath='" + filePath + '\'' +
                ", timeOut=" + timeOut +
                ", requestHeader=" + requestHeader +
                '}';
    }
}
