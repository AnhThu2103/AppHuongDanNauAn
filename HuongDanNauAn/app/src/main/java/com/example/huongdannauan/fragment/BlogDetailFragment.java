package com.example.huongdannauan.fragment;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.graphics.Color;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import android.text.SpannableStringBuilder;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.huongdannauan.R;
import com.example.huongdannauan.model.Blog;
import com.example.huongdannauan.model.Ingredient;
import com.example.huongdannauan.model.Instruction;
import com.example.huongdannauan.model.Recipe;
import com.example.huongdannauan.model.Step;
import com.example.huongdannauan.model.TrangThai;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link BlogDetailFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class BlogDetailFragment extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;
    private DatabaseReference databaseReference1;
    ImageView img, savebolg;
    TextView title, date, content;
    ProgressBar progressBar;
    Blog blog = new Blog();

    public BlogDetailFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment BlogDetailFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static BlogDetailFragment newInstance(String param1, String param2) {
        BlogDetailFragment fragment = new BlogDetailFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @SuppressLint("MissingInflatedId")
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view =  inflater.inflate(R.layout.fragment_blog_detail, container, false);

        img = view.findViewById(R.id.blogimg);
        title = view.findViewById(R.id.blogtitle);
        content = view.findViewById(R.id.blogcontent);
        date = view.findViewById(R.id.blogdate);
        savebolg = view.findViewById(R.id.saveblog);

        progressBar = view.findViewById(R.id.progressBar);
        progressBar.setVisibility(View.VISIBLE);

        SuKienluuBlog();


        databaseReference1 = FirebaseDatabase.getInstance().getReference().child("blog").child(mParam1);

        // Lấy dữ liệu món ăn từ Firebase
        databaseReference1.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                // Chuyển đổi dữ liệu Firebase thành đối tượng Recipe
                blog = snapshot.getValue(Blog.class);

                // Kiểm tra và cập nhật giao diện nếu recipe không null
                if (blog != null) {
                    updateUI();
                }

                progressBar.setVisibility(View.GONE);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.e("RecipeDetailActivity", "Lỗi khi lấy dữ liệu", error.toException());
                progressBar.setVisibility(View.GONE);
            }
        });

        if(!TrangThai.userEmail.isEmpty()) {
            getUserByEmail();
        }

        return view;
    }
    private void updateUI() {
        Glide.with(this).load(blog.getImg()).into(img);
        title.setText(blog.getTitle());
        date.setText(blog.getDate());
        String nd = blog.getContent();

        if (nd != null && !nd.isEmpty()) {
            // Tách chuỗi content bằng "\n"
            nd = nd.replace("\\n", "\n");
            String[] paragraphs = nd.split("\n");

            // Dùng SpannableStringBuilder để định dạng đoạn văn
            SpannableStringBuilder formattedContent = new SpannableStringBuilder();

            for (String paragraph : paragraphs) {
                // Thêm mỗi đoạn và cách một dòng
                formattedContent.append(paragraph).append("\n\n");
            }

            // Hiển thị nội dung đã định dạng lên TextView
            content.setText(formattedContent);
        } else {
            content.setText("Không có nội dung để hiển thị.");
        }
    }
    void SuKienluuBlog(){
        savebolg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!TrangThai.userEmail.isEmpty()) {
                    luuBlog();
                } else {
                    AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
                    builder.setTitle("Đăng nhập");
                    builder.setMessage("Đăng nhập để lưu tin tức.");

                    // Nút xác nhận
                    builder.setPositiveButton("Đồng ý", (dialog, which) -> {

                        // Điều hướng từ Fragment Món ăn đến Fragment Đăng nhập
                        Bundle args = new Bundle();
                        args.putString("return_fragment", "BlogDetailFragment");
                        args.putString("idTinTuc", mParam1);
                        DangNhapFragment loginFragment = new DangNhapFragment();
                        loginFragment.setArguments(args);

                        openAccountFragment(loginFragment);
                    });

                    // Nút hủy
                    builder.setNegativeButton("Hủy", (dialog, which) -> dialog.dismiss());

                    // Hiển thị AlertDialog
                    AlertDialog dialog = builder.create();
                    dialog.show();

                }
            }
        });
    }
    private void openAccountFragment(Fragment fragment) {
        FragmentTransaction transaction = requireActivity().getSupportFragmentManager().beginTransaction();

        if (getActivity() != null) {
            Log.d("DangNhapFragment", "Opening Account Fragment");
            transaction.replace(R.id.fragment_container, fragment);
            transaction.addToBackStack(null);
            transaction.commit();
        } else {
            Log.e("DangNhapFragment", "Activity is null, cannot open fragment");
        }
    }
    void luuBlog(){
        DatabaseReference userRef = FirebaseDatabase.getInstance().getReference("user");

        // Truy vấn tìm kiếm theo trường "email"
        Query query = userRef.orderByChild("email").equalTo(TrangThai.userEmail);

        // Lắng nghe kết quả truy vấn
        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    // Lặp qua các kết quả
                    for (DataSnapshot userSnapshot : dataSnapshot.getChildren()) {
                        String monAnDaLuu = userSnapshot.child("tinTucDaLuu").getValue(String.class);

                        // Kiểm tra nếu "monAnDaLuu" không null
                        if (monAnDaLuu != null) {
                            // Chuyển đổi "monAnDaLuu" thành danh sách các ID
                            List<String> listMonAn = new ArrayList<>(Arrays.asList(monAnDaLuu.split(",")));

                            if (listMonAn.contains(mParam1)) {
                                // Nếu ID_MONAN tồn tại thì xóa
                                listMonAn.remove(mParam1);
                                savebolg.setImageResource(R.drawable.icon_no_save);
                                Toast.makeText(getContext(), "Đã hủy yêu thích", Toast.LENGTH_SHORT).show();
                            } else {
                                // Nếu ID_MONAN không tồn tại thì thêm
                                listMonAn.add(0,mParam1);
                                savebolg.setImageResource(R.drawable.icon_save);
                                Toast.makeText(getContext(), "Đã thích", Toast.LENGTH_SHORT).show();
                            }

                            // Cập nhật lại "monAnDaLuu" trong Firebase
                            String updatedMonAnDaLuu = String.join(",", listMonAn);
                            userSnapshot.getRef().child("tinTucDaLuu").setValue(updatedMonAnDaLuu);
                        } else {
                            // Nếu "monAnDaLuu" null, khởi tạo danh sách mới
                            userSnapshot.getRef().child("tinTucDaLuu").setValue(mParam1);
                            savebolg.setImageResource(R.drawable.icon_save);
                        }
                    }
                } else {
                    System.out.println("No user found with email: " + TrangThai.userEmail);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                System.out.println("Database error: " + databaseError.getMessage());
            }
        });
    }
    public void getUserByEmail() {
        // Tham chiếu tới node "user"
        DatabaseReference userRef = FirebaseDatabase.getInstance().getReference("user");

        // Truy vấn tìm kiếm theo trường "email"
        Query query = userRef.orderByChild("email").equalTo(TrangThai.userEmail);

        // Lắng nghe kết quả truy vấn
        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    // Lặp qua các kết quả
                    for (DataSnapshot userSnapshot : dataSnapshot.getChildren()) {
                        String kq = userSnapshot.child("tinTucDaLuu").getValue(String.class);
                        if(kq!=null){
                            if(kq.contains(mParam1)){
                                savebolg.setImageResource(R.drawable.icon_save);
                            }
                        }
                    }
                } else {
                    System.out.println("No user found with email: " + TrangThai.userEmail);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                System.out.println("Database error: " + databaseError.getMessage());
            }
        });
    }
}