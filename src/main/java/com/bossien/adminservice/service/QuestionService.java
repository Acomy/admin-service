package com.bossien.adminservice.service;

import com.alibaba.fastjson.JSONObject;
import com.bossien.adminservice.mapper.*;
import com.bossien.adminservice.redis.RedisDao;
import com.bossien.adminservice.util.CaesarCipher;
import com.bossien.adminservice.util.PageBean;
import com.bossien.common.base.ResponseData;
import com.bossien.common.model.basics.*;
import com.bossien.common.util.IdGenerator;
import com.bossien.common.util.ImportExecl;
import com.bossien.common.util.SearchEntity;
import com.bossien.common.model.*;
import com.github.pagehelper.PageHelper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.codec.Base64;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Service
public class QuestionService {
    @Autowired
    private QuestionMapper questionMapper;
    @Autowired
    private CourseTypeMapper courseTypeMapper;
    @Autowired
    private CourseMapper courseMapper;
    @Autowired
    private RedisDao redisDao;
    @Autowired
    private ProjectBasicMapper projectBasicMapper;
    @Autowired
    private ProjectQuestionMapper projectQuestionMapper;
    @Autowired
    private ExamStrategyMapper examStrategyMapper;
    @Autowired
    private ExaminationMapper examinationMapper;
    @Autowired
    private HttpServletRequest request;
    @Autowired
    private BusinessMapper businessMapper;

    public String addBatchUploadProject(Long projectId, String filePath) {
        String result = "";
        try {
            if(StringUtils.isEmpty(projectId)){
                return JSONObject.toJSONString(new ResponseData(500, "项目ID不能为空！", false));
            }
            ProjectBasic projectBasic = projectBasicMapper.selectByPrimaryKey(projectId);
            if(StringUtils.isEmpty(projectBasic)){
                return JSONObject.toJSONString(new ResponseData(500, "项目不存在！", false));
            }else{
                if(projectBasic.getState() == 4){
                    return JSONObject.toJSONString(new ResponseData(500, "项目已结束！", false));
                }
            }

            ImportExecl poi = new ImportExecl();
            List<List<String>> list = poi.read(filePath);
            if (list.size() < 400) {
                return JSONObject.toJSONString(new ResponseData(500, "上传试题数量过少！", false));
            }
            List<String> stringList = list.get(0);
            if (!"课程编号".equals(stringList.get(0)) && !"试题编号".equals(stringList.get(1))){
                return JSONObject.toJSONString(new ResponseData(200, "试题模板不正确！", false));
            }

            projectQuestionMapper.deleteByProjectId(projectId);
            StringBuilder questionStr = new StringBuilder();
            questionStr.append("导入成功，个别试题不存在:");
            List<ProjectQuestion> projectQuestionList = new ArrayList<ProjectQuestion>();
            for (int i = 1; i < list.size(); i++) {
                List<String> cellList = list.get(i);
                if (!StringUtils.isEmpty(cellList.get(1))) {
                    Question question = questionMapper.selectByQuestionNo(cellList.get(1));
                    if (!StringUtils.isEmpty(question)) {
                        int num = projectQuestionMapper.selectProjectQuestionSize(projectId, question.getQuestionId());
                        if(num == 0){
                            ProjectQuestion projectQuestion = new ProjectQuestion();
                            long id = new IdGenerator(0, 0).nextId();
                            projectQuestion.setId(id);
                            projectQuestion.setProjectId(projectId);
                            projectQuestion.setCourseId(question.getCourseId().longValue());
                            projectQuestion.setQuestionId(question.getQuestionId().longValue());
                            projectQuestion.setIsMandatory(Byte.valueOf(cellList.get(2)));
                            projectQuestionList.add(projectQuestion);
//                            projectQuestionMapper.insertSelective(projectQuestion);
                        }
                    }else{
                        questionStr.append(cellList.get(1));
                        questionStr.append(";");
                    }
                }/*else{
                    return JSONObject.toJSONString(new ResponseData(200, questionStr.toString(), false));
                }*/
            }
            projectQuestionMapper.batchInsert(projectQuestionList);
            projectBasic.setState((byte) 2);
            projectBasicMapper.updateByPrimaryKeySelective(projectBasic);
            if(questionStr.length() > 11){
                result = JSONObject.toJSONString(new ResponseData(200, questionStr.toString(), false));
            }else{
                result = JSONObject.toJSONString(new ResponseData(200, "请求成功！", true));
            }
        } catch (Exception e) {
            result = JSONObject.toJSONString(new ResponseData(500, "上传数据有错误！", false));
            e.printStackTrace();
        }
        return result;
    }

