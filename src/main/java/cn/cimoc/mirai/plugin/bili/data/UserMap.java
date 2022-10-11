package cn.cimoc.mirai.plugin.bili.data;

import net.mamoe.mirai.console.data.AutoSavePluginDataHolder;
import net.mamoe.mirai.console.data.Value;
import net.mamoe.mirai.console.data.java.JavaAutoSavePluginData;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.Map;

/**
 * @author LGZ
 * <p>
 */
public class UserMap extends JavaAutoSavePluginData {

    public static final UserMap INSTANCE = new UserMap();

    public Value<Map<Long, Long>> map = typedValue("map", createKType(HashMap.class, createKType(Long.class), createKType(Long.class)));
    public UserMap() {
        super("bili_userMap");
    }
}
