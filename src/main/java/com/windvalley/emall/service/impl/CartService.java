package com.windvalley.emall.service.impl;

import com.google.common.base.Splitter;
import com.windvalley.emall.common.ServerResponse;
import com.windvalley.emall.dao.CartMapper;
import com.windvalley.emall.dto.CartDTO;
import com.windvalley.emall.dto.CartItemDTO;
import com.windvalley.emall.dto.ProductDTO;
import com.windvalley.emall.enums.CartCheck;
import com.windvalley.emall.pojo.Cart;
import com.windvalley.emall.service.ICartService;
import com.windvalley.emall.service.IProductService;
import com.windvalley.emall.util.BigDecimalUtil;
import com.windvalley.emall.util.PropertiesUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.*;

@Service
public class CartService implements ICartService {
    @Autowired
    CartMapper cartMapper;

    @Autowired
    IProductService productService;

    @Override
    public ServerResponse update(Integer userId, Integer productId, Integer count) {
    //检查购物车中是否已经有改商品
        Cart cart = productInCart(userId, productId);
        if (cart == null) {
    //检查商品是否上架可用
            if (productExist(productId) == false){
                return ServerResponse.createByError("添加到购物车的产品不存在或已下架");
            }

            if (addCart(userId, productId, count) == false){
                return ServerResponse.createByError("添加商品到购物车失败");
            }
        } else {
            if (updateCart(cart, count) == false){
                return ServerResponse.createByError("添加商品到购物车失败");
            };
        }
        return ServerResponse.createBySuccess("添加商品到购物车成功");
    }

    @Override
    public ServerResponse<CartDTO> getListByUserId(Integer userId) {
        CartDTO cartDTO = new CartDTO();

        List<Cart> carts = cartMapper.selectByUserId(userId);

        if (carts != null){
            cartDTO.setCartItems(getCartItemListByCarts(carts));
            cartDTO.setImageHost(PropertiesUtil.getProperty("ftp.server.http.prefix"));
        }

        return ServerResponse.createBySuccess(cartDTO);
    }

    @Override
    public ServerResponse delete(Integer userId, String productIds) {
        List<String> productIdList = Splitter.on(",").splitToList(productIds);
        if (CollectionUtils.isEmpty(productIdList)){
            return ServerResponse.createByError("购物车删除商品失败->参数错误");
        }
        if (cartMapper.deleteByUserIdAndProductIds(userId, productIdList) > 1){
            return ServerResponse.createBySuccess("购物车删除商品成功");
        }
        return ServerResponse.createByError("购物车删除商品失败");
    }

    @Override
    public ServerResponse checkAll(Integer userId) {
        if (changeCheckStatus(userId, CartCheck.CHECK.getCode(), null)){
            return ServerResponse.createBySuccess("购物车全选成功");
        }
        return ServerResponse.createBySuccess("购物车全选失败");
    }

    @Override
    public ServerResponse unCheckAll(Integer userId) {
        if (changeCheckStatus(userId, CartCheck.UNCHECK.getCode(), null)){
            return ServerResponse.createBySuccess("购物车全反选成功");
        }
        return ServerResponse.createBySuccess("购物车全反选失败");
    }

    @Override
    public ServerResponse check(Integer userId, Integer productId) {
        if (changeCheckStatus(userId, CartCheck.CHECK.getCode(), productId)){
            return ServerResponse.createBySuccess("购物车全反选成功");
        }
        return ServerResponse.createBySuccess("购物车全反选失败");
    }

    @Override
    public ServerResponse unCheck(Integer userId, Integer productId) {
        if (changeCheckStatus(userId, CartCheck.UNCHECK.getCode(), productId)){
            return ServerResponse.createBySuccess("购物车全反选成功");
        }
        return ServerResponse.createBySuccess("购物车全反选失败");
    }

    @Override
    public ServerResponse<Integer> getCartProductNumber(Integer userId) {
        if (userId == null){
            return ServerResponse.createBySuccess(0);
        }
        return ServerResponse.createBySuccess(cartMapper.getCartProductNumberByUserId(userId));
    }

    private boolean changeCheckStatus(Integer userId, Integer checked, Integer productId) {
        return cartMapper.changeAllCheckStatus(userId, checked, productId) > 0;
    }

    private List<CartItemDTO> getCartItemListByCarts(List<Cart> carts) {
        List<CartItemDTO> cartItemDTOs = new ArrayList<>();
        for (Cart cart : carts) {
            CartItemDTO cartItemDTO = new CartItemDTO();
            cartItemDTO.setId(cart.getId());
            cartItemDTO.setCheck(cart.getChecked());
            cartItemDTO.setProductId(cart.getProductId());
            cartItemDTO.setUserId(cart.getUserId());
            cartItemDTO.setQuantity(cart.getQuantity());
            ProductDTO productDTO = productService.getDetailByUser(cart.getProductId()).getData();
            if (productDTO != null){
                cartItemDTO.setProductName(productDTO.getName());
                cartItemDTO.setProductMainImage(productDTO.getMainImage());
                cartItemDTO.setProductSubtitle(productDTO.getSubtitle());
                cartItemDTO.setProductPrice(productDTO.getPrice());
                cartItemDTO.setProductStock(productDTO.getStock());
                cartItemDTO.setProductStatus(productDTO.getStatus());
                cartItemDTO.setProductAmount(BigDecimalUtil.mul(cartItemDTO.getQuantity(), cartItemDTO.getProductPrice().doubleValue()));
            }
            cartItemDTOs.add(cartItemDTO);
        }
        return cartItemDTOs;
    }

    private boolean updateCart(Cart cart, Integer count) {
        cart.setQuantity(cart.getQuantity() + count);
        cart.setChecked(CartCheck.CHECK.getCode());
        return cartMapper.updateByPrimaryKeySelective(cart) > 0;
    }

    private boolean addCart(Integer userId, Integer productId, Integer count) {
        Cart cart = new Cart();
        cart.setUserId(userId);
        cart.setProductId(productId);
        cart.setQuantity(count);
        cart.setChecked(CartCheck.CHECK.getCode());
        return cartMapper.insert(cart) > 0;
    }

    private Cart productInCart(Integer userId, Integer productId) {
        return cartMapper.selectByUserIdAndProductId(userId, productId);
    }

    private boolean productExist(Integer productId) {
        ServerResponse<ProductDTO> serverResponse = productService.getDetailByUser(productId);
        return serverResponse.getData() != null;
    }
}


/*
    private CartDTO getCartLimit(Integer userId){
    }

 */
