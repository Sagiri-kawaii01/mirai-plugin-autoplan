package cn.cimoc.mirai.plugin.util;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class JsonUtil {

    /**
     * json字符串转map集合
     */
    public static HashMap<String, Object> json2Map(String jsonStr){
        return JSON.parseObject(jsonStr, HashMap.class);
    }

    /**
     * map转json字符串
     */
    public static String map2Json(Map<String, Object> map){
        return JSON.toJSONString(map);
    }

    /**
     * json字符串转换成对象
     */
    public static <T> T json2Bean(String jsonString, Class<T> cls){
        T t = null;
        try {
            t = JSON.parseObject(jsonString,cls);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return t;
    }

    /**
     * 对象转换成json字符串
     */
    public static String bean2Json(Object obj){
        return JSON.toJSONString(obj);
    }

    /**
     * json字符串转换成List集合
     * (需要实体类)
     */
    public static <T> List<T> json2List(String jsonString,Class cls){
        List<T> list = null;
        try {
            list = JSON.parseArray(jsonString, cls);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return list;
    }

    /**
     * json字符串转换成ArrayList集合
     * (需要实体类)
     */
    public static <T> ArrayList<T> json2ArrayList(String jsonString,Class cls){
        ArrayList<T> list = null;
        try {
            list = (ArrayList<T>) JSON.parseArray(jsonString, cls);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return list;
    }

    /**
     * List集合转换成json字符串
     */
    public static String list2Json(List<?> obj){
        return JSONArray.toJSONString(obj, true);
    }

    /**
     * json转List
     * (不需要实体类)
     */
    public static JSONArray json2List(String jsonStr){
        return JSON.parseArray(jsonStr);
    }

}
