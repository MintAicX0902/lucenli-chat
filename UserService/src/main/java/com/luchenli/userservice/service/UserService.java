package com.luchenli.userservice.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.luchenli.userservice.model.dto.UserLoginCodeRequest;
import com.luchenli.userservice.model.dto.UserLoginPasswordRequest;
import com.luchenli.userservice.model.dto.UserRegisterRequest;
import com.luchenli.userservice.model.entity.User;
import com.luchenli.userservice.model.vo.LoginAndRegisterResponse;
import com.luchenli.userservice.model.vo.TokenResponse;


public interface UserService extends IService<User> {

    void sendCaptcha(String targetEmail);


    LoginAndRegisterResponse register(UserRegisterRequest userRegisterRequest);


    LoginAndRegisterResponse loginPassword(UserLoginPasswordRequest userLoginPasswordRequest);



    LoginAndRegisterResponse loginCode(UserLoginCodeRequest userLoginCodeRequest);

    boolean logout(String userId);


    TokenResponse refreshToken(String refreshToken);
}
