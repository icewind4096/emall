package com.windvalley.emall.service.impl;

import com.alipay.api.AlipayApiException;
import com.alipay.api.AlipayClient;
import com.alipay.api.DefaultAlipayClient;
import com.alipay.api.domain.AlipayTradePrecreateModel;
import com.alipay.api.domain.GoodsDetail;
import com.alipay.api.request.AlipayTradePrecreateRequest;
import com.alipay.api.response.AlipayTradePrecreateResponse;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.windvalley.emall.common.ServerResponse;
import com.windvalley.emall.converter.Order2OrderDTO;
import com.windvalley.emall.converter.OrderItem2OrderItemDTO;
import com.windvalley.emall.converter.Shipping2ShippingDTO;
import com.windvalley.emall.dao.*;
import com.windvalley.emall.dto.OrderDTO;
import com.windvalley.emall.dto.OrderItemDTO;
import com.windvalley.emall.dto.OrderProductDTO;
import com.windvalley.emall.enums.AlipayTradeStatus;
import com.windvalley.emall.enums.OrderStatus;
import com.windvalley.emall.enums.PayPlatform;
import com.windvalley.emall.enums.ProductStatus;
import com.windvalley.emall.pay.alipay.PayConfig;
import com.windvalley.emall.pojo.*;
import com.windvalley.emall.service.IFileService;
import com.windvalley.emall.service.IOrderService;
import com.windvalley.emall.util.BigDecimalUtil;
import com.windvalley.emall.util.PropertiesUtil;
import com.windvalley.emall.util.QRCodeUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.File;
import java.math.BigDecimal;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

@Service
@Slf4j
public class OrderService implements IOrderService {
    @Autowired
    CartMapper cartMapper;

    @Autowired
    OrderMapper orderMapper;

    @Autowired
    OrderItemMapper orderItemMapper;

    @Autowired
    PayInfoMapper payInfoMapper;

    @Autowired
    IFileService fileService;

    @Autowired
    ProductMapper productMapper;

    @Autowired
    ShippingMapper shippingMapper;

    //前台服务接口
    @Override
    public ServerResponse pay(Integer userId, Long orderNo, String webAppUploadPath) {
        Order order = orderMapper.selectByUserIdAndOrderNo(userId, orderNo);
        if (order == null){
            return ServerResponse.createByError("该用户下不存在对应订单");
        }

        List<OrderItem> orderItems = orderItemMapper.selectByUserIdAndOrderNo(userId, orderNo);

        ServerResponse serverResponse = aliPay4Order(order, orderItems);
        if (serverResponse.isSuccess()){
        //如果调用成功会返回一个当前预下单请求生成的二维码码串，根据该码串值生成对应的二维码
        //产生二维码文件存放于webapp/upload目录下
            if (generateQRFile(webAppUploadPath, getQRFileName(orderNo, QRCodeUtil.FORMATNAME), QRCodeUtil.FORMATNAME, serverResponse.getMsg()) == false){
                return ServerResponse.createByError("产生二维码失败");
            }
            String ftpFileName = fileService.upload(new File(webAppUploadPath, getQRFileName(orderNo, QRCodeUtil.FORMATNAME)));
            if (StringUtils.isNoneBlank((ftpFileName))){
                Map<String, String> map = new HashMap();
                map.put("qrURL", String.format("%s/%s", getFTPServerPrefix(), ftpFileName));
                log.info(">>>二维码上传ftp服务器成功, 文件位于:{}", ftpFileName);
                return ServerResponse.createBySuccess(map);
            } else {
                log.info(">>>二维码上传ftp服务器失败");
                return ServerResponse.createBySuccess("上传二维码到FTP服务器失败");
            }
        }
        return ServerResponse.createByError("支付宝预调用下单API错误");
    }

    private String getFTPServerPrefix() {
        return PropertiesUtil.getProperty("ftp.server.http.prefix");
    }

