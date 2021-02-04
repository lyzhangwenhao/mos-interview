package com.alibaba.mos.service;

import com.alibaba.mos.api.SkuAnalyzeService;
import com.alibaba.mos.data.ChannelInventoryDO;
import com.alibaba.mos.data.SkuDO;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.*;

/**
 * ClassName: SkuAnalyzeServiceImpl
 * Description:
 *
 * @author 张文豪
 * @date 2021/1/24 23:48
 */
@Service
public class SkuAnalyzeServiceImpl implements SkuAnalyzeService {

    /**
     * 获取价格在最中间的skuid
     *
     * @param list 所有商品
     * @return 返回id
     */
    @Override
    public String getPriceMiddleSkuId(List<SkuDO> list) {
        if (list == null || list.size() == 0) {
            return null;
        }
        list.sort((o1, o2) -> {
            float p1 = o1.getPrice().floatValue();
            float p2 = o2.getPrice().floatValue();
            if (p1 < p2) {
                return -1;
            } else if (p1 == p2) {
                return 0;
            } else {
                return 1;
            }
        });
        return list.get(list.size() / 2).getId();
    }

    /**
     * 获取各个渠道前五的skuid
     *
     * @param list 所有的数据
     * @return 返回的数据 例如( miao:[1,2,3,4,5],tmall:[3,4,5,6,7],intime:[7,8,4,3,1]）
     */
    @Override
    public Map<String, List<String>> getTop5SkuId(List<SkuDO> list) {
        if (list == null || list.size() == 0) {
            return null;
        }
        Map<String, Map<String, BigDecimal>> allDataMap = new HashMap<>();
        List<ChannelInventoryDO> inventoryList;
        Map<String, BigDecimal> newMap;
        for (SkuDO skuDO : list) {
            inventoryList = skuDO.getInventoryList();
            if (inventoryList != null) {
                for (ChannelInventoryDO channelInventoryDO : inventoryList) {
                    if (allDataMap.get(channelInventoryDO.getChannelCode()) != null) {
                        allDataMap.get(channelInventoryDO.getChannelCode()).put(skuDO.getId(), channelInventoryDO.getInventory());
                    } else {
                        newMap = new HashMap<>();
                        newMap.put(skuDO.getId(), channelInventoryDO.getInventory());
                        allDataMap.put(channelInventoryDO.getChannelCode(), newMap);
                    }
                }
            }
        }
        //返回结果集
        Map<String, List<String>> resultMap = new HashMap<>();
        allDataMap.forEach((channelCode,map)->{
            List<Map.Entry<String, BigDecimal>> sortList = new ArrayList<>(map.entrySet());
            sortList.sort((o1, o2) -> o2.getValue().compareTo(o1.getValue()));
            List<String> skuIDList = new ArrayList<>();
            sortList.stream().limit(5).forEach(limitList->{
                skuIDList.add(limitList.getKey());
            });
            resultMap.put(channelCode, skuIDList);
        });
        return resultMap;
    }

    /**
     * 计算所有sku的总价值
     *
     * @param list 所有数据
     * @return 返回总价值
     */
    @Override
    public BigDecimal calculateAllPrice(List<SkuDO> list) {
        if (list == null || list.size() == 0){
            return new BigDecimal(0);
        }
        BigDecimal resultPrice = new BigDecimal(0);
        List<ChannelInventoryDO> inventoryList;
        for (SkuDO skuDO : list) {
            inventoryList = skuDO.getInventoryList();
            if (inventoryList != null){
                for (ChannelInventoryDO channelInventoryDO : inventoryList) {
                    resultPrice = resultPrice.add(channelInventoryDO.getInventory().multiply(skuDO.getPrice()));
                }
            }
        }
        return resultPrice;
    }
}
