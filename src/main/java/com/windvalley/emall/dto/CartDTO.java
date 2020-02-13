package com.windvalley.emall.dto;

import com.windvalley.emall.enums.CartCheck;
import com.windvalley.emall.util.BigDecimalUtil;
import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

@Data
public class CartDTO {
    public void setCartItems(List<CartItemDTO> cartItems) {
        this.cartItems = cartItems;
        this.amount = calculateAmount(cartItems);
        this.allChecked = calculateAllCheck(cartItems);
    }

    private Boolean calculateAllCheck(List<CartItemDTO> cartItems) {
        for (CartItemDTO cartItemDTO: cartItems){
            if (cartItemDTO.getCheck() == CartCheck.UNCHECK.getCode()){
                return false;
            }
        }
        return (cartItems != null) && (cartItems.size() > 0);
    }

    private BigDecimal calculateAmount(List<CartItemDTO> cartItems) {
        BigDecimal amount = new BigDecimal("0");
        for (CartItemDTO cartItemDTO: cartItems){
            amount = BigDecimalUtil.add(amount.doubleValue(), cartItemDTO.getProductAmount().doubleValue());
        }
        return amount;
    }

    private List<CartItemDTO> cartItems;
    private BigDecimal amount;
    private Boolean allChecked;
    private String imageHost;
}
