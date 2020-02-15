package com.windvalley.emall.dao;

import com.windvalley.emall.pojo.User;
import org.apache.ibatis.annotations.Param;

public interface UserMapper {
    int deleteByPrimaryKey(Integer id);

    int insert(User record);

    int insertSelective(User record);

    User selectByPrimaryKey(Integer id);

    int updateByPrimaryKeySelective(User record);

    int updateByPrimaryKey(User record);

    int checkUserName(String userName);

    int checkEMail(String email);

    User selectLogin(@Param("userName") String userName, @Param("password") String password);

    String selectQustionByUserName(String userName);

    int checkAnswer(@Param("userName")String userName, @Param("question")String question, @Param("answer")String answer);

    int updatePasswordByUserName(@Param("userName")String userName, @Param("password")String password);

    int checkEMailByUserName(@Param("userName")String username, @Param("email")String email);

    User getInformationByUserName(String username);
}