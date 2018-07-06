package com.bossien.adminservice.service;

import com.alibaba.fastjson.JSONObject;
import com.bossien.adminservice.mapper.StatisticsMapper;
import com.bossien.adminservice.mapper.UsersMapper;
import com.bossien.adminservice.redis.RedisDao;
import com.bossien.common.base.ResponseData;
import com.bossien.common.model.CompanyRanking;
import com.bossien.common.model.Statistics;
import com.bossien.common.model.UserRanking;
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.xssf.usermodel.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.DecimalFormat;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Service
public class StatisticsService {
    @Autowired
    private UsersMapper usersMapper;
    @Autowired
    private RedisDao redisDao;
    @Autowired
    private StatisticsMapper statisticsMapper;
    @Value("${file.path}")
    private String filePath;

    public String findUserSize(){
        String result = "";
        try {
            redisDao.setDatabase(0);
            long online = redisDao.getKeySize();
            long total = usersMapper.findUserSize();
            Map<String, Long> params = new HashMap<>();
            params.put("total", total);
            params.put("online", online);
            result = JSONObject.toJSONString(new ResponseData(200, "请求成功！", params));
        }catch (Exception e){
            e.printStackTrace();
            result = JSONObject.toJSONString(new ResponseData(500, "请求失败！", false));
        }

        return result;
    }

    public String selectRoleSize(){
        String result = "";
        try {
            List<Statistics> statisticsList = statisticsMapper.selectRoleSize();
            result = JSONObject.toJSONString(new ResponseData(200, "请求成功！", statisticsList));
        }catch (Exception e){
            e.printStackTrace();
            result = JSONObject.toJSONString(new ResponseData(500, "请求失败！", false));
        }

        return result;
    }


