package com.windvalley.emall.dao;

import com.windvalley.emall.pojo.Product;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface ProductMapper {
    int deleteByPrimaryKey(Integer id);

    int insert(Product record);

    int insertSelective(Product record);

    Product selectByPrimaryKey(Integer id);

    int updateByPrimaryKeySelective(Product record);

    int updateByPrimaryKey(Product record);

    Product selectByProductNameAndCategoryId(@Param("productName")String productName, @Param("categoryId")Integer categoryId);

    List<Product> productList();

    List<Product> productListByNameAndId(@Param("productName")String productName, @Param("productId")Integer productId);

    List<Product> productListByNameAndCategorys(@Param("productName")String productName, @Param("categorys")List<Integer> categorys);
}