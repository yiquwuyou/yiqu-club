package com.yiquwuyou.oss.entity;

/**
 * 文件类
 * 
 * @author: yiquwuyou
 * @date: 2023/10/12
 */
public class FileInfo {

    // 文件名称

    private String fileName;

    // 是否是目录

    private Boolean directoryFlag;

    // 文件etag

    private String etag;

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public Boolean getDirectoryFlag() {
        return directoryFlag;
    }

    public void setDirectoryFlag(Boolean directoryFlag) {
        this.directoryFlag = directoryFlag;
    }

    public String getEtag() {
        return etag;
    }

    public void setEtag(String etag) {
        this.etag = etag;
    }
}