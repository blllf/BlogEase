package com.blllf.blogease.pojo;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.validator.constraints.URL;

import java.time.LocalDateTime;


@AllArgsConstructor
@NoArgsConstructor
@Data
@TableName("user_pictures")
public class UserPictures {

    private Integer id;
    private Integer createUser;
    private String nickname;
    @URL
    private String userPicture;
    private LocalDateTime createTime;
}
