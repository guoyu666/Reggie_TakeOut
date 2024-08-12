package com.itheima.reggie_takeout.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
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
     *
     * @param request  如果登陆成功把对象放入Session中，方便后续拿取
     * @param employee 利用@RequestBody注解来解析前端传来的Json，同时用对象来封装
     * @return
     */
    @PostMapping("/login")
    public R<Employee> login(HttpServletRequest request, @RequestBody Employee employee) {   // @RequestBody注解来解析前端传来的Json，同时用POJO对象来封装
        /*
          1. 将页面提交的密码password进行md5加密
          2. 根据页面提交的用户名username查询数据库
          3. 如果没有查询到，返回错误信息
          4，密码比对，如果不一致，返回错误信息
          5. 查询员工状态，如果为禁用，返回员工已禁用结果
          6. 登录成功，将员工对象放入Session中，返回成功信息
         */
        // 1. 将页面提交的密码password进行md5加密（页面提交的例如用户名及密码的信息已经封装到了Employee对象中）
        String password = employee.getPassword();
        password = DigestUtils.md5DigestAsHex(password.getBytes());

        // 2. 根据页面提交的用户名username查询数据库（理解）
        LambdaQueryWrapper<Employee> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(Employee::getUsername, employee.getUsername());
        Employee emp = employeeService.getOne(queryWrapper);

        // 3. 如果没有查询到，返回错误信息
        if (emp == null) {
            return R.error("登陆失败！！");
        }

        // 4. 密码比对，如果不一致，返回错误信息
        if (!emp.getPassword().equals(password)) {
            return R.error("登陆失败！！");
        }

        // 5. 查询员工状态，如果为禁用，返回员工已禁用结果
        if (emp.getStatus() == 0) {
            return R.error("员工账号已禁用！！");
        }

        // 6. 走到这里，表示登录成功，将员工ID放入Session中，返回成功信息
        request.getSession().setAttribute("employee", emp.getId());
        return R.success(emp);
    }

    /**
     * 员工退出
     *
     * @param request
     * @return
     */
    @PostMapping("/logout")
    public R<String> logout(HttpServletRequest request) {
        // 清理Session中保存的当前登录员工的id
        request.getSession().removeAttribute("employee");
        return R.success("退出成功！");
    }

    /**
     * 新增员工
     *
     * @param request
     * @param employee
     * @return
     */
    @PostMapping// 因为请求就是/employee，在类上已经写了，所以咱俩不用再写了
    public R<String> save(HttpServletRequest request, @RequestBody Employee employee) {
        log.info("新增员工，员工信息：{}", employee.toString());

        // 统一对新增员设置初始化密码123456，需要进行md5加密
        employee.setPassword(DigestUtils.md5DigestAsHex("123456".getBytes()));

        // employee.setCreateTime(LocalDateTime.now());
        // employee.setUpdateTime(LocalDateTime.now());

        // 获得当前登录用户的id（因为getAttribute的返回值是Object，所以需要强转成Long类型）
        // Long empId = (Long) request.getSession().getAttribute("employee");
        // employee.setCreateUser(empId);  // 创建人的id,就是当前用户的id
        // employee.setUpdateUser(empId);  // 更新人的id,就是当前用户的id

        employeeService.save(employee); // mybatis-plus提供的方法
        return R.success("新增员工成功！");
    }

    /**
     * 员工信息分页查询
     *
     * @param page
     * @param pageSize
     * @param name
     * @return
     */
    @GetMapping("/page")
    public R<Page> page(int page, int pageSize, String name) {
        log.info("分页查询员工，页码：{}，每页数量：{}，员工名称：{}", page, pageSize, name);
        // 构造分页构造器
        Page pageInfo = new Page(page, pageSize);
        // 条件构造器
        LambdaQueryWrapper<Employee> queryWrapper = new LambdaQueryWrapper<>();
        // 添加过滤条件
        queryWrapper.like(StringUtils.isNotBlank(name), Employee::getName, name);
        // 添加排序条件
        queryWrapper.orderByDesc(Employee::getUpdateTime);
        // 执行分页查询
        employeeService.page(pageInfo, queryWrapper);
        return R.success(pageInfo);
    }

    /**
     * 通用的修改员工信息的方法
     * @param employee
     * @param request
     * @return
     */
    @PutMapping
    public R<String> update(@RequestBody Employee employee, HttpServletRequest request) {
        log.info(employee.toString());

        // 获得当前登录用户的id（从Session中获取）,强制转成Long类型
        // Long empId = (Long) request.getSession().getAttribute("employee");

        // 设置更新人的id
        // employee.setUpdateUser(empId);

        // 设置更新时间
        // employee.setUpdateTime(LocalDateTime.now());

        employeeService.updateById(employee);
        return R.success("员工信息修改成功！");
    }

    /**
     * 根据id查询员工信息
     * @param id
     * @return
     */
    @GetMapping("/{id}")
    public R<Employee> getById(@PathVariable Long id) {
        log.info("根据id查询员工信息....");
        Employee employee = employeeService.getById(id);
        if (employee != null){
            return R.success(employee);
        }
        return R.error("员工信息不存在！");
    }
}
