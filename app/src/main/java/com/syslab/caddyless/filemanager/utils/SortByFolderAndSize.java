package com.syslab.caddyless.filemanager.utils;

import android.annotation.TargetApi;
import android.os.Build;

import java.io.File;
import java.util.Comparator;

/**
 * Created by caddyless on 2017/5/15.
 */

public class SortByFolderAndSize implements Comparator<File> {
    boolean frist;
    boolean second;

    public SortByFolderAndSize(boolean fristSequence, boolean secondSequence){
        this.frist=fristSequence;
        this.second=secondSequence;
    }

    @TargetApi(Build.VERSION_CODES.KITKAT)
    @Override
    public int compare(File lhs, File rhs) {
        if(frist){
            if(!lhs.isFile()&&rhs.isFile()){
                return -1;
            }
            if(lhs.isFile()&&!rhs.isFile()){
                return 1;
            }
        }
        else{
            if(!lhs.isFile()&&rhs.isFile()){
                return 1;
            }
            if(lhs.isFile()&&!rhs.isFile()){
                return -1;
            }
        }
        if(second){
            if(lhs.isFile()&&rhs.isFile()){
                return -Long.compare(lhs.length(),rhs.length());
            }
            else{
                if(lhs.isFile()&&rhs.isFile()){
                    return -Long.compare(lhs.length(),rhs.length());
                }
            }
        }
        return 0;
    }
}
