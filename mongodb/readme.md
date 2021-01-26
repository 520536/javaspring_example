### 1.mvn

````xml
        <!--添加SpringDataMongoDB依赖-->
        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-data-mongodb</artifactId>
        </dependency>
````

### 2.pojo

````java
package com.tensquare.article.pojo;

import org.springframework.data.annotation.Id;

import java.io.Serializable;
import java.util.Date;

public class Comment implements Serializable {
    @Id
    private String _id;
    private String articleid;
    private String content;
    private String userid;
    private String parentid;
    private Date publishdate;
    private Integer thumbup;

    public String get_id() {
        return _id;
    }

    public void set_id(String _id) {
        this._id = _id;
    }

    public String getArticleid() {
        return articleid;
    }

    public void setArticleid(String articleid) {
        this.articleid = articleid;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getUserid() {
        return userid;
    }

    public void setUserid(String userid) {
        this.userid = userid;
    }

    public String getParentid() {
        return parentid;
    }

    public void setParentid(String parentid) {
        this.parentid = parentid;
    }

    public Date getPublishdate() {
        return publishdate;
    }

    public void setPublishdate(Date publishdate) {
        this.publishdate = publishdate;
    }

    public Integer getThumbup() {
        return thumbup;
    }

    public void setThumbup(Integer thumbup) {
        this.thumbup = thumbup;
    }

    @Override
    public String toString() {
        return "Comment{" +
                "_id='" + _id + '\'' +
                ", articleid='" + articleid + '\'' +
                ", content='" + content + '\'' +
                ", userid='" + userid + '\'' +
                ", parentid='" + parentid + '\'' +
                ", publishdate=" + publishdate +
                ", thumbup=" + thumbup +
                '}';
    }
}
````



### 3.repository extends MongoRepository

````java
package com.tensquare.article.repository;

import com.tensquare.article.pojo.Comment;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.Date;
import java.util.List;

public interface CommentRepository extends MongoRepository<Comment, String> {

    //SpringDataMongoDB，支持通过查询方法名进行查询定义的方式
    //根据文章id查询文章评论数据
    List<Comment> findByArticleid(String articleId);

    //根据发布时间和点赞数查询查询
    // List<Comment> findByPublishdateAndThumbup(Date date, Integer thumbup);

    //根据用户id查询，并且根据发布时间倒序排序
    // List<Comment> findByUseridOrderbOrderByPublishdateDesc(String userid);
}

````

### 4.service 引用repository

````java
package com.tensquare.article.service;

import com.tensquare.article.pojo.Comment;
import com.tensquare.article.repository.CommentRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Service;
import util.IdWorker;

import java.util.Date;
import java.util.List;

@Service
public class CommentService {

    @Autowired
    private CommentRepository commentRepository;

    @Autowired
    private IdWorker idWorker;

    @Autowired
    private MongoTemplate mongoTemplate;

    public List<Comment> findAll() {
        List<Comment> list = commentRepository.findAll();
        return list;
    }

    public Comment findById(String commentId) {
        Comment comment = commentRepository.findById(commentId).get();
        return comment;
    }

    public void save(Comment comment) {
        //分布式id生成器生成id
        String id = idWorker.nextId() + "";
        comment.set_id(id);

        //初始化点赞数据，发布时间等
        comment.setThumbup(0);
        comment.setPublishdate(new Date());

        //保存数据
        commentRepository.save(comment);
    }

    public void updateById(Comment comment) {
        //使用的是MongoRepository的方法
        //其中save方法，主键如果存在，执行修改，如果不存在执行新增
        commentRepository.save(comment);
    }

    public void deleteById(String commentId) {
        commentRepository.deleteById(commentId);
    }

    public List<Comment> findByArticleId(String articleId) {
        //调用持久层，根据文章id查询
        List<Comment> list = commentRepository.findByArticleid(articleId);
        return list;
    }

