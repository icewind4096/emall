package com.windvalley.emall.controller.back;

import com.windvalley.emall.common.ServerResponse;
import com.windvalley.emall.dto.UserDTO;
import com.windvalley.emall.enums.ResponseCode;
import com.windvalley.emall.service.ICategoryService;
import com.windvalley.emall.service.IUserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;

import static com.windvalley.emall.controller.common.UserLogin.getUserDTOFromRedis;
import static com.windvalley.emall.controller.common.UserLogin.getUserDTOKey;

@Controller
@RequestMapping("/manager/category/")
@Slf4j
public class CategoryManagerController {
    @Autowired
    private IUserService userService;

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
        ServerResponse serverResponse = checkUserCanOperate(request);
        if (serverResponse.isSuccess() == true){
            return categoryService.addCategory(categoryName, parentId);
        }
        return serverResponse;
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
        ServerResponse serverResponse = checkUserCanOperate(request);
        if (serverResponse.isSuccess() == true){
            return categoryService.updateCategoryNameById(categoryName, categoryId);
        }
        return serverResponse;
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
        ServerResponse serverResponse = checkUserCanOperate(request);
        if (serverResponse.isSuccess() == true){
            return categoryService.getChildrenParallelCategorys(categoryId);
        }
        return serverResponse;
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
        ServerResponse serverResponse = checkUserCanOperate(request);
        if (serverResponse.isSuccess() == true){
            return categoryService.selectCategoryChildById(categoryId);
        }
        return serverResponse;
    }

    private ServerResponse checkUserCanOperate(HttpServletRequest request) {
        if (checkUserLogin(request) == false){
            return ServerResponse.createByError(ResponseCode.NEED_LOGIN.getCode(), "用户未登录，需要登录");
        }

        if (checkUserIsManager(request) == false){
            return ServerResponse.createByError("无管理员权限");
        }

        return ServerResponse.createBySuccess();
    }

    private boolean checkUserIsManager(HttpServletRequest request) {
        UserDTO userDTO = getUserDTOFromRedis(getUserDTOKey(request));
        return userService.isManagerRole(userDTO.getUsername()).isSuccess();
    }

    private boolean checkUserLogin(HttpServletRequest request) {
        return getUserDTOFromRedis(getUserDTOKey(request)) != null;
    }
}
