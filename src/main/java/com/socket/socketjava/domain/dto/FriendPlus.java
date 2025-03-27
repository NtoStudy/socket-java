package com.socket.socketjava.domain.dto;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.socket.socketjava.domain.pojo.Users;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
public class FriendPlus {
    private String remark;
    private Integer isPinned;

    private Integer userId;

    @ApiModelProperty(value = "用户名")
    private String username;

    @ApiModelProperty(value = "随机分配的十位数字")
    private String number;

    @ApiModelProperty(value = "用户头像URL")
    private String avatarUrl;


    @ApiModelProperty(value = "用户状态")
    private String status;

    @ApiModelProperty(value = "用户自定义状态")
    private String customStatus;

    @ApiModelProperty(value = "生日")
    private LocalDate birthday;

    @ApiModelProperty(value = "兴趣爱好")
    private String hobbies;

    @ApiModelProperty(value = "个性签名")
    private String signature;

    @ApiModelProperty(value = "性别")
    private String gender;

    @ApiModelProperty(value = "省")
    private String province;

    @ApiModelProperty(value = "市")
    private String city;

    @ApiModelProperty(value = "被点赞的个数")
    private Integer likeCount;
}
