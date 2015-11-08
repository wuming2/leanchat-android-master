package com.lxy.test.whv.entity.avobject;

import com.avos.avoscloud.AVClassName;
import com.avos.avoscloud.AVException;
import com.avos.avoscloud.AVObject;
import com.avos.avoscloud.AVQuery;
import com.avos.avoscloud.AVUser;
import com.lxy.test.whv.util.DateUtils;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created by LXY on 2015/10/28.
 * <p/>
 * 约伴帖子类 TODO 需要完善，把查找....方法都加上呗
 */
@AVClassName("Post")
public class Post extends AVObject {

    public static final String TITLE = "title";
    public static final String CONTENT = "content";
    public static final String USER = "author";
    public static final String IMGURL = "imgUrl";
    public static final String URL = "url";
    public static final String ACTIVE = "active";
    public static final String WEIGHT = "weight";
    public static final String ORDER = "order";

    public Post() {

    }

    public Post(String title, String content, String imgUrl, String url, AVUser user) {
        put(TITLE, title);
        put(CONTENT, content);
        put(USER, user);
        put(IMGURL, imgUrl);
        put(URL, url);
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

    public String getImgurl() {
        return getString(IMGURL);
    }

    public String getUrl() {
        return getString(URL);
    }

    public AVUser getPublisher() {
        return getAVUser(USER);
    }

    public void setPublisher(AVUser user) {
        put(USER, user);
    }


    public static List<Post> getPost(int skip, int limit) throws AVException {

        AVQuery<Post> q = Post.getQuery(Post.class);
        //hehe 还好哟示例
        //TODO  这里如果include user 其他用户没法发布评论啊....尼玛啊  但是为什么能取到ObjectId呢啊啊啊啊，ffff  这里查到了是不是要保存到cache里边啊？！
        //q.include(Post.USER);
        q.orderByDescending(ORDER);
        q.skip(skip);
        q.limit(limit);
        q.setCachePolicy(AVQuery.CachePolicy.NETWORK_ELSE_CACHE);
        List<Post> posts = q.find();
        return posts;
    }
}
