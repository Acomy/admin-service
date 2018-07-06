package com.bossien.adminservice.redis;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.data.redis.connection.RedisConnection;
import org.springframework.data.redis.connection.jedis.JedisConnectionFactory;
import org.springframework.data.redis.core.SetOperations;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.stereotype.Repository;

import java.util.Set;
import java.util.concurrent.TimeUnit;

@Repository
public class RedisDao {

    @Autowired
    private StringRedisTemplate template;

    public void setDatabase(int index){
        JedisConnectionFactory jedisConnectionFactory = (JedisConnectionFactory) template.getConnectionFactory();
        jedisConnectionFactory.setDatabase(index);
        template.setConnectionFactory(jedisConnectionFactory);
    }

    /**
     * 获取所有的keys
     * @return
     */
//    public Set<String> getKeys(){
//        Set<String> keys = template.keys("*");
//        return keys;
//    }

    public  void setForValueKey(String key, String value){
        ValueOperations<String, String> ops = template.opsForValue();
        ops.set(key,value,1, TimeUnit.MINUTES);//1分钟过期
    }

    public String getForValueValue(String key){
        ValueOperations<String, String> ops = this.template.opsForValue();
        return ops.get(key);
    }

    /**
     * 向指定key中存放set集合
     * @param key
     * @param strarrays
     * @param expiryTime
     */
    public  void setForSetKey(String key, String[] strarrays, Integer expiryTime){
        SetOperations<String, String> setOperations = template.opsForSet();
        setOperations.add(key, strarrays);
        template.expire(key, expiryTime, TimeUnit.MINUTES);//设置过期时间
    }

    /**
     * 随机获取key无序集合中的一个元素
     * @param setKey
     * @return
     */
    public String getForSetRandomValue(String setKey){
        String result = template.opsForSet().randomMember(setKey);
        return result;
    }

    /**
     * 根据key获取set集合
     * @param setKey
     * @return
     */
    public Set<String> getForSetMembers(String setKey){
        Set<String> result = template.opsForSet().members(setKey);
        return result;
    }

    /**
     * 根据key查看集合中是否存在指定数据
     * @return
     */
    public boolean isMember(String key, String value){
        boolean result = template.opsForSet().isMember(key, value);
        return result;
    }

    /**
     * 根据key删除缓存
     * @author zhangyubei
     * @date 2018-03-05 16:23:39
     */
    public void delete(String key){
        template.delete(key);
    }

    /**
     * 根据key获取过期时间
     * @author zhangyubei
     * @date 2018-04-16 17:08:56
     */
    public Long getExpire(String key){
        Long time = template.getExpire(key);
        return time;
    }

    /**
     * 给key设置过期时间
     * @author zhangyubei
     * @date 2018-04-16 17:08:56
     */
    public void setExpire(String key, Integer expiryTime){
        template.expire(key, expiryTime, TimeUnit.MINUTES);//设置过期时间
    }

    /**
     * 获取db库key数量
     * @return
     */
    public long getKeySize(){
        RedisConnection redisConnection = null;
        long size = 0;
        try {
            redisConnection = template.getConnectionFactory().getConnection();
            size = redisConnection.dbSize().longValue();
        } catch (DataAccessException e) {
            e.printStackTrace();
        } finally {
            if( null != redisConnection){
                redisConnection.close();
            }
        }
        return size;
    }
}
