package com.lxy.whv.entity.avobject;

import com.avos.avoscloud.AVClassName;
import com.avos.avoscloud.AVException;
import com.avos.avoscloud.AVObject;
import com.avos.avoscloud.AVQuery;
import com.avos.avoscloud.AVUser;
import com.avoscloud.leanchatlib.model.LeanchatUser;
import com.lxy.whv.service.CacheService;

import java.util.Date;
import java.util.List;

/**
 * Created by LXY on 2015/10/28.
 * <p/>
 * 普通帖子 TODO 需要完善，把查找....方法都加上呗
 */
@AVClassName("Post")
public class Post extends AVObject {

    public static final String TITLE = "title";
    public static final String CONTENT = "content";
    public static final String DATEPLANNED = "datePlanned";
    public static final String DESTINATION = "destination";
    public static final String USER = "publisher";
    public static final String COMMENT = "comments";

    public Post() {

    }

    public Post(String title, String content, Date datePlanned, String destination, AVUser user) {
        put(TITLE, title);
        put(CONTENT, content);
        put(DATEPLANNED, datePlanned);
        put(DESTINATION, destination);
        put(USER, user);
        //TODO 如果变更，无法实时更新啊
    }

    public Post(String title, String content, AVUser user) {
        put(TITLE, title);
        put(CONTENT, content);
//        put(DATEPLANNED, datePlanned);
//        put(DESTINATION, destination);
        put(USER, user);
        //TODO 如果变更，无法实时更新啊
    }

    public void setTitle(String title) {
        put(TITLE, title);
    }

    public String getTitle() {
        return getString(TITLE);
    }

    public void setContent(String content) {
        put(CONTENT, content);
    }

    public String getContent() {
        return getString(CONTENT);
    }

    public void setDateplanned(Date datePlanned) {
        put(DATEPLANNED, datePlanned);
    }

    public Date getDateplanned() {
        return getDate(DATEPLANNED);
    }

    public void setDestination(String destination) {
        put(DESTINATION, destination);
    }

    public String getDestination() {
        return getString(DESTINATION);
    }

    public AVUser getPublisher() {
        return getAVUser(USER);
    }

    public void setPublisher(AVUser user) {
        put(USER, user);
    }


    public static List<Post> findPosts(int skip, int limit) throws AVException {

        AVQuery<Post> q = Post.getQuery(Post.class);
        //hehe 还好哟示例
        //TODO  这里如果include user 其他用户没法发布评论啊....尼玛啊  但是为什么能取到ObjectId呢啊啊啊啊，ffff  这里查到了是不是要保存到cache里边啊？！
        q.include(Post.USER);
        q.include(Post.COMMENT);
        q.skip(skip);
        q.limit(limit);
        q.orderByDescending(AVObject.CREATED_AT);
        q.setCachePolicy(AVQuery.CachePolicy.NETWORK_ELSE_CACHE);
        List<Post> posts = q.find();
        //TODO 是不是可以优化……
        for (int i = 0; i < posts.size(); i++) {
            CacheService.registerUser((LeanchatUser) posts.get(i).getPublisher());
        }
        return posts;
    }

//    @SuppressWarnings("unchecked")
//    public List getComments() {
//        return (List) getList(CompanyPost.COMMENT);
//    }
//
//    public void addComment(PostComment com) {
//        addUnique("comments", com);
//    }
}
