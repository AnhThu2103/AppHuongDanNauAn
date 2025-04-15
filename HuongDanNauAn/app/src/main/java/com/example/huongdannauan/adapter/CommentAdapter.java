package com.example.huongdannauan.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.RecyclerView;

import com.example.huongdannauan.R;
import com.example.huongdannauan.fragment.ChiTietMonAnFragment;
import com.example.huongdannauan.fragment.DangNhapFragment;
import com.example.huongdannauan.model.Comment;
import com.example.huongdannauan.model.Reply;
import com.example.huongdannauan.model.TrangThai;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

public class CommentAdapter extends RecyclerView.Adapter<CommentAdapter.CommentViewHolder> {

    private List<Comment> commentList;
    private Context context;
    private DatabaseReference databaseReference;
    private FragmentManager fragmentManager;

    public CommentAdapter(List<Comment> commentList, Context context, FragmentManager fragmentManager) {
        this.commentList = commentList;
        this.context = context;
        this.databaseReference = FirebaseDatabase.getInstance().getReference("comments");
        this.fragmentManager = fragmentManager;
    }

    @NonNull
    @Override
    public CommentViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.comment_item, parent, false);
        return new CommentViewHolder(view);
    }

//    private void fetchAvatarFromFirebase(String email, CommentViewHolder holder) {
//        DatabaseReference userRef = FirebaseDatabase.getInstance().getReference("users");
//
//        userRef.get().addOnSuccessListener(dataSnapshot -> {
//            if (dataSnapshot.exists()) {
//                for (DataSnapshot userSnapshot : dataSnapshot.getChildren()) {
//                    String firebaseKey = userSnapshot.getKey();
//                    if (firebaseKey != null && firebaseKey.equals(email)) { // So sánh key với email
//                        String avatarUrl = userSnapshot.child("avatar").getValue(String.class);
//                        if (avatarUrl != null && !avatarUrl.isEmpty()) {
//                            Picasso.get()
//                                    .load(avatarUrl)
//                                    .placeholder(R.drawable.icon_loading)
//                                    .error(R.drawable.icon_error)
//                                    .into(holder.avatarImageView);
//                        } else {
//                            Picasso.get()
//                                    .load(R.drawable.icon_loading)
//                                    .into(holder.avatarImageView);
//                        }
//                        break; // Nếu tìm thấy, thoát vòng lặp
//                    }
//                }
//            }
//        }).addOnFailureListener(e -> {
//            Toast.makeText(context, "Không thể lấy thông tin avatar", Toast.LENGTH_SHORT).show();
//        });
//    }

    @Override
    @SuppressLint({"MissingInflatedId", "LocalSuppress"})
    public void onBindViewHolder(@NonNull CommentViewHolder holder, int position) {
        Comment comment = commentList.get(position);

        // Gán dữ liệu bình luận
        holder.userNameTextView.setText(comment.getUserEmail());
        holder.dateTextView.setText(comment.getDate());
        holder.contentTextView.setText(comment.getContent());
        holder.likesTextView.setText(String.valueOf(comment.getLikes()));
//        fetchAvatarFromFirebase(TrangThai.userEmail, holder);
        if(comment.getSentiment()!=null) {
            if (comment.getSentiment().equals("Positive")) {
                holder.avatarImageView.setImageResource(R.drawable.icon_positive);
            } else if (comment.getSentiment().equals("Negative")) {
                holder.avatarImageView.setImageResource(R.drawable.icon_negative);
            } else {
                holder.avatarImageView.setImageResource(R.drawable.icon_matcuoi);
            }
        } else {
            holder.avatarImageView.setImageResource(R.drawable.icon_matcuoi);
        }

        // Xử lý sự kiện khi nhấn Like
        holder.likeTextView.setOnClickListener(v -> {
            if(holder.isLike){
                int newLikes = comment.getLikes() - 1;
                if(newLikes<0) newLikes=0;
                comment.setLikes(newLikes);
                databaseReference.child(ChiTietMonAnFragment.ID_MONAN).child(comment.getCommentId()).child("likes").setValue(newLikes);
                holder.likesTextView.setText(String.valueOf(newLikes));
                holder.isLike=false;
                holder.likeTextView.setText("Yêu thích");
                holder.likeTextView.setTextColor(Color.GRAY);
            } else {
                int newLikes = comment.getLikes() + 1;
                comment.setLikes(newLikes);
                databaseReference.child(ChiTietMonAnFragment.ID_MONAN).child(comment.getCommentId()).child("likes").setValue(newLikes);
                holder.likesTextView.setText(String.valueOf(newLikes));
                holder.isLike=true;
                holder.likeTextView.setText("Đã thích");
                holder.likeTextView.setTextColor(Color.RED);
            }
        });
        // Xử lý sự kiện khi nhấn nút trả lời
        holder.replyTextView.setOnClickListener(v -> {
            if(TrangThai.userEmail.isEmpty() || TrangThai.userEmail==null){
                Toast.makeText(context, "Dang nhap de tra loi", Toast.LENGTH_SHORT).show();

                Bundle args = new Bundle();
                args.putString("return_fragment", "ChiTietMonAnFragment");
                args.putString("idmonan", ChiTietMonAnFragment.ID_MONAN);
                DangNhapFragment loginFragment = new DangNhapFragment();
                loginFragment.setArguments(args);

                FragmentTransaction transaction = fragmentManager.beginTransaction();
                transaction.replace(R.id.fragment_container, loginFragment);
                transaction.addToBackStack(null);
                transaction.commit();
            } else {
                if (holder.replyInputLayout.getVisibility() == View.GONE) {
                    holder.replyInputLayout.setVisibility(View.VISIBLE);
                    holder.replyTextView.setTextColor(Color.BLUE);
                    holder.replyEditText.requestFocus();
                    InputMethodManager imm = (InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.showSoftInput(holder.replyEditText, InputMethodManager.SHOW_IMPLICIT);
                } else {
                    holder.replyInputLayout.setVisibility(View.GONE);
                    holder.replyTextView.setTextColor(Color.GRAY);
                    InputMethodManager imm = (InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.hideSoftInputFromWindow(holder.replyEditText.getWindowToken(), 0);
                }
            }
        });

        // Xử lý sự kiện khi nhấn nút Gửi
        holder.replySendButton.setOnClickListener(v -> {

            String replyContent = holder.replyEditText.getText().toString().trim();
            if (!replyContent.isEmpty()) {
                // Tạo một đối tượng Reply mới
                Reply reply = new Reply();
                reply.setUserEmail(TrangThai.currentUser.getName());
                reply.setContent(replyContent);
                reply.setDate(TrangThai.getCurrentDateString());
                reply.setLikes(0);

                // Thêm reply vào danh sách replies của comment hiện tại
                List<Reply> updatedReplies = comment.getReplies();
                if (updatedReplies == null) {
                    updatedReplies = new ArrayList<>();
                }
                updatedReplies.add(reply);

                // Cập nhật lại danh sách replies trên Firebase
                databaseReference.child(ChiTietMonAnFragment.ID_MONAN).child(comment.getCommentId()).child("replies").setValue(updatedReplies);

                // Xóa nội dung EditText sau khi gửi
                holder.replyEditText.setText("");
                holder.replyInputLayout.setVisibility(View.GONE);
                holder.replyTextView.setTextColor(Color.GRAY);
                InputMethodManager imm = (InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(holder.replyEditText.getWindowToken(), 0);

                // Load lai comment
                View replyView = LayoutInflater.from(context).inflate(R.layout.reply_item, holder.repliesLayout, false);
                TextView replyContentTV = replyView.findViewById(R.id.replyContentTextView);
                TextView replyDateTV = replyView.findViewById(R.id.replyDateTextView);

                TextView replyNameTV = replyView.findViewById(R.id.replyNameTextView);
                replyNameTV.setText(reply.getUserEmail());
                replyContentTV.setText(reply.getContent());
                replyDateTV.setText(reply.getDate());


                holder.repliesLayout.addView(replyView);
            }
        });

        // Gán các bình luận trả lời
        holder.repliesLayout.removeAllViews(); // Xóa các bình luận cũ nếu có
        if(comment.getReplies()!=null){
            for (Reply reply : comment.getReplies()) {
                View replyView = LayoutInflater.from(context).inflate(R.layout.reply_item, holder.repliesLayout, false);

                // Ánh xạ dữ liệu
                TextView replyContent = replyView.findViewById(R.id.replyContentTextView);
                TextView replyDate = replyView.findViewById(R.id.replyDateTextView);

                TextView replyName = replyView.findViewById(R.id.replyNameTextView);

                replyName.setText(reply.getUserEmail());
                replyContent.setText(reply.getContent());
                replyDate.setText(reply.getDate());


                holder.repliesLayout.addView(replyView);
            }
        }

    }

    @Override
    public int getItemCount() {
        return commentList.size();
    }

    public static class CommentViewHolder extends RecyclerView.ViewHolder {
        ImageView avatarImageView;
        TextView userNameTextView, dateTextView, contentTextView, likesTextView, likeTextView, replyTextView;
        LinearLayout repliesLayout, replyInputLayout;
        EditText replyEditText;
        Button replySendButton;
        boolean isLike = false;


        public CommentViewHolder(@NonNull View itemView) {
            super(itemView);
            avatarImageView = itemView.findViewById(R.id.avatarImageView);
            userNameTextView = itemView.findViewById(R.id.userNameTextView);
            dateTextView = itemView.findViewById(R.id.dateTextView);
            contentTextView = itemView.findViewById(R.id.contentTextView);
            likesTextView = itemView.findViewById(R.id.likesTextView);
            likeTextView = itemView.findViewById(R.id.likeTextView);
            repliesLayout = itemView.findViewById(R.id.repliesLayout);
            replyEditText = itemView.findViewById(R.id.replyEditText);
            replySendButton = itemView.findViewById(R.id.replySendButton);
            replyTextView = itemView.findViewById(R.id.replyTextView);
            replyInputLayout = itemView.findViewById(R.id.replyInputLayout);
        }
    }

}
