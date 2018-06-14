package cn.hellovega.zhimingdi.model.network;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;
import com.google.gson.JsonPrimitive;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;

import org.joda.time.DateTime;

import java.lang.reflect.Type;

/**
 * Created by vega on 3/2/18.
 */

public class NetworkDefine {
    //    URL and Useragent
    public static final String HOST_BASE_URL = "http://hellovega.cn/";
    public static final String ZHIMINGDI_BASE_URL = HOST_BASE_URL + "zhimingdi/";
    public static final String AVATER_PATH_PREFIX = "avater/";
    public static final String PIC_QUERY_URl =ZHIMINGDI_BASE_URL + "pic.php?date=";
    public static final String VERSION_URL =ZHIMINGDI_BASE_URL+"version.php";
    public static final String BG_QUERY_SUFFIX ="&type=1";  //背景图
    public static final String SM_QUERY_SUFFIX ="&type=0";  //缩略图
    public static final String USER_AGENT = "Mozilla/5.0 (Windows NT 10.0; WOW64)";
    public static final boolean DEBUG = true;

    //others
    public static final Gson gson = new GsonBuilder()
            .registerTypeAdapter(DateTime.class, new DateTimeTypeAdapter())
            .create();


    private static class DateTimeTypeAdapter implements JsonSerializer<DateTime>, JsonDeserializer<DateTime> {

        @Override
        public JsonElement serialize(DateTime src, Type typeOfSrc, JsonSerializationContext context) {
            return new JsonPrimitive(src.toString());
        }

        @Override
        public DateTime deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
            return new DateTime(json.getAsString());
        }

    }

}
