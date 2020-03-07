package com.windvalley.emall.controller.back;

import com.windvalley.emall.common.Const;
import com.windvalley.emall.common.ServerResponse;
import com.windvalley.emall.dto.UserDTO;
import com.windvalley.emall.enums.RoleCode;
import com.windvalley.emall.service.IUserService;
import com.windvalley.emall.util.JsonUtil;
import com.windvalley.emall.util.RedisShardedPoolUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpSession;

@Controller
@RequestMapping("/manager/user")
@Slf4j
public class UserManagerController {
    @Autowired
    private IUserService userService;

    /**
     * 管理员后台登录
     * @param userName
     * @param password
     * @param httpSession
     * @return
     */
    @RequestMapping(value = "login.do", method = RequestMethod.POST)
    @ResponseBody
    public ServerResponse<UserDTO> login(String userName, String password, HttpSession httpSession){
        ServerResponse<UserDTO> serverResponse = userService.login(userName, password);
        if (serverResponse.isSuccess()){
            UserDTO userDTO = serverResponse.getData();
            if (userDTO.getRole() == RoleCode.ADMIN.getCode()){
                saveUserDataToRedis(httpSession.getId(), serverResponse.getData());
                return serverResponse;
            } else {
                return ServerResponse.createByError("不是管理员, 不可登录");
            }
        }
        return serverResponse;
    }

    private void saveUserDataToRedis(String sessionId, UserDTO userDTO) {
        RedisShardedPoolUtil.setExpire(sessionId, JsonUtil.object2String(userDTO), Const.REDIS_EXPIRE_TIME);
    }
}