    /**
     *  根据项目ID查询试卷生成进度
     * @author zhangyubei
     * @date 2018-03-08 17:26:59
     */
    public String selectSpeedByProjectId(Long projectId){
        ProjectBasic projectBasic = projectBasicMapper.selectByPrimaryKey(projectId);
        redisDao.setDatabase(2);
        int number = redisDao.getForSetMembers(String.valueOf(projectId)).size();
        int speed = 0;
        if(projectBasic.getCount() != 0){
            speed = Math.round(number/projectBasic.getCount())*100;
        }
        return  JSONObject.toJSONString(new ResponseData(200, "请求成功！", String.valueOf(speed)));
    }


    public String getProjectBasic(Long projectId){
        String result;
        try{
            ProjectBasic projectBasic = projectBasicMapper.selectByPrimaryKey(projectId);
            result = JSONObject.toJSONString(new ResponseData(200, "请求成功！", projectBasic));
        }catch (Exception e){
            result = JSONObject.toJSONString(new ResponseData(500, "请求失败！", null));
            e.printStackTrace();
        }

        return  result;
    }


    public String getExamination(Long id){
        String result;
        try{
            Examination examination = examinationMapper.selectByPrimaryKey(id);
            result = new ResponseData(200, "请求成功！", examination.toString()).toString();
        }catch (Exception e){
            result = JSONObject.toJSONString(new ResponseData(500, "请求失败！", null));
            e.printStackTrace();
        }

        return  result;
    }

    /**
     * 查询试卷记录
     * @return
     */
    public String selectExaminationList(SearchEntity searchEntity){
        String result = "";
        try{
            PageHelper.startPage(searchEntity.getPageNum(), searchEntity.getPageSize());
            List<Examination> projectBasicList = examinationMapper.selectExaminationList(searchEntity);
            PageBean<Examination> pageBean = new PageBean<>(projectBasicList, 200, "请求成功！");
            result = JSONObject.toJSONString(pageBean);
        }catch (Exception e){
            result = JSONObject.toJSONString(new ResponseData(500, "请求失败！", null));
            e.printStackTrace();
        }
        return result;
    }

    /**
     * 根据课程ID获取题库
     * @return
     */
    public String selectProjectBasicList(SearchEntity searchEntity){
        String result = "";
        try{
            PageHelper.startPage(searchEntity.getPageNum(), searchEntity.getPageSize());
            List<ProjectBasic> projectBasicList = projectBasicMapper.selectProjectBasicList(searchEntity);
            if(!StringUtils.isEmpty(projectBasicList)){
                for(ProjectBasic projectBasic : projectBasicList){
                    if(!StringUtils.isEmpty(projectBasic.getIndustry())){
                        Business business = businessMapper.selectByPrimaryKey(Long.valueOf(projectBasic.getIndustry()));
                        if(!StringUtils.isEmpty(business)){
                            projectBasic.setIndustryName(business.getBusinessName());
                        }
                    }
                }
            }
            PageBean<ProjectBasic> pageBean = new PageBean<>(projectBasicList, 200, "请求成功！");
            result = JSONObject.toJSONString(pageBean);
        }catch (Exception e){
            result = JSONObject.toJSONString(new ResponseData(500, "请求失败！", null));
            e.printStackTrace();
        }
        return result;
    }

