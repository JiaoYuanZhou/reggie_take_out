package com.itheima.reggie.controller;


import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.itheima.reggie.common.R;
import com.itheima.reggie.entity.Employee;
import com.itheima.reggie.service.EmployeeService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.DigestUtils;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.time.LocalDateTime;

@Slf4j
@RestController
@RequestMapping("/employee")
public class EmployeeController {

    @Autowired
    private EmployeeService employeeService;


    /**
     * 员工登录
     * @param employee
     * @param request
     * @return
     */
    @PostMapping("login")
    public R<Employee> login(@RequestBody Employee employee, HttpServletRequest request) {
        //1、将页面提交的密码进行md5加密
        String password = employee.getPassword();
        password = DigestUtils.md5DigestAsHex(password.getBytes());

        //2、根据页面提交的用户名username去查数据库
        LambdaQueryWrapper<Employee> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(Employee::getUsername,employee.getUsername());
        Employee emp = employeeService.getOne(queryWrapper);

        //3、判断用户名是否存在
        if (emp == null) {
            return R.error("用户名不存在");
        }

        //4、判断密码是否正确
        if (!emp.getPassword().equals(password)) {
            return R.error("用户名密码错误");
        }

        //5、判断用户是否被禁用
        if (emp.getStatus() == 0) {
            return R.error("该账号已被禁用");
        }

        //6、登录成功将员工ID存入seesion中
        request.getSession().setAttribute("employ",emp.getId());

        return R.success(emp);
    }


    /**
     * 员工退出
     * @param request
     * @return
     */
    @RequestMapping("logout")
    public R<String> logout(HttpServletRequest request) {
        //清楚session中的id
        request.getSession().removeAttribute("employ");
        return R.success("退出成功");
    }


    @RequestMapping
    public R<String> save(HttpServletRequest request ,@RequestBody Employee employee) {

        log.info("新增员工信息{}",employee.toString());

        //设置员工信息
        employee.setPassword(DigestUtils.md5DigestAsHex("123456".getBytes()));
        employee.setCreateTime(LocalDateTime.now());
        employee.setUpdateTime(LocalDateTime.now());

        //获取当前登录用户的ID
        Long empId = (Long) request.getSession().getAttribute("employ");

        employee.setCreateUser(empId);
        employee.setUpdateUser(empId);

        employeeService.save(employee);

        return R.success("新增员工成功");
    }


    /**
     * 员工分页查询
     * @param page
     * @param pageSize
     * @param name
     * @return
     */
    @GetMapping("/page")
    public R<Page> page(int page,int pageSize, String name) {

        //构造分页构造器
        Page pageinfo = new Page(page,pageSize);

        //构造条件构造器
        LambdaQueryWrapper<Employee> queryWrapper = new LambdaQueryWrapper();

        //添加过滤条件
        if (name != null) {
            queryWrapper.like(Employee::getName,name);
        }

        //添加排序条件
        queryWrapper.orderByDesc(Employee::getUpdateTime);

        //执行查询
        employeeService.page(pageinfo,queryWrapper);

        return R.success(pageinfo);
    }

    /**
     * 根据ID修改员工信息
     * @param employee
     * @return
     */
    @PutMapping
    public R<String> update(HttpServletRequest request, @RequestBody Employee employee) {


        Long employ = (Long) request.getSession().getAttribute("employ");
        employee.setUpdateTime(LocalDateTime.now());
        employee.setUpdateUser(employ);

        employeeService.updateById(employee);

        return R.success("修改成功");
    }


    /**
     * 根据员工ID查找员工信息
     * @param id
     * @return
     */
    @GetMapping("/{id}")
    public R<Employee> getById(@PathVariable Long id) {
        Employee employee = employeeService.getById(id);
        if (employee != null) {
            return R.success(employee);
        }
        return R.error("没有查询到对应员工信息");
    }

}
