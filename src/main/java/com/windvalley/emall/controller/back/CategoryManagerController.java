package com.windvalley.emall.controller.back;

import com.windvalley.emall.common.Const;
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

import javax.servlet.http.HttpSession;

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
     * @param httpSession
     * @param categoryName
     * @param parentId
     * @return
     */
    @RequestMapping("addcategory.do")
    @ResponseBody
    public ServerResponse addCategory(HttpSession httpSession, String categoryName
                                     ,@RequestParam(value = "parentId", defaultValue = "0") int parentId){
        ServerResponse serverResponse = checkUserCanOperate(httpSession);
        if (serverResponse.isSuccess() == true){
            return categoryService.addCategory(categoryName, parentId);
        }
        return serverResponse;
    }

    /**
     * 修改分类名称
     * @param httpSession
     * @param categoryName
     * @param categoryId
     * @return
     */
    @RequestMapping("updatecategoryname.do")
    @ResponseBody
    public ServerResponse updateCategoryName(HttpSession httpSession, String categoryName, Integer categoryId){
        ServerResponse serverResponse = checkUserCanOperate(httpSession);
        if (serverResponse.isSuccess() == true){
            return categoryService.updateCategoryNameById(categoryName, categoryId);
        }
        return serverResponse;
    }

    /**
     * 得到当前category下的第一层子节点
     * @param httpSession
     * @param categoryId
     * @return
     */
    @RequestMapping("getcategory.do")
    @ResponseBody
    public ServerResponse getChildrenParallelCategorys(HttpSession httpSession
                                                     ,@RequestParam(value = "categoryId", defaultValue = "0") Integer categoryId){
        ServerResponse serverResponse = checkUserCanOperate(httpSession);
        if (serverResponse.isSuccess() == true){
            return categoryService.getChildrenParallelCategorys(categoryId);
        }
        return serverResponse;
    }

    /**
     * 获得当前category下的所有子节点
     * @param httpSession
     * @param categoryId
     * @return
     */
    @RequestMapping("getdeepcategory.do")
    @ResponseBody
    public ServerResponse getAllCategorys(HttpSession httpSession
            ,@RequestParam(value = "categoryId", defaultValue = "0") Integer categoryId){
        ServerResponse serverResponse = checkUserCanOperate(httpSession);
        if (serverResponse.isSuccess() == true){
            return categoryService.selectCategoryChildById(categoryId);
        }
        return serverResponse;
    }

    private ServerResponse checkUserCanOperate(HttpSession httpSession) {
        if (checkUserLogin(httpSession) == false){
            return ServerResponse.createByError(ResponseCode.NEED_LOGIN.getCode(), "用户未登录，需要登录");
        }

        if (checkUserIsManager(httpSession) == false){
            return ServerResponse.createByError("无管理员权限");
        }

        return ServerResponse.createBySuccess();
    }

    private boolean checkUserIsManager(HttpSession httpSession) {
        UserDTO userDTO = (UserDTO) httpSession.getAttribute(Const.CURRENT_USER);
        return userService.isManagerRole(userDTO.getUsername()).isSuccess();
    }

    private boolean checkUserLogin(HttpSession httpSession) {
        return (UserDTO) httpSession.getAttribute(Const.CURRENT_USER) != null;
    }
}
