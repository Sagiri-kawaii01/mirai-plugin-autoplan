package cn.cimoc.mirai.plugin.bili.data.dataBean;

import lombok.Data;

/**
 * @author LGZ
 * <p>
 */
@Data
public class DynamicCard {
    /**
     * 视频封面
     */
    private String pic;

    /**
     * 标题
     */
    private String title;

    /**
     * 短链接
     */
    private String shortLink;

    /**
     * 时长
     */
    private Long duration;

    /**
     * 动态
     */
    private String dynamic;

    /**
     * 简介
     */
    private String desc;

    public String getTime() {
        long hh = duration / 3600;
        long mm = (duration % 3600) / 60;
        long ss = (duration % 60);
        String pattern = "%d小时%02d分钟%02d秒";
        String ret = String.format(pattern, hh, mm, ss);
        if (hh == 0) {
            ret = ret.substring(3);
            if (mm == 0) {
                ret = ret.substring(4);
            }
        }
        if (ret.charAt(0) == '0') {
            ret = ret.substring(1);
        }
        return ret;
    }
}
