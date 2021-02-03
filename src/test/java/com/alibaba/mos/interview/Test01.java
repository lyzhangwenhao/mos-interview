package com.alibaba.mos.interview;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.mos.data.ChannelInventoryDO;
import com.alibaba.mos.service.SkuSimpleReadServiceImpl;
import jxl.Workbook;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.math.BigDecimal;
import java.util.*;

/**
 * ClassName: Test
 * Description:
 *
 * @author 张文豪
 * @date 2021/2/1 15:06
 */
public class Test01 {
    private List<Position_user> list;
    @Test
    public void testo1(){
        list = loadSkus();
        Map<String, Object> map = getRelation("40");
        Map<String,Object>  resultMap = new HashMap<>();
        resultMap.put("总经理", map);
        System.out.println(JSON.toJSON(resultMap));

    }
    public Map<String,Object> getRelation(String id){
        Map<String,Object> map = new HashMap<>();
        for (Position_user position_user : list) {
            if (id.equals(position_user.getParent())){
                map.put(position_user.getName(), getRelation(position_user.getId()));
            }
        }
        return map;
    }



    public List<Position_user> loadSkus() {
        //TODO 在此实现从resource文件excel中加载sku的代码
        Logger logger = LoggerFactory.getLogger(SkuSimpleReadServiceImpl.class);
        Workbook workbook;
        List<Position_user> resultList = new ArrayList<>();
        try {
            InputStream in = new FileInputStream(new File("src/main/resources/data/data.xls"));
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
                Position_user skuDO = new Position_user();
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
                            case "parent":
                                skuDO.setParent(strData);
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
