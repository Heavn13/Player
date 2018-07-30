package com.example.heavn.player.Live;

/**
 * Created by Administrator on 2017/4/27 0027.
 */

public class Live {

    /**
     * 直播电视台
     */
    public String live_name;
    /**
     * 直播地址
     */
    public String path;

    public Live(String live_name,String path){
        this.live_name = live_name;
        this.path = path;
    }
}