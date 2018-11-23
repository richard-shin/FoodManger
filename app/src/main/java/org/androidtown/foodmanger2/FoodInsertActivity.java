package org.androidtown.foodmanger2;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Adapter;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import org.androidtown.foodmanger2.common.TitleBackgroundButton;
import org.androidtown.foodmanger2.common.TitleBitmapButton;
import org.androidtown.foodmanger2.db.MemoDatabase;

import java.text.ParseException;
import java.util.Calendar;
import java.util.Date;

public class FoodInsertActivity extends AppCompatActivity {

    private static final String TAG="FoodInsertActivity";

    TitleBitmapButton insertSaveBtn, insertCancelBtn, deleteBtn;
    TitleBackgroundButton titleBackgroundBtn;

    EditText editName;
    TextView mCategoryName;
    TextView mInsertDate;
    RadioGroup mRadioGroup;
    Calendar mCalendar = Calendar.getInstance();
    GridView mgridView;
    CategoryAdapter madapter;

    String mFoodMode;

    String mFoodId;
    String mDateStr;
    String mFoodName;
    int mFoodDuration;
    int mFoodResId;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.food_insert_activity);

        editName = findViewById(R.id.foodName);
        mFoodName = editName.getText().toString();

        mCategoryName = findViewById(R.id.category_name);

        titleBackgroundBtn = findViewById(R.id.titleBackgroundBtn);
        deleteBtn = findViewById(R.id.delete_btn);
        deleteBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showDialog(BasicInfo.CONFIRM_DELETE);
            }
        });

        // 그리드뷰 설정
        setGridView();

        // 유통기간 설정
        setRadioButton();

        //추가 및 닫기 버튼 설정
        setBottomButtons();

        // 입력날짜 설정
        setInsertDate();
    }

    @Override
    protected void onStart() {

        // intent 처리
        Intent intent = getIntent();
        mFoodMode = intent.getStringExtra(BasicInfo.KEY_FOOD_MODE);
        if(mFoodMode.equals(BasicInfo.MODE_MODIFY) ||
                mFoodMode.equals(BasicInfo.MODE_VIEW)) {
            processIntent(intent);

            titleBackgroundBtn.setText("FOOD 보기");
            insertSaveBtn.setText("수정");
            deleteBtn.setVisibility(View.VISIBLE);
        }else {
            titleBackgroundBtn.setText("FOOD 추가");
            insertSaveBtn.setText("저장");
            deleteBtn.setVisibility(View.GONE);
        }

        super.onStart();
    }

    public void processIntent(Intent intent) {
        mFoodId = intent.getStringExtra(BasicInfo.KEY_FOOD_ID);
        editName.setText(intent.getStringExtra(BasicInfo.KEY_FOOD_NAME));

        // 등록날짜 설정
        mDateStr = intent.getStringExtra(BasicInfo.KEY_FOOD_DATE);
        String insertDateStr =null;
        try {
            Date insertDate = BasicInfo.dateDayFormat.parse(mDateStr);
            insertDateStr = BasicInfo.dateDayNameFormat.format(insertDate);
        }catch (Exception e) {
            e.printStackTrace();
        }
        mInsertDate.setText(insertDateStr);

        // 유통기간 설정
        mFoodDuration = intent.getIntExtra(BasicInfo.KEY_FOOD_DURATION, 0);
        RadioButton radioButton;
        if(mFoodDuration == 3) {
            radioButton = (RadioButton) mRadioGroup.getChildAt(0);
        }else if(mFoodDuration == 7) {
            radioButton = (RadioButton)mRadioGroup.getChildAt(1);
        }else {
            radioButton = (RadioButton)mRadioGroup.getChildAt(2);
            radioButton.setText(String.valueOf(mFoodDuration) + "일");
        }
        if(radioButton != null) {
            radioButton.setChecked(true);
        }

        // 카테고리 설정
        mFoodResId = intent.getIntExtra(BasicInfo.KEY_FOOD_RESID, 0);
        Resources res = getResources();
        String[] category= res.getStringArray(R.array.array_category);
        mCategoryName.setText(category[mFoodResId]);

    }

    public void setGridView() {

        madapter = new CategoryAdapter(
                getApplicationContext(),
                R.layout.grid_view,
                BasicInfo.image_category);

        mgridView = findViewById(R.id.gridView);
        mgridView.setAdapter(madapter);
        mgridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long l) {
                Log.d(TAG, "selected item: " + position);
                for (int i=0; i <parent.getChildCount(); i++)	{
                    parent.getChildAt(i).setBackgroundColor(Color.TRANSPARENT);
                }
                view.setBackgroundColor(Color.YELLOW);
                mFoodResId = position;

                Resources res = getResources();
                String[] category= res.getStringArray(R.array.array_category);
                mCategoryName.setText(category[mFoodResId]);
            }
        });

    }

    public void setRadioButton() {

         mRadioGroup = (RadioGroup) findViewById(R.id.radioGroup1);
         mRadioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup radioGroup, int checkedId) {
                if(checkedId == R.id.radio3) { // insert number
                    showDialog( BasicInfo.EDIT_DURATION_NUM);
                }else if(checkedId == R.id.radio4) { // select date
                    setDurationDate();
                }else {
                     RadioButton button = findViewById(checkedId);
                     String day= button.getText().toString();
                     int index = day.lastIndexOf("일");
                     String day2 = day.substring(0,index);
                     mFoodDuration = Integer.parseInt(day2);
                     Log.d(TAG, "radioButton value: " + mFoodDuration);
                }
            }
        });

    }

    protected Dialog onCreateDialog(int id) {
        AlertDialog.Builder builder = null;
        final View view;

        switch (id) {

            case BasicInfo.EDIT_DURATION_NUM:
                builder = new AlertDialog.Builder(this);

                LayoutInflater inflater = (LayoutInflater)
                        this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                view =  inflater.inflate(R.layout.dialog_circulation_day, null);
                builder.setView(view);
                builder.setTitle("유통기간 설정");
                builder.setPositiveButton("확인", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        EditText editDay = view.findViewById(R.id.editDay);

                        String foodDayStr = editDay.getText().toString();
                        mFoodDuration = Integer.parseInt(foodDayStr);
                        Log.d(TAG, "직접입력 유통기간: " + mFoodDuration);

                        RadioButton radioButton = findViewById(R.id.radio3);
                        radioButton.setText(String.valueOf(mFoodDuration) + "일");
                    }
                });

                builder.setNegativeButton("취소", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        Log.d(TAG, "취소 되었습니다.");
                    }
                });
                break;

            case BasicInfo.CONFIRM_TEXT_INPUT:
                builder = new AlertDialog.Builder(this);
                builder.setTitle("재료이름");
                builder.setMessage("재료이름을 입력하세요");
                builder.setPositiveButton("확인", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {

                    }
                });
                break;

             case BasicInfo.CONFIRM_DELETE:
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
                 break;

            default:
                break;

        }
        return builder.create();
    }

    public void setDurationDate() {
        Calendar calendar = Calendar.getInstance();
        Date date = new Date();
        try {
            date = BasicInfo.dateDayNameFormat.parse(mDateStr);
        }catch (Exception e) {
            Log.d(TAG, "Exception in parsing date : " + date);
        }

        calendar.setTime(date);

        new DatePickerDialog(FoodInsertActivity.this,
                dateSetListener,
                calendar.get(Calendar.YEAR),
                calendar.get(Calendar.MONTH),
                calendar.get(Calendar.DAY_OF_MONTH)).show();

    }

    DatePickerDialog.OnDateSetListener dateSetListener = new DatePickerDialog.OnDateSetListener() {
        @Override
        public void onDateSet(DatePicker datePicker, int year, int month, int day) {
            String dateStr = year + "-" + (month+1) + "-" + day;
            if (dateStr.length() > 10) {
                dateStr = dateStr.substring(0, 10);
            }

            RadioButton radioButton = findViewById(R.id.radio4);
            radioButton.setText(dateStr);

            Date currentTime = new Date ();
            String today = BasicInfo.dateDayFormat.format(currentTime);

            int days = calDate(dateStr, today);
            Log.d(TAG, "날짜지정 후 duration: " + days);

            mFoodDuration = days;

        }
    };

    public int calDate(String date1, String date2) {

        long calDateDays = 0;

        try{
            Date firstDate =  BasicInfo.dateDayFormat.parse(date1);
            Date secondDate = BasicInfo.dateDayFormat.parse(date2);

            long calDate = firstDate.getTime() - secondDate.getTime();

            calDateDays = calDate / (24*60*60*1000);

        }catch (ParseException e){
            e.printStackTrace();
        }

        return (int) calDateDays;
    }


    public void setInsertDate() {


        mInsertDate = findViewById(R.id.insert_date);

        Date curDate = new Date(); // 현재 날짜
        mCalendar.setTime(curDate);

        int year = mCalendar.get(Calendar.YEAR);
        int month =mCalendar.get(Calendar.MONTH);
        int dayofMonth = mCalendar.get(Calendar.DAY_OF_MONTH);

        String date = year + "년" + (month+1) + "월" + dayofMonth + "일";
        mInsertDate.setText(date);
    }

    public void setBottomButtons() {

        insertSaveBtn = findViewById(R.id.insert_btn);
        insertCancelBtn = findViewById(R.id.close_btn);

        insertSaveBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                boolean isParsed = parseValues();
                if(isParsed) {
                    if(mFoodMode.equals(BasicInfo.MODE_INSERT)){
                        saveInput();
                    }else if(mFoodMode.equals(BasicInfo.MODE_MODIFY)){
                        modifyInput();
                    }

                }
            }
        });


        insertCancelBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });


    }

    class CategoryAdapter extends BaseAdapter {

        Context context;
        int layout;
        int img[];
        LayoutInflater inflater;

        public CategoryAdapter(Context context, int layout, int[] img) {
            this.context = context;
            this.layout = layout;
            this.img = img;
            inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        }

        @Override
        public int getCount() {
            return img.length;
        }

        @Override
        public Object getItem(int i) {
            return img[i];
        }

        @Override
        public long getItemId(int i) {
            return i;
        }

        @Override
        public View getView(int position, View view, ViewGroup viewGroup) {
            if(view == null)
                view = inflater.inflate(layout, null);
            ImageView imageView = (ImageView) view.findViewById(R.id.imageView1);
            imageView.setImageResource(img[position]);

            return view;
        }
    }

    // 입력 날짜 저장 및 메모 확인
    private boolean parseValues() {

        // 입력날짜 저장하기
        String insertDateStr = mInsertDate.getText().toString();
        try {
            Date insertDate = BasicInfo.dateDayNameFormat.parse(insertDateStr);
            mDateStr = BasicInfo.dateDayFormat.format(insertDate);
        } catch (ParseException e) {
            Log.e(TAG, "Exception in parsing date : " + insertDateStr);
        }

        // Food name 저장하기
        mFoodName = editName.getText().toString();

        if(mFoodName.trim().length() < 1) {
            showDialog(BasicInfo.CONFIRM_TEXT_INPUT);
            return false;
        }

        return true;
    }

    // 데이터베이스에 Food record 추가
    private void saveInput() {


        String SQL = "insert into " + MemoDatabase.TABLE_MEMO +
                "(INPUT_DATE, FOOD_NAME, ID_RES, FOOD_DAY) values(" +
                "DATETIME('" + mDateStr + "'), " +
                "'" + mFoodName + "', " +
                "'" + mFoodResId + "' , " +
                "'"+ mFoodDuration + "')";
        if(MainActivity.mDatabase !=null) {
            MainActivity.mDatabase.execSQL(SQL);
            Log.d(TAG, " new [ " + mFoodName + " ] food inserted");
        }

        Intent intent = getIntent();
        setResult(RESULT_OK, intent);
        finish();
    }

    // 데이터베이스에 Food record 수정
    public void modifyInput() {

        String SQL = "update " + MemoDatabase.TABLE_MEMO +
                " set " +
                " INPUT_DATE = DATETIME('" + mDateStr + "')," +
                " FOOD_NAME = '" + mFoodName + "', " +
                " ID_RES = '" + mFoodResId + "' , " +
                " FOOD_DAY = '"+ mFoodDuration + "'" +
                "  where _id = '" + mFoodId + "'";
        Log.d(TAG, "SQL : " + SQL);
        if(MainActivity.mDatabase != null) {
            MainActivity.mDatabase.execSQL(SQL);
        }

        Intent intent = getIntent();
        intent.putExtra(BasicInfo.KEY_FOOD_DATE, mDateStr);
        intent.putExtra(BasicInfo.KEY_FOOD_NAME, mFoodName);
        intent.putExtra(BasicInfo.KEY_FOOD_DURATION, mFoodDuration);
        intent.putExtra(BasicInfo.KEY_FOOD_RESID, mFoodResId);

        setResult(RESULT_OK, intent);
        finish();


    }

    public void deleteFood() {

        // delete food record
        String SQL = "delete from " + MemoDatabase.TABLE_MEMO +
                " where _id = '" + mFoodId + "'";
        Log.d(TAG, "SQL : " + SQL);
        if(MainActivity.mDatabase !=null) {
            MainActivity.mDatabase.execSQL(SQL);
        }

        setResult(RESULT_OK);

        finish();


    }




}
