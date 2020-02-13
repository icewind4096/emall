package com.windvalley.emall.controller.portal;

import com.github.pagehelper.PageInfo;
import com.windvalley.emall.common.ServerResponse;
import com.windvalley.emall.dto.ProductDTO;
import com.windvalley.emall.service.IProductService;
import com.windvalley.emall.service.IUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpSession;

@Controller
@RequestMapping("/product/")
public class ProductController {
    @Autowired
    IUserService userService;

    @Autowired
    IProductService productService;

    /**
     * 得到商品信息
     * @param httpSession
     * @param productId
     * @return
     */
    @RequestMapping("detail.do")
    @ResponseBody
    public ServerResponse<ProductDTO> detail(HttpSession httpSession, Integer productId){
        return productService.getDetailByUser(productId);
    }

    /**
     * 查找商品
     * @param httpSession
     * @param keyWord
     * @param categoryId
     * @param pageNumber
     * @param pageSize
     * @param orderBy
     * @return
     */
    @RequestMapping("list.do")
    @ResponseBody
    public ServerResponse<PageInfo> getList(HttpSession httpSession
                                   ,@RequestParam(value = "keyWord", required = false) String keyWord
                                   ,@RequestParam(value = "categoryId", required = false) Integer categoryId
                                   ,@RequestParam(value = "pageNumber", defaultValue = "1") Integer pageNumber
                                   ,@RequestParam(value = "pageSize", defaultValue = "10") Integer pageSize
                                   ,@RequestParam(value = "orderBy", defaultValue = "") String orderBy){
        return productService.getProductByKeyWordAndCategoryId(keyWord, categoryId, pageNumber, pageSize, orderBy);
    }
}