    public String selectByBasicName(String basicName, Long id){

        try{
            ProjectBasic projectBasic = projectBasicMapper.selectByBasicName(basicName, id);
            if(StringUtils.isEmpty(projectBasic)){
                return JSONObject.toJSONString(new ResponseData(200, "请求成功！", true));
            }else{
                return JSONObject.toJSONString(new ResponseData(200, "请求成功！", false));
            }
        }catch (Exception e){
            e.printStackTrace();
            return JSONObject.toJSONString(new ResponseData(500, "请求失败！", false));
        }
    }

    /**
     * 根据项目ID查询
     * @param projectId
     * @return
     */
    public String selectGroupByChrType(Long projectId){
        String result = null;
        try {
            List<GroupProject> groupProjectList = projectQuestionMapper.selectGroupByChrType(projectId);
            result = JSONObject.toJSONString(new ResponseData(200, "请求成功！", JSONObject.toJSONString(groupProjectList)));
        }catch (Exception e){
            result = JSONObject.toJSONString(new ResponseData(500, "请求失败！", false));
            e.printStackTrace();
        }
        return result;
    }

    /**
     * 新增项目
     * @param projectBasic
     * @return
     */
    public String insertProjectBasic(ProjectBasic projectBasic){
        String result = null;
        try {
            ProjectBasic pb = projectBasicMapper.selectByBasicName(projectBasic.getBasicName(), projectBasic.getId());
            if(!StringUtils.isEmpty(pb)){
                return JSONObject.toJSONString(new ResponseData(500, "项目已存在！", true));
            }
            Date beginTime = projectBasic.getBeginTime();
            Date endTime = projectBasic.getEndTime();
            if(endTime.getTime() < beginTime.getTime()){
                return JSONObject.toJSONString(new ResponseData(500, "结束时间不能小于开始时间！", false));
            }
            if(endTime.getTime() < System.currentTimeMillis()){
                return JSONObject.toJSONString(new ResponseData(500, "结束时间不能小于现在时间！", false));
            }
            if(StringUtils.isEmpty(projectBasic.getId())){
                long id =  new IdGenerator(0, 0).nextId();
                projectBasic.setId(id);
                projectBasic.setState((byte) 1);//项目未发布状态
                projectBasic.setMode((byte) 1);//项目正常状态
                projectBasic.setCreateDate(new Date());
                projectBasic.setCreateUser(request.getHeader("username"));
                projectBasic.setSign((byte) 1);//表示项目第一次生成
                projectBasic.setForm((byte) 1);//表示项目可生成试卷
                projectBasicMapper.insertSelective(projectBasic);
            }else{
                ProjectBasic basic = projectBasicMapper.selectByPrimaryKey(projectBasic.getId());
                if(!StringUtils.isEmpty(basic)){
                    if(basic.getState() == 4){
                        if(endTime.getTime() > System.currentTimeMillis()){
                            basic.setState((byte)3);
                        }
                    }
                    basic.setOperDate(new Date());
                    basic.setOperUser(request.getHeader("username"));
                    basic.setEndTime(projectBasic.getEndTime());
                    basic.setBeginTime(projectBasic.getBeginTime());
                    basic.setBasicName(projectBasic.getBasicName());
                    basic.setCount(projectBasic.getCount());
                    basic.setRemark(projectBasic.getRemark());
                    //basic.setMode(projectBasic.getMode());
                    basic.setIndustry(projectBasic.getIndustry());
                    //basic.setCategory(projectBasic.getCategory());
                    //basic.setImportant1(projectBasic.getImportant1());
                    //basic.setImportant2(projectBasic.getImportant2());
                    //basic.setImportant3(projectBasic.getImportant3());
                    projectBasicMapper.updateByPrimaryKeySelective(basic);
                }
            }
            result = JSONObject.toJSONString(new ResponseData(200, "请求成功！", true));
        }catch (Exception e){
            result = JSONObject.toJSONString(new ResponseData(500, "请求失败！", false));
            e.printStackTrace();
        }
        return result;
    }


