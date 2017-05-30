package com.syslab.caddyless.filemanager.utils;

/**
 * Created by caddyless on 2017/4/21.
 */

public class Message {
    private int iconId;
    private String fileName;
    private String time;
    private String appName;
    public Message(int iconId,String fileName,String time,String appName){
        this.appName=appName;
        this.fileName=fileName;
        this.iconId=iconId;
        this.time=time;
    }
    public String getFileName(){
        return fileName;
    }
    public String getAppName(){
        return appName;
    }
    public String getTime(){
        return time;
    }
    public int getIconId(){
        return iconId;
    }
}
