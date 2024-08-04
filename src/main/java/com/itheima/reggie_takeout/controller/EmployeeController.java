package com.itheima.reggie_takeout.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.itheima.reggie_takeout.common.R;
import com.itheima.reggie_takeout.entity.Employee;
import com.itheima.reggie_takeout.service.EmployeeService;
import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.util.DigestUtils;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequestMapping("/employee")
public class EmployeeController {

    @Resource
    private EmployeeService employeeService;

    /**
     * 员工登录
     * @param request 如果登陆成功把对象放入Session中，方便后续拿取
     * @param employee 利用@RequestBody注解来解析前端传来的Json，同时用对象来封装
     * @return
     */
    @PostMapping("/login")
    public R<Employee> login(HttpServletRequest request, @RequestBody Employee employee){   // @RequestBody注解来解析前端传来的Json，同时用POJO对象来封装
        /*
          1. 将页面提交的密码password进行md5加密
          2. 根据页面提交的用户名username查询数据库
          3. 如果没有查询到，返回错误信息
          4，密码比对，如果不一致，返回错误信息
          5. 查询员工状态，如果为禁用，返回员工已禁用结果
          6. 登录成功，将员工对象放入Session中，返回成功信息
         */
        // 1. 将页面提交的密码password进行md5加密
        String password = employee.getPassword();
        password = DigestUtils.md5DigestAsHex(password.getBytes());

        // 2. 根据页面提交的用户名username查询数据库
        LambdaQueryWrapper<Employee> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(Employee::getUsername, employee.getUsername());
        Employee emp = employeeService.getOne(queryWrapper);

        // 3. 如果没有查询到，返回错误信息
        if(emp == null){
            return R.error("登陆失败！！");
        }

        // 4. 密码比对，如果不一致，返回错误信息
        if(!emp.getPassword().equals(password)){
            return R.error("登陆失败！！");
        }

        // 5. 查询员工状态，如果为禁用，返回员工已禁用结果
        if(emp.getStatus() == 0){
            return R.error("员工账号已禁用！！");
        }

        // 6. 登录成功，将员工ID放入Session中，返回成功信息
        request.getSession().setAttribute("employee", emp.getId());
        return R.success(emp);
    }

    @PostMapping("/logout")
    public R<String> logout(HttpServletRequest request){
        request.getSession().removeAttribute("employee");
        return R.success("退出成功！");
    }
}
