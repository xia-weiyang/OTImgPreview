package com.jiushig.imgpreview.utils;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * 解决Intent传值过大问题
 */
public class IntentMap {
    private static Map<String, Serializable> map = new HashMap<>();

    /**
     * 设置值
     *
     * @param value
     * @return key
     */
    public static String set(Serializable value) {
        final String uuid = getUUID();
        if (value == null) return uuid;

        try {
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            ObjectOutputStream oos = new ObjectOutputStream(baos);
            oos.writeObject(value);
            ByteArrayInputStream bais = new ByteArrayInputStream(baos.toByteArray());
            ObjectInputStream ois = new ObjectInputStream(bais);
            value = (Serializable) ois.readObject();
            oos.close();
            baos.close();
            ois.close();
            bais.close();
        } catch (Exception e) {
            e.printStackTrace();
        }

        map.put(uuid, value);
        return uuid;
    }

    /**
     * 取值
     *
     * @param key
     * @return
     */
    public static Serializable get(String key) {
        if (key == null) return null;
        return map.get(key);
    }

    public static void clear(){
        map.clear();
    }

    public static String getUUID() {
        return UUID.randomUUID().toString().replaceAll("-", "");
    }
}
