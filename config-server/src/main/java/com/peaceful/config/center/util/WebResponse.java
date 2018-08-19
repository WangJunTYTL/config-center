package com.peaceful.config.center.util;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;

import org.apache.velocity.VelocityContext;
import org.apache.velocity.app.Velocity;
import org.apache.velocity.runtime.resource.loader.ClasspathResourceLoader;

import java.io.StringWriter;
import java.util.Map;

/**
 * Created by Jun on 2018/7/20.
 */
public class WebResponse {

    static {
        Velocity.setProperty(org.apache.velocity.runtime.RuntimeConstants.RESOURCE_LOADER, "classpath");
        Velocity.setProperty("classpath.resource.loader.class", ClasspathResourceLoader.class.getName());
        Velocity.init();

    }

    public static String HTML(String templateName, Map<String, Object> data) {
        VelocityContext context = new VelocityContext();
        if (data != null && !data.isEmpty()) {
            data.forEach((k, v) -> context.put(k, v));
        }
        StringWriter writer = new StringWriter();
        Velocity.mergeTemplate(templateName, "UTF-8", context, writer);
        return writer.toString();
    }

    public static Object JSON(int code, String message) {
        JSONObject data = new JSONObject();
        data.put("code", code);
        data.put("message", message);
        return data;
    }
}
