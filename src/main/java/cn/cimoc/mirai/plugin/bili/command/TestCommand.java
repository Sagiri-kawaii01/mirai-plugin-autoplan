package cn.cimoc.mirai.plugin.bili.command;

import cn.cimoc.mirai.plugin.AutoPlanPlugin;
import cn.cimoc.mirai.plugin.bili.data.UserData;
import cn.cimoc.mirai.plugin.bili.data.UserMap;
import lombok.extern.slf4j.Slf4j;
import net.mamoe.mirai.console.command.CommandContext;
import net.mamoe.mirai.console.command.CommandOwner;
import net.mamoe.mirai.console.command.ConsoleCommandOwner;
import net.mamoe.mirai.console.command.java.JRawCommand;
import net.mamoe.mirai.console.data.AbstractPluginData;
import net.mamoe.mirai.message.data.MessageChain;
import org.jetbrains.annotations.NotNull;

/**
 * @author LGZ
 * <p>
 */
@Slf4j
public class TestCommand extends JRawCommand {
    public TestCommand() {
        super(ConsoleCommandOwner.INSTANCE, "test", "test");
    }

    @Override
    public void onCommand(@NotNull CommandContext context, @NotNull MessageChain args) {
//        UserData userData = new UserData("391252241");
//        AutoPlanPlugin.INSTANCE.reloadPluginData(userData);
//        log.info(String.valueOf(userData.getValueNodes().size()));
//        for (var valueNode : userData.getValueNodes()) {
//            log.info(valueNode.getValueName() + "=" + valueNode.getValue());
//        }
        UserMap userMap = UserMap.INSTANCE;
        AutoPlanPlugin.INSTANCE.reloadPluginData(userMap);
        log.info(String.valueOf(userMap.getValueNodes().size()));
        for (var valueNode : userMap.getValueNodes()) {
            log.info(valueNode.getValueName() + "=" + valueNode.getValue());
        }
    }
}
