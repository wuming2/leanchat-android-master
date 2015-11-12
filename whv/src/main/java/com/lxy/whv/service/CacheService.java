package com.lxy.whv.service;

import com.avos.avoscloud.AVException;
import com.avos.avoscloud.AVQuery;
import com.avos.avoscloud.AVUser;
import com.avos.avoscloud.FindCallback;
import com.avoscloud.leanchatlib.model.LeanchatUser;
import com.avoscloud.leanchatlib.utils.AVUserCacheUtils;
import com.avoscloud.leanchatlib.utils.Constants;
import com.lxy.whv.util.LogUtils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Created by lzw on 14/12/19.
 * TODO 此类需要与 AVUserCacheUtils 合并
 * 在内存中缓存用户信息&好友信息等
 */
public class CacheService {
    private static volatile List<String> friendIds = new ArrayList<String>();

    public static LeanchatUser lookupUser(String userId) {
        //TODO bug  如果没有数据的话会崩溃吧? ！！！ 这里一定要改啊，不然会崩溃吧啊啊啊啊啊......
        return AVUserCacheUtils.getCachedUser(userId);
    }

    public static void registerUser(LeanchatUser user) {
        //TODO 这里cached you 问题，需要更新吧?
//        LogUtils.d("whv registerUser" + user.getString("aboutMe"));
        AVUserCacheUtils.cacheUser(user.getObjectId(), user);
    }

    public static void registerUsers(List<LeanchatUser> users) {
        for (LeanchatUser user : users) {
            registerUser(user);
        }
    }

    public static List<String> getFriendIds() {
        return friendIds;
    }

    public static void removeFriend(LeanchatUser user) {
        friendIds.remove(user.getObjectId());
    }

    public static void setFriendIds(List<String> friendList) {
        friendIds.clear();
        if (friendList != null) {
            friendIds.addAll(friendList);
        }
    }

    //TODO 是不是要在线程中做啊...
    public static void cacheUsers(List<String> ids) throws AVException {
        Set<String> uncachedIds = new HashSet<String>();
        for (String id : ids) {
            LogUtils.d("cacheUsers id = " + id);
            if (lookupUser(id) == null) {
                LogUtils.d("cacheUsers id = " + id + " null!!!!");
                uncachedIds.add(id);
            }
        }
        List<LeanchatUser> foundUsers = findUsers(new ArrayList<String>(uncachedIds));
        LogUtils.d("foundUsers size = " + foundUsers.size());
        registerUsers(foundUsers);
    }

    public static List<LeanchatUser> findUsers(List<String> userIds) throws AVException {
        //FIXME  NetworkOnMainThreadException
        LogUtils.d("findUsers");
        if (userIds.size() <= 0) {
            return Collections.EMPTY_LIST;
        }
        AVQuery<LeanchatUser> q = AVUser.getQuery(LeanchatUser.class);
        q.whereContainedIn(Constants.OBJECT_ID, userIds);
        q.setLimit(1000);
        q.setCachePolicy(AVQuery.CachePolicy.NETWORK_ELSE_CACHE);
        return q.find();
    }

    public static void cacheFriends() {
        //TODO cache 优化  CACHE_ELSE_NETWORK
        LeanchatUser.getCurrentUser(LeanchatUser.class).findFriendsWithCachePolicy(AVQuery.CachePolicy.NETWORK_ELSE_CACHE, new FindCallback<LeanchatUser>() {
            @Override
            public void done(List<LeanchatUser> avUsers, AVException e) {
                if (e != null) {
                    e.printStackTrace();
                } else {
                    LogUtils.d("FindCallback done size = " + avUsers.size());
                    final List<String> userIds = new ArrayList<String>();
                    for (AVUser user : avUsers) {
                        LogUtils.d("objid = " + user.getObjectId());
                        userIds.add(user.getObjectId());
                    }
                    CacheService.setFriendIds(userIds);

                    try {
                        new Thread() {
                            @Override
                            public void run() {
                                super.run();
                                try {
                                    CacheService.cacheUsers(userIds);
                                } catch (AVException e2) {
                                    e2.printStackTrace();
                                }
                            }
                        }.start();
                    } catch (Exception e3) {
                        e3.printStackTrace();
                    }
                }
            }
        });
    }
}
