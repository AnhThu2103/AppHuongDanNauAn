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

public class FoodCategoryAdapter extends RecyclerView.Adapter<FoodCategoryAdapter.FoodCategoryViewHolder> {

    private List<FoodCategory> foodCategoryList;
    private OnFoodCategoryClickListener listener;  // Interface listener

    // Interface để xử lý sự kiện click
    public interface OnFoodCategoryClickListener {
        void onFoodCategoryClick(String foodCategoryName);
    }

    public FoodCategoryAdapter(List<FoodCategory> foodCategoryList, OnFoodCategoryClickListener listener) {
        this.foodCategoryList = foodCategoryList;
        this.listener = listener;
    }


    @NonNull
    @Override
    public FoodCategoryViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_food_category, parent, false);
        return new FoodCategoryViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull FoodCategoryViewHolder holder, int position) {
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

    static class FoodCategoryViewHolder extends RecyclerView.ViewHolder {
        TextView foodName;
        ImageView foodImage;

        public FoodCategoryViewHolder(@NonNull View itemView) {
            super(itemView);
            foodName = itemView.findViewById(R.id.tvFoodName);
            foodImage = itemView.findViewById(R.id.ivFoodImage);
        }
    }
}

