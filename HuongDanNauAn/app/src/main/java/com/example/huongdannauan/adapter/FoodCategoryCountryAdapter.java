package com.example.huongdannauan.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.huongdannauan.R;
import com.example.huongdannauan.model.FoodCategory;

import java.util.List;

public class FoodCategoryCountryAdapter extends RecyclerView.Adapter<FoodCategoryCountryAdapter.FoodCategoryCountryViewHolder> {

    private List<FoodCategory> foodCategoryList;
    private OnFoodCategoryClickListener listener;  // Interface listener

    // Interface để xử lý sự kiện click
    public interface OnFoodCategoryClickListener {
        void onFoodCategoryClick(String foodCategoryName);
    }

    public FoodCategoryCountryAdapter(List<FoodCategory> foodCategoryList, OnFoodCategoryClickListener listener) {
        this.foodCategoryList = foodCategoryList;
        this.listener = listener;
    }


    @NonNull
    @Override
    public FoodCategoryCountryViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_food_category_country, parent, false);
        return new FoodCategoryCountryViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull FoodCategoryCountryViewHolder holder, int position) {
        FoodCategory category = foodCategoryList.get(position);
        holder.foodName.setText(category.getName());
        Glide.with(holder.itemView.getContext())
                .load(category.getImageResId())  // Đây là URL của hình ảnh
                .into(holder.foodImage);

        // Gắn sự kiện click vào item
        holder.itemView.setOnClickListener(v -> {
            if (listener != null) {
                listener.onFoodCategoryClick(category.getName());
            }
        });
    }

    @Override
    public int getItemCount() {
        return foodCategoryList.size();
    }

    static class FoodCategoryCountryViewHolder extends RecyclerView.ViewHolder {
        TextView foodName;
        ImageView foodImage;

        public FoodCategoryCountryViewHolder(@NonNull View itemView) {
            super(itemView);
            foodName = itemView.findViewById(R.id.tvFoodName);
            foodImage = itemView.findViewById(R.id.ivFoodImage);
        }
    }
}