    private boolean generateQRFile(String path, String fileName, String formatname, String content) {
    //产生二维码文件
        if (QRCodeUtil.encode(content, path, fileName, formatname)){
            log.info(">>>建立二维码成功,文件存放在:{}", String.format("%s%s",path, fileName));
            return true;
        } else {
            log.info(">>>建立二维码文件失败");
            return false;
        }
    }

    private void makeDirectory(String path) {
        File directory = new File(path);
        if (directory.isDirectory() == false){
            directory.setWritable(true);
            directory.mkdirs();
        }
    }

    private String getQRFileName(Long orderNo, String formatName) {
        return String.format("\\%d.%s", orderNo, formatName);
    }

    private ServerResponse<String> aliPay4Order(Order order, List<OrderItem> orderItems) {
        try {
        //1.创建使用的Open API对应的Request请求对象 这里使用的当面付
        //  具体参数参考https://docs.open.alipay.com/api_1/alipay.trade.precreate/
        //  以下为关键入参
        //      out_trade_no            商户订单号，需要保证不重复
        //      total_amount            订单金额
        //      subject                 订单标题
        //      store_id                商户门店编号
        //      timeout_express         交易超时时间
            AlipayTradePrecreateModel model = new AlipayTradePrecreateModel();
            model.setOutTradeNo(String.valueOf(order.getOrderNo()));
            model.setSubject(String.format("EMall扫码支付, 订单号{}", model.getOutTradeNo()));
            model.setTotalAmount(order.getPayment().toString());
            model.setDiscountableAmount("0");
            //商户编码一定要写
            model.setSellerId(PayConfig.SELLID);
            model.setBody(String.format("订单{}, 购买商品需支付{}元", model.getOutTradeNo(), model.getTotalAmount()));
            model.setStoreId("");
            model.setTimeoutExpress("120m");
            model.setGoodsDetail(getGoodsDetailByOrderItemList(orderItems));
        //2.实例化具体API对应的request类,类名称和接口名称对应,接口在泳道图里面标明，当前调用接口名称：alipay.trade.precreate
            AlipayTradePrecreateRequest request = new AlipayTradePrecreateRequest();
            request.setBizModel(model);
            request.setNotifyUrl(PayConfig.ALIPAY_NOTIFY_URL);
            log.info(">>>支付宝统一收单线下交易预创建参数->Body={},TotalAmount={}", model.getBody(), model.getTotalAmount());

        //3.创建AlipayClient实例
            AlipayClient alipayClient = new DefaultAlipayClient(PayConfig.SERVERURL, PayConfig.APPID
                                                               ,PayConfig.APP_PRIVATE_KEY, PayConfig.FORMAT
                                                               ,PayConfig.CHARSET, PayConfig.ALIPAY_PUBLIC_KEY
                                                               ,PayConfig.SIGN_TYPE);
            //3.得到调用返回的数据
            AlipayTradePrecreateResponse response = alipayClient.execute(request);
            if (response.isSuccess()){
                log.info(">>>支付宝调用预下单API成功, 返回参数Code->{}Message->{},Body->{},QrCode->{}", response.getCode(), response.getMsg(), response.getBody(), response.getQrCode());
                return ServerResponse.createBySuccess(response.getQrCode());
            } else {
                log.info(">>>支付宝调用预下单API失败Code={}Message={},Body={}", response.getCode(), response.getMsg(), response.getBody());
                return ServerResponse.createByError("支付宝调用预下单API失败");
            }
        } catch (AlipayApiException e) {
            log.error("支付宝预调用下单API错误", e);
            throw new RuntimeException(e.getMessage(), e);
        }
    }

    private String getFileNameByOrderNo(String uploadFilePath, String outTradeNo) {
        return String.format("{}/qr-{}.png", uploadFilePath, outTradeNo);
    }

