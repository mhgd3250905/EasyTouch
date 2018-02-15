package com.skkk.easytouch;

import android.os.Environment;

/**
 * 创建于 2017/10/25
 * 作者 admin
 */
/*
* 
* 描    述：
* 作    者：ksheng
* 时    间：2017/10/25$ 23:24$.
*/
public class Configs {

    public static final String SAVED_IMAGE_NAME = "touchDrawable.png";
    public static final String KEY_TOUCH_UI_BACKGROUND_BALL_CUSTOM = "key_touch_ui_background_ball_custom";
    public static final int RESULT_PHOTO_REQUEST_CUT = 100;
    public static final int RESULT_PHOTO_REQUEST_TAKE_PHOTO = 101;
    public static final int RESULT_PHOTO_REQUEST_GALLERY = 102;
    public static final String KEY_PHOTO_CUSTOM_DRAWABLE = "key_photo_custom_drawable";
    public static final int RESULT_PERMISS_REQUEST_FLOAT_LINEAR = 200;
    public static final int RESULT_PERMISS_REQUEST_FLOAT_BALL = 201;

    //截屏传递Key
    public static final int REQUEST_MEDIA_PROJECTION = 300;
    public static String SAVED_IMAGE_DIR_PATH =
            Environment.getExternalStorageDirectory().getPath()
                    + "/EasyTouch/camera/";

    public enum Position {
        LEFT,
        RIGHT;

        public int getValue() {
            return this.ordinal();
        }
    }

    public enum LinearPos {
        TOP,
        MID,
        BOTTOM;

        public int getValue() {
            return this.ordinal();
        }

    }

    public enum MenuDetailType {
        VOICE,
        PAY,
        APPS;

        public int getValue() {
            return this.ordinal();
        }

    }


    public enum AppType {
        APP,
        SHORTCUT;

        public int getValue() {
            return this.ordinal();
        }
    }

    public enum TouchType {
        LINEAR,
        BALL,
        NONE;

        public int getValue() {
            return this.ordinal();
        }
    }

    public enum TouchDirection {
        UP,
        LEFT,
        DOWN,
        RIGHT;

        public int getValue() {
            return this.ordinal();
        }

    }

    public interface OnAnimEndListener {
        void onAnimEnd();
    }

    public static final String KEY_VERSION_UPDATE="key_version_update";//记录是否有版本更新

    public static final String NAME_SERVICE_TOUCH_BALL = "com.skkk.easytouch.Services.EasyTouchBallService";
    public static final String NAME_SERVICE_TOUCH_LINEAR = "com.skkk.easytouch.Services.EasyTouchLinearService";

    public static final String KEY_TOUCH_UI_WIDTH = "key_touch_ui_width";
    public static final String KEY_TOUCH_UI_HEIGHT = "key_touch_ui_height";
    public static final String KEY_TOUCH_UI_TOP_COLOR = "key_touch_ui_top_color";
    public static final String KEY_TOUCH_UI_MID_COLOR = "key_touch_ui_mid_color";
    public static final String KEY_TOUCH_UI_BOTTOM_COLOR = "key_touch_ui_bottom_color";
    public static final String KEY_TOUCH_UI_TOP_DRAWABLE = "key_touch_ui_top_drawable";
    public static final String KEY_TOUCH_UI_MID_DRAWABLE = "key_touch_ui_mid_drawable";
    public static final String KEY_TOUCH_UI_BOTTOM_DRAWABLE = "key_touch_ui_bottom_drawable";
    public static final String KEY_TOUCH_UI_VIBRATE_LEVEL_LINEAR = "key_touch_ui_vibrate_level_linear";
    public static final String KEY_TOUCH_UI_COLOR_ALPHA_LINEAR = "key_touch_ui_color_alpha_linear";
    public static final String KEY_TOUCH_UI_THEME_HIDE = "key_touch_ui_theme_hide";
    public static final String KEY_TOUCH_UI_DIRECTION = "key_touch_ui_direction";
    public static final String KEY_TOUCH_UI_POS_LINEAR_FREEZE = "key_touch_ui_pos_linear_freeze";
    public static final String KEY_TOUCH_UI_POS_BALL_FREEZE = "key_touch_ui_pos_ball_freeze";


    public static final String KEY_TOUCH_UI_RADIUS = "key_touch_ui_radius";
    public static final String KEY_TOUCH_UI_VIBRATE_LEVEL_BALL = "key_touch_ui_vibrate_level_ball";
    public static final String KEY_TOUCH_UI_COLOR_ALPHA_BALL = "key_touch_ui_color_alpha_ball";
    public static final String KEY_TOUCH_UI_BACKGROUND_BALL = "key_touch_ui_background_ball";

    public static final String KEY_BALL_MENU_TOP_APPS_ = "key_ball_menu_top_apps_";
    public static final String KEY_BALL_MENU_BOTTOM_APPS_ = "key_ball_menu_bottom_apps_";

    public static final String KEY_LINEAR_MENU_TOP_APPS_ = "key_linear_menu_top_apps_";
    public static final String KEY_LINEAR_MENU_BOTTOM_APPS_ = "key_linear_menu_bottom_apps_";

    public static final String KEY_BALL_MENU_SELECT_APP_INDEX = "key_ball_menu_select_app_index";

    public static final String KEY_APP_TYPE = "key_app_type";
    public static final String KEY_TOUCH_TYPE = "key_touch_type";

    //自定义
    public static final int DEFAULT_TOUCH_WIDTH_BALL = 25;
    public static final int DEFAULT_TOUCH_WIDTH = 15;
    public static final int DEFAULT_TOUCH_HEIGHT = 240;
    public static final int DEFAULT_VIBRATE_LEVEL = 30;

    public static final int DEFAULT_ALPHA = 150;
    public static final int DEFAULT_THEME = 0;

    public static final int TOUCH_UI_THEME_0 = 0;
    public static final int TOUCH_UI_THEME_1 = 1;

    public static final int TOUCH_UI_DIRECTION_LEFT = 0;
    public static final int TOUCH_UI_DIRECTION_RIGHT = 1;

    public static final int TOUCH_UI_THEME_HIDE_LINE_1 = 0;
    public static final int TOUCH_UI_THEME_HIDE_LINE_2 = 1;
    public static final int TOUCH_UI_THEME_HIDE_RECT = 2;


    public static final String BROADCAST_SHAPE_COLOR_SHETTING = "broadcast_shape_color_shetting";

    public static final String KEY_SHAPE_COLOR_SETTING = "key_shape_color_setting";
    public static final int KEY_SHAPE_COLOR_SETTING_TOP = 1;
    public static final int KEY_SHAPE_COLOR_SETTING_MID = 2;
    public static final int KEY_SHAPE_COLOR_SETTING_BOTTOM = 3;

}
