package com.windvalley.emall.controller.back;

import com.windvalley.emall.common.ServerResponse;
import com.windvalley.emall.service.ICategoryService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;

@Controller
@RequestMapping("/manager/category/")
@Slf4j
public class CategoryManagerController {
    @Autowired
    private ICategoryService categoryService;

    /**
     * 添加商品分类
     * @param request
     * @param categoryName
     * @param parentId
     * @return
     */
    @RequestMapping("addcategory.do")
    @ResponseBody
    public ServerResponse addCategory(HttpServletRequest request, String categoryName
                                     ,@RequestParam(value = "parentId", defaultValue = "0") int parentId){
        return categoryService.addCategory(categoryName, parentId);
    }

    /**
     * 修改分类名称
     * @param request
     * @param categoryName
     * @param categoryId
     * @return
     */
    @RequestMapping("updatecategoryname.do")
    @ResponseBody
    public ServerResponse updateCategoryName(HttpServletRequest request, String categoryName, Integer categoryId){
        return categoryService.updateCategoryNameById(categoryName, categoryId);
    }

    /**
     * 得到当前category下的第一层子节点
     * @param request
     * @param categoryId
     * @return
     */
    @RequestMapping("getcategory.do")
    @ResponseBody
    public ServerResponse getChildrenParallelCategorys(HttpServletRequest request
                                                     ,@RequestParam(value = "categoryId", defaultValue = "0") Integer categoryId){
        return categoryService.getChildrenParallelCategorys(categoryId);
    }

    /**
     * 获得当前category下的所有子节点
     * @param request
     * @param categoryId
     * @return
     */
    @RequestMapping("getdeepcategory.do")
    @ResponseBody
    public ServerResponse getAllCategorys(HttpServletRequest request
            ,@RequestParam(value = "categoryId", defaultValue = "0") Integer categoryId){
        return categoryService.selectCategoryChildById(categoryId);
    }
}
