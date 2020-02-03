package com.windvalley.emall.service.impl;

import com.windvalley.emall.common.ServerResponse;
import com.windvalley.emall.dao.CategoryMapper;
import com.windvalley.emall.pojo.Category;
import com.windvalley.emall.service.ICategoryService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.ArrayList;
import java.util.List;

@Service
@Slf4j
public class CategoryService implements ICategoryService {
    @Autowired
    private CategoryMapper categoryMapper;

    @Override
    public ServerResponse addCategory(String categoryName, int parentId) {
        if (StringUtils.isBlank(categoryName)) {
            return ServerResponse.createByError("添加分类，参数错误");
        }

        if (parentId > 0 && categoryMapper.checkParentId(parentId) == 0){
            return ServerResponse.createByError("添加分类，根节点不存在");
        }

        if (categoryMapper.checkCategoryNameByParentId(categoryName, parentId) > 0){
            return ServerResponse.createByError("添加分类，类别已存在");
        }

        if (categoryMapper.insert(new Category(parentId, categoryName, true, 0)) > 0){
            return ServerResponse.createByError("添加分类成功");
        }

        return ServerResponse.createByError("添加分类错误");
    }

    @Override
    public ServerResponse updateCategoryNameById(String categoryName, Integer categoryId) {
        if (StringUtils.isBlank(categoryName) || categoryId == null) {
            return ServerResponse.createByError("更新分类，参数错误");
        }

        Category category = categoryMapper.selectByPrimaryKey(categoryId);
        if (category == null){
            return ServerResponse.createByError("更新分类，编号不存在");
        }

        if (categoryMapper.checkCategoryNameByParentIdAndId(categoryName, category.getParentId(), category.getId()) > 0){
            return ServerResponse.createByError("更新分类，类别已存在");
        }

        category.setName(categoryName);
        if (categoryMapper.updateByPrimaryKeySelective(category) > 0){
            return ServerResponse.createBySuccess("更新分类成功");
        }

        return ServerResponse.createByError("更新分类失败");
    }

    @Override
    public ServerResponse<List<Category>> getChildrenParallelCategorys(Integer categoryId) {
        List<Category> categories = categoryMapper.selectCategoryChildrenByParentId(categoryId);
        if (CollectionUtils.isEmpty(categories)){
            log.info("不存在当前分类的子节点");
        }
        return ServerResponse.createBySuccess(categories);
    }

    @Override
    public ServerResponse<List<Integer>> selectCategoryChildById(Integer categoryId) {
        List<Integer> categoryIds = new ArrayList<>();
        categoryIds.add(categoryId);
        findChildCategory(categoryId, categoryIds);
        return ServerResponse.createBySuccess(categoryIds);
    }

    private void findChildCategory(Integer categoryId, List<Integer> categoryIds) {
        List<Category> categories = categoryMapper.selectCategoryChildrenByParentId(categoryId);
        for (Category category: categories){
            categoryIds.add(category.getId());
            findChildCategory(category.getId(), categoryIds);
        }
    }
}
