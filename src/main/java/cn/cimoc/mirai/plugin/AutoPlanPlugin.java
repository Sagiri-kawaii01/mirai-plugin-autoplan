package cn.cimoc.mirai.plugin;

import cn.cimoc.mirai.plugin.bili.command.LoginCommand;
import cn.cimoc.mirai.plugin.bili.command.RunCommand;
import cn.cimoc.mirai.plugin.bili.command.TestCommand;
import cn.cimoc.mirai.plugin.bili.data.UserData;
import cn.cimoc.mirai.plugin.bili.data.UserMap;
import net.mamoe.mirai.console.command.CommandManager;
import net.mamoe.mirai.console.plugin.jvm.JavaPlugin;
import net.mamoe.mirai.console.plugin.jvm.JvmPluginDescriptionBuilder;

/**
 * @author LGZ
 * <p>
 */
public class AutoPlanPlugin extends JavaPlugin {

    public static final AutoPlanPlugin INSTANCE = new AutoPlanPlugin();


    private AutoPlanPlugin() {
        super(new JvmPluginDescriptionBuilder("cn.cimoc.autoplan-plugin", "1.0.0")
                .author("lgz")
                .info("自动刷经验（目前只支持b站）")
                .build());
//        super(JvmPluginDescription.loadFromResource("plugin.yml"));
    }

    @Override
    public void onEnable() {
        CommandManager.INSTANCE.registerCommand(new LoginCommand(), false);
        CommandManager.INSTANCE.registerCommand(new TestCommand(), false);
        CommandManager.INSTANCE.registerCommand(new RunCommand(), false);
        reloadPluginData(UserMap.INSTANCE);
    }
}
