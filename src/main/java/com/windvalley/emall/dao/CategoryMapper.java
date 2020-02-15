package com.windvalley.emall.dao;

import com.windvalley.emall.pojo.Category;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface CategoryMapper {
    int deleteByPrimaryKey(Integer id);

    int insert(Category record);

    int insertSelective(Category record);

    Category selectByPrimaryKey(Integer id);

    int updateByPrimaryKeySelective(Category record);

    int updateByPrimaryKey(Category record);

    int checkCategoryNameByParentId(@Param("categoryName")String categoryName, @Param("parentId")Integer parentId);

    int checkCategoryNameByParentIdAndId(@Param("categoryName")String categoryName, @Param("parentId")Integer parentId, @Param("id")Integer id);

    int checkParentId(int parentId);

    int checkCategoryId(int categoryId);

    List<Category> selectCategoryChildrenByParentId(Integer parentId);
}