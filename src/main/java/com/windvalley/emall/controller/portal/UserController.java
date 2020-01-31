package com.windvalley.emall.controller.portal;

import com.windvalley.emall.common.Const;
import com.windvalley.emall.common.ServerResponse;
import com.windvalley.emall.converter.User2UserDTO;
import com.windvalley.emall.dto.UserDTO;
import com.windvalley.emall.enums.ResponseCode;
import com.windvalley.emall.form.UserInformationForm;
import com.windvalley.emall.form.UserRegisterForm;
import com.windvalley.emall.service.IUserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpSession;

@Controller
@RequestMapping("/user/")
@Slf4j
/**
 * @Author icewind
 */
public class UserController {
    @Autowired
    private IUserService userService;
    /**
     * 前台用户登录
     * @Author icewind
     * @param userName
     * @param password
     * @param httpSession
     * @return UserDTO
     */
    @RequestMapping(value = "login.do", method = RequestMethod.POST)
    @ResponseBody
    public ServerResponse<UserDTO> login(String userName, String password, HttpSession httpSession){
        ServerResponse<UserDTO> serverResponse = userService.login(userName, password);
        if (serverResponse.isSuccess()){
            saveUserDataToSession(httpSession, serverResponse.getData());
        }
        return serverResponse;
    }

    /**
     * 前台用户登出
     * @Author icewind
     * @param httpSession
     * @return
     */
    @RequestMapping(value = "checkout.do", method = RequestMethod.POST)
    @ResponseBody
    public ServerResponse<String> checkout(HttpSession httpSession){
        httpSession.removeAttribute(Const.CURRENT_USER);
        return ServerResponse.createBySuccess();
    }

    /**
     * 前台用户注册
     * @Author icewind
     * @param userRegisterForm
     * @return
     */
    @RequestMapping(value = "register.do", method = RequestMethod.POST)
    @ResponseBody
    public ServerResponse<String> register(UserRegisterForm userRegisterForm){
        return userService.register(User2UserDTO.convert(userRegisterForm));
    }

    /**
     * 前台校验接口
     * @Author icewind
     * @param value
     * @param type
     * @return
     */
    @RequestMapping(value = "checkvalid.do", method = RequestMethod.POST)
    @ResponseBody
    public ServerResponse<String> checkValid(String value, String type){
        return userService.checkValid(value, type);
    }

    /**
     * 获取登录用户信息
     * @Author icewind
     * @param httpSession
     * @return
     */
    @RequestMapping(value = "getuserinfo.do", method = RequestMethod.POST)
    @ResponseBody
    public ServerResponse<UserDTO> getUserInfo(HttpSession httpSession){
        UserDTO userDTO = (UserDTO) httpSession.getAttribute(Const.CURRENT_USER);
        if (userDTO != null){
            return ServerResponse.createBySuccess(userDTO);
        }
        return ServerResponse.createByError("用户未登录");
    }

    /**
     * 获取用户密码提示问题
     * @Author icewind
     * @param userName
     * @return
     */
    @RequestMapping(value = "getquestion.do", method = RequestMethod.POST)
    @ResponseBody
    public ServerResponse<String> getQuestion(String userName){
        return userService.getQuestion(userName);
    }

    /**
     * 检查遗忘密码时的答案
     * @Author icewind
     * @param userName
     * @param question
     * @param answer
     * @return
     */
    @RequestMapping(value = "checkAnswer.do", method = RequestMethod.POST)
    @ResponseBody
    public ServerResponse<String> checkAnswer(String userName, String question, String answer){
        return userService.checkAnswer(userName, question, answer);
    }

    /**
     * 客户未登录, 重置密码
     * @Author icewind
     * @param userName
     * @param password
     * @param token
     * @return
     */
    @RequestMapping(value = "resetPassword4forget.do", method = RequestMethod.POST)
    @ResponseBody
    public ServerResponse<String> resetPassword4Forget(String userName, String password, String token){
        return userService.resetPassword4Forget(userName, password, token);
    }

    /**
     * 客户已登录, 重置密码
     * @Author icewind
     * @param httpSession
     * @param oldPassword
     * @param newPassword
     * @return
     */
    @RequestMapping(value = "resetPassword.do", method = RequestMethod.POST)
    @ResponseBody
    public ServerResponse<String> resetPassword(HttpSession httpSession, String oldPassword, String newPassword){
        UserDTO userDTO = (UserDTO) httpSession.getAttribute(Const.CURRENT_USER);
        if (userDTO == null) {
            return ServerResponse.createByError("用户未登录");
        }
        return userService.resetPassword(userDTO, oldPassword, newPassword);
    }

    /**
     * 修改用户信息
     * @Author icewind
     * @param httpSession
     * @param userInformationForm
     * @return
     */
    @RequestMapping(value = "updateinformation.do", method = RequestMethod.POST)
    @ResponseBody
    public ServerResponse<UserDTO> updateInformation(HttpSession httpSession, UserInformationForm userInformationForm) {
        UserDTO userDTO = (UserDTO) httpSession.getAttribute(Const.CURRENT_USER);
        if (userDTO == null) {
            return ServerResponse.createByError("用户未登录");
        }

    //这里检查一下UserDTO的ID，是不是还是session里面取出来的ID
        userInformationForm.setId(userDTO.getId());
        userInformationForm.setUsername(userDTO.getUsername());
        userInformationForm.setRole(userDTO.getRole());
        ServerResponse<UserDTO> serverResponse = userService.updateInformation(User2UserDTO.convert(userInformationForm));
        if (serverResponse.isSuccess()){
            saveUserDataToSession(httpSession, serverResponse.getData());
        }
        return serverResponse;
    }

    /**
     * 获得用户信息
     * @Author icewind
     * @param httpSession
     * @return
     */
    @RequestMapping(value = "getinfomation.do", method = RequestMethod.POST)
    @ResponseBody
    public ServerResponse<UserDTO> getInformation(HttpSession httpSession) {
        UserDTO userDTO = (UserDTO) httpSession.getAttribute(Const.CURRENT_USER);
        if (userDTO == null) {
            return ServerResponse.createByError(ResponseCode.NEED_LOGIN.getCode(), "用户未登录，需要登录");
        }
        return userService.getInformatioin(userDTO.getUsername());
    }

    private void saveUserDataToSession(HttpSession httpSession, UserDTO userDTO) {
        httpSession.setAttribute(Const.CURRENT_USER, userDTO);
    }
}
