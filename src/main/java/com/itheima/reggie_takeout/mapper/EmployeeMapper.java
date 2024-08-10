package com.itheima.reggie_takeout.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.itheima.reggie_takeout.entity.Employee;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface EmployeeMapper extends BaseMapper<Employee> {  // 继承了BaseMapper，可以使用BaseMapper中一些常见的增删改查的的方法

}
