package com.skkk.easytouch.View.FunctionSelect;

/**
 * 创建于 2018/1/5
 * 作者 admin
 */
/*
* 
* 描    述：
* 作    者：ksheng
* 时    间：2018/1/5$ 21:00$.
*/
public class FuncConfigs {

    public static final String KEY_FUNC_OP="key_func_op";

    public static final String VALUE_FUNC_OP_CLICK="value_func_op_click";
    public static final String VALUE_FUNC_OP_LONG_CLICK="value_func_op_long_click";
    public static final String VALUE_FUNC_OP_FLING_UP="value_func_op_fling_up";
    public static final String VALUE_FUNC_OP_FLING_LEFT="value_func_op_fling_left";
    public static final String VALUE_FUNC_OP_FLING_BOTTOM="value_func_op_fling_bottom";
    public static final String VALUE_FUNC_OP_FLING_RIGHT="value_func_op_fling_right";

    public static final String VALUE_FUNC_OP_TOP_CLICK="value_func_op_top_click";
    public static final String VALUE_FUNC_OP_TOP_FLING_UP="value_func_op_top_fling_up";
    public static final String VALUE_FUNC_OP_TOP_FLING_BOTTOM="value_func_op_top_fling_bottom";
    public static final String VALUE_FUNC_OP_MID_CLICK="value_func_op_mid_click";
    public static final String VALUE_FUNC_OP_BOTTOM_CLICK="value_func_op_bottom_click";
    public static final String VALUE_FUNC_OP_BOTTOM_FLING_UP="value_func_op_bottom_fling_up";
    public static final String VALUE_FUNC_OP_BOTTOM_FLING_BOTTOM="value_func_op_bottom_fling_bottom";

    public static final String VALUE_FUNC_OP_MENU_BALL="value_func_op_menu_ball_";

    public static final int REQUEST_SELECT_FUNC_DETAIL = 101;

    public enum Func {
        BACK,
        HOME,
        RECENT,
        NOTIFICATION,
        TRUN_POS,
        VOICE_MENU,
        PAY_MENU,
        APP_MENU,
        APPS,
        MENU,
        PREVIOUS_APP,
        LOCK_SCREEN,
        SHOT_SCREEN;
        public int getValue() {
            return this.ordinal();
        }
    }
}
