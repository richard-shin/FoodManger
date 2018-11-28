package org.androidtown.foodmanger3;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.content.res.Resources;
import android.database.Cursor;
import android.graphics.Canvas;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.Toast;

import org.androidtown.foodmanger3.common.TitleBitmapButton;
import org.androidtown.foodmanger3.db.MemoDatabase;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;


/* author: ssh */

public class MainActivity extends AppCompatActivity {

    private static final String TAG="MainActivity";

    ListView listView;
    FoodAdapter mFoodListadapter;
    ImageButton insertBtn;
    TextView greenText, yellowText, redText;

    SwipeController swipeController = null;

    public static MemoDatabase mDatabase=null;

    String mFoodId;

    int mGreenNum = 0;
    int mYellowNum = 0;
    int mRedNum = 0;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // 디바이스 내장 스토리지 상태 체크 후 경로 설정
        if (!Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
            Toast.makeText(this, "내장 스토리지를 사용할 수 없습니다.", Toast.LENGTH_LONG).show();
            return;
        } else {
            String externalPath = Environment.getExternalStorageDirectory().getAbsolutePath();
            if (!BasicInfo.ExternalChecked && externalPath != null) {
                BasicInfo.ExternalPath = externalPath;
                Log.d(TAG, "ExternalPath : " + BasicInfo.ExternalPath);

                BasicInfo.DATABASE_NAME = BasicInfo.ExternalPath + BasicInfo.DATABASE_NAME;
                BasicInfo.ExternalChecked = true;
            }
        }


        greenText = (TextView) findViewById(R.id.green_text);
        yellowText = (TextView) findViewById(R.id.yellow_text);
        redText = (TextView) findViewById(R.id.red_text);

        // RecyclerView 설정
        setupRecyclerView();

        // 추가 버튼 설정
        insertBtn = findViewById(R.id.newFoodBtn);
        insertBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.d(TAG, "new Food Button clicked");

