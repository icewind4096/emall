package com.windvalley.emall.service.impl;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.windvalley.emall.common.ServerResponse;
import com.windvalley.emall.converter.Shipping2ShippingDTO;
import com.windvalley.emall.converter.ShippingDTO2Shipping;
import com.windvalley.emall.dao.ShippingMapper;
import com.windvalley.emall.dto.ShippingDTO;
import com.windvalley.emall.pojo.Shipping;
import com.windvalley.emall.service.IShippingService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@Slf4j
public class ShippingService implements IShippingService {
    @Autowired
    ShippingMapper shippingMapper;

    @Override
    public ServerResponse add(ShippingDTO shippingDTO) {
        Shipping shipping = ShippingDTO2Shipping.convert(shippingDTO);
        if (shippingMapper.insert(shipping) > 0){
            return ServerResponse.createBySuccess("添加收货地址->成功", getAddData(shipping));
        }
        return ServerResponse.createByError("添加收货地址->失败");
    }

    @Override
    public ServerResponse del(Integer userId, Integer shippingId) {
        if (shippingMapper.del(userId, shippingId) > 0){
            return ServerResponse.createBySuccess("删除收货地址->成功");
        }
        return ServerResponse.createByError("删除收货地址->失败");
    }

    @Override
    public ServerResponse update(ShippingDTO shippingDTO) {
        Shipping shipping = ShippingDTO2Shipping.convert(shippingDTO);
        if (shippingMapper.update(shipping) > 0){
            return ServerResponse.createBySuccess("修改收货地址->成功");
        }
        return ServerResponse.createByError("修改收货地址->失败");
    }

    @Override
    public ServerResponse<ShippingDTO> detail(Integer userId, Integer shippingId) {
        Shipping shipping = shippingMapper.getInfoByUserIdAndShippingId(userId, shippingId);
        if (shipping != null){
            ShippingDTO shippingDTO = Shipping2ShippingDTO.convert(shipping);
            return ServerResponse.createBySuccess("查询收货地址详细成功", shippingDTO);
        }
        return ServerResponse.createByError("查询收货地址详细->无数据");
    }

    @Override
    public ServerResponse<PageInfo> listShipping(Integer userId, Integer pageNumber, Integer pageSize) {
        PageHelper.startPage(pageNumber, pageSize);

        List<Shipping> shippings = shippingMapper.listByUserId(userId);
        PageInfo pageInfo = new PageInfo(assemble2ShipDTOList(shippings));

        return ServerResponse.createBySuccess(pageInfo);
    }

    private List<ShippingDTO> assemble2ShipDTOList(List<Shipping> shippings) {
        List<ShippingDTO> shippingDTOs = new ArrayList<>();
        for (Shipping shipping : shippings){
            ShippingDTO shippingDTO = Shipping2ShippingDTO.convert(shipping);
            shippingDTOs.add(shippingDTO);
        }
        return shippingDTOs;
    }

    private Map<String, Integer> getAddData(Shipping shipping) {
        Map<String, Integer> map = new HashMap<>();
        map.put("shippingId", shipping.getId());
        return map;
    }
}
