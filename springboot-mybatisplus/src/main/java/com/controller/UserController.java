package com.controller;


import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.pojo.User;
import com.service.IUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import org.springframework.web.bind.annotation.RestController;

/**
 * <p>
 *  前端控制器
 * </p>
 */
@RestController
@RequestMapping("/user")
public class UserController {
    @Autowired
    private IUserService userService;

    @GetMapping("/user/{name}")
    public String sayHello(@PathVariable String name) throws Exception {

        // System.out.println("接收到的名字：" + name);

        User user = userService.findUserByName(name);

        return "Hello..."+ user.getName() +" 你好呀！";

    }

    @GetMapping("/findEmail")
    public String findEmail() throws Exception {

        String email = userService.findEmail();

        return email;

    }
    @GetMapping("/page")
    public String page() throws Exception {

        Page page = new Page<User>();
        IPage userPage = userService.selectUserPage(page, "cuihua");

        System.out.println("userPage--->" + userPage);

        return "";

    }
}