    /**
     * 修改项目状态
     * @author zhangyubei
     * @date 2018-03-09 09:25:21
     */
    public String updateProjectBasicMode(ProjectBasic projectBasic){
        String result = null;
        try {
            if(!StringUtils.isEmpty(projectBasic.getId())){
                ProjectBasic basic = projectBasicMapper.selectByPrimaryKey(projectBasic.getId());
                if(!StringUtils.isEmpty(basic)){
                    basic.setOperDate(new Date());
                    basic.setOperUser(request.getHeader("username"));
                    basic.setMode(projectBasic.getMode());
                    projectBasicMapper.updateByPrimaryKeySelective(basic);
                }
            }
            result = JSONObject.toJSONString(new ResponseData(200, "请求成功！", true));
        }catch (Exception e){
            result = JSONObject.toJSONString(new ResponseData(500, "请求失败！", false));
            e.printStackTrace();
        }
        return result;
    }

    /**
     * 修改项目的标记位和试卷状态
     * @author zhangyubei
     * @date 2018-03-09 09:25:21
     */
    public String updateProjectBasicSign(ProjectBasic projectBasic){
        String result = null;
        try {
            if(!StringUtils.isEmpty(projectBasic.getId())){
                ProjectBasic basic = projectBasicMapper.selectByPrimaryKey(projectBasic.getId());
                if(!StringUtils.isEmpty(basic)){
                    if(basic.getState() == 4){
                        return JSONObject.toJSONString(new ResponseData(500, "项目已结束，请您修改项目结束时间！", false));
                    }
                    if(basic.getEndTime().getTime() < System.currentTimeMillis()){
                        return JSONObject.toJSONString(new ResponseData(500, "项目结束时间小于现在时间！", false));
                    }
                    basic.setOperDate(new Date());
                    basic.setOperUser(request.getHeader("username"));
                    basic.setSign((byte)(basic.getSign()+1));
                    basic.setForm((byte)1);
                    projectBasicMapper.updateByPrimaryKeySelective(basic);
                }
            }
            result = JSONObject.toJSONString(new ResponseData(200, "请求成功！", true));
        }catch (Exception e){
            result = JSONObject.toJSONString(new ResponseData(500, "请求失败！", false));
            e.printStackTrace();
        }
        return result;
    }


    /**
     * 维护项目课程的关联关系表
     * @param projectId
     * @param questionIds
     * @return
     */
    public String insertProjectCourse(Long projectId, String[] questionIds){
        String result = null;
        try {
            if(questionIds.length>0){
                projectQuestionMapper.deleteByProjectId(projectId);
                for(int num=0; num<questionIds.length; num++){
                    ProjectQuestion projectCourse = new ProjectQuestion();
                    long id =  new IdGenerator(0, 0).nextId();
                    projectCourse.setId(id);
                    String[] questionStr = questionIds[num].split("_");
                    projectCourse.setCourseId(Long.valueOf(questionStr[1]));
                    projectCourse.setQuestionId(Long.valueOf(questionStr[0]));
                    projectCourse.setProjectId(projectId);
                    projectCourse.setCreateDate(new Date());
                    projectCourse.setCreateUser(request.getHeader("username"));
                    projectCourse.setOperDate(new Date());
                    projectCourse.setOperUser(request.getHeader("username"));
                    projectQuestionMapper.insertSelective(projectCourse);
                }
            }
            result = JSONObject.toJSONString(new ResponseData(200, "请求成功！", true));
        }catch (Exception e){
            result = JSONObject.toJSONString(new ResponseData(500, "请求失败！", false));
            e.printStackTrace();
        }
        return result;
    }

    /**
     * 维护项目考试策略
     * @param examStrategy
     * @return
     */
    public String insertExamStrategy(ExamStrategy examStrategy){
        String result = null;
        try {
            examStrategyMapper.deleteByProjectId(examStrategy.getProjectId());
            long id =  new IdGenerator(0, 0).nextId();
            examStrategy.setId(id);
            examStrategy.setCreateDate(new Date());
            examStrategy.setCreateUser(request.getHeader("username"));
            examStrategy.setOperDate(new Date());
            examStrategy.setOperUser(request.getHeader("username"));
            examStrategyMapper.insertSelective(examStrategy);
            ProjectBasic projectBasic = new ProjectBasic();
            projectBasic.setId(examStrategy.getProjectId());
            projectBasic.setState((byte)2);
            projectBasicMapper.updateByPrimaryKeySelective(projectBasic);
            result = JSONObject.toJSONString(new ResponseData(200, "请求成功！", true));
        }catch (Exception e){
            result = JSONObject.toJSONString(new ResponseData(500, "请求成功！", false));
            e.printStackTrace();
        }
        return result;
    }

