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
@AVClassName("Feedback")
public class Feedback extends AVObject {

//    public static final String TITLE = "title";
    public static final String CONTENT = "content";
    public static final String USER = "publisher";
    public static final String COMMENT = "comments";

    public Feedback() {

    }

    public Feedback( String content, AVUser user) {
//        put(TITLE, title);
        put(CONTENT, content);
        put(USER, user);
        //TODO 如果变更，无法实时更新啊
    }

//    public void setTitle(String title) {
//        put(TITLE, title);
//    }
//
//    public String getTitle() {
//        return getString(TITLE);
//    }

    public void setContent(String content) {
        put(CONTENT, content);
    }

    public String getContent() {
        return getString(CONTENT);
    }

    public AVUser getPublisher() {
        return getAVUser(USER);
    }

    public void setPublisher(AVUser user) {
        put(USER, user);
    }

}
