package com.lxy.test.whv.util;

import com.avos.avoscloud.AVException;
import com.avos.avoscloud.AVGeoPoint;
import com.avos.avoscloud.AVQuery;
import com.avoscloud.leanchatlib.model.LeanchatUser;
import com.avoscloud.leanchatlib.utils.Constants;
import com.lxy.test.whv.App;
import com.lxy.test.whv.service.CacheService;
import com.lxy.test.whv.service.PreferenceMap;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created by wuming on 2015/10/25.
 * <p/>
 * 用来处理用户User信息获取等
 */
public class UserDAOUtils {

    /**
     * @param orderType  排序类型 时间或位置
     * @param skip       跳过数据 用于分页
     * @param limit      每次获取数量
     * @param activeDays 只显示activeDays日内的活跃用户
     * @return 附近用户LeanchatUser 数组
     * @throws AVException TODO 优化代码， 多用缓存!
     */
    public static List<LeanchatUser> findNearbyPeople(int orderType, int skip, int limit, int activeDays) throws AVException {
        PreferenceMap preferenceMap = PreferenceMap.getCurUserPrefDao(App.ctx);
        AVGeoPoint geoPoint = preferenceMap.getLocation();
        if (geoPoint == null) {
            com.avoscloud.leanchatlib.utils.LogUtils.i("geo point is null");
            return new ArrayList<>();
        }
        AVQuery<LeanchatUser> q = LeanchatUser.getQuery(LeanchatUser.class);
        LeanchatUser user = LeanchatUser.getCurrentUser();
        q.whereNotEqualTo(Constants.OBJECT_ID, user.getObjectId());
        Date nowDate = new Date();
        if (orderType == Constants.ORDER_DISTANCE) {
            q.whereNear(LeanchatUser.LOCATION, geoPoint);
            //dayAddNum 天内活跃 TODO 是否提取为用户可编辑参数
            Date newDate2 = new Date(nowDate.getTime() - activeDays * 24 * 60 * 60 * 1000);
            q.whereGreaterThan("updatedAt", newDate2);
        } else {
            q.orderByDescending(Constants.UPDATED_AT);
            // 只显示7天内活跃用户
            Date newDate2 = new Date(nowDate.getTime() - 7 * 24 * 60 * 60 * 1000);
            q.whereGreaterThan("updatedAt", newDate2);
        }
        q.skip(skip);
        q.limit(limit);
        q.setCachePolicy(AVQuery.CachePolicy.NETWORK_ELSE_CACHE);
        List<LeanchatUser> users = q.find();
        CacheService.registerUsers(users);
        return users;
    }

    public static List<LeanchatUser> findCompany(int skip, int limit, String dateString, String cityName) throws AVException {

        AVQuery<LeanchatUser> q = LeanchatUser.getQuery(LeanchatUser.class);
        LeanchatUser user = LeanchatUser.getCurrentUser();
        //TODO test 显示自身
//        q.whereNotEqualTo(Constants.OBJECT_ID, user.getObjectId());

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
        q.whereEqualTo("showMyPlan", true);
        q.skip(skip);
        q.limit(limit);
        q.setCachePolicy(AVQuery.CachePolicy.NETWORK_ELSE_CACHE);
        List<LeanchatUser> users = q.find();
        CacheService.registerUsers(users);
        return users;
    }
}
