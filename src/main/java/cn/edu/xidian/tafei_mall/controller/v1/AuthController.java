package cn.edu.xidian.tafei_mall.controller.v1;

import cn.edu.xidian.tafei_mall.model.vo.Response.Auth.LoginResponse;
import cn.edu.xidian.tafei_mall.model.vo.Response.Auth.LogoutResponse;
import cn.edu.xidian.tafei_mall.model.vo.Response.Auth.RegisterResponse;
import cn.edu.xidian.tafei_mall.model.entity.User;
import cn.edu.xidian.tafei_mall.model.vo.*;
import cn.edu.xidian.tafei_mall.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;


/**
*  <p>
*  这里对应的是/auth路径，用于处理用户的注册、登录、登出请求
* </p>
*
* @auther: shenyaoguan
*
* @date: 2025-03-17
*
*/

@RestController
@RequestMapping("/api/v1/auth")
public class AuthController {
    @Autowired
    private UserService userService;


    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestBody UserRegistrationVO userRegistrationVO) {
        try{
            User user = userService.register(userRegistrationVO);
            return new ResponseEntity<>(new RegisterResponse(user.getUserId(),"注册成功"),HttpStatus.CREATED);
        }catch (Exception e){
            return new ResponseEntity<>(new RegisterResponse("","注册失败"),HttpStatus.BAD_REQUEST);
        }
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginRequestVO loginRequestVO) {
        try{
            String str = userService.login(loginRequestVO);
            String[] strs = str.split(",");
            if(strs[0].equals("Failed")){
                return new ResponseEntity<>(new LoginResponse("","",""),HttpStatus.UNAUTHORIZED);
            }else {
                return new ResponseEntity<>(new LoginResponse(strs[0], strs[1], loginRequestVO.getUsername()), HttpStatus.OK);
            }
        }catch (Exception e){
            return new ResponseEntity<>(new LoginResponse("","",""),HttpStatus.BAD_REQUEST);
        }
    }

    @PostMapping("/logout")
    public ResponseEntity<?> logout(@RequestHeader("Session-Id") String sessionId) {
        try{
            if(userService.logout(sessionId)){
                return new ResponseEntity<>(new LogoutResponse("登出成功"),HttpStatus.OK);
            }else{
                return new ResponseEntity<>(new LogoutResponse("账号已登出"),HttpStatus.NO_CONTENT);
            }
        }catch (Exception e) {
            return new ResponseEntity<>(new LogoutResponse("登出失败"), HttpStatus.BAD_REQUEST);
        }
    }
}