    private List<GoodsDetail> getGoodsDetailByOrderItemList(List<OrderItem> orderItems) {
        List<GoodsDetail> goodsDetails = new ArrayList<>();
        for (OrderItem orderItem : orderItems){
            GoodsDetail goodsDetail = new GoodsDetail();
            goodsDetail.setGoodsId(orderItem.getProductId().toString());
            goodsDetail.setGoodsName(orderItem.getProductName());
            goodsDetail.setQuantity(orderItem.getQuantity().longValue());
            goodsDetail.setPrice(orderItem.getCurrentUnitPrice().toString());
            goodsDetails.add(goodsDetail);
        }
        return goodsDetails;
    }

    @Override
    public ServerResponse processAlipayBack(Map<String, String> map) {
        Order order = orderMapper.selectByOrderNo(getOrerNoFromMap(map));
        ServerResponse serverResponse = (checkBackOrderData(order, getPayAmountFromMap(map), getSellIdFromMap(map)));
        if (serverResponse.isSuccess() == false){
            return serverResponse;
        }

        if (checkBackOrderDataNeedProcess(order)){
            return ServerResponse.createBySuccess("支付宝重复调用");
        }

        return _process(order, map);
    }

    @Override
    public ServerResponse<Boolean> orderpaied(Integer userId, Long orderNo) {
        Order order = orderMapper.selectByUserIdAndOrderNo(userId, orderNo);
        if (order == null){
            return ServerResponse.createByError("订单不存在");
        }

        return ServerResponse.createBySuccess(order.getStatus() > OrderStatus.NOPAY.getCode());
    }

    @Override
    public ServerResponse create(Integer userId, Integer shippingId) {
    //取出用户在购物车中选中的商品
        List<Cart> carts = cartMapper.selectCheckByUserId(userId);
        if (carts == null){
            return ServerResponse.createByError("购物车为空");
        }

    //获得购物车此时对应的库存单价，数量，状态
        ServerResponse serverResponse = getOrderItemByCart(userId, carts);
        if (serverResponse.isSuccess() == false){
            return serverResponse;
        }

        List<OrderItem> orderItems = (List<OrderItem>) serverResponse.getData();
        if (orderItems.size() == 0){
            return ServerResponse.createByError("订单->购物车为空");
        }
        BigDecimal totalAmount = getOrderTotalAmount(orderItems);

    //构建订单
        Order order = saveOrderMain(userId, shippingId, totalAmount);
        if (order == null){
            return ServerResponse.createByError("订单->产生主订单错误");
        }

        orderItems = prepareOrderDetail(order.getOrderNo(), orderItems);
        if (saveOrderDetail(orderItems) == false){
            return ServerResponse.createByError("订单->产生订单明细错误");
        }

    //减少库存
        reduceProductStock(orderItems);

    //清空购物车
        clearCart(carts);

        return ServerResponse.createBySuccess(assembleOrderDTO(order, orderItems));
    }

    @Override
    public ServerResponse cancel(Integer userId, Long orderId) {
    //取出订单
        Order order = orderMapper.selectByUserIdAndOrderNo(userId, orderId);
        if (order == null){
            return ServerResponse.createByError("取消订单->该用户无此订单");
        }

    //检查订单状态
        if (order.getStatus() == OrderStatus.CANCELED.getCode()){
            return ServerResponse.createByError("取消订单->该订单已取消, 不可再次取消");
        }

        if (order.getStatus() != OrderStatus.NOPAY.getCode()){
            return ServerResponse.createByError("取消订单->该订单已付款, 不可取消");
        }

     //修改状态
        order.setStatus(OrderStatus.CANCELED.getCode());
        if (orderMapper.updateByPrimaryKeySelective(order) > 0){
            return ServerResponse.createBySuccess("取消订单->取消成功");
        } else {
            return ServerResponse.createByError("取消订单->取消失败");
        }
    }

