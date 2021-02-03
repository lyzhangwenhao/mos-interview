/**
 * Alipay.com Inc.
 * Copyright (c) 2004-2019 All Rights Reserved.
 */
package com.alibaba.mos.service;

import com.alibaba.mos.api.ItemService;
import com.alibaba.mos.data.ChannelInventoryDO;
import com.alibaba.mos.data.ItemDO;
import com.alibaba.mos.data.SkuDO;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author superchao
 * @version $Id: ItemServiceImpl.java, v 0.1 2019年10月28日 11:11 AM superchao Exp $
 */
@Service
public class ItemServiceImpl implements ItemService {

    @Override
    public List<ItemDO> aggregationSkus(List<SkuDO> skuList) {
        //TODO 在此实现聚合sku的代码
        if (skuList == null || skuList.size() == 0) {
            return null;
        }
        List<ItemDO> resultList = new ArrayList<>();

        skuList.stream().collect(Collectors.groupingBy(SkuDO::getSkuType)).forEach((type, list) -> {
            list.stream().collect(Collectors.groupingBy("ORIGIN".equals(type)?SkuDO::getArtNo : SkuDO::getSpuId)).forEach((artnoOrSpuId, skuDO) -> {
                ItemDO itemDO = new ItemDO();
                if ("ORIGIN".equals(type)){
                    itemDO.setArtNo(artnoOrSpuId);
                } else if ("DIGITAL".equals(type)){
                    itemDO.setSpuId(artnoOrSpuId);
                }
                BigDecimal maxPrice = new BigDecimal(0);
                BigDecimal minPrice = new BigDecimal(0);
                BigDecimal inventory = new BigDecimal(0);
                List<String> skuIdList = new ArrayList<>();
                for (SkuDO aDo : skuDO) {
                    if (minPrice.compareTo(new BigDecimal(0)) == 0){
                        minPrice = aDo.getPrice();
                    }
                    skuIdList.add(aDo.getId());
                    if (maxPrice.compareTo(aDo.getPrice()) < 0) {
                        maxPrice = aDo.getPrice();
                    }
                    if (minPrice.compareTo(aDo.getPrice()) > 0) {
                        minPrice = aDo.getPrice();
                    }
                    List<ChannelInventoryDO> inventoryList = aDo.getInventoryList();
                    if (inventoryList != null){
                        for (ChannelInventoryDO channelInventoryDO : inventoryList) {
                            inventory = inventory.add(channelInventoryDO.getInventory());
                        }
                    }
                }
                itemDO.setInventory(inventory);
                itemDO.setMaxPrice(maxPrice);
                itemDO.setMinPrice(minPrice);
                itemDO.setSkuIds(skuIdList);
                resultList.add(itemDO);
            });
        });
        return resultList;
    }
}