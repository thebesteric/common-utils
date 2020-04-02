package org.wesoft.common.utils;

import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import javax.script.ScriptException;
import java.util.HashMap;
import java.util.Map;

/**
 * 脚本工具类
 *
 * @author Eric Joe
 * @version Ver 1.0
 * @build 2020-02-28 10:21
 */
public class ScriptUtils {

    private static ScriptEngineManager manager = new ScriptEngineManager();

    private static ScriptEngine engine = manager.getEngineByName("JavaScript");

    public static boolean eval(String script, Map<String, Object> variables) {
        boolean result = false;
        try {
            variables.forEach((key, value) -> {
                engine.put(key, value);
            });
            result = (boolean) engine.eval(script);
        } catch (ScriptException e) {
            e.printStackTrace();
        }
        return result;
    }

    public static boolean eval(String script, String key, Object value) {
        boolean result = false;
        try {
            engine.put(key, value);
            result = (boolean) engine.eval(script);
        } catch (ScriptException e) {
            e.printStackTrace();
        }
        return result;
    }

    public static void main(String[] args) {
        String script = "x>5 && x<=10";
        System.out.println(ScriptUtils.eval(script, "x", 11));

        script = "x>5 && x<=10 && y < 0";
        Map<String, Object> vars = new HashMap<>();
        vars.put("x", 6);
        vars.put("y", -1);
        System.out.println(ScriptUtils.eval(script, vars));
    }

}
