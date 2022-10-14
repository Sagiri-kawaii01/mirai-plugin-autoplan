package cn.cimoc.mirai.plugin.bili.data;

import cn.cimoc.mirai.plugin.bili.data.dataBean.Owner;
import cn.cimoc.mirai.plugin.bili.data.dataBean.Stat;
import lombok.Data;

/**
 * @author LGZ
 * <p>
 */
@Data
public class Video {
    private String bvid;

    private Long aid;

    private String tname;

    private String pic;

    private String title;

    private Long pubdate;

    private Long ctime;

    private Owner owner;

    private Stat stat;



}
