package com.bike.controller;

import com.bike.dto.LoginDTO;
import com.bike.entity.AppUser;
import com.bike.repository.AppUserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping(value = "/user", produces = "application/json;charset=UTF-8")
@CrossOrigin
public class UserController {

    @Autowired
    private AppUserRepository appUserRepository;

    /**
     * 登录接口
     * POST /user/login
     */
    @PostMapping("/login")
    public Map<String, Object> login(@RequestBody LoginDTO loginDTO) {
        Map<String, Object> result = new HashMap<>();

        if (loginDTO.getUsername() == null || loginDTO.getUsername().trim().isEmpty()) {
            result.put("code", 0);
            result.put("msg", "error");
            result.put("detail", "username is empty");
            return result;
        }

        if (loginDTO.getPassword() == null || loginDTO.getPassword().trim().isEmpty()) {
            result.put("code", 0);
            result.put("msg", "error");
            result.put("detail", "password is empty");
            return result;
        }

        Optional<AppUser> optionalUser = appUserRepository.findByUsername(loginDTO.getUsername());

        if (optionalUser.isEmpty()) {
            result.put("code", 0);
            result.put("msg", "error");
            result.put("detail", "user not found");
            return result;
        }

        AppUser user = optionalUser.get();

        if (!user.getPassword().equals(loginDTO.getPassword())) {
            result.put("code", 0);
            result.put("msg", "error");
            result.put("detail", "password incorrect");
            return result;
        }

        Map<String, Object> data = new HashMap<>();
        data.put("id", user.getId());
        data.put("username", user.getUsername());
        data.put("role", user.getRole());
        data.put("phone", user.getPhone());

        result.put("code", 1);
        result.put("msg", "success");
        result.put("data", data);

        return result;
    }

    // 在 UserController.java 中添加
    @PostMapping("/register")
    public Map<String, Object> register(@RequestBody AppUser newUser) {
        Map<String, Object> result = new HashMap<>();

        // 1. 非空校验
        if (newUser.getUsername() == null || newUser.getUsername().trim().isEmpty() ||
                newUser.getPassword() == null || newUser.getPassword().trim().isEmpty() ||
                newUser.getPhone() == null || newUser.getPhone().trim().isEmpty()) {
            result.put("code", 0);
            result.put("msg", "FORM_INCOMPLETE");
            return result;
        }

        // 2. 手机号格式校验
        if (!newUser.getPhone().matches("^1[3-9]\\d{9}$")) {
            result.put("code", 0);
            result.put("msg", "PHONE_INVALID");
            return result;
        }

        // 3. 用户名重复
        if (appUserRepository.existsByUsername(newUser.getUsername())) {
            result.put("code", 0);
            result.put("msg", "USERNAME_EXISTS");
            return result;
        }

        // 4. 手机号重复
        if (appUserRepository.existsByPhone(newUser.getPhone())) {
            result.put("code", 0);
            result.put("msg", "PHONE_EXISTS");
            return result;
        }

        // 5. 默认属性
        newUser.setRole("user");
        newUser.setCreateTime(LocalDateTime.now());

        try {
            appUserRepository.save(newUser);
            result.put("code", 1);
            result.put("msg", "SUCCESS");
        } catch (Exception e) {
            result.put("code", 0);
            result.put("msg", "REGISTER_FAILED");
        }

        return result;
    }

    /**
     * 获取用户信息
     * GET /user/info?userId=1
     */
    @GetMapping("/info")
    public Map<String, Object> getUserInfo(@RequestParam Long userId) {
        Map<String, Object> result = new HashMap<>();

        Optional<AppUser> optionalUser = appUserRepository.findById(userId);

        if (optionalUser.isEmpty()) {
            result.put("code", 0);
            result.put("msg", "error");
            result.put("detail", "user not found");
            return result;
        }

        AppUser user = optionalUser.get();

        Map<String, Object> data = new HashMap<>();
        data.put("id", user.getId());
        data.put("username", user.getUsername());
        data.put("role", user.getRole());
        data.put("phone", user.getPhone());

        result.put("code", 1);
        result.put("msg", "success");
        result.put("data", data);

        return result;
    }
}