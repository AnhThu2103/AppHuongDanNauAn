package com.example.huongdannauan.adapter;

import android.app.AlertDialog;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.huongdannauan.R;
import com.example.huongdannauan.fragment.DangNhapFragment;
import com.example.huongdannauan.model.Recipe;
import com.example.huongdannauan.model.TrangThai;

import java.util.List;

public class AllRecipeAdapter extends RecyclerView.Adapter<AllRecipeAdapter.AllRecipeViewHolder> {
    private List<Recipe> recipeList;
    private Context context;
    private RecipeAdapter.OnRecipeClickListener listener;
    public interface OnRecipeClickListener {
        void onRecipeClick(int recipeId);
    }

    public AllRecipeAdapter(Context context, List<Recipe> recipeList, RecipeAdapter.OnRecipeClickListener listener) {
        this.context = context;
        this.recipeList = recipeList;
        this.listener = listener;
    }

    // Phương thức updateData để cập nhật danh sách


    @NonNull
    @Override
    public AllRecipeViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.recipe_all_item, parent, false);
        return new AllRecipeViewHolder(view);
    }


    @Override
    public void onBindViewHolder(@NonNull AllRecipeViewHolder holder, int position) {
        Recipe recipe = recipeList.get(position);
        holder.title.setText(recipe.getTitle());
        holder.healthScore.setText("Điểm sức khỏe: "+recipe.getHealthScore());

        // Hiển thị danh sách loại món ăn và vùng miền dưới dạng chuỗi
        if(recipe.getDishTypes()!=null)
            holder.dishTypes.setText(String.join(", ", recipe.getDishTypes()));
        if(recipe.getCuisines()!=null)
            holder.cuisines.setText(String.join(", ", recipe.getCuisines()));

        // Tải ảnh từ URL vào ImageView (sử dụng thư viện Glide)
        Glide.with(context).load(recipe.getImage()).into(holder.image);

        // Gắn sự kiện click vào ViewHolder
        holder.itemView.setOnClickListener(v -> {
            if (listener != null && recipe.getId() != null) {
                listener.onRecipeClick(recipe.getId());
            }
        });
    }

    @Override
    public int getItemCount() {
        return recipeList.size();
    }

    public static class AllRecipeViewHolder extends RecyclerView.ViewHolder {
        TextView title, healthScore, dishTypes, cuisines;
        ImageView image;

        public AllRecipeViewHolder(@NonNull View itemView) {
            super(itemView);
            title = itemView.findViewById(R.id.recipe_title);
            healthScore = itemView.findViewById(R.id.recipe_healthScore);
            dishTypes = itemView.findViewById(R.id.recipe_dishTypes);
            cuisines = itemView.findViewById(R.id.recipe_cuisines);
            image = itemView.findViewById(R.id.recipe_image);
        }
    }

}