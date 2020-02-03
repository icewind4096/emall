package com.windvalley.emall.service.impl;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.windvalley.emall.common.ServerResponse;
import com.windvalley.emall.converter.Product2ProductDTO;
import com.windvalley.emall.converter.ProductDTO2Product;
import com.windvalley.emall.dao.CategoryMapper;
import com.windvalley.emall.dao.ProductMapper;
import com.windvalley.emall.dto.ProductDTO;
import com.windvalley.emall.enums.ProductStatus;
import com.windvalley.emall.pojo.Category;
import com.windvalley.emall.pojo.Product;
import com.windvalley.emall.service.ICategoryService;
import com.windvalley.emall.service.IProductService;
import com.windvalley.emall.util.PropertiesUtil;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class ProductService implements IProductService {
    @Autowired
    ProductMapper productMapper;

    @Autowired
    CategoryMapper categoryMapper;

    @Autowired
    ICategoryService categoryService;

    @Override
    public ServerResponse save(ProductDTO productDTO) {
    //检查Product是否为空
        if (productDTO == null){
            return ServerResponse.createByError("添加产品，参数错误");
        }

    //检查类别是否存在
        if (categoryMapper.checkCategoryId(productDTO.getCategoryId()) == 0){
            return ServerResponse.createByError("添加产品，商品类别不存在");
        }

    //检查相同类别下是否有相同的产品名称
        if (productMapper.selectByProductNameAndCategoryId(productDTO.getName(), productDTO.getCategoryId()) != null){
            return ServerResponse.createByError("添加产品，同类别下存在相同商品名称");
        };

    //把子图的第一个图片作为组图的数据
        productDTO.setMainImage(getMainImageFromSubImage(productDTO.getSubImages()));
    //新建产品默认为为在线销售状态
        productDTO.setStatus(ProductStatus.ONLINE.getCode());

    //添加商品
        if (productMapper.insert(ProductDTO2Product.convert(productDTO)) == 0){
            return ServerResponse.createByError("添加产品失败");
        }

        return ServerResponse.createBySuccess("添加产品成功");
    }

    @Override
    public ServerResponse update(ProductDTO productDTO) {
    //检查Product是否为空
        if (productDTO.getId() == null){
            return ServerResponse.createByError("修改产品，参数错误");
        }

    //把子图的第一个图片作为组图的数据
        productDTO.setMainImage(getMainImageFromSubImage(productDTO.getSubImages()));

    //名字不让修改,设置为NULL
        productDTO.setName(null);

    //修改产品信息
        if (productMapper.updateByPrimaryKeySelective(ProductDTO2Product.convert(productDTO)) == 0){
            return ServerResponse.createByError("修改产品失败");
        }

        return ServerResponse.createBySuccess("修改产品成功");
    }

    @Override
    public ServerResponse updateStatus(Integer productId, Integer status) {
    //检查status是否为空
        if (status == null){
            return ServerResponse.createByError("产品上下架失败，参数错误");
        }

    //得到Product信息
        Product product = productMapper.selectByPrimaryKey(productId);
        if (product == null){
            return ServerResponse.createByError("产品上下架失败，产品不存在");
        }

    //设置上下架状态
        product.setStatus(status);

    //修改产品信息
        if (productMapper.updateByPrimaryKeySelective(product) == 0){
            return ServerResponse.createByError("产品上下架失败");
        }

        return ServerResponse.createBySuccess("产品上下架成功");
    }

    @Override
    public ServerResponse getDetailByManager(Integer productId) {
    //检查status是否为空
        if (productId == null){
            return ServerResponse.createByError("产品详细，参数错误");
        }

    //获得详细
        Product product = productMapper.selectByPrimaryKey(productId);
        if (product == null){
            return ServerResponse.createByError("产品已下架或已被删除");
        }

        return ServerResponse.createBySuccess(assemble2ProductDTO(product));
    }

    @Override
    public ServerResponse<PageInfo> getProductListByManager(Integer pageNumber, Integer pageSize) {
        PageHelper.startPage(pageNumber, pageSize);
        List<Product> products = productMapper.productList();
        PageInfo pageInfo = new PageInfo(assemble2ProductDTOList(products));
        return ServerResponse.createBySuccess(pageInfo);
    }

    @Override
    public ServerResponse<PageInfo> search(String productName, Integer productId, Integer pageNumber, Integer pageSize) {
        if (StringUtils.isNoneBlank(productName)) {
            productName = String.format("%s%s%s", "%", productName, "%");
        }
        PageHelper.startPage(pageNumber, pageSize);
        List<Product> products = productMapper.productListByNameAndId(productName, productId);
        PageInfo pageInfo = new PageInfo(assemble2ProductDTOList(products));
        return ServerResponse.createBySuccess(pageInfo);
    }

    public ServerResponse<ProductDTO> getDetailByUser(Integer productId) {
    //检查status是否为空
        if (productId == null){
            return ServerResponse.createByError("产品详细，参数错误");
        }

    //获得详细
        Product product = productMapper.selectByPrimaryKey(productId);
    //前台不可以查看非上架商品
        if (product == null || ProductStatus.ONLINE.getCode() != product.getStatus()){
            return ServerResponse.createByError("产品已下架或已被删除");
        }

        return ServerResponse.createBySuccess(assemble2ProductDTO(product));
    }

    @Override
    public ServerResponse<PageInfo> getProductByKeyWordAndCategoryId(String keyWord, Integer categoryId, Integer pageNumber, Integer pageSize, String orderBy) {
    //检查status是否为空
        if (StringUtils.isBlank(keyWord) && categoryId == null){
            return ServerResponse.createByError("产品详细，参数错误");
        }
    //检查Category是否存在
        List<Integer> categoryIds = new ArrayList<>();
        if (categoryId != null){
            Category category = categoryMapper.selectByPrimaryKey(categoryId);
            if (category == null){
                PageHelper.startPage(pageNumber, pageSize);
                PageInfo pageInfo = new PageInfo(new ArrayList<ProductDTO>());
                return ServerResponse.createBySuccess(pageInfo);
            }
    //获得全部Category已经全部子节点
            categoryIds = categoryService.selectCategoryChildById(categoryId).getData();
        }
    //检查keyword是否为空
        if (StringUtils.isNoneBlank(keyWord)){
            keyWord = String.format("%s%s%s", "%", keyWord, "%");
        }
    //处理排序
        String[] orderBys = null;
        if (StringUtils.isNoneBlank(orderBy)){
            orderBys = orderBy.split("_");
        }

    //开始分页
        PageHelper.startPage(pageNumber, pageSize);
    //处理排序
        if (orderBys != null && orderBys.length == 2){
            PageHelper.orderBy(String.format("%s %s", orderBys[0], orderBys[1]));
        }
        List<Product> products = productMapper.productListByNameAndCategorys(StringUtils.isBlank(keyWord) ? null : keyWord
                                                                            ,categoryIds.size() == 0 ? null : categoryIds);
        PageInfo pageInfo = new PageInfo(assemble2ProductDTOList(products));
        return ServerResponse.createBySuccess(pageInfo);
    }

    private List<ProductDTO> assemble2ProductDTOList(List<Product> products) {
        List<ProductDTO> productDTOs = new ArrayList<>();
        for (Product product: products){
            productDTOs.add(assemble2ProductDTO(product));
        }
        return productDTOs;
    }

    private ProductDTO assemble2ProductDTO(Product product) {
        ProductDTO productDTO = Product2ProductDTO.convert(product);
        productDTO.setImageHost(PropertiesUtil.getProperty("ftp.server.http.prefix"));

        Category category = categoryMapper.selectByPrimaryKey(product.getCategoryId());
        if (category == null){
    //如果不存在，默认为根节点
            productDTO.setCategoryParentId(0);
        }
        productDTO.setCategoryParentId(category.getParentId());

        return productDTO;
    }

    private String getMainImageFromSubImage(String subImages) {
        if (StringUtils.isBlank(subImages)){
            return "";
        } else {
            String[] images = subImages.split(",");
            return images[0];
        }
    }
}
