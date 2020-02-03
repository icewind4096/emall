package com.windvalley.emall.service;

import com.windvalley.emall.common.ServerResponse;
import com.windvalley.emall.pojo.Category;

import java.util.List;

public interface ICategoryService {
    ServerResponse addCategory(String categoryName, int parentId);

    ServerResponse updateCategoryNameById(String categoryName, Integer categoryId);

    ServerResponse<List<Category>> getChildrenParallelCategorys(Integer categoryId);

    ServerResponse<List<Integer>> selectCategoryChildById(Integer categoryId);
}
