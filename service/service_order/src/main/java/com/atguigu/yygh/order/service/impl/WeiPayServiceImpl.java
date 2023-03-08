package com.atguigu.yygh.order.service.impl;


import com.alibaba.fastjson.JSONObject;
import com.atguigu.yygh.common.exception.YyghException;
import com.atguigu.yygh.enums.OrderStatusEnum;
import com.atguigu.yygh.enums.PaymentStatusEnum;
import com.atguigu.yygh.enums.PaymentTypeEnum;
import com.atguigu.yygh.enums.RefundStatusEnum;
import com.atguigu.yygh.model.order.OrderInfo;
import com.atguigu.yygh.model.order.PaymentInfo;
import com.atguigu.yygh.model.order.RefundInfo;
import com.atguigu.yygh.order.properties.WeiPayProperties;
import com.atguigu.yygh.order.service.OrderInfoService;
import com.atguigu.yygh.order.service.PaymentService;
import com.atguigu.yygh.order.service.RefundInfoService;
import com.atguigu.yygh.order.service.WeiPayService;
import com.atguigu.yygh.order.util.HttpClient;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.github.wxpay.sdk.WXPayUtil;
import org.joda.time.DateTime;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * @author: fs
 * @date: 2023/3/7 15:02
 * @Description: everything is ok
 */


@Service
public class WeiPayServiceImpl implements WeiPayService {

    @Autowired
    private OrderInfoService orderInfoService;
    @Autowired
    private PaymentService paymentService;

    @Autowired
    private WeiPayProperties weiPayProperties;

    @Autowired
    private RefundInfoService refundInfoService;

    @Override
    public String createNative(Long orderId) {
        //1.根据订单id去数据库中获取订单信息
        OrderInfo orderInfo = orderInfoService.getById(orderId);

        //2.保存支付记录信息
        paymentService.savePaymentInfo(orderInfo, PaymentTypeEnum.WEIXIN.getStatus());
        //3.请求微信服务器获取微信支付的url地址
        HttpClient httpClient = new HttpClient("https://api.mch.weixin.qq.com/pay/unifiedorder");

        HashMap<String,String> paramMap = new HashMap<>();
        paramMap.put("appid",weiPayProperties.getAppid());
        paramMap.put("mch_id",weiPayProperties.getPartner());
        paramMap.put("nonce_str",WXPayUtil.generateNonceStr());

        Date reserveDate = orderInfo.getReserveDate();
        String reserveDateString = new DateTime(reserveDate).toString("yyyy/MM/dd");
        String body = reserveDateString + "就诊"+ orderInfo.getDepname();

        paramMap.put("body",body);
        paramMap.put("out_trade_no",orderInfo.getOutTradeNo());
        paramMap.put("total_fee","1");
        paramMap.put("spbill_create_ip", "127.0.0.1");
        paramMap.put("notify_url", "http://guli.shop/api/order/weixinPay/weixinNotify");
        paramMap.put("trade_type", "NATIVE");

        try {
            String xml = WXPayUtil.generateSignedXml(paramMap, weiPayProperties.getPartnerkey());
            httpClient.setXmlParam(xml);

            httpClient.setHttps(true);//支持https
            httpClient.post();//发送请求

            String xmlResult = httpClient.getContent();
            Map<String, String> stringStringMap = WXPayUtil.xmlToMap(xmlResult);
            System.out.println(stringStringMap);

            return stringStringMap.get("code_url");

        } catch (Exception e) {
            return "";
        }

        //4.将url返回给前端
    }

