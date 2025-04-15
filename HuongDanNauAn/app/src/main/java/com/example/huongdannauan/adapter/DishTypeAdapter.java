package com.example.huongdannauan.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.huongdannauan.R;
import com.example.huongdannauan.model.DishType;
import com.squareup.picasso.Picasso;

import java.util.List;

public class DishTypeAdapter extends RecyclerView.Adapter<DishTypeAdapter.LoaiViewHolder> {
    private List<DishType> dishTypeList;
    private Context context;
    private OnRecipeClickListener listener;

    public interface OnRecipeClickListener {
        void onRecipeClick(String dishTypeName);
    }

    public DishTypeAdapter(List<DishType> dishTypeList, Context context, OnRecipeClickListener listener) {
        this.dishTypeList = dishTypeList;
        this.context = context;
        this.listener = listener;
    }

    @NonNull
    @Override
    public LoaiViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.dishtype_item, parent, false);
        return new LoaiViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull LoaiViewHolder holder, int position) {
        DishType recipe = dishTypeList.get(position);
        holder.textViewTitle.setText(recipe.getName());

        // Sử dụng Picasso hoặc Glide để load hình ảnh từ URL
        if (recipe.getImage() != null && !recipe.getImage().isEmpty()) {
            Picasso.get().load(recipe.getImage()).into(holder.imageViewLoai);
        } else {
            holder.imageViewLoai.setImageResource(R.drawable.icon_loading);
        }

        // Gắn sự kiện click vào ViewHolder
        holder.itemView.setOnClickListener(v -> {
            if (listener != null && recipe.getName() != null) {
                listener.onRecipeClick(recipe.getName());
            }
        });
    }

    @Override
    public int getItemCount() {
        return dishTypeList.size();
    }

    public static class LoaiViewHolder extends RecyclerView.ViewHolder {
        ImageView imageViewLoai;
        TextView textViewTitle;

        public LoaiViewHolder(@NonNull View itemView) {
            super(itemView);
            imageViewLoai = itemView.findViewById(R.id.imageViewLoai);
            textViewTitle = itemView.findViewById(R.id.textViewName);
        }
    }
}