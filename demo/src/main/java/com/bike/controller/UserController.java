package com.bike.controller;

import com.bike.dto.LoginDTO;
import com.bike.entity.AppUser;
import com.bike.repository.AppUserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

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
        data.put("nickname", user.getNickname());
        data.put("phone", user.getPhone());

        result.put("code", 1);
        result.put("msg", "success");
        result.put("data", data);

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
        data.put("nickname", user.getNickname());
        data.put("phone", user.getPhone());

        result.put("code", 1);
        result.put("msg", "success");
        result.put("data", data);

        return result;
    }
}