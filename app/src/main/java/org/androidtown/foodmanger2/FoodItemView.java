package org.androidtown.foodmanger2;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

public class FoodItemView extends LinearLayout {

    public static final String TAG = "FoodItemView";

    TextView itemName;
    TextView itemRemainday;
    TextView itemDate;
    ImageView itemCategory;


    public FoodItemView(Context context) {
        super(context);

        // View 초기화
        init(context);
    }

    public void init(Context context) {
        LayoutInflater inflater = (LayoutInflater)
                context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        inflater.inflate(R.layout.food_listitem, this, true);

        itemCategory = findViewById(R.id.itemCategory);
        itemName = findViewById(R.id.itemName);
        itemRemainday = findViewById(R.id.itemD_day);
        itemDate = findViewById(R.id.itemDate);
    }

    public void setName(String name) {
        itemName.setText(name);
    }

    public void setDay(int remainDay) {
        itemRemainday.setText(String.valueOf(remainDay));
    }

    public void setDate(String date) {
        itemDate.setText(date);
    }

    public void setImage(int resId) {
        itemCategory.setImageResource(BasicInfo.image_category[resId]);
    }


}
