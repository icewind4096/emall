package com.windvalley.emall.service.impl;

import com.windvalley.emall.common.Const;
import com.windvalley.emall.common.ServerResponse;
import com.windvalley.emall.converter.User2UserDTO;
import com.windvalley.emall.converter.UserDTO2User;
import com.windvalley.emall.dao.UserMapper;
import com.windvalley.emall.dto.UserDTO;
import com.windvalley.emall.enums.RoleCode;
import com.windvalley.emall.pojo.User;
import com.windvalley.emall.service.IUserService;
import com.windvalley.emall.util.MD5Util;
import com.windvalley.emall.util.RedisPoolUtil;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
public class UserService implements IUserService {
    @Autowired
    UserMapper userMapper;

    @Override
    public ServerResponse<UserDTO> login(String userName, String password) {
    //检查用户是否存在
        if (checkUserNameValid(userName) == 0){
            return ServerResponse.createByError("用户不存在");
        }

    //转换用户明文密码为MD5加密字符串
        password = convertPassword2MD5(password);

    //检查用户密码是否正确
        User user = userMapper.selectLogin(userName, password);
        if(user == null){
            return ServerResponse.createByError("用户密码错误");
        }

        user.setPassword("");
        return ServerResponse.createBySuccess("登录成功", User2UserDTO.convert(user));
    }

    @Override
    public ServerResponse<String> register(UserDTO userDTO) {
    //检查用户是否存在
        if (checkUserNameValid(userDTO.getUsername()) > 0){
            return ServerResponse.createByError("用户已存在");
        }

    //检查EMail是否存在
        if (checkEMailValid(userDTO.getEmail()) > 0){
            return ServerResponse.createByError("电子邮箱已存在");
        }

    //设置初始注册用户权限为普通用户
        userDTO.setRole(RoleCode.CUSTOMER.getCode());

    //转换用户明文密码为MD5加密字符串
        userDTO.setPassword(convertPassword2MD5(userDTO.getPassword()));

        if (userMapper.insert(UserDTO2User.convert(userDTO)) == 0){
            return ServerResponse.createByError("注册失败");
        }
        return ServerResponse.createBySuccess("注册成功");
    }

    @Override
    public ServerResponse<String> checkValid(String value, String type) {
        if (Const.VALID_EMAIL.equalsIgnoreCase(type)) {
            if (checkEMailValid(value) > 0){
                return ServerResponse.createByError("电子邮箱已存在");
            }
        } else {
            if (Const.VALID_USERNAME.equalsIgnoreCase(type)) {
                if (checkUserNameValid(value) > 0){
                    return ServerResponse.createByError("用户已存在");
                }
            } else {
                return ServerResponse.createByError("校验参数错误");
            }
        }
        return ServerResponse.createBySuccess("校验成功");
    }

    @Override
    public ServerResponse<String> getQuestion(String userName) {
    //检查用户是否存在
        if (checkUserNameValid(userName) == 0){
            return ServerResponse.createByError("用户不存在");
        }

    //获得用户信息
        String question = userMapper.selectQustionByUserName(userName);
        if (StringUtils.isNoneBlank(question) == true){
            return ServerResponse.createBySuccess(question);
        }
        return ServerResponse.createByError("找回密码问题为空");
    }

    @Override
    public ServerResponse<String> checkAnswer(String userName, String question, String answer) {
    //检查用户答案是否正确
        if (checkAnswerAndQuestion(userName, question, answer) > 0){
    //向本地Cache中添加Token
            String token = UUID.randomUUID().toString();
            RedisPoolUtil.setExpire(getForgetTokenKey(userName), token, 60 * 60 * 12);
            return ServerResponse.createBySuccess(token);
        }
        return ServerResponse.createByError("用户答案错误");
    }

    @Override
    public ServerResponse<String> resetPassword4Forget(String userName, String password, String token) {
    //检查出入的Token是否为空
        if (StringUtils.isBlank(token)) {
            return ServerResponse.createByError("无效的Token");
        }

    //检查用户是否存在
        if (checkUserNameValid(userName) == 0){
            return ServerResponse.createByError("用户不存在");
        }

    //向本地Cache中查询，对应Token是否存在
        String localToken = RedisPoolUtil.get(getForgetTokenKey(userName));
        if (StringUtils.equals(token, localToken) == false){
            return ServerResponse.createByError("无效的Token或者已过期");
        }

    //修改数据库中的用户密码
        if (updatePassword(userName, password) > 0){
            return ServerResponse.createBySuccess("重置密码成功");
        };

        return ServerResponse.createByError("重置密码失败");
    }

    @Override
    public ServerResponse<String> resetPassword(UserDTO userDTO, String oldPassword, String newPassword) {
    //检查用户密码是否正确
        User user = userMapper.selectLogin(userDTO.getUsername(), convertPassword2MD5(oldPassword));
        if(user == null){
            return ServerResponse.createByError("用户旧密码错误");
        }

    //修改数据库中的用户密码
        if (updatePassword(userDTO.getUsername(), newPassword) > 0){
            return ServerResponse.createBySuccess("重置密码成功");
        }
        return ServerResponse.createByError("重置密码失败");
    }

    @Override
    public ServerResponse<UserDTO> updateInformation(UserDTO userDTO) {
    //检查用户是否存在
        if (checkUserNameValid(userDTO.getUsername()) == 0){
            return ServerResponse.createByError("用户不存在");
        }

    //检查用户邮箱是否已存在
        if (userMapper.checkEMailByUserName(userDTO.getUsername(), userDTO.getEmail()) > 0){
            return ServerResponse.createByError("用户邮箱已存在");
        }

    //修改用户信息
        if (userMapper.updateByPrimaryKeySelective(UserDTO2User.convert(userDTO)) > 0){
            return ServerResponse.createBySuccess("修改用户数据成功", userDTO);
        }

        return ServerResponse.createByError("修改用户数据失败");
    }

    @Override
    public ServerResponse<UserDTO> getInformatioin(String username) {
    //获取用户信息
        User user = userMapper.getInformationByUserName(username);
        if(user == null){
            return ServerResponse.createByError("用户不存在");
        }

    //清除密码
        user.setPassword("");
        return ServerResponse.createBySuccess(User2UserDTO.convert(user));
    }

    @Override
    public ServerResponse isManagerRole(String username) {
    //获取用户信息
        User user = userMapper.getInformationByUserName(username);
        if(user == null){
            return ServerResponse.createByError("用户不存在");
        }
    //判断是否是管理员
        if(user.getRole() == RoleCode.ADMIN.getCode()){
            return ServerResponse.createBySuccess();
        }
        return ServerResponse.createByError("用户不具备管理员权限");
    }

    private int updatePassword(String username, String password) {
        return userMapper.updatePasswordByUserName(username, convertPassword2MD5(password));
    }

    private String getForgetTokenKey(String userName) {
        return Const.TOKEN_FORGET_PREFIX + userName;
    }

    private int checkEMailValid(String value) {
        return userMapper.checkEMail(value);
    }

    private int checkUserNameValid(String value) {
        return userMapper.checkUserName(value);
    }

    private int checkAnswerAndQuestion(String userName, String question, String answer) {
        return userMapper.checkAnswer(userName, question, answer);
    }

    private String convertPassword2MD5(String password) {
        return MD5Util.MD5EncodeUtf8(password);
    }
}
