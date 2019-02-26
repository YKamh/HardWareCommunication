package fangpai.cloudabull.com.usblibrary.utils;

import com.solidfire.gson.Gson;
import com.solidfire.gson.JsonArray;
import com.solidfire.gson.JsonElement;
import com.solidfire.gson.JsonParser;
import com.solidfire.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by Administrator on 2017/1/19.
 */

public class GsonUtils {
    public static String toJsonString(Object object){
        Gson gson = new Gson();
        return gson.toJson(object);
    }
    public static<T> T getObjFromJsonString(String jstr, Class<T> typeToken){
        Gson gson =new Gson();
        return gson.fromJson(jstr,typeToken);
    }
    public static <T> List<T> getListFormJsonString(String jstr, Class<T> typeTokem){
        JsonParser parser = new JsonParser();
        JsonArray jsonElements = parser.parse(jstr).getAsJsonArray();
        Gson gson =new Gson();
        List<T> results = new ArrayList<>();

        for (JsonElement element :jsonElements){
            T t = gson.fromJson(element,typeTokem);
            results.add(t);
        }
        return results;

    }

    public static <T> Map<String,T> getMapFormJsonString(String jstr){

        Gson gson = new Gson();
        Type type = new TypeToken<HashMap<String, T>>() {
        }.getType();
        Map<String,T> map = gson.fromJson(jstr, type);

        return map;

    }

}
