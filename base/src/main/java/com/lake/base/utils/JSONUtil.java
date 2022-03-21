package com.lake.base.utils;

import com.fasterxml.jackson.databind.ObjectMapper;

import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

/**
 * <p>Title: 旅客信息服务系统 - JSONUtil </p>
 *
 * <p>Description: 对json进行操作的工具类 </p>
 *
 * <p>Copyright: Copyright BSST(c) 2001 - 2015</p>
 *
 * <p>Company: 北京博能科技股份有限公司 </p>
 *
 * @author 王超然
 * @version 1.0.0
 */
public class JSONUtil {

    /**
     * 保存类与json之间的映射关系
     */
    private static Map<Class, ObjectMapper> mapper = new HashMap<>();

    /**
     * 解析json对象
     * @param json json对象
     * @param cls 需要转换为的java对象
     * @param <T> 对象泛型
     * @return 转换后的对象
     */
    public static <T> T parse(JSONObject json, Class<T> cls) {
        ObjectMapper objectMapper = null;
        T object = null;

        try {
            //是否有缓存的映射关系
            if (mapper.containsKey(cls)) {
                objectMapper = mapper.get(cls);
            } else {
                objectMapper = new ObjectMapper();
                mapper.put(cls, objectMapper);
            }

            //json转换为对象
            object = objectMapper.readValue(json.toString(), cls);
        } catch (Exception e) {
            e.printStackTrace();
        }

        return object;
    }

    /**
     * 转换对象为map
     * @param obj 对象
     * @param <T>
     * @return
     */
    public static <T> Map toMap(T obj) {
        Map params = null;
        try {
            ObjectMapper objectMapper;
            Class cls = obj.getClass();
            //是否有缓存的映射关系
            if (mapper.containsKey(cls)) {
                objectMapper = mapper.get(cls);
            } else {
                objectMapper = new ObjectMapper();
                mapper.put(cls, objectMapper);
            }
            //对象转换为map
            String jsonStr = objectMapper.writeValueAsString(obj);
            params = objectMapper.readValue(jsonStr, Map.class);

        } catch (Exception e) {
            e.printStackTrace();
        }

        return params;
    }
}
