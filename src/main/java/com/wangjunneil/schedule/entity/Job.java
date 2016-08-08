package com.wangjunneil.schedule.entity;

import org.springframework.data.mongodb.core.mapping.Document;

import java.util.Date;

/**
 * Created by wangjun on 8/2/16.
 */
@Document(collection = "job_detail")
public class Job {
    private String platform;
    private String oprType;
    private Date executeTime;
    private String status;
    private String msg;

    public Job(String platform, String oprType, Date executeTime) {
        this.platform = platform;
        this.oprType = oprType;
        this.executeTime = executeTime;
    }

    public String getPlatform() {
        return platform;
    }

    public void setPlatform(String platform) {
        this.platform = platform;
    }

    public String getOprType() {
        return oprType;
    }

    public void setOprType(String oprType) {
        this.oprType = oprType;
    }

    public Date getExecuteTime() {
        return executeTime;
    }

    public void setExecuteTime(Date executeTime) {
        this.executeTime = executeTime;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }
}
