package com.longzx.blog.utils;

public class RedisKeyUtils {

    private static final String SPLIT = ":";
    private static final String HOTArticle = "HotArticle";

    public static String getHotScoreKey(){
        return HOTArticle + SPLIT + "score";
    }
}
