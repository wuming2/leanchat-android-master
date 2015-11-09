package com.lxy.test.whv.entity.avobject;

import com.avos.avoscloud.AVClassName;
import com.avos.avoscloud.AVException;
import com.avos.avoscloud.AVObject;
import com.avos.avoscloud.AVQuery;
import com.avos.avoscloud.AVUser;
import com.avoscloud.leanchatlib.model.LeanchatUser;
import com.lxy.test.whv.service.CacheService;

import java.util.List;

/**
 * Created by wuming on 2015/10/31.
 */
@AVClassName("PostComment")
public class PostComment extends AVObject {
    public PostComment() {
        super();
    }

    public void setPostId(String postId) {
        put("postId", postId);
    }

    public String getPostId() {
        return getString("postId");
    }

    public String getContent() {
        return getString("content");
    }

    public void setContent(String value) {
        put("content", value);
    }

    public void setCreator(AVUser user) {
        put("creator", user);
    }

    public AVUser getCreator() {
        return getAVUser("creator");
    }

    public static List<PostComment> findCompanyPostComment(int skip, int limit, String postId) throws AVException {

        AVQuery<PostComment> q = CompanyPost.getQuery(PostComment.class);
        q.include("creator");
//        q.whereContains("postId", postId);
        q.whereEqualTo("postId", postId);
        q.skip(skip);
        q.limit(limit);
        q.setCachePolicy(AVQuery.CachePolicy.NETWORK_ELSE_CACHE);
        List<PostComment> comments = q.find();
        for (int i = 0; i < comments.size(); i++) {
            CacheService.registerUser((LeanchatUser) comments.get(i).getCreator());
        }
        return comments;
    }
}

