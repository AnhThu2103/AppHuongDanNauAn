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
import com.example.huongdannauan.model.Blog;
import com.example.huongdannauan.model.Recipe;
import com.squareup.picasso.Picasso;

import java.util.List;

public class BlogAdapter extends RecyclerView.Adapter<BlogAdapter.BlogViewHolder>{
    private List<Blog> blogList;
    private Context context;
    private BlogAdapter.OnRecipeClickListener listener;

    public interface OnRecipeClickListener {
        void onRecipeClick(int recipeId);
    }

    public BlogAdapter(List<Blog> blogList, Context context, BlogAdapter.OnRecipeClickListener listener) {
        this.blogList = blogList;
        this.context = context;
        this.listener = listener;
    }

    @NonNull
    @Override
    public BlogAdapter.BlogViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.blog_item, parent, false);
        return new BlogAdapter.BlogViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull BlogAdapter.BlogViewHolder holder, int position) {
        Blog blog = blogList.get(position);
        holder.title.setText(blog.getTitle());
        holder.date.setText(blog.getDate());

        // Sử dụng Picasso hoặc Glide để load hình ảnh từ URL
        if (blog.getImg() != null && !blog.getImg().isEmpty()) {
            Picasso.get().load(blog.getImg()).into(holder.img);
        } else {
            holder.img.setImageResource(R.drawable.icon_loading);
        }

        // Gắn sự kiện click vào ViewHolder
        holder.itemView.setOnClickListener(v -> {
            if (listener != null && blog.getId() != null) {
                listener.onRecipeClick(blog.getId());
            }
        });
    }

    @Override
    public int getItemCount() {
        return blogList.size();
    }

    public static class BlogViewHolder extends RecyclerView.ViewHolder {
        ImageView img;
        TextView title;
        TextView date;

        public BlogViewHolder(@NonNull View itemView) {
            super(itemView);
            img = itemView.findViewById(R.id.imgBlog);
            title = itemView.findViewById(R.id.titleBlog);
            date = itemView.findViewById(R.id.dateBolg);
        }
    }
}
