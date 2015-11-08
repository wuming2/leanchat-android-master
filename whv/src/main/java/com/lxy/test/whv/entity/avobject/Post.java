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


    public static List<Post> findCompanyPost(int skip, int limit, String dateString, String cityName) throws AVException {

        AVQuery<Post> q = Post.getQuery(Post.class);
        //hehe 还好哟示例
        //TODO  这里如果include user 其他用户没法发布评论啊....尼玛啊  但是为什么能取到ObjectId呢啊啊啊啊，ffff  这里查到了是不是要保存到cache里边啊？！
        q.include(Post.USER);
        q.include(Post.COMMENT);
        if (dateString != null && !dateString.isEmpty()) {
            Date startDate;
            Date endDate;
            Date nowDate = new Date();
            try {
                startDate = DateUtils.getFirstDayOfMonth(dateString, "yyyy-MM-dd");
                // 必须比今天要早不是么...
                if (nowDate.getTime() > startDate.getTime()) {
                    startDate = nowDate;
                }
                endDate = DateUtils.getLastDayOfMonth(dateString, "yyyy-MM-dd");
            } catch (Exception e) {
                e.printStackTrace();
                return new ArrayList<>();
            }
            q.whereGreaterThan("datePlanned", startDate);
            q.whereLessThan("datePlanned", endDate);
        }

        if (cityName != null && !cityName.isEmpty()) {
            q.whereEqualTo("destination", cityName);
        }
        q.skip(skip);
        q.limit(limit);
        q.setCachePolicy(AVQuery.CachePolicy.NETWORK_ELSE_CACHE);
        List<Post> posts = q.find();
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
