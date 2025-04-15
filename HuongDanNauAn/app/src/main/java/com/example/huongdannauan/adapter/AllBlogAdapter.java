package com.example.huongdannauan.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.huongdannauan.R;
import com.example.huongdannauan.model.Blog;
import com.example.huongdannauan.model.Recipe;

import java.util.List;

public class AllBlogAdapter extends RecyclerView.Adapter<AllBlogAdapter.AllBlogViewHolder>{
    private List<Blog> recipeList;
    private Context context;
    private AllBlogAdapter.OnRecipeClickListener listener;
    public interface OnRecipeClickListener {
        void onRecipeClick(int recipeId);
    }

    public AllBlogAdapter(Context context, List<Blog> recipeList, AllBlogAdapter.OnRecipeClickListener listener) {
        this.context = context;
        this.recipeList = recipeList;
        this.listener = listener;
    }

    // Phương thức updateData để cập nhật danh sách


    @NonNull
    @Override
    public AllBlogAdapter.AllBlogViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.blog_item, parent, false);
        return new AllBlogAdapter.AllBlogViewHolder(view);
    }


    @Override
    public void onBindViewHolder(@NonNull AllBlogAdapter.AllBlogViewHolder holder, int position) {
        Blog blog = recipeList.get(position);
        holder.title.setText(blog.getTitle());
        holder.date.setText(blog.getDate());


        // Tải ảnh từ URL vào ImageView (sử dụng thư viện Glide)
        Glide.with(context).load(blog.getImg()).into(holder.image);

        // Gắn sự kiện click vào ViewHolder
        holder.itemView.setOnClickListener(v -> {
            if (listener != null && blog.getId() != null) {
                listener.onRecipeClick(blog.getId());
            }
        });
    }

    @Override
    public int getItemCount() {
        return recipeList.size();
    }

    public static class AllBlogViewHolder extends RecyclerView.ViewHolder {
        TextView title, date;
        ImageView image;

        public AllBlogViewHolder(@NonNull View itemView) {
            super(itemView);
            title = itemView.findViewById(R.id.titleBlog);
            date = itemView.findViewById(R.id.dateBolg);
            image = itemView.findViewById(R.id.imgBlog);
        }
    }
}
