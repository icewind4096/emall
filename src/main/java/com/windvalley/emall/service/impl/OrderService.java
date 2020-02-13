package com.windvalley.emall.service.impl;

import com.alipay.api.AlipayApiException;
import com.alipay.api.AlipayClient;
import com.alipay.api.DefaultAlipayClient;
import com.alipay.api.domain.AlipayTradePrecreateModel;
import com.alipay.api.domain.GoodsDetail;
import com.alipay.api.request.AlipayTradePrecreateRequest;
import com.alipay.api.response.AlipayTradePrecreateResponse;
import com.windvalley.emall.common.ServerResponse;
import com.windvalley.emall.dao.OrderItemMapper;
import com.windvalley.emall.dao.OrderMapper;
import com.windvalley.emall.dao.PayInfoMapper;
import com.windvalley.emall.enums.AlipayTradeStatus;
import com.windvalley.emall.enums.OrderStatus;
import com.windvalley.emall.enums.PayPlatform;
import com.windvalley.emall.pay.alipay.PayConfig;
import com.windvalley.emall.pojo.Order;
import com.windvalley.emall.pojo.OrderItem;
import com.windvalley.emall.pojo.PayInfo;
import com.windvalley.emall.service.IFileService;
import com.windvalley.emall.service.IOrderService;
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
    OrderMapper orderMapper;

    @Autowired
    OrderItemMapper orderItemMapper;

    @Autowired
    PayInfoMapper payInfoMapper;

    @Autowired
    IFileService fileService;

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
}
