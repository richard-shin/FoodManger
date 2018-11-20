package org.androidtown.foodmanger2;

import java.text.SimpleDateFormat;

public class BasicInfo {

        /**
         * 외장 메모리 패스
         */
        public static String ExternalPath = "/sdcard/";

        /**
         * 외장 메모리 패스 체크 여부
         */
        public static boolean ExternalChecked = false;


        /**
         * 데이터베이스 이름
         */
        public static String DATABASE_NAME = "/FoodManger/food.db";

        public static int image_category[] = { R.drawable.vegetables_category, R.drawable.fruit_category,
            R.drawable.fish_category, R.drawable.meat_category,
            R.drawable.sausage_category, R.drawable.seasoning_category,
            R.drawable.milk_category, R.drawable.jam_category };


        //========== 인텐트 부가정보 전달을 위한 키값 ==========//
        public static final String KEY_FOOD_MODE = "FOOD_MODE";
        public static final String KEY_FOOD_NAME = "FOOD_NAME";
        public static final String KEY_FOOD_ID = "FOOD_ID";
        public static final String KEY_FOOD_DATE = "FOOD_DATE";
        public static final String KEY_FOOD_DURATION = "FOOD_DURATION";
        public static final String KEY_FOOD_RESID = "FOOD_RESID";


        //========== 날짜 포맷  ==========//
        public static SimpleDateFormat dateDayNameFormat = new SimpleDateFormat("yyyy년MM월dd일");
        public static SimpleDateFormat dateDayFormat = new SimpleDateFormat("yyyy-MM-dd");

        //========== 메모 모드 상수 ==========//
        public static final String MODE_INSERT = "MODE_INSERT";
        public static final String MODE_MODIFY = "MODE_MODIFY";
        public static final String MODE_VIEW = "MODE_VIEW";

        //========== 액티비티 요청 코드  ==========//
        public static final int REQ_VIEW_ACTIVITY = 1001;
        public static final int REQ_INSERT_ACTIVITY = 1002;

        //========== 대화상자 키값  ==========//
        public static final int CONFIRM_DELETE = 3001;
        public static final int CONFIRM_TEXT_INPUT = 3002;
        public static final int EDIT_DURATION_NUM = 3003;
        public static final int EDIT_DURATION_DATE = 3004;




}
