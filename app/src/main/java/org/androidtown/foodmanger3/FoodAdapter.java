package org.androidtown.foodmanger3;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

public class FoodAdapter extends RecyclerView.Adapter<FoodAdapter.FoodViewHolder> {

    public List<FoodItem> mItems = new ArrayList<FoodItem>();
    private Context mContext;

    public class FoodViewHolder extends RecyclerView.ViewHolder {

        TextView itemName;
        TextView itemRemainday;
        TextView itemDate;
        ImageView itemCategory;

        public FoodViewHolder(View view) {
            super(view);

            itemCategory = view.findViewById(R.id.itemCategory);
            itemName = view.findViewById(R.id.itemName);
            itemRemainday = view.findViewById(R.id.itemD_day);
            itemDate = view.findViewById(R.id.itemDate);
        }
    }

    public FoodAdapter(Context context) {
        mContext = context;
    }

    @Override
    public FoodViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.food_listitem, parent,false);
        return new FoodViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull FoodViewHolder holder, int position) {
        FoodItem item = mItems.get(position);

        holder.itemDate.setText(item.getDate());
        holder.itemRemainday.setText(String.valueOf(item.getDay()));
        holder.itemName.setText(item.getName());
        holder.itemCategory.setImageResource(BasicInfo.image_category[item.getResId()]);

    }

    @Override
    public int getItemCount() {
        return mItems.size();
    }

    public void clear() {
        mItems.clear();
    }

    public void addItem(FoodItem item) {
        mItems.add(item);
    }

    public Object getItem(int i) {
        return mItems.get(i);
    }

    public long getItemId(int i) {
        return i;
    }


}