    @Override
    public ServerResponse getOrderCartProduct(Integer userId) {
    //从购物车中取出数据
        List<Cart> carts = cartMapper.selectCheckByUserId(userId);
    //获得购物车此时对应的库存单价，数量，状态
        ServerResponse serverResponse = getOrderItemByCart(userId, carts);
    //计算总价
        List<OrderItem> orderItems = (List<OrderItem>) serverResponse.getData();
        if (orderItems.size() == 0){
            return ServerResponse.createByError("订单->购物车为空");
        }

        OrderProductDTO orderProductDTO = new OrderProductDTO();
        orderProductDTO.setPayment(getOrderTotalAmount(orderItems));
        orderProductDTO.setImageHost(getFTPServerPrefix());
        orderProductDTO.setOrderItemDTOList(OrderItem2OrderItemDTO.convert(orderItems));

        return ServerResponse.createBySuccess(orderProductDTO);
    }

    @Override
    public ServerResponse<OrderDTO> detailByOrderIdAndUserId(Integer userId, Long orderId) {
    //取出订单
        Order order = orderMapper.selectByUserIdAndOrderNo(userId, orderId);
        if (order == null){
            return ServerResponse.createByError("订单详细->该用户无此订单");
        }

        List<OrderItem> orderItems = orderItemMapper.selectByUserIdAndOrderNo(userId, orderId);
        return ServerResponse.createBySuccess(assembleOrderDTO(order, orderItems));
    }

    @Override
    public ServerResponse<PageInfo> getlistByUserId(Integer userId, Integer pageNumber, Integer pageSize) {
    //开始分页
        PageHelper.startPage(pageNumber, pageSize);

        List<Order> orders = orderMapper.selectByUserId(userId);

        List<OrderDTO> orderDTOs = getOrderDTOsByList(false, orders);

        PageInfo pageInfo = new PageInfo(orderDTOs);

        return ServerResponse.createBySuccess(pageInfo);
    }

    private List<OrderDTO> getOrderDTOsByList(Boolean isManager, List<Order> orders) {
        List<OrderDTO> orderDTOs = new ArrayList<>();
        for (Order order: orders){
            List<OrderItem> orderItems;
            if (isManager == true){
                 orderItems = orderItemMapper.selectByUserIdAndOrderNo(order.getUserId(), order.getOrderNo());
            } else {
                orderItems = orderItemMapper.selectByOrderNo(order.getOrderNo());
            }

            OrderDTO orderDTO = assembleOrderDTO(order, orderItems);
            orderDTOs.add(orderDTO);
        }
        return orderDTOs;
    }

    private OrderDTO assembleOrderDTO(Order order, List<OrderItem> orderItems) {
        OrderDTO orderDTO = Order2OrderDTO.convert(order);
        orderDTO.setImageHost(getFTPServerPrefix());

        List<OrderItemDTO> orderItemDTOs = new ArrayList<>();
        for (OrderItem orderItem: orderItems){
            OrderItemDTO orderItemDTO = OrderItem2OrderItemDTO.convert(orderItem);
            orderItemDTOs.add(orderItemDTO);
        }
        orderDTO.setOrderItemDTOList(orderItemDTOs);

        Shipping shipping = shippingMapper.selectByPrimaryKey(order.getShippingId());
        orderDTO.setShippingDTO(Shipping2ShippingDTO.convert(shipping));

        return orderDTO;
    }

    private void clearCart(List<Cart> carts) {
        for (Cart cart: carts){
            cartMapper.deleteByPrimaryKey(cart.getId());
        }
    }

    private void reduceProductStock(List<OrderItem> orderItems) {
        for (OrderItem orderItem : orderItems){
            Product product = productMapper.selectByPrimaryKey(orderItem.getProductId());
            product.setStock(product.getStock() - orderItem.getQuantity());
            productMapper.updateByPrimaryKeySelective(product);
        }
    }

    private boolean saveOrderDetail(List<OrderItem> orderItems) {
        return orderItemMapper.batchInsert(orderItems) > 0;
    }

