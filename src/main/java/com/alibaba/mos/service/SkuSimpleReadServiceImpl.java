/**
 * Alipay.com Inc.
 * Copyright (c) 2004-2019 All Rights Reserved.
 */
package com.alibaba.mos.service;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.mos.api.SkuSimpleReadService;
import com.alibaba.mos.data.ChannelInventoryDO;
import com.alibaba.mos.data.SkuDO;
import jxl.Workbook;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.math.BigDecimal;
import java.util.*;

/**
 *
 * @author superchao
 * @version $Id: SkuSimpleReadServiceImpl.java, v 0.1 2019年10月28日 11:54 AM superchao Exp $
 */
@Service
public class SkuSimpleReadServiceImpl implements SkuSimpleReadService {

    @Override
    public List<SkuDO> loadSkus() {
        //TODO 在此实现从resource文件excel中加载sku的代码
        Logger logger = LoggerFactory.getLogger(SkuSimpleReadServiceImpl.class);
        Workbook workbook;
        List<SkuDO> resultList = new ArrayList<>();
        try {
            InputStream in = new FileInputStream(new File("src/main/resources/data/skus.xls"));
            //读取工作表
            HSSFWorkbook sheets = new HSSFWorkbook(in);
            //获取数据所在的sheet，数据来源为第1张
            Sheet sheet = sheets.getSheetAt(0);
            //开始读取该工作表
            Iterator<Row> rowIterator = sheet.rowIterator();
            //第一行为表头
            Row firstRow = rowIterator.next();
            Iterator<Cell> firstCellIterator = firstRow.cellIterator();
            Map<Integer,String> titleMap = new HashMap<>();
            firstCellIterator.forEachRemaining(cell -> {
                titleMap.put(cell.getColumnIndex(), cell.getStringCellValue());
            });
            while (rowIterator.hasNext()){
                Row dataRow = rowIterator.next();
                SkuDO skuDO = new SkuDO();
                resultList.add(skuDO);
                Iterator<Cell> cellIterator = dataRow.cellIterator();
                cellIterator.forEachRemaining(cell -> {
                    //每个单元格
                    cell.setCellType(CellType.STRING);
                    String strData = cell.getStringCellValue();
                    int columnIndex = cell.getColumnIndex();
                    String title = titleMap.get(columnIndex);
                    if (strData != null) {
                        switch (title) {
                            case "id":
                                skuDO.setId(strData);
                                break;
                            case "name":
                                skuDO.setName(strData);
                                break;
                            case "artNo":
                                skuDO.setArtNo(strData);
                                break;
                            case "spuId":
                                skuDO.setSpuId(strData);
                                break;
                            case "skuType":
                                skuDO.setSkuType(strData);
                                break;
                            case "price":
                                skuDO.setPrice(BigDecimal.valueOf(Double.parseDouble(strData)));
                                break;
                            case "inventorys":
                                JSONArray jsonArray = JSON.parseArray(strData);
                                List<ChannelInventoryDO> channelInventoryDOS = JSONArray.parseArray(jsonArray.toJSONString(), ChannelInventoryDO.class);
                                skuDO.setInventoryList(channelInventoryDOS);
                                break;
                            default:
                                break;
                        }
                    }
                });
            }
        } catch (Exception e){
            logger.error("解析错误",e);
        }
        return resultList;
    }
}