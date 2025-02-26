package com.yupi.yupicturebackend.service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.yupi.yupicturebackend.model.dto.user.UserQueryRequest;
import com.yupi.yupicturebackend.model.entity.User;
import com.baomidou.mybatisplus.extension.service.IService;
import com.yupi.yupicturebackend.model.vo.LoginUserVO;
import com.yupi.yupicturebackend.model.vo.UserVO;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

/**
* @author 张浩艺
* @description 针对表【user(用户)】的数据库操作Service
* @createDate 2025-02-07 21:34:23
*/
public interface UserService extends IService<User> {
    long userRegister(String userAccount,String userPassword,String checkPassword);

    LoginUserVO userLogin(String userAccount, String userPassword, HttpServletRequest httpServletRequest);
    /**
     * 获取当前登录用户
     *
     * @param request
     * @return
     */
    User getLoginUser(HttpServletRequest request);

    /**
     * 获取脱敏的已登录用户信息
     *
     * @return
     */
    LoginUserVO getLoginUserVO(User user);

    Boolean userLogout(HttpServletRequest request);

    String getEncryptPassword(String password);

    UserVO getUserVO(User user);

    QueryWrapper<User> getQueryWrapper(UserQueryRequest userQueryRequest);
    List<UserVO> getUserVOList(List<User> userList);

    void sendVerificationCode(String email, String code);

    boolean isAdmin(User user);
}
