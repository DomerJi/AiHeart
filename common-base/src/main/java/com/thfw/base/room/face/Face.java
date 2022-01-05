package com.thfw.base.room.face;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

/**
 * Author:pengs
 * Date: 2022/1/4 15:59
 * Describe:Todo
 */
@Entity(tableName = "face")
public class Face {

    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "face_id")
    private int faceId;

    private String uid;

    @ColumnInfo(name = "token")
    private String token;

    @ColumnInfo(name = "file_name")
    private String fileName;

    @ColumnInfo(name = "create_time")
    private long createTime;

    @ColumnInfo(name = "update_time")
    private long updateTime;

    public Face(String uid, String token, String fileName) {
        this.uid = uid;
        this.token = token;
        this.fileName = fileName;
        this.createTime = System.currentTimeMillis();
    }

    public int getFaceId() {
        return faceId;
    }

    public void setFaceId(int faceId) {
        this.faceId = faceId;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public long getCreateTime() {
        return createTime;
    }

    public void setCreateTime(long createTime) {
        this.createTime = createTime;
    }

    public long getUpdateTime() {
        return updateTime;
    }

    public void setUpdateTime(long updateTime) {
        this.updateTime = updateTime;
    }


    @Override
    public String toString() {
        return "Face{" +
                "faceId=" + faceId +
                ", uid='" + uid + '\'' +
                ", token='" + token + '\'' +
                ", fileName='" + fileName + '\'' +
                ", createTime=" + createTime +
                ", updateTime=" + updateTime +
                '}';
    }
}
