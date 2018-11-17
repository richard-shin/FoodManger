package org.androidtown.foodmanger2;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

import java.util.ArrayList;
import java.util.List;

public class FoodAdapter extends BaseAdapter {

    private List<FoodItem> mItems = new ArrayList<FoodItem>();
    private Context mContext;

    public FoodAdapter(Context context) {
        mContext = context;
    }

    @Override
    public int getCount() {
        return mItems.size();
    }

    public void clear() {
        mItems.clear();
    }

    public void addItem(FoodItem item) {
        mItems.add(item);
    }

    @Override
    public Object getItem(int i) {
        return mItems.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup viewGroup) {

        FoodItemView itemView;

        if(convertView == null) {
            itemView = new FoodItemView(mContext);
        }else {
            itemView = (FoodItemView) convertView;
        }

        FoodItem item = mItems.get(position);

        itemView.setDate(item.getDate());
        itemView.setDay(item.getDay());
        itemView.setName(item.getName());
        itemView.setImage(item.getResId());

        return itemView;
    }
}
