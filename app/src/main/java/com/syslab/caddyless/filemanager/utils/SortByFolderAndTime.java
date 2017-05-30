package com.syslab.caddyless.filemanager.utils;

import java.io.File;
import java.util.Comparator;

/**
 * Created by caddyless on 2017/5/15.
 */

public class SortByFolderAndTime implements Comparator<File> {
    boolean frist;
    boolean second;

    public SortByFolderAndTime(boolean frist,boolean second){
        this.frist=frist;
        this.second=second;
    }
    @Override
    public int compare(File lhs, File rhs) {
        if(frist){
            if((!lhs.isFile() && rhs.isFile())){
                return -1;
            }
            if(lhs.isFile() && !rhs.isFile()){
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
            if(!(lhs.isFile()^rhs.isFile())){
                return -Long.compare(lhs.lastModified(),rhs.lastModified());
            }
        }
        else{
            if (!(lhs.isFile()^rhs.isFile())) {
                return Long.compare(lhs.lastModified(),rhs.lastModified());
            }
        }
        return 0;
    }
}