    public String getPersonOrderInCompany(List<UserRanking> userRankingList){
        String result = null;
        String fileName = UUID.randomUUID().toString();
        //创建工作簿
        XSSFWorkbook workBook = new XSSFWorkbook();
        //创建合并单元格对象
        //CellRangeAddress rangeAddress = new CellRangeAddress(2, 2, 2, 4);
        //创建样式
        XSSFCellStyle style = workBook.createCellStyle();
        //style.setAlignment(HorizontalAlignment.CENTER);
        //style.setVerticalAlignment(VerticalAlignment.CENTER);
        //创建字体
        XSSFFont font = workBook.createFont();
        font.setFontHeightInPoints((short) 14);
        //font.setFontHeight((short)320); 效果和上面一样。用这个方法设置大小，值要设置为字体大小*20倍，具体看API文档
        //font.setColor(HSSFColor.GREEN.index);
        font.setBold(true);
        style.setFont(font);
        //设置背景
        //style.setFillPattern(FillPatternType.SOLID_FOREGROUND);
        //style.setFillForegroundColor(HSSFColor.RED.index);

        //创建工作表
        XSSFSheet sheet = workBook.createSheet("学员排名统计");
        //添加合并区域
        //sheet.addMergedRegion(rangeAddress);
        //创建行
        XSSFRow sheetRow = sheet.createRow(0);

        XSSFCell cell0 = sheetRow.createCell(0, CellType.STRING);
        cell0.setCellValue("区域内排名");
        cell0.setCellStyle(style);
        XSSFCell cell1 = sheetRow.createCell(1, CellType.STRING);
        cell1.setCellValue("全国排名");
        cell1.setCellStyle(style);
        XSSFCell cell2 = sheetRow.createCell(2, CellType.STRING);
        cell2.setCellValue("姓名");
        cell2.setCellStyle(style);
        XSSFCell cell3 = sheetRow.createCell(3, CellType.STRING);
        cell3.setCellValue("手机号码");
        cell3.setCellStyle(style);
        XSSFCell cell4 = sheetRow.createCell(4, CellType.STRING);
        cell4.setCellValue("竞赛总分");
        cell4.setCellStyle(style);
        XSSFCell cell5 = sheetRow.createCell(5, CellType.STRING);
        cell5.setCellValue("耗时（毫秒）");
        cell5.setCellStyle(style);
        XSSFCell cell6 = sheetRow.createCell(6, CellType.STRING);
        cell6.setCellValue("所属单位");
        cell6.setCellStyle(style);

//        JSONArray jsonArray = JSONObject.parseArray(dataJson);
        for(int num=0; num<userRankingList.size(); num++){
            XSSFRow row = sheet.createRow(num+1);
//            JSONObject jsonObject = JSONObject.parseObject(jsonArray.get(num).toString());
            UserRanking userRanking = userRankingList.get(num);

            //String companyRank = jsonObject.getString("companyRank");
            XSSFCell xssfCell0 = row.createCell(0, CellType.STRING);
            xssfCell0.setCellValue(num+1);

            String countryRank = userRanking.getRanking().toString();
            XSSFCell xssfCell1 = row.createCell(1, CellType.STRING);
            if(!StringUtils.isEmpty(countryRank)){
                xssfCell1.setCellValue(Integer.valueOf(countryRank)+1);
            }else{
                xssfCell1.setCellValue(countryRank);
            }

            String userName = userRanking.getUserName();
            XSSFCell xssfCell2 = row.createCell(2, CellType.STRING);
            xssfCell2.setCellValue(userName);

            String phone = userRanking.getPhone();
            XSSFCell xssfCell3 = row.createCell(3, CellType.STRING);
            xssfCell3.setCellValue(phone);

            Double score = userRanking.getScore();
            DecimalFormat df = new DecimalFormat("#.00");
            df.format(score);
            XSSFCell xssfCell4 = row.createCell(4, CellType.STRING);
            xssfCell4.setCellValue(score);

            String duration = userRanking.getDuration().toString();
            XSSFCell xssfCell5 = row.createCell(5, CellType.STRING);

            if(!StringUtils.isEmpty(duration)){
                Long time = Long.valueOf(duration);
                xssfCell5.setCellValue(time);
            }else{
                xssfCell5.setCellValue(0);
            }

            String companyName = userRanking.getCompanyName();
            XSSFCell xssfCell6 = row.createCell(6, CellType.STRING);
            xssfCell6.setCellValue(companyName);
        }


        FileOutputStream outputStream = null;
        try {
            outputStream = new FileOutputStream(new File(filePath +File.separator+ fileName + ".xlsx"));
            workBook.write(outputStream);

            workBook.close();//记得关闭工作簿
            result = JSONObject.toJSONString(new ResponseData(200, "请求成功！", fileName));
        } catch (IOException e) {
            result = JSONObject.toJSONString(new ResponseData(500, "请求失败！", false));
            e.printStackTrace();
        }
        return result;
    }

