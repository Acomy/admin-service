package com.bossien.adminservice.mongo;

import com.bossien.common.model.CompanyRanking;
import com.bossien.common.model.UserRanking;
import com.bossien.common.model.basics.QuestionAnswer;
import com.bossien.common.model.basics.StuTotalScore;
import com.bossien.common.model.basics.UserInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class MongoDao {
    private  String collectionName = "stu_total_score";
    private final static String companyRankingRecord = "company_ranking_record";
    private final static String rankingRecord = "ranking_record";

    @Autowired
    private MongoTemplate mongoTemplate;

    public void addQuestionAnswer(Object object){
        mongoTemplate.save(object, collectionName);
    }

    public void updateQuestionAnswer(QuestionAnswer questionAnswer){
        Query query = new Query();
        query.addCriteria(Criteria.where("id").is(questionAnswer.getId()));
        Update update = new Update();
        update.set("questionId", questionAnswer.getQuestionId());
        update.set("answer",questionAnswer.getAnalysis());
        update.set("analysis",questionAnswer.getAnalysis());
        mongoTemplate.updateMulti(query, update, collectionName);
    }

    public UserInfo getQueryUserInfo(String username, String password){
        Query query = new Query();
        query.addCriteria(Criteria.where("varAccount").is(username));
        query.addCriteria(Criteria.where("varPasswd").is(password));
        UserInfo userInfo = mongoTemplate.findOne(query, UserInfo.class,collectionName);
        return userInfo;
    }

    public List<UserInfo> getQueryUserInfoList(String username){
        Query query = new Query();
        query.addCriteria(Criteria.where("varAccount").is(username));
        List<UserInfo> userInfoList = mongoTemplate.find(query, UserInfo.class,collectionName);
        return userInfoList;
    }

    public List<StuTotalScore> getQueryStuTotalScoreList(Long companyId, Integer score){
        Query query = new Query();
        query.addCriteria(Criteria.where("departid").is(companyId));
        query.addCriteria(Criteria.where("personScore").gt(score));
        List<StuTotalScore> userInfoList = mongoTemplate.find(query, StuTotalScore.class,collectionName);
        return userInfoList;
    }

    public List<CompanyRanking> getCompanyRanking(String companyId){
        Long longId = Long.valueOf(companyId);
        Query query = new Query();
        Criteria criteria = new Criteria();
        criteria.orOperator(Criteria.where("companyId").is(longId),
                Criteria.where("firstCompanyId").is(longId),
                Criteria.where("secondCompanyId").is(longId),
                Criteria.where("thirdCompanyId").is(longId),
                Criteria.where("fourthCompanyId").is(longId),
                Criteria.where("fifthCompanyId").is(longId));
        query.addCriteria(criteria);
        query.with(new Sort(Sort.Direction.ASC, "ranking"));
        List<CompanyRanking> ompanyRankingList = mongoTemplate.find(query, CompanyRanking.class,companyRankingRecord);
        return ompanyRankingList;
    }

    public List<UserRanking> getUserRanking(String companyId){
        Long longId = Long.valueOf(companyId);
        Query query = new Query();
        Criteria criteria = new Criteria();
        criteria.orOperator(Criteria.where("companyId").is(longId),
                Criteria.where("firstCompanyId").is(longId),
                Criteria.where("secondCompanyId").is(longId),
                Criteria.where("thirdCompanyId").is(longId),
                Criteria.where("fourthCompanyId").is(longId),
                Criteria.where("fifthCompanyId").is(longId));
        query.addCriteria(criteria);
        query.with(new Sort(Sort.Direction.ASC, "ranking"));
        List<UserRanking> userRankingList = mongoTemplate.find(query, UserRanking.class,rankingRecord);
        return userRankingList;
    }
}
