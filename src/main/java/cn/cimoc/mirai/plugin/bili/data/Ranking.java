package cn.cimoc.mirai.plugin.bili.data;

import lombok.Data;

import java.util.Date;

/**
 * @author LGZ
 * <p>
 */
@Data
public class Ranking {

    private String aid;

    private String bvid;

    private String typename;

    private String title;

    private String subtitle;

    private Long play;

    private Long review;

    private Long videoReview;

    private Long favorites;

    private Long mid;

    private String author;

    private String description;

    private Date create;

    private String pic;

    private Long coins;

    private String duration;

}
