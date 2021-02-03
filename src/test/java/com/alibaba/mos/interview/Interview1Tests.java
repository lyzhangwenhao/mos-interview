package com.alibaba.mos.interview;

import com.alibaba.fastjson.JSON;
import com.alibaba.mos.api.ItemService;
import com.alibaba.mos.api.SkuAnalyzeService;
import com.alibaba.mos.api.SkuSimpleReadService;
import com.alibaba.mos.data.ItemDO;
import com.alibaba.mos.data.SkuDO;
import org.apache.commons.collections4.CollectionUtils;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.util.Assert;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

@SpringBootTest
class Interview1Tests {

    @Autowired
    SkuSimpleReadService skuSimpleReadService;

    @Autowired
    ItemService itemService;

    @Autowired
    SkuAnalyzeService skuAnalyzeService;


    /**
     * 试题1:
     * 在com.alibaba.mos.service.SkuSimpleReadServiceImpl中实现com.alibaba.mos.api.SkuSimpleReadService#loadSkus()
     * 从/resources/data/skus.xls读取数据并返回对应数据实体
     */
    @Test
    void readSkuDataFromExcelTest() {
        List<SkuDO> list = skuSimpleReadService.loadSkus();
        for (SkuDO skuDO : list) {
            System.out.println(skuDO.toString());
        }
        Assert.isTrue(CollectionUtils.isNotEmpty(list), "未能读取到sku数据列表");
    }

    /**
     * 试题2:
     * 计算以下统计值:
     * 1、价格在最中间 的skuId
     * 2、每个渠道库存量为前五的skuId列表 例如( miao:[1,2,3,4,5],tmall:[3,4,5,6,7],intime:[7,8,4,3,1]
     * 3、所有sku的总价值
     */
    @Test
    void statisticsDataTest() {
        List<SkuDO> list = skuSimpleReadService.loadSkus();
        Assert.isTrue(CollectionUtils.isNotEmpty(list), "未能读取到sku数据列表");
        //TODO 自定义service和方法并实现以上注释功能
        //1.价格在最中间的skuId
        String skuId = skuAnalyzeService.getPriceMiddleSkuId(list);
        System.out.println("1.价格在最中间的skuId:"+skuId);
        //2.每个渠道库存量为前五的skuId列表
        System.out.println("2.每个渠道库存量为前五的skuId列表如下:");
        Map<String, List<String>> top5SkuId = skuAnalyzeService.getTop5SkuId(list);
        if (top5SkuId != null){
            top5SkuId.forEach((k,v)->{
                System.out.println(k+":"+v);
            });
        }
        //3.所有sku的总价值
        System.out.println("3.所有sku的总价值："+skuAnalyzeService.calculateAllPrice(list).toString());

    }

    /**
     * 试题3:
     * 在com.alibaba.mos.service.ItemServiceImpl中实现com.alibaba.mos.service.ItemService#aggregationSkus(java.util.List)
     * 读取sku列表并聚合为商品, 聚合规则为：
     * 对于sku type为原始商品(ORIGIN)的, 按货号(artNo)聚合成ITEM
     * 对于sku type为数字化商品(DIGITAL)的, 按spuId聚合成ITEM
     * 聚合结果需要包含: item的最大价格、最小价格、sku列表及全渠道总库存
     */
    @Test
    void aggregationSkusTest() {
        List<SkuDO> list = skuSimpleReadService.loadSkus();
        Assert.isTrue(CollectionUtils.isNotEmpty(list), "未能读取到sku数据列表");
        List<ItemDO> items = itemService.aggregationSkus(list);
        Assert.isTrue(CollectionUtils.isNotEmpty(items), "未能聚合商品列表");
//        System.out.println(JSON.toJSON(items));
        System.out.println("试题3.运行结果如下：");
        if (items != null){
            for (ItemDO item : items) {
                System.out.println(item.toString());
            }
        }
    }
}
