package com.inteink.common.utils;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.parser.Feature;
import com.alibaba.fastjson.serializer.SerializerFeature;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.codehaus.jackson.map.DeserializationConfig;
import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.map.type.TypeFactory;
import org.codehaus.jackson.type.JavaType;
import org.json.XML;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class JsonUtil {
  private static final Logger logger = LoggerFactory.getLogger(JsonUtil.class);
  
  @Deprecated
  public static Map<String, Object> toMap(String jsonStr) {
    Map<String, Object> argsMap;
    if (StringUtils.isEmpty(jsonStr)) {
      return new HashMap<>();
    }
    try {
      ObjectMapper om = new ObjectMapper();
      argsMap = (Map<String, Object>)om.readValue(jsonStr, Map.class);
    } catch (Exception ex) {
      if (jsonStr.contains("\\")) {
        try {
          jsonStr = jsonStr.replace("\\", "\\\\");
          ObjectMapper om = new ObjectMapper();
          argsMap = (Map<String, Object>)om.readValue(jsonStr, Map.class);
        } catch (Exception e) {
          logger.error("JSON转Map出错，jsonStr=" + jsonStr, e);
          argsMap = new HashMap<>();
        } 
      } else {
        logger.error("JSON转Map出错，jsonStr=" + jsonStr, ex);
        argsMap = new HashMap<>();
      } 
    } 
    return argsMap;
  }
  
  public static Map<String, Object> toMapFromJsonStr(Object obj) {
    return toMapFromJsonStr(StringUtils.toStringNotNull(obj));
  }
  
  public static Map<String, Object> toMapFromJsonStr(String jsonStr) {
    Map<String, Object> argsMap;
    if (StringUtils.isEmpty(jsonStr)) {
      return new HashMap<>();
    }
    try {
      argsMap = (Map<String, Object>)JSONObject.parseObject(jsonStr, Map.class);
    } catch (Exception ex) {
      if (jsonStr.contains("\\")) {
        try {
          jsonStr = jsonStr.replace("\\", "\\\\");
          argsMap = (Map<String, Object>)JSONObject.parseObject(jsonStr, Map.class);
        } catch (Exception e) {
          logger.error("JSON转Map出错，jsonStr=" + jsonStr, e);
          argsMap = new HashMap<>();
        } 
      } else {
        logger.error("JSON转Map出错，jsonStr=" + jsonStr, ex);
        argsMap = new HashMap<>();
      } 
    } 
    return argsMap;
  }
  
  public static Map<String, Object> toMapFromJsonStrSort(String jsonStr) {
    Map<String, Object> argsMap;
    if (StringUtils.isEmpty(jsonStr)) {
      return new HashMap<>();
    }
    try {
      argsMap = (Map<String, Object>)JSONObject.parseObject(jsonStr, Map.class, new Feature[] { Feature.OrderedField });
    } catch (Exception ex) {
      if (jsonStr.contains("\\")) {
        try {
          jsonStr = jsonStr.replace("\\", "\\\\");
          argsMap = (Map<String, Object>)JSONObject.parseObject(jsonStr, Map.class, new Feature[] { Feature.OrderedField });
        } catch (Exception e) {
          logger.error("JSON转Map出错，jsonStr=" + jsonStr, e);
          argsMap = new HashMap<>();
        } 
      } else {
        logger.error("JSON转Map出错，jsonStr=" + jsonStr, ex);
        argsMap = new HashMap<>();
      } 
    } 
    return argsMap;
  }
  
  public static void main(String[] args) {
    String a = "{\"a\":\"a\",\"e\":\"e\",\"b\":\"b\",\"d\":\"d\"}";
    System.err.println(toMapFromJsonStr(a));
  }
  
  @Deprecated
  public static String toJsonString(Object obj) {
    if (obj == null || "".equals(obj)) {
      return "";
    }
    ObjectMapper om = new ObjectMapper();
    try {
      return om.writeValueAsString(obj);
    } catch (Exception e) {
      logger.error("Json转对象出错，obj=" + obj, e);
      return null;
    } 
  }
  
  public static String toJsonFromObject(Object obj) {
    if (obj == null || "".equals(obj)) {
      return "";
    }
    try {
      return JSON.toJSONString(obj, new SerializerFeature[] { SerializerFeature.WriteNullStringAsEmpty, SerializerFeature.WriteNullBooleanAsFalse });
    } catch (Exception e) {
      logger.error("Json转对象出错，obj=" + obj, e);
      return null;
    } 
  }
  
  public static String toJsonFromObjectMapNull(Object obj) {
    if (obj == null || "".equals(obj)) {
      return "";
    }
    try {
      return JSON.toJSONString(obj, new SerializerFeature[] { SerializerFeature.WriteMapNullValue, SerializerFeature.WriteNullStringAsEmpty, SerializerFeature.WriteNullBooleanAsFalse });
    } catch (Exception e) {
      logger.error("Json转对象出错，obj=" + obj, e);
      return null;
    } 
  }
  
  public static String toSqlJsonFromObject(Object obj) {
    String sqlStr = toJsonFromObject(obj);
    return (sqlStr == null) ? null : sqlStr.replaceAll("\\\\", "\\\\\\\\");
  }
  
  @Deprecated
  public static <T> T fromString(String jsonStr, Class<T> c) {
    if (StringUtils.isEmpty(jsonStr)) {
      return null;
    }
    ObjectMapper om = new ObjectMapper();
    try {
      om.configure(DeserializationConfig.Feature.FAIL_ON_UNKNOWN_PROPERTIES, false);
      return (T)om.readValue(jsonStr, c);
    } catch (Exception ex) {
      if (jsonStr.contains("\\")) {
        try {
          jsonStr = jsonStr.replace("\\", "\\\\");
          om.configure(DeserializationConfig.Feature.FAIL_ON_UNKNOWN_PROPERTIES, false);
          return (T)om.readValue(jsonStr, c);
        } catch (Exception e) {
          logger.error("JSON转对象出错，jsonStr=" + jsonStr, e);
        } 
      } else {
        logger.error("JSON转对象出错，jsonStr=" + jsonStr, ex);
      } 
      return null;
    } 
  }
  
  public static <T> T toBeanFromStr(String jsonStr, Class<T> c) {
    if (StringUtils.isEmpty(jsonStr)) {
      return null;
    }
    try {
      return (T)JSONObject.parseObject(jsonStr, c);
    } catch (Exception ex) {
      if (jsonStr.contains("\\")) {
        try {
          jsonStr = jsonStr.replace("\\", "\\\\");
          return (T)JSONObject.parseObject(jsonStr, c);
        } catch (Exception e) {
          logger.error("JSON转对象出错，jsonStr=" + jsonStr, e);
        } 
      } else {
        logger.error("JSON转对象出错，jsonStr=" + jsonStr, ex);
      } 
      return null;
    } 
  }
  
  public static <T> T toBeanFromStrSort(String jsonStr, Class<T> c) {
    if (StringUtils.isEmpty(jsonStr)) {
      return null;
    }
    try {
      return (T)JSONObject.parseObject(jsonStr, c, new Feature[] { Feature.OrderedField });
    } catch (Exception ex) {
      if (jsonStr.contains("\\")) {
        try {
          jsonStr = jsonStr.replace("\\", "\\\\");
          return (T)JSONObject.parseObject(jsonStr, c, new Feature[] { Feature.OrderedField });
        } catch (Exception e) {
          logger.error("JSON转对象出错，jsonStr=" + jsonStr, e);
        } 
      } else {
        logger.error("JSON转对象出错，jsonStr=" + jsonStr, ex);
      } 
      return null;
    } 
  }
  
  public static <T> List<T> toList(Object obj, Class<T> c) {
    return (obj instanceof String) ? toList((String)obj, c) : toList(toJsonFromObject(obj), c);
  }
  
  public static <T> List<T> toList(String jsonStr, Class<T> c) {
    if (StringUtils.isEmpty(jsonStr)) {
      return null;
    }
    ObjectMapper om = new ObjectMapper();
    TypeFactory typeFactory = TypeFactory.defaultInstance();
    try {
      om.configure(DeserializationConfig.Feature.FAIL_ON_UNKNOWN_PROPERTIES, false);
      return (List<T>)om.readValue(jsonStr, (JavaType)typeFactory.constructCollectionType(List.class, c));
    } catch (Exception ex) {
      if (jsonStr.contains("\\")) {
        try {
          jsonStr = jsonStr.replace("\\", "\\\\");
          om.configure(DeserializationConfig.Feature.FAIL_ON_UNKNOWN_PROPERTIES, false);
          return (List<T>)om.readValue(jsonStr, (JavaType)typeFactory.constructCollectionType(List.class, c));
        } catch (Exception e) {
          logger.error("JSON转List出错，jsonStr=" + jsonStr, e);
        } 
      } else {
        logger.error("JSON转List出错，jsonStr=" + jsonStr, ex);
      } 
      return null;
    } 
  }
  
  public static <T> List<T> toMapList(String jsonStr, Class<Map> c) {
    if (StringUtils.isEmpty(jsonStr)) {
      return null;
    }
    ObjectMapper om = new ObjectMapper();
    TypeFactory typeFactory = TypeFactory.defaultInstance();
    try {
      om.configure(DeserializationConfig.Feature.FAIL_ON_UNKNOWN_PROPERTIES, false);
      return (List<T>)om.readValue(jsonStr, (JavaType)typeFactory.constructCollectionType(List.class, c));
    } catch (Exception ex) {
      if (jsonStr.contains("\\")) {
        try {
          jsonStr = jsonStr.replace("\\", "\\\\");
          om.configure(DeserializationConfig.Feature.FAIL_ON_UNKNOWN_PROPERTIES, false);
          return (List<T>)om.readValue(jsonStr, (JavaType)typeFactory.constructCollectionType(List.class, c));
        } catch (Exception e) {
          logger.error("JSON转List出错，jsonStr=" + jsonStr, e);
        } 
      } else {
        logger.error("JSON转List出错，jsonStr=" + jsonStr, ex);
      } 
      return null;
    } 
  }
  
  public static <T> List<T> toListFromJsonStr(String jsonStr) {
    List<T> argsMap;
    if (StringUtils.isEmpty(jsonStr)) {
      return new ArrayList<>();
    }
    try {
      argsMap = (List<T>)JSONObject.parseObject(jsonStr, List.class);
    } catch (Exception ex) {
      if (jsonStr.contains("\\")) {
        try {
          jsonStr = jsonStr.replace("\\", "\\\\");
          return (List<T>)JSONObject.parseObject(jsonStr, List.class);
        } catch (Exception e) {
          logger.error("JSON转List出错，jsonStr=" + jsonStr, e);
        } 
      } else {
        logger.error("JSON转List出错，jsonStr=" + jsonStr, ex);
      } 
      return new ArrayList<>();
    } 
    return argsMap;
  }
  
  public static <T> List<T> toListFromJsonStrSort(String jsonStr) {
    List<T> argsMap;
    if (StringUtils.isEmpty(jsonStr)) {
      return new ArrayList<>();
    }
    try {
      argsMap = (List<T>)JSONObject.parseObject(jsonStr, List.class, new Feature[] { Feature.OrderedField });
    } catch (Exception ex) {
      if (jsonStr.contains("\\")) {
        try {
          jsonStr = jsonStr.replace("\\", "\\\\");
          return (List<T>)JSONObject.parseObject(jsonStr, List.class, new Feature[] { Feature.OrderedField });
        } catch (Exception e) {
          logger.error("JSON转List出错，jsonStr=" + jsonStr, e);
        } 
      } else {
        logger.error("JSON转List出错，jsonStr=" + jsonStr, ex);
      } 
      return new ArrayList<>();
    } 
    return argsMap;
  }
}
