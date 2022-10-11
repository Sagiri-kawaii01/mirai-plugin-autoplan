package cn.cimoc.mirai.plugin.bili.command;

import cn.cimoc.mirai.plugin.AutoPlanPlugin;
import cn.cimoc.mirai.plugin.bili.data.UserData;
import cn.cimoc.mirai.plugin.bili.data.UserMap;
import cn.cimoc.mirai.plugin.bili.pojo.QRCode;
import cn.cimoc.mirai.plugin.bili.util.BiliTaskUtil;
import lombok.extern.slf4j.Slf4j;
import net.mamoe.mirai.console.command.CommandSender;
import net.mamoe.mirai.console.command.ConsoleCommandOwner;
import net.mamoe.mirai.console.command.java.JRawCommand;
import net.mamoe.mirai.console.data.AbstractPluginData;
import net.mamoe.mirai.console.data.PluginData;
import net.mamoe.mirai.console.data.PluginDataStorage;
import net.mamoe.mirai.message.data.MessageChain;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.util.List;
import java.util.Map;


/**
 * @author LGZ
 * <p>
 */
@Slf4j
public class LoginCommand extends JRawCommand {


    public LoginCommand() {
        super(ConsoleCommandOwner.INSTANCE, "biliLogin", "biliLogin");
    }


    @Override
    public void onCommand(@NotNull CommandSender sender, @NotNull MessageChain args) {
        try {
            QRCode loginQR = BiliTaskUtil.getLoginQR(sender.getSubject());
            sender.sendMessage("请在1分钟内扫码登录");
            sender.sendMessage(loginQR.getQrCode());
            new Thread(()->{
                int cnt = 0;
                while (cnt++ < 60) {
                    try {
                        Thread.sleep(1000);
                        Map<String, String> cookie = BiliTaskUtil.getQRCodeStatus(loginQR.getOauthKey());
                        UserData data = new UserData(cookie.get("DedeUserID"));
                        data.bilijct.set(cookie.get("bili_jct"));
                        data.dedeUserId.set(Long.parseLong(cookie.get("DedeUserID")));
                        data.sessdata.set(cookie.get("SESSDATA"));
                        AutoPlanPlugin.INSTANCE.savePluginData(data);
                        UserMap.INSTANCE.map.get().put(sender.getUser().getId(), Long.parseLong(cookie.get("DedeUserID")));
                    } catch (Exception ignored) {

                    }
                }
                if (cnt == 60) {
                    sender.sendMessage("二维码已过期");
                }
            }).start();
        } catch (Exception ignored) {

        }

    }
}
