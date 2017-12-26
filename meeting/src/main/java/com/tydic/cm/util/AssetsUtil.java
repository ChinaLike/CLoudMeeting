package com.tydic.cm.util;

import android.content.Context;

import com.google.gson.Gson;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

/**
 * Created by like on 2017/12/26.
 */

public class AssetsUtil {

    /**
     * 从Assets下获取文件对象
     *
     * @param fileName
     * @return
     */
    public static <T> T getObjectFromAssets(String fileName, Class<T> clazz, Context context) {
        Gson gson = new Gson();
        T t= gson.fromJson(getStringFromAssets(fileName, context), clazz);
        return t;
    }

    /**
     * 获取字符串从Assets下
     *
     * @param fileName
     * @return
     */
    public static String getStringFromAssets(String fileName, Context context) {
        try {
            InputStreamReader inputStreamReader = new InputStreamReader(
                    context.getClassLoader().getResourceAsStream("assets/" + "VideoConfig.json"));
            BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
            String sLine;
            StringBuilder stringBuilder = new StringBuilder();
            while ((sLine = bufferedReader.readLine()) != null) {
                stringBuilder.append(sLine);
            }
            return stringBuilder.toString();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

}
