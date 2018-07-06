package com.bossien.adminservice.controller;

import com.alibaba.fastjson.JSONObject;
import com.bossien.adminservice.service.QuestionService;
import com.bossien.common.base.ResponseData;
import com.bossien.common.model.basics.ExamStrategy;
import com.bossien.common.model.basics.ProjectBasic;
import com.bossien.common.util.SearchEntity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.util.ResourceUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.util.UUID;

@RestController
@RequestMapping("/projectBasic")
public class QuestionController {

    @Autowired
    public QuestionService questionService;

    @Value("${file.path}")
    private String filePath;


    @RequestMapping(value = "/addBatchUploadProject", method = {RequestMethod.POST})
    public String addBatchUploadProject(@RequestParam(value = "file")MultipartFile multiReq, @RequestParam(value = "projectId") Long projectId) throws FileNotFoundException {
        String uploadFilePath = multiReq.getOriginalFilename();
        // 截取上传文件的后缀
        String uploadFileSuffix = uploadFilePath.substring(
                uploadFilePath.indexOf('.') + 1, uploadFilePath.length());
        String fileName = UUID.randomUUID() +"."+ uploadFileSuffix;
        try {
            File dest = new File(filePath +File.separator+ fileName);
            // 检测是否存在目录
            if (!dest.getParentFile().exists()) {
                dest.getParentFile().mkdirs();
            }
            multiReq.transferTo(dest);
        } catch (IllegalStateException e) {
            e.printStackTrace();
            return JSONObject.toJSONString(new ResponseData(500, "上传失败！", false));
        } catch (IOException e) {
            e.printStackTrace();
            return JSONObject.toJSONString(new ResponseData(500, "上传失败！", false));
        }
        return questionService.addBatchUploadProject(projectId, filePath+File.separator+ fileName);
    }

    @RequestMapping("/selectProjectBasicList")
    public String selectProjectBasicList(@RequestBody SearchEntity searchEntity){
        String result = questionService.selectProjectBasicList(searchEntity);
        return  result;
    }

    @RequestMapping("/updateProjectBasicSign")
    public String updateProjectBasicSign(@RequestBody ProjectBasic projectBasic){
        String result = questionService.updateProjectBasicSign(projectBasic);
        return  result;
    }

    @RequestMapping("/updateProjectBasicMode")
    public String updateProjectBasicMode(@RequestBody ProjectBasic projectBasic){
        String result = questionService.updateProjectBasicMode(projectBasic);
        return  result;
    }

    @RequestMapping("/selectExaminationList")
    public String selectExaminationList(@RequestBody SearchEntity searchEntity){
        String result = questionService.selectExaminationList(searchEntity);
        return  result;
    }

    @RequestMapping("/selectSpeedByProjectId")
    public String selectSpeedByProjectId(@RequestParam(value = "projectId")Long projectId){
        String result = questionService.selectSpeedByProjectId(projectId);
        return  result;
    }

    @RequestMapping("/selectByBasicName")
    public String selectByBasicName(@RequestParam(value = "basicName")String basicName, @RequestParam(value = "id", required = false)Long id){
        String result = questionService.selectByBasicName(basicName, id);
        return  result;
    }

    @RequestMapping("/selectExamination")
    public String selectExamination() {
        String result = questionService.selectByProjectExamination();
        return result;
    }

    @RequestMapping("/selectGroupByChrType")
    public String selectGroupByChrType(@RequestParam(value = "projectId") Long projectId){
        String result = questionService.selectGroupByChrType(projectId);
        return result;
    }

    @RequestMapping("/addProjectBasic")
    public String insertProjectBasic(@RequestBody ProjectBasic projectBasic){
        String result = questionService.insertProjectBasic(projectBasic);
        return result;
    }

    @RequestMapping("/getProjectBasic")
    public String getProjectBasic(@RequestParam(value = "projectId") Long projectId){
        String result = questionService.getProjectBasic(projectId);
        return result;
    }

    @RequestMapping("/getExamination")
    public String getExamination(@RequestParam(value = "id") Long id){
        String result = questionService.getExamination(id);
        return result;
    }

    @RequestMapping("/addProjectCourse")
    public String insertProjectCourse(@RequestParam(value="projectId") Long projectId, @RequestParam(value="questionIds[]") String[] questionIds){
        String result = questionService.insertProjectCourse(projectId, questionIds);
        return result;
    }

    @RequestMapping("/addExamStrategy")
    public String insertExamStrategy(@RequestBody ExamStrategy examStrategy){
        String result = questionService.insertExamStrategy(examStrategy);
        return result;
    }

    @RequestMapping("/selectQuestionByCourseId")
    public String selectQuestionByCourseId(@RequestParam(value="courseId")Long courseId) {
        String result = questionService.selectQuestionByCourseId(courseId);
        return result;
    }


    @RequestMapping("/selectCourseTypeTree")
    public String selectCourseTypeTree(){
        String result = questionService.selectCourseTypeTree();
        return result;
    }

    @RequestMapping("/selectCourseList")
    public String selectCourseList(@RequestParam(value="typeIds[]") Long[] typeIds, @RequestParam(value="pageNum")Integer pageNum, @RequestParam(value="pageSize")Integer pageSize
            ,@RequestParam(value = "courseName") String courseName) {
        String result = questionService.selectCourseList(typeIds, pageNum ,pageSize, courseName);
        return result;
    }
}
