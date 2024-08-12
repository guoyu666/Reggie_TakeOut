package com.itheima.reggie_takeout.entity;

import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.TableField;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 员工实体类
 */
@Data
public class Employee implements Serializable {

    private static final long serialVersionUID = 1L;

    private Long id;

    private String username;

    private String name;

    private String password;

    private String phone;

    private String sex;

    private String idNumber;    // 身份证号码(前面在进行环境搭建的时候，已经在yaml文件中配置好了，开启驼峰命名，所以这里命名为idNumber对应的就是数据库中的id_number)

    private Integer status;

    @TableField(fill = FieldFill.INSERT)    // 自动填充，插入时自动填充当前时间
    private LocalDateTime createTime;   // 驼峰命名，所以这里命名为createTime对应的就是数据库中的create_time

    @TableField(fill = FieldFill.INSERT_UPDATE)    // 自动填充，插入和更新时自动填充当前时间
    private LocalDateTime updateTime;   // 驼峰命名，所以这里命名为updateTime对应的就是数据库中的update_time

    // 自动填充，插入时自动填充当前用户id
    @TableField(fill = FieldFill.INSERT)
    private Long createUser;

    // 自动填充，插入和更新时自动填充当前用户id
    @TableField(fill = FieldFill.INSERT_UPDATE)
    private Long updateUser;
}