    public void thumbup(String commentId) {
        // //根据评论id查询评论数据
        // Comment comment = commentRepository.findById(commentId).get();
        // //对评论点赞数据加一
        // comment.setThumbup(comment.getThumbup() + 1);
        // //保存修改数据
        // commentRepository.save(comment);

        //点赞功能优化
        //封装修改的条件
        Query query = new Query();
        query.addCriteria(Criteria.where("_id").is(commentId));

        //封装修改的数值
        Update update = new Update();
        //使用inc列值增长
        update.inc("thumbup", 1);

        //直接修改数据
        //第一个参数是修改的条件
        //第二个参数是修改的数值
        //第三个参数是MongoDB的集合名称
        mongoTemplate.updateFirst(query, update, "comment");
    }
}

````

### 5.controller

````java
package com.tensquare.article.controller;

import com.tensquare.article.pojo.Comment;
import com.tensquare.article.service.CommentService;
import entity.Result;
import entity.StatusCode;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("comment")
public class CommentController {

    @Autowired
    private CommentService commentService;

    @Autowired
    private RedisTemplate redisTemplate;

    //PUT /comment/thumbup/{commentId} 根据评论id点赞评论
    @RequestMapping(value = "thumbup/{commentId}", method = RequestMethod.PUT)
    public Result thumbup(@PathVariable String commentId) {
        //把用户点赞信息保存到Redis中
        //每次点赞之前，先查询用户点赞信息
        //如果没有点赞信息，用户可以点赞
        //如果有点赞信息，用户不能重复点赞

        //模拟用户id
        String userId = "123";

        //查询用户点赞信息，根据用户id和评论id
        Object flag = redisTemplate.opsForValue().get("thumbup_" + userId + "_" + commentId);

        //判断查询到的结果是否为空
        if (flag == null) {
            //如果为空，表示用户没有点过赞，可以点赞
            commentService.thumbup(commentId);

            //点赞成功，保存点赞信息
            redisTemplate.opsForValue().set("thumbup_" + userId + "_" + commentId, 1);

            return new Result(true, StatusCode.OK, "点赞成功");
        }

        //如果不为空，表示用户点过赞，不可以重复点赞
        return new Result(false, StatusCode.REPERROR, "不能重复点赞");

    }

    //GET /comment/article/{articleId} 根据文章id查询文章评论
    @RequestMapping(value = "article/{articleId}", method = RequestMethod.GET)
    public Result findByArticleId(@PathVariable String articleId) {
        List<Comment> list = commentService.findByArticleId(articleId);
        return new Result(true, StatusCode.OK, "查询成功", list);
    }

    //GET /comment 查询所有评论
    @RequestMapping(method = RequestMethod.GET)
    public Result findAll() {
        List<Comment> list = commentService.findAll();

        return new Result(true, StatusCode.OK, "查询成功", list);
    }


    //GET /comment/{commentId} 根据评论id查询评论数据
    @RequestMapping(value = "{commentId}", method = RequestMethod.GET)
    public Result findById(@PathVariable String commentId) {
        Comment comment = commentService.findById(commentId);
        return new Result(true, StatusCode.OK, "查询成功", comment);
    }

    //POST /comment 新增评论
    @RequestMapping(method = RequestMethod.POST)
    public Result save(@RequestBody Comment comment) {
        commentService.save(comment);
        return new Result(true, StatusCode.OK, "新增成功");
    }

    //PUT /comment/{commentId} 修改评论
    @RequestMapping(value = "{commentId}", method = RequestMethod.PUT)
    public Result updateById(@PathVariable String commentId, @RequestBody Comment comment) {
        //设置评论主键
        comment.set_id(commentId);
        //执行修改
        commentService.updateById(comment);

        return new Result(true, StatusCode.OK, "修改成功");
    }

    //DELETE /comment/{commentId} 根据id删除评论
    @RequestMapping(value = "{commentId}", method = RequestMethod.DELETE)
    public Result deleteById(@PathVariable String commentId) {
        commentService.deleteById(commentId);
        return new Result(true, StatusCode.OK, "删除成功");
    }

}

````

