package cn.cimoc.mirai.plugin.bili.data;

import net.mamoe.mirai.console.data.Value;
import net.mamoe.mirai.console.data.java.JavaAutoSavePluginData;
import org.jetbrains.annotations.NotNull;

/**
 * @author LGZ
 * <p>
 */
public class UserData extends JavaAutoSavePluginData {

    public Value<Long> dedeUserId = value("DedeUserID", -1L);

    public Value<String> sessdata = value("SESSDATA", "");

    public Value<String> bilijct = value("bili_jct", "");

    public UserData(@NotNull String saveName) {
        super("bili_" + saveName);
    }

}