    /**
     * 抽取试卷
     * @return
     */
    public String selectByProjectExamination(){
        String result = "";
        String uid = request.getHeader("uid");
        try{
            List<ProjectBasic> projectBasicList = projectBasicMapper.selectByProjectState((byte)3);
            if(projectBasicList!=null && projectBasicList.size()>0){
                for(ProjectBasic projectBasic : projectBasicList){
                    long dateTime = projectBasic.getBeginTime().getTime() - System.currentTimeMillis();
                    if(dateTime<0){
                        String questionRedisName = projectBasic.getBasicName()+"_"+projectBasic.getId();
                        redisDao.setDatabase(2);
                        String plainText = redisDao.getForSetRandomValue(questionRedisName);
                        if(!StringUtils.isEmpty(plainText)){

                            //暂时不加密
                           //String encodeStr = Base64.encode(plainText.getBytes("utf-8"));
                            result = JSONObject.toJSONString(new ResponseData(200, "抽取试卷成功！", CaesarCipher.encrypt(plainText)));
                        }else{
                            result = JSONObject.toJSONString(new ResponseData(404, "暂无试卷可供抽取！", null));
                        }
                    }else{
                        result = JSONObject.toJSONString(new ResponseData(404, "还没有到考试时间，请敬请期待！", null));
                    }
                }
            }else{
                result = JSONObject.toJSONString(new ResponseData(404, "暂无试卷可供抽取！", null));
            }
        }catch (Exception e){
            result = JSONObject.toJSONString(new ResponseData(500, "抽取试卷报错！", null));
            e.printStackTrace();
        }
        return result;
    }

    /**
     * 根据课程ID获取题库
     * @return
     */
    public String selectQuestionByCourseId(Long courseId){
        String result = "";
        try{
            //PageHelper.startPage(1, 1000);
            List<Question> questionList = questionMapper.selectQuestionByCourseId(courseId);
            PageBean<Question> pageBean = new PageBean<>(questionList, 200, "请求成功！");
            String plainText = JSONObject.toJSONString(pageBean);
            String encodeStr = new String(Base64.encode(plainText.getBytes("utf-8")),"utf-8");
            result = JSONObject.toJSONString(new ResponseData(200, "请求成功！", CaesarCipher.encrypt(encodeStr)));
        }catch (Exception e){
            result = JSONObject.toJSONString(new ResponseData(500, "请求失败！", null));
            e.printStackTrace();
        }
        return result;
    }

    /**
     * 课程分类树
     * @return
     */
    public String selectCourseTypeTree(){
        String result = "";
        try{
            //PageHelper.startPage(1, 1000);
            List<CourseType> courseList = courseTypeMapper.selectCourseTypeTree();
            result = JSONObject.toJSONString(new ResponseData(200, "请求成功！", courseList));
        }catch (Exception e){
            result = JSONObject.toJSONString(new ResponseData(500, "请求失败！", null));
        }
        return result;
    }

    /**
     * 根据课程类型ID获取课程
     * @param typeIds
     * @return
     */
    public String selectCourseList(Long[] typeIds, Integer pageNum, Integer pageSize, String courseName){
        String result = "";
        try{
            PageHelper.startPage(pageNum, pageSize);
            List<Course> courseList = courseMapper.selectCourseList(typeIds, courseName);
            PageBean<Course> pageBean = new PageBean<>(courseList, 200, "请求成功！");
            result = JSONObject.toJSONString(pageBean);
        }catch (Exception e){
            PageBean<Course> pageBean = new PageBean<>(null, 500, "发生异常！");
            result = JSONObject.toJSONString(pageBean);
            e.printStackTrace();
        }
        return result;
    }
}
