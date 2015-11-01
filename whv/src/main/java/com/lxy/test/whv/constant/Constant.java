package com.lxy.test.whv.constant;

import android.content.Context;

import com.lxy.test.whv.R;
import com.lxy.test.whv.util.LogUtils;

import java.util.HashMap;

/**
 * Created by wuming on 2015/10/25.
 */
public class Constant {

    public static final int[] applicationStateButtonIndex = {R.id.radiobutton_want_to_know, R.id.radiobutton_preparing,
            R.id.radiobutton_submiting, R.id.radiobutton_granted, R.id.radiobutton_abroad,
            R.id.radiobutton_returned, R.id.radiobutton_pr};
    public static final int[] applicationState = {0, 1, 2, 3, 4, 5, 6};
    public static final int[] applicationStateTextId = {R.string.bootstrap_state_want_to_know, R.string.bootstrap_state_preparing,
            R.string.bootstrap_state_submiting, R.string.bootstrap_state_granted, R.string.bootstrap_state_abroad,
            R.string.bootstrap_state_returned, R.string.bootstrap_state_pr};

    public static final int[] genderTextId = {R.string.gender_female, R.string.gender_male};

    //TODO 城市选择
    public static final int[] cityStringId = {R.string.city_sydney,
            R.string.city_melb, R.string.city_brisbane, R.string.city_perth,
            R.string.city_adelaide, R.string.city_canberra, R.string.city_hobart, R.string.city_other, R.string.city_newzealand};
    public static final String[] cityCode = {"SYD", "MEL", "BNE", "PER",
            "ADL", "CBR", "HBA", "OTHER", "NZ"};

    public static HashMap<String, String> getCityMap(Context ctx) {

        HashMap<String, String> map = new HashMap<>();
        for (int i = 0; i < cityStringId.length; i++) {
            map.put(ctx.getString(cityStringId[i]), cityCode[i]);
        }
        return map;
    }

    public static String SQUARE_CHAT_ID = "5635d3ee60b259742c3a0031";

}