    public String queryCompanyInParent(List<CompanyRanking> companyRankingList){
        String result = null;
        String fileName = UUID.randomUUID().toString();
        //创建工作簿
        XSSFWorkbook workBook = new XSSFWorkbook();
        //创建合并单元格对象
        //CellRangeAddress rangeAddress = new CellRangeAddress(2, 2, 2, 4);
        //创建样式
        XSSFCellStyle style = workBook.createCellStyle();
        //style.setAlignment(HorizontalAlignment.CENTER);
        //style.setVerticalAlignment(VerticalAlignment.CENTER);
        //创建字体
        XSSFFont font = workBook.createFont();
        font.setFontHeightInPoints((short) 14);
        //font.setFontHeight((short)320); 效果和上面一样。用这个方法设置大小，值要设置为字体大小*20倍，具体看API文档
        //font.setColor(HSSFColor.GREEN.index);
        font.setBold(true);
        style.setFont(font);
        //设置背景
        //style.setFillPattern(FillPatternType.SOLID_FOREGROUND);
        //style.setFillForegroundColor(HSSFColor.RED.index);

        //创建工作表
        XSSFSheet sheet = workBook.createSheet("单位排名统计");
        //添加合并区域
        //sheet.addMergedRegion(rangeAddress);
        //创建行
        XSSFRow sheetRow = sheet.createRow(0);

        XSSFCell cell0 = sheetRow.createCell(0, CellType.STRING);
        cell0.setCellValue("区域排名");
        cell0.setCellStyle(style);
        XSSFCell cell1 = sheetRow.createCell(1, CellType.STRING);
        cell1.setCellValue("全国排名");
        cell1.setCellStyle(style);
        XSSFCell cell2 = sheetRow.createCell(2, CellType.STRING);
        cell2.setCellValue("单位名称");
        cell2.setCellStyle(style);
        XSSFCell cell3 = sheetRow.createCell(3, CellType.STRING);
        cell3.setCellValue("总人数");
        cell3.setCellStyle(style);
        XSSFCell cell4 = sheetRow.createCell(4, CellType.STRING);
        cell4.setCellValue("参赛人数");
        cell4.setCellStyle(style);
        XSSFCell cell5 = sheetRow.createCell(5, CellType.STRING);
        cell5.setCellValue("总得分");
        cell5.setCellStyle(style);

//        JSONArray jsonArray = JSONObject.parseArray(dataJson);
        for(int num=0; num<companyRankingList.size(); num++){
            CompanyRanking companyRanking = companyRankingList.get(num);
            XSSFRow row = sheet.createRow(num+1);
//            JSONObject jsonObject = JSONObject.parseObject(jsonArray.get(num).toString());

//            String parentRank = jsonObject.getString("provinceRanking");
            XSSFCell xssfCell0 = row.createCell(0, CellType.STRING);
            /*if(!StringUtils.isEmpty(parentRank)){
                xssfCell0.setCellValue(Integer.valueOf(parentRank)+1);
            }else{
                xssfCell0.setCellValue(parentRank);
            }*/
            xssfCell0.setCellValue(num + 1);

            String countryRank = companyRanking.getRanking().toString();
            XSSFCell xssfCell1 = row.createCell(1, CellType.STRING);
            if(!StringUtils.isEmpty(countryRank)){
                xssfCell1.setCellValue(Integer.valueOf(countryRank)+1);
            }else{
                xssfCell1.setCellValue(countryRank);
            }

            String companyName = companyRanking.getCompanyName();
            XSSFCell xssfCell2 = row.createCell(2, CellType.STRING);
            xssfCell2.setCellValue(companyName);


            String count = companyRanking.getPersonCount().toString();
            XSSFCell xssfCell3 = row.createCell(3, CellType.STRING);

            if(!StringUtils.isEmpty(count)){
                int number = Double.valueOf(count).intValue();
                xssfCell3.setCellValue(number);
            }else{
                xssfCell3.setCellValue(0);
            }


            Long personCount = companyRanking.getExamPersonCount();
            if(personCount == null) {
                personCount = 0L;
            }
            XSSFCell xssfCell4 = row.createCell(4, CellType.STRING);
            xssfCell4.setCellValue(personCount);

            Double score = companyRanking.getScore();
            DecimalFormat df = new DecimalFormat("#.00");
            df.format(score);
            XSSFCell xssfCell5 = row.createCell(5, CellType.STRING);
            xssfCell5.setCellValue(score);
        }


        FileOutputStream outputStream = null;
        try {
            outputStream = new FileOutputStream(new File(filePath +File.separator+ fileName + ".xlsx"));
            workBook.write(outputStream);

            workBook.close();//记得关闭工作簿
            result = JSONObject.toJSONString(new ResponseData(200, "请求成功！", fileName));
        } catch (IOException e) {
            result = JSONObject.toJSONString(new ResponseData(500, "请求失败！", false));
            e.printStackTrace();
        }
        return result;
    }
}
