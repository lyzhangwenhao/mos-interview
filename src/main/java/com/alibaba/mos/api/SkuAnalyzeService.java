package com.alibaba.mos.api;

import com.alibaba.mos.data.SkuDO;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

/**
 * ClassName: SkuAnalyzeService
 * Description:
 *
 * @author 张文豪
 * @date 2021/1/24 23:48
 */
public interface SkuAnalyzeService {
    /**
     * 获取价格在最中间的skuid
     * @param list 所有商品
     * @return 返回id
     */
    String getPriceMiddleSkuId(List<SkuDO> list);

    /**
     * 获取各个渠道前五的skuid
     * @param list 所有的数据
     * @return 返回的数据 例如( miao:[1,2,3,4,5],tmall:[3,4,5,6,7],intime:[7,8,4,3,1]）
     */
    Map<String ,List<String>> getTop5SkuId(List<SkuDO> list);

    /**
     * 计算所有sku的总价值
     * @param list 所有数据
     * @return 返回总价值
     */
    BigDecimal calculateAllPrice(List<SkuDO> list);
}
