package cn.cimoc.mirai.plugin.bili.data;

import lombok.Data;

/**
 * @author LGZ
 * <p>
 */
@Data
public class Reward {

    private Boolean login;

    private Boolean watch;

    private Integer coins;

    private Boolean share;

    private Boolean email;

    private Boolean tel;

    private Boolean safeQuestion;

    private Boolean identifyCard;
}
