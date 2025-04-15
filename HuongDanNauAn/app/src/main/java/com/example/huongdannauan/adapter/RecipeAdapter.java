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
import com.example.huongdannauan.model.Recipe;
import com.squareup.picasso.Picasso;

import java.util.List;

public class RecipeAdapter extends RecyclerView.Adapter<RecipeAdapter.RecipeViewHolder> {
    private List<Recipe> recipeList;
    private Context context;
    private OnRecipeClickListener listener;

    public interface OnRecipeClickListener {
        void onRecipeClick(int recipeId);
    }

    public RecipeAdapter(List<Recipe> recipeList, Context context, OnRecipeClickListener listener) {
        this.recipeList = recipeList;
        this.context = context;
        this.listener = listener;
    }

    @NonNull
    @Override
    public RecipeViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.recipe_item, parent, false);
        return new RecipeViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RecipeViewHolder holder, int position) {
        Recipe recipe = recipeList.get(position);
        holder.textViewTitle.setText(recipe.getTitle());

        // Sử dụng Picasso hoặc Glide để load hình ảnh từ URL
        if (recipe.getImage() != null && !recipe.getImage().isEmpty()) {
            Picasso.get().load(recipe.getImage()).into(holder.imageViewRecipe);
        } else {
            holder.imageViewRecipe.setImageResource(R.drawable.icon_loading);
        }

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

    public static class RecipeViewHolder extends RecyclerView.ViewHolder {
        ImageView imageViewRecipe;
        TextView textViewTitle;

        public RecipeViewHolder(@NonNull View itemView) {
            super(itemView);
            imageViewRecipe = itemView.findViewById(R.id.imageViewRecipe);
            textViewTitle = itemView.findViewById(R.id.textViewTitle);
        }
    }
}