                Intent intent = new Intent(getApplicationContext(), FoodInsertActivity.class);
                intent.putExtra(BasicInfo.KEY_FOOD_MODE, BasicInfo.MODE_INSERT);
                startActivityForResult(intent, BasicInfo.REQ_INSERT_ACTIVITY);
            }
        });
    }

    @Override
    protected void onStart() {
        openDatabase();
        loadFoodListData();
        setStats();
        setBadge(getApplicationContext(), mRedNum);

       super.onStart();
    }

    @Override
    protected void onStop() {
        super.onStop();
    }

    public void openDatabase() {
        if(mDatabase !=null) {
            mDatabase.close();
            mDatabase = null;
        }

        mDatabase = MemoDatabase.getInstance(this);
        boolean isOpen = mDatabase.open();
        if(isOpen) {
            Log.d(TAG, "Food database is open");
        }else {
            Log.d(TAG, "Food database is not open");
        }

    }

    private void setupRecyclerView() {

        RecyclerView recyclerView = findViewById(R.id.foodList);
        mFoodListadapter = new FoodAdapter(getApplicationContext());

        recyclerView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
        recyclerView.setAdapter(mFoodListadapter);


        swipeController = new SwipeController(new SwipeControllerActions() {
            @Override
            public void onRightClicked(int position) {
                FoodItem item =  mFoodListadapter.mItems.get(position);
                mFoodId = item.getId();
                showDialog(BasicInfo.CONFIRM_DELETE);

            }

            @Override
            public void onLeftClicked(int position) {
                viewFood(position);

            }
        });

        ItemTouchHelper itemTouchHelper = new ItemTouchHelper(swipeController);
        itemTouchHelper.attachToRecyclerView(recyclerView);

        recyclerView.addItemDecoration(new RecyclerView.ItemDecoration() {
            @Override
            public void onDraw(Canvas c, RecyclerView parent, RecyclerView.State state) {
                swipeController.onDraw(c);
            }
        });

    }

    public void viewFood(int position) {
        Toast.makeText(getApplicationContext(), "Selected Item: " + position, Toast.LENGTH_LONG).show();

        FoodItem item =(FoodItem)mFoodListadapter.getItem(position);

        Intent intent = new Intent(getApplicationContext(), FoodInsertActivity.class);
        intent.putExtra(BasicInfo.KEY_FOOD_MODE, BasicInfo.MODE_MODIFY);

        intent.putExtra(BasicInfo.KEY_FOOD_ID,    item.getId());
        intent.putExtra(BasicInfo.KEY_FOOD_DATE,  item.getDate());
        intent.putExtra(BasicInfo.KEY_FOOD_NAME,  item.getName());
        intent.putExtra(BasicInfo.KEY_FOOD_DURATION,   item.getDuration());
        intent.putExtra(BasicInfo.KEY_FOOD_RESID, item.getResId());

        startActivityForResult(intent, BasicInfo.REQ_VIEW_ACTIVITY);

    }


    public int loadFoodListData() {
        String SQL = "select  _id, INPUT_DATE, FOOD_NAME, ID_RES, FOOD_DAY from  FOOD  order by INPUT_DATE  desc";
        int recordCount = -1;
        if(mDatabase !=null) {
            Cursor outCursor = mDatabase.rawQuery(SQL);

            recordCount = outCursor.getCount();
            Log.d(TAG, "cursor count: " + recordCount + "in loadFoodListData().\n");

            mFoodListadapter.clear();
            Resources res = getResources();

            // 통계값 초기화
            mRedNum=0;
            mYellowNum = 0;
            mGreenNum = 0;

            for(int i=0; i < recordCount; i++) {
                outCursor.moveToNext();

                String foodId = outCursor.getString(0);
                String dateStr = outCursor.getString(1);
                if (dateStr.length() > 10) {
                    dateStr = dateStr.substring(0, 10);
                }
                String foodName = outCursor.getString(2);
                int resId = outCursor.getInt(3);
                int foodduration = outCursor.getInt(4);
                Log.d(TAG, "duration day: " + foodduration);

                Date currentTime = new Date ();
                String today = BasicInfo.dateDayFormat.format(currentTime);
                int days = calDate(today, dateStr);
                int calDateDays = foodduration - days;
                Log.d(TAG, "remain day: " + calDateDays);

                mFoodListadapter.addItem(new FoodItem(foodId, foodName, dateStr,
                        foodduration, resId, calDateDays));

                calStats(calDateDays);

            }
            outCursor.close();
            setStats();
            mFoodListadapter.notifyDataSetChanged();
        }

        return recordCount;

    }

    protected Dialog onCreateDialog(int id) {
        AlertDialog.Builder builder = null;

        if(id == BasicInfo.CONFIRM_DELETE) {

            builder = new AlertDialog.Builder(this);
            builder.setTitle("FOOD");
            builder.setMessage("정말로 삭제하시겠습니까?");
            builder.setPositiveButton("예", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    deleteFood();
                }
            });

            builder.setNegativeButton("아니오", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    dismissDialog(BasicInfo.CONFIRM_DELETE);
                }
            });
        }
        return builder.create();
    }

    public void deleteFood() {

        // delete food record
        String SQL = "delete from " + MemoDatabase.TABLE_MEMO +
                " where _id = '" + mFoodId + "'";
        Log.d(TAG, "SQL : " + SQL);
        if(mDatabase !=null) {
            mDatabase.execSQL(SQL);
        }

        loadFoodListData();

    }

    // 신선도 통계 계산
    public void calStats(int remainday) {

        if(remainday < 0) {   // 기간 초과
            mRedNum = mRedNum + 1;
        }else if(remainday >=0 && remainday < 2) { // 0~1일 사이
            mYellowNum = mYellowNum + 1;
        }else if(remainday >= 2) {  // 2일 이상
            mGreenNum = mGreenNum + 1;
        }
        Log.d(TAG, " stats: " + mGreenNum + "," + mYellowNum + "," + mRedNum);

    }

    // 신선도 통계 표시
    public void setStats() {
        greenText.setText(String.valueOf(mGreenNum));
        yellowText.setText(String.valueOf(mYellowNum));
        redText.setText(String.valueOf(mRedNum));
    }

    public int calDate(String date1, String date2) {

        long calDateDays = 0;

        try{
           Date firstDate =  BasicInfo.dateDayFormat.parse(date1);
           Date secondDate = BasicInfo.dateDayFormat.parse(date2);

           long calDate = firstDate.getTime() - secondDate.getTime();

           calDateDays = calDate / (24*60*60*1000);

//           Log.d(TAG, "두 날짜의 날짜 차이: " + calDateDays);

        }catch (ParseException e){
            e.printStackTrace();
        }

        return (int) calDateDays;
    }


    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        switch (requestCode) {
            case BasicInfo.REQ_INSERT_ACTIVITY:
                if(resultCode == RESULT_OK) {
                    loadFoodListData();
                }
                break;

            case BasicInfo.REQ_VIEW_ACTIVITY:
                loadFoodListData();

                break;
        }

    }


    // app icon badge function
    public static void setBadge(Context context, int count) {
        String launcherClassName = getLauncherClassName(context);
        if (launcherClassName == null) {
            return;
        }
        Intent intent = new Intent("android.intent.action.BADGE_COUNT_UPDATE");
        intent.putExtra("badge_count", count);
        intent.putExtra("badge_count_package_name", context.getPackageName());
        intent.putExtra("badge_count_class_name", launcherClassName);
        context.sendBroadcast(intent);
    }

    public static String getLauncherClassName(Context context) {

        PackageManager pm = context.getPackageManager();

        Intent intent = new Intent(Intent.ACTION_MAIN);
        intent.addCategory(Intent.CATEGORY_LAUNCHER);

        List<ResolveInfo> resolveInfos = pm.queryIntentActivities(intent, 0);
        for (ResolveInfo resolveInfo : resolveInfos) {
            String pkgName = resolveInfo.activityInfo.applicationInfo.packageName;
            if (pkgName.equalsIgnoreCase(context.getPackageName())) {
                String className = resolveInfo.activityInfo.name;
                return className;
            }
        }
        return null;
    }
}
