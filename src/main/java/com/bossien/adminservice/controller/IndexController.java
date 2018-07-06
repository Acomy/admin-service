package com.bossien.adminservice.controller;

import com.alibaba.fastjson.JSONObject;
import com.bossien.adminservice.service.UserService;
import com.bossien.common.base.ResponseData;
import org.apache.ibatis.annotations.Param;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.util.ResourceUtils;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.nio.channels.FileChannel;

@RestController
public class IndexController {

    @Value("${server.port}")
    private String port;


    @Value("${file.path}")
    private String filePath;

    @Autowired
    private UserService userService;

    @RequestMapping("/sendMessage")
    public void sendMessage() {

        userService.sendMessage();
    }

//    @RequestMapping("/getRedis")
//    public String getRedis() {
//
//        String result = userService.getRedis();
//        return result;
//    }
//
//    @RequestMapping("/userList")
//    public String userList(@RequestParam(value="pageNum") int pageNum, @RequestParam(value="pageSize")  int pageSize) {
//
//        String result = userService.userList(pageNum, pageSize);
//        return result;
//    }
//

    @RequestMapping("/downloadFile")
    public String findDownload(HttpServletResponse response, @Param(value = "fileName") String fileName) throws IOException {
        File  file = new File(this.filePath + File.separator + fileName+".xlsx");
        FileChannel fc= null;
        if(file.exists()){ //判断文件父目录是否存在
            String filename = "统计排名.xlsx";
            if("student".equals(fileName)){
                filename="批量上传人员信息.xlsx";
            }else if("performance".equals(fileName)){
                filename="学员导分功能.xlsx";
            }else if("company".equals(fileName)){
                filename="批量上传单位信息.xlsx";
            } else if("question".equals(fileName)){
                filename="导入项目试题模板.xlsx";
            }
            FileInputStream fis = new FileInputStream(file);
            fc= fis.getChannel();
            //修正 Excel在“xxx.xlsx”中发现不可读取的内容。是否恢复此工作薄的内容？如果信任此工作簿的来源，请点击"是"
            response.setHeader("Content-Length", String.valueOf(fc.size()));
            //response.setContentType("application/force-download");
            response.setHeader("Content-Disposition", "attachment;fileName=" + new String(filename.getBytes("UTF-8"), "ISO8859-1"));
            response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");

            byte[] buffer = new byte[1024];

            BufferedInputStream bis = null;
            OutputStream os = null; //输出流
            try {
                os = response.getOutputStream();
                bis = new BufferedInputStream(fis);
                int i = bis.read(buffer);
                while(i != -1){
                    os.write(buffer);
                    i = bis.read(buffer);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }

            try {
                bis.close();
                fis.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }else{
            return JSONObject.toJSONString(new ResponseData(500, "路径不存在！", false));
        }
        return null;
    }

}
