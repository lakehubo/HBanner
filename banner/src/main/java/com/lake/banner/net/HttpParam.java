package com.lake.banner.net;

import java.io.File;
import java.util.HashMap;

public class HttpParam {
    public static final int GET = 1;
    public static final int POST = 2;
    public static final int PUT = 3;

    private String url = null;//请求地址
    private String param = null;//请求参数
    private String filePath = null;//返回的文件保存地址
    private String fileName = null;//返回的文件保存名称
    private File file = null;//要上传的文件
    private int requestType = GET;//请求类型 默认为get
    public int timeOut = 60 * 1000;//超时时间
    public int readTimeOut = 60 * 1000;//独取时间
    private HashMap<String, String> requestHeader = new HashMap<>();

    public HttpParam() {
        requestHeader.put("Content-type", "application/json");
    }

    public HttpParam(String url) {
        this.url = url;
        requestHeader.put("Content-type", "application/json");
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

    public void setType(int type) {
        if (type < GET || type > PUT) {
            return;
        }
        this.requestType = type;
    }

    public int getTypeByInt() {
        return requestType;
    }

    public String getType() {
        if (requestType == GET) {
            return "GET";
        }
        if (requestType == POST) {
            return "POST";
        }
        if (requestType == PUT) {
            return "PUT";
        }
        return "GET";
    }

    public File getFile() {
        return file;
    }

    public void setFile(File file) {
        this.file = file;
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
                ", requestType=" + getType() +
                ", timeOut=" + timeOut +
                ", requestHeader=" + requestHeader +
                '}';
    }
}
