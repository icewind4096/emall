package com.windvalley.emall.service;

import com.windvalley.emall.common.ServerResponse;
import com.windvalley.emall.dto.UserDTO;

public interface IUserService {
    ServerResponse<UserDTO> login(String userName, String password);

    ServerResponse<String> register(UserDTO userDTO);

    ServerResponse<String> checkValid(String value, String type);

    ServerResponse<String> getQuestion(String userName);

    ServerResponse<String> checkAnswer(String userName, String question, String answer);

    ServerResponse<String> resetPassword4Forget(String userName, String password, String token);

    ServerResponse<String> resetPassword(UserDTO userDTO, String oldPassword, String newPassword);

    ServerResponse<UserDTO> updateInformation(UserDTO convert);

    ServerResponse<UserDTO> getInformatioin(String username);
}