    @Override
    public Map<String, String> queryPayStatus(Long orderId) {
        HttpClient httpClient = new HttpClient("https://api.mch.weixin.qq.com/pay/orderquery");

        OrderInfo orderInfo = orderInfoService.getById(orderId);
        //1、封装参数
        Map<String,String> paramMap = new HashMap<>();
        paramMap.put("appid", weiPayProperties.getAppid());
        paramMap.put("mch_id", weiPayProperties.getPartner());
        paramMap.put("out_trade_no", orderInfo.getOutTradeNo());
        paramMap.put("nonce_str", WXPayUtil.generateNonceStr());

        try {
            String xml = WXPayUtil.generateSignedXml(paramMap, weiPayProperties.getPartnerkey());
            httpClient.setXmlParam(xml);
            httpClient.setHttps(true);
            httpClient.post();

            String content = httpClient.getContent();
            Map<String, String> stringStringMap = WXPayUtil.xmlToMap(content);

            return stringStringMap;

        } catch (Exception e) {
            return null;
        }
    }

    @Transactional
    @Override
    public void paySuccess(Long orderId,Map<String,String> map) {
        //更新订单表的订单状态
        OrderInfo orderInfo = new OrderInfo();
        orderInfo.setId(orderId);
        orderInfo.setOrderStatus(OrderStatusEnum.PAID.getStatus());
        orderInfoService.updateById(orderInfo);
        //更新支付记录表的支付状态
        UpdateWrapper<PaymentInfo> updateWrapper = new UpdateWrapper<>();
        updateWrapper.eq("order_id",orderId);
        updateWrapper.set("trade_no",map.get("transaction_id"));
        updateWrapper.set("payment_status", PaymentStatusEnum.PAID.getStatus());
        updateWrapper.set("callback_time",new Date());

        updateWrapper.set("callback_content", JSONObject.toJSONString(map));
        paymentService.update(updateWrapper);
    }

    @Override
    public Boolean refund(Long orderId) {
        QueryWrapper<PaymentInfo> paymentInfoQueryWrapper = new QueryWrapper<>();
        paymentInfoQueryWrapper.eq("order_id",orderId);
        PaymentInfo paymentInfo = paymentService.getOne(paymentInfoQueryWrapper);

        RefundInfo refundInfo = refundInfoService.saveRefundInfo(paymentInfo);
        //已经退过款了
        if(refundInfo.getRefundStatus().intValue() == RefundStatusEnum.REFUND.getStatus()){
            return true;
        }
        //执行微信退款
        HttpClient httpClient = new HttpClient("https://api.mch.weixin.qq.com/secapi/pay/refund");
        Map<String,String> paramMap = new HashMap<>(8);
        paramMap.put("appid",weiPayProperties.getAppid());       //公众账号ID
        paramMap.put("mch_id", weiPayProperties.getPartner());   //商户编号
        paramMap.put("nonce_str",WXPayUtil.generateNonceStr());
        paramMap.put("transaction_id",paymentInfo.getTradeNo()); //微信订单号
        paramMap.put("out_trade_no",paymentInfo.getOutTradeNo()); //商户订单编号
        paramMap.put("out_refund_no","tk"+paymentInfo.getOutTradeNo()); //商户退款单号
        //       paramMap.put("total_fee",paymentInfoQuery.getTotalAmount().multiply(new BigDecimal("100")).longValue()+"");
        //       paramMap.put("refund_fee",paymentInfoQuery.getTotalAmount().multiply(new BigDecimal("100")).longValue()+"");
        paramMap.put("total_fee","1");
        paramMap.put("refund_fee","1");

        try {
            String paramXml = WXPayUtil.generateSignedXml(paramMap,weiPayProperties.getPartnerkey());
            httpClient.setXmlParam(paramXml);
            httpClient.setHttps(true);
            httpClient.setCert(true);//设置证书支持
            httpClient.setCertPassword(weiPayProperties.getPartner());//设置证书密码
            httpClient.post();

            String content = httpClient.getContent();
            Map<String, String> resultMap = WXPayUtil.xmlToMap(content);
            if("SUCCESS".equals(resultMap.get("result_code"))){

                refundInfo.setTradeNo(resultMap.get("refund_id"));
                refundInfo.setRefundStatus(RefundStatusEnum.REFUND.getStatus());
                refundInfo.setCallbackTime(new Date());
                refundInfo.setCallbackContent(JSONObject.toJSONString(resultMap));
                refundInfoService.updateById(refundInfo);
                return true;
            }

            return false;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;

    }
}
