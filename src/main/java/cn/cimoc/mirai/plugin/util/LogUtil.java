package cn.cimoc.mirai.plugin.util;

import lombok.extern.java.Log;

import java.util.ArrayList;
import java.util.Arrays;

/**
 * @author LGZ
 * <p>
 */
public class LogUtil {
    private final StringBuilder log;
    public LogUtil() {
        log = new StringBuilder();
    }

    public String getLog() {
        return log.toString();
    }

    public LogUtil appendLog(String text, Object... value) {
        ArrayList<Object> objects = new ArrayList<>(Arrays.asList(value));
        if (objects.size() == 0) {
            log.append(text).append("\n");
        } else {
            Object[] objs = new Object[objects.size()];
            for (int i = 0; i < objects.size(); i++) {
                objs[i] = objects.get(i);
            }
            log.append(String.format(text, objs)).append("\n");
        }
        return this;
    }
}
