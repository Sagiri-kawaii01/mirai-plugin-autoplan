package cn.cimoc.mirai.plugin.bili.command;

import cn.cimoc.mirai.plugin.AutoPlanPlugin;
import cn.cimoc.mirai.plugin.bili.data.BiliData;
import cn.cimoc.mirai.plugin.bili.data.Reward;
import cn.cimoc.mirai.plugin.bili.data.UserData;
import cn.cimoc.mirai.plugin.bili.data.UserMap;
import cn.cimoc.mirai.plugin.bili.util.BiliTaskUtil;
import cn.cimoc.mirai.plugin.bili.util.BiliWebUtil;
import cn.cimoc.mirai.plugin.util.HttpUtil;
import cn.cimoc.mirai.plugin.util.LogUtil;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import lombok.extern.slf4j.Slf4j;
import net.mamoe.mirai.console.command.CommandManager;
import net.mamoe.mirai.console.command.CommandOwner;
import net.mamoe.mirai.console.command.CommandSender;
import net.mamoe.mirai.console.command.ConsoleCommandOwner;
import net.mamoe.mirai.console.command.java.JRawCommand;
import net.mamoe.mirai.console.plugin.PluginManager;
import net.mamoe.mirai.message.data.MessageChain;
import net.mamoe.mirai.message.data.PlainText;
import org.jetbrains.annotations.NotNull;

import java.util.Objects;

/**
 * @author LGZ
 * <p>
 */
@Slf4j
public class RunCommand extends JRawCommand {
    public RunCommand() {
        super(ConsoleCommandOwner.INSTANCE, "biliRun", "biliRun");
        setDescription("立刻执行b站计划任务");
    }

    @Override
    public void onCommand(@NotNull CommandSender sender, @NotNull MessageChain args) {
        long id = Objects.requireNonNull(sender.getUser()).getId();
        if (!UserMap.INSTANCE.map.get().containsKey(id)) {
            sender.sendMessage("您还未登录");
            CommandManager.INSTANCE.executeCommand(sender, new PlainText("/bililogin"), false);
        }
        // 获取本地存储的cookie
        Long dedeuserid = UserMap.INSTANCE.map.get().get(id);
        UserData userData = new UserData(dedeuserid);
        AutoPlanPlugin.INSTANCE.reloadPluginData(userData);
//        sender.sendMessage("uid=" + userData.dedeUserId.get());
//        sender.sendMessage("sessdata=" + userData.sessdata.get());
//        sender.sendMessage("bili_jct=" + userData.bilijct.get());
        boolean isLogin = false;
        BiliWebUtil webUtil = new BiliWebUtil();
        webUtil.setHttp(BiliTaskUtil.getHttpUtilWithCookies(userData));
        webUtil.setUserData(userData);
        webUtil.setLog(new LogUtil());
        try {
            // 判断cookie过期并重新登录
            if (!(isLogin = BiliTaskUtil.login(webUtil))) {
                sender.sendMessage("Cookie已过期，请重新登录");
                CommandManager.INSTANCE.executeCommand(sender, new PlainText("/bililogin"), false);
                isLogin = BiliTaskUtil.login(webUtil);
            }
        } catch (Exception e) {
            log.error(e.getMessage());
        }
        // 登录成功开始执行计划任务
        if (isLogin) {
            BiliTaskUtil.run(webUtil);
        }
        sender.sendMessage(webUtil.log.getLog());
    }
}
