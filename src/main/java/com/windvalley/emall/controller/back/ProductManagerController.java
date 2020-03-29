package com.windvalley.emall.controller.back;

import com.github.pagehelper.PageInfo;
import com.windvalley.emall.common.ServerResponse;
import com.windvalley.emall.converter.Product2ProductDTO;
import com.windvalley.emall.dto.ProductDTO;
import com.windvalley.emall.enums.ProductStatus;
import com.windvalley.emall.form.ProductFrom;
import com.windvalley.emall.service.IFileService;
import com.windvalley.emall.service.IProductService;
import com.windvalley.emall.util.PropertiesUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.HashMap;
import java.util.Map;

@Controller
@RequestMapping("/manager/product")
public class ProductManagerController {
    @Autowired
    private IProductService productService;

    @Autowired
    private IFileService fileService;

    /**
     * 新建产品信息
     * @param request
     * @param productFrom
     * @return
     */
    @RequestMapping("save.do")
    @ResponseBody
    public ServerResponse save(HttpServletRequest request, ProductFrom productFrom){
        return productService.save(Product2ProductDTO.convert(productFrom));
    }

    /**
     * 修改产品信息
     * @param request
     * @param productFrom
     * @return
     */
    @RequestMapping("update.do")
    @ResponseBody
    public ServerResponse update(HttpServletRequest request, ProductFrom productFrom){
        return productService.update(Product2ProductDTO.convert(productFrom));
    }

    /**
     * 商品删除
     * @param request
     * @param productId
     * @return
     */
    @RequestMapping("delete.do")
    @ResponseBody
    public ServerResponse delete(HttpServletRequest request, Integer productId){
        return updateStatus(request, productId, ProductStatus.DELETE.getCode());
    }

    /**
     * 商品明细
     * @param request
     * @param productId
     * @return
     */
    @RequestMapping("detail.do")
    @ResponseBody
    public ServerResponse<ProductDTO> getDetail(HttpServletRequest request, Integer productId){
        return productService.getDetailByManager(productId);
    }

    /**
     * 商品列表
     * @param request
     * @param pageNumber
     * @param pageSize
     * @return
     */
    @RequestMapping("list.do")
    @ResponseBody
    public ServerResponse<PageInfo> getList(HttpServletRequest request, @RequestParam(value = "pageNumber", defaultValue = "1") Integer pageNumber
                                 , @RequestParam(value = "pageSize", defaultValue = "10") Integer pageSize){
        return productService.getProductListByManager(pageNumber, pageSize);
    }

    /**
     * 商品查询
     * @param httpSession
     * @param productName
     * @param productId
     * @param pageNumber
     * @param pageSize
     * @return
     */
    @RequestMapping("search.do")
    @ResponseBody
    public ServerResponse<PageInfo> search(HttpServletRequest httpSession
                                ,String productName, Integer productId
                                ,@RequestParam(value = "pageNumber", defaultValue = "1") Integer pageNumber
                                ,@RequestParam(value = "pageSize", defaultValue = "10") Integer pageSize){
        return productService.search(productName, productId, pageNumber, pageSize);
    }

    /**
     * 商品上线
     * @param httpSession
     * @param productId
     * @return
     */
    @RequestMapping("statusonline.do")
    @ResponseBody
    public ServerResponse updateStatusOnline(HttpServletRequest httpSession, Integer productId){
        return updateStatus(httpSession, productId, ProductStatus.ONLINE.getCode());
    }

    /**
     * 商品下线
     * @param httpSession
     * @param productId
     * @return
     */
    @RequestMapping("statusoffline.do")
    @ResponseBody
    public ServerResponse updateStatusOffline(HttpServletRequest httpSession, Integer productId){
        return updateStatus(httpSession, productId, ProductStatus.OFFLINE.getCode());
    }

    /**
     * 上传文件
     * @param request
     * @param file
     * @param request
     * @return
     */
    @RequestMapping("upload.do")
    @ResponseBody
    public ServerResponse upload(MultipartFile file, HttpServletRequest request){
        String path = request.getSession().getServletContext().getRealPath(getWebAppUploadDir());
        String targetFileName = fileService.upload(file, path);

        return ServerResponse.createBySuccess(getUploadFileResponseData(targetFileName));
    }

    private Map getUploadFileResponseData(String fileName) {
        Map fileMap = new HashMap<String, String>();
        fileMap.put("uri", fileName);
        fileMap.put("url", getUploadFTPServerFileName(fileName));
        return fileMap;
    }

    private String getFTPServerPrefix() {
        return PropertiesUtil.getProperty("ftp.server.http.prefix");
    }

    private String getWebAppUploadDir() {
        return PropertiesUtil.getProperty("webapp.upload.dir");
    }

    /**
     * 上传富文本
     * @param response
     * @param file
     * @param request
     * @return 富文本的返回有特定格式
     * {
     *  "success", true/false), #成功/失败
     *  "msg", "error message"  #选项
     *  "file_path", url        #文件路径
     * }
     */
    @RequestMapping("richUpload.do")
    @ResponseBody
    public Map richUpload(MultipartFile file, HttpServletRequest request, HttpServletResponse response){
        Map<String, String> map = new HashMap<>();
        String path = request.getSession().getServletContext().getRealPath(getWebAppUploadDir());
        String targetFileName = fileService.upload(file, path);
        map = getUploadRichResponseDataSuccess(targetFileName);
        response.addHeader("Access-Control-Allow-Headers", "X-File-Name");
        return map;
    }

    private Map<String, String> getUploadRichResponseDataSuccess(String fileName) {
        Map map = new HashMap<String, String>();
        map.put("success", "true");
        map.put("msg", "上传富文本成功");
        map.put("file_path", getUploadFTPServerFileName(fileName));
        return map;
    }

    private String getUploadFTPServerFileName(String fileName) {
        return getFTPServerPrefix() + fileName;
    }

    private ServerResponse updateStatus(HttpServletRequest request, Integer productId, Integer status) {
        return productService.updateStatus(productId, status);
    }
}