    private List<OrderItem> prepareOrderDetail(long orderId, List<OrderItem> orderItems) {
        for (OrderItem orderItem: orderItems){
            orderItem.setOrderNo(orderId);
        }
        return orderItems;
    }

    private Order saveOrderMain(Integer userId, Integer shippingId, BigDecimal totalAmount) {
        Order order = new Order();

        order.setOrderNo(generalOrderNo());
        order.setStatus(OrderStatus.NOPAY.getCode());
        //暂时设置为0，全场包邮->哭死
        order.setPostage(0);
        order.setPaymentType(PayPlatform.Alipay.getCode());
        order.setPayment(totalAmount);

        order.setUserId(userId);
        order.setShippingId(shippingId);

        if (orderMapper.insert(order) > 0){
            return order;
        }
        else {
            return null;
        }
    }

    private Long generalOrderNo() {
        long currentTime = System.currentTimeMillis();
        return currentTime + new Random().nextInt(100);
    }

    private BigDecimal getOrderTotalAmount(List<OrderItem> orderItems) {
        BigDecimal amount = new BigDecimal("0");
        for (OrderItem orderItem: orderItems){
            amount = BigDecimalUtil.add(amount.doubleValue(), orderItem.getTotalPrice().doubleValue());
        }
        return amount;
    }

    private ServerResponse<List<OrderItem>> getOrderItemByCart(Integer userId, List<Cart> carts){
        List<OrderItem> orderItems = new ArrayList<>();

        for (Cart cart : carts){
            Product product = productMapper.selectByPrimaryKey(cart.getProductId());
            if (product.getStatus() != ProductStatus.ONLINE.getCode()){
                return ServerResponse.createByError(String.format("商品{}已下架或已删除", product.getName()));
            }
            if (cart.getQuantity() > product.getStock()){
                return ServerResponse.createByError(String.format("商品{}库存不足", product.getName()));
            }
            OrderItem orderItem = new OrderItem();
            orderItem.setUserId(userId);
            orderItem.setCurrentUnitPrice(product.getPrice());
            orderItem.setProductId(product.getId());
            orderItem.setProductImage(product.getMainImage());
            orderItem.setProductName(product.getName());
            orderItem.setQuantity(cart.getQuantity());
            orderItem.setTotalPrice(BigDecimalUtil.mul(orderItem.getQuantity().doubleValue(), orderItem.getCurrentUnitPrice().doubleValue()));
            orderItems.add(orderItem);
        }

        return ServerResponse.createBySuccess(orderItems);
    }

    private ServerResponse _process(Order order, Map<String, String> map) {
        String tradeNo = getTradeNoFromMap(map);

        Date payTime = getPayTimeFromMap(map);

        String tradeStatus = getTradeStatusFromMap(map);

        if (AlipayTradeStatus.SUCCESS.getDescript().equals(tradeStatus)){
            if (updateOrderStatus(order, OrderStatus.PAIED.getCode(), payTime) == false){
                return ServerResponse.createByError("支付回调处理->修改订单状态错误");
            }
        }

        //记录每次回调内容
        if (savePayInfo(order, tradeStatus, tradeNo)){
            return ServerResponse.createBySuccess();
        }

        return ServerResponse.createByError("支付回调处理->添加支付信息错误");
    }

    private boolean savePayInfo(Order order, String tradeStatus, String tradeNo) {
        PayInfo payInfo = new PayInfo();
        payInfo.setUserId(order.getUserId());
        payInfo.setOrderNo(order.getOrderNo());
        payInfo.setPayPlatform(PayPlatform.Alipay.getCode());
        payInfo.setPlatformNumber(tradeNo);
        payInfo.setPlatformStatus(tradeStatus);
        return payInfoMapper.insert(payInfo) > 0;
    }

