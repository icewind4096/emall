package com.windvalley.emall.dao;

import com.windvalley.emall.pojo.Product;
import com.windvalley.emall.pojo.Shipping;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface ShippingMapper {
    int deleteByPrimaryKey(Integer id);

    int insert(Shipping record);

    int insertSelective(Shipping record);

    Shipping selectByPrimaryKey(Integer id);

    int updateByPrimaryKeySelective(Shipping record);

    int updateByPrimaryKey(Shipping record);

    int del(@Param("userId") Integer userId, @Param("shippingId") Integer shippingId);

    int update(Shipping shipping);

    Shipping getInfoByUserIdAndShippingId(@Param("userId") Integer userId, @Param("shippingId") Integer shippingId);

    List<Shipping> listByUserId(Integer userId);
}