    private boolean updateOrderStatus(Order order, int status, Date payTime) {
        order.setPaymentType(PayPlatform.Alipay.getCode());
        order.setStatus(status);
        order.setPaymentTime(payTime);
        return orderMapper.updateByPrimaryKeySelective(order) != 0;
    }

    private boolean checkBackOrderDataNeedProcess(Order order) {
        return order.getStatus() > OrderStatus.NOPAY.getCode();
    }

    private ServerResponse checkBackOrderData(Order order, String payAmount, String sellId) {
        if (order == null){
            return ServerResponse.createByError("商户订单号与返回订单号不一致");
        }

        if (payAmount != null && new BigDecimal(payAmount).compareTo(order.getPayment()) != 0) {
            return ServerResponse.createByError("订单金额与返回金额不一致");
        }

        if (sellId != null && sellId.equals(PayConfig.SELLID) == false){
            return ServerResponse.createByError("商家ID与返回商家ID不一致");
        }
        return ServerResponse.createBySuccess("");
    }

    private String getSellIdFromMap(Map<String, String> map) {
        return map.get("seller_id");
    }

    private Date getPayTimeFromMap(Map<String, String> map) {
        try {
            return new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse(map.get("gmt_payment"));
        } catch (ParseException e) {
            log.error("转换支付时间出错", e);
            return null;
        }
    }

    private String getPayAmountFromMap(Map<String, String> map) {
        return map.get("total_amount");
    }

    private String getTradeStatusFromMap(Map<String, String> map) {
        return map.get("trade_status");
    }

    private String getTradeNoFromMap(Map<String, String> map) {
        return map.get("trade_no");
    }

    private Long getOrerNoFromMap(Map<String, String> map) {
        return Long.parseLong(map.get("out_trade_no"));
    }

    //后台服务接口
    @Override
    public ServerResponse<PageInfo> getlistByManager(Integer pageNumber, Integer pageSize) {
    //开始分页
        PageHelper.startPage(pageNumber, pageSize);

        List<Order> orders = orderMapper.selectAll();

        List<OrderDTO> orderDTOs = getOrderDTOsByList(true, orders);

        PageInfo pageInfo = new PageInfo(orderDTOs);

        return ServerResponse.createBySuccess(pageInfo);
    }

    @Override
    public ServerResponse<OrderDTO> detailByOrderId(Long orderId) {
    //取出订单
        Order order = orderMapper.selectByOrderNo(orderId);
        if (order == null){
            return ServerResponse.createByError("后台订单详细->无此订单");
        }

        List<OrderItem> orderItems = orderItemMapper.selectByOrderNo(orderId);
        return ServerResponse.createBySuccess(assembleOrderDTO(order, orderItems));
    }

    @Override
    public ServerResponse<PageInfo> search(Long orderId, Integer pageNumber, Integer pageSize) {
    //开始分页
        PageHelper.startPage(pageNumber, pageSize);

        Order order = orderMapper.selectByOrderNo(orderId);

        List<OrderDTO> orderDTOs = getOrderDTOsByList(false, Collections.singletonList(order));

        PageInfo pageInfo = new PageInfo(orderDTOs);

        return ServerResponse.createBySuccess(pageInfo);
    }

    @Override
    public ServerResponse<String> delivery(Long orderId) {
    //取出订单
        Order order = orderMapper.selectByOrderNo(orderId);
        if (order == null){
            return ServerResponse.createByError("后台订单详细->无此订单");
        }

        if (order.getStatus() == OrderStatus.PAIED.getCode()){
            order.setStatus(OrderStatus.SHIPPED.getCode());
            order.setSendTime(new Date());
            if (orderMapper.updateByPrimaryKeySelective(order) > 0){
                return ServerResponse.createBySuccess("发货成功");
            } else {
                return ServerResponse.createByError("发货失败");
            }
        } else {
            return ServerResponse.createByError(OrderStatus.getDescriptByCode(order.getStatus()));
        }
    }
}
