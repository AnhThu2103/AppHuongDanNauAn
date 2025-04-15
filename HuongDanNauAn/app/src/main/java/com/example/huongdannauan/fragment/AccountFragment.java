package com.example.huongdannauan.fragment;



import android.net.Uri;
import android.os.Bundle;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.huongdannauan.Interface.CloudflareApi;
import com.example.huongdannauan.R;
import com.example.huongdannauan.model.TrangThai;
import com.example.huongdannauan.model.ImageResponseModel;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class    AccountFragment extends Fragment {
    LinearLayout cnMonAnYeuThich, cnTinTucDaLuu, cnBinhLuanCuaToi, cnChinhSuaThongTin, cnDangXuat;
    TextView userNameTextView, userEmailTextView;
    ImageView profileImageView;

    private DatabaseReference databaseReference;

    public AccountFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        databaseReference = FirebaseDatabase.getInstance().getReference("user");
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_account, container, false);

        userNameTextView = view.findViewById(R.id.user_name);
        userEmailTextView = view.findViewById(R.id.user_email);
        profileImageView = view.findViewById(R.id.profile_image);


        cnMonAnYeuThich = view.findViewById(R.id.CNMonAnYeuThich);
        cnTinTucDaLuu = view.findViewById(R.id.CNTinTucDaLuu);
        cnBinhLuanCuaToi = view.findViewById(R.id.CNBinhLuanCuaToi);
        cnChinhSuaThongTin = view.findViewById(R.id.CNChinhSuaThongTin);
        cnDangXuat = view.findViewById(R.id.CNDangXuat);

        // Xử lý sự kiện đăng xuất
        cnDangXuat.setOnClickListener(v -> {
            TrangThai.currentUser = null;
            openFragmentOfUser(cnDangXuat ,new DangNhapFragment(), true);
        });

        // Lấy thông tin người dùng từ Firebase
        getUserInfoByEmail();

        // Mở các fragment khác
        openFragmentOfUser(cnMonAnYeuThich, new MonAnYeuThichFragment(), false);
        openFragmentOfUser(cnTinTucDaLuu, new TinTucDaLuuFragment(), false);
        openFragmentOfUser(cnBinhLuanCuaToi, new LichSuXemFragment(), false);
        openFragmentOfUser(cnChinhSuaThongTin, new ChinhSuaThongTinFragment(), false);

        return view;
    }

    private void getUserInfoByEmail() {
        databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot userSnapshot : dataSnapshot.getChildren()) {
                    // Lấy email từ Firebase
                    String email = userSnapshot.child("email").getValue(String.class);
                    String emailToCheck = TrangThai.userEmail;

                    // Kiểm tra nếu email cần kiểm tra là null hoặc rỗng, thì thoát
                    if (emailToCheck == null || emailToCheck.isEmpty()) {
                        openFragmentDangNhap();
                    }

                    // Kiểm tra nếu email khớp
                    if (email != null && email.equals(emailToCheck)) {
                        // Gán giá trị email vào TrangThai.userEmail
                        TrangThai.userEmail = email;

                        // Lấy các giá trị khác từ Firebase
                        String userName = userSnapshot.child("name").getValue(String.class);
                        String avatarUri = userSnapshot.child("avatar").getValue(String.class); // URL ảnh
                        userNameTextView.setText(userName);
                        userEmailTextView.setText(email);

                        // Kiểm tra và tải ảnh nếu có
                        if (avatarUri != null && !avatarUri.isEmpty()) {
                            // Nếu URL ảnh có sẵn từ Firebase
                            Glide.with(getContext())
                                    .load(avatarUri)
                                    .placeholder(R.drawable.icon_loading) // Ảnh hiển thị khi đang tải
                                    .error(R.drawable.icon_loading)         // Ảnh hiển thị khi lỗi tải
                                    .into(profileImageView);
                        } else {
                            // Nếu không có avatar, gọi API Cloudflare để lấy lại ảnh (nếu cần)
                            String imageId = userSnapshot.child("imageId").getValue(String.class); // ID ảnh
                            if (imageId != null) {
                                fetchImageFromCloudflare(imageId);
                            }
                        }
                        break; // Thoát vòng lặp vì đã tìm thấy người dùng
                    }
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                // Xử lý lỗi nếu có
                Log.e("FirebaseError", databaseError.getMessage());
            }
        });
    }


    private void fetchImageFromCloudflare(String imageId) {
        // Retrofit setup
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("https://api.cloudflare.com/")
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        CloudflareApi cloudflareApi = retrofit.create(CloudflareApi.class);
        String authHeader = "Bearer ";

        // Gọi API để lấy thông tin ảnh từ Cloudflare
        Call<ImageResponseModel> call = cloudflareApi.getImageDetails(authHeader, imageId);
        call.enqueue(new Callback<ImageResponseModel>() {
            @Override
            public void onResponse(Call<ImageResponseModel> call, Response<ImageResponseModel> response) {
                if (response.isSuccessful() && response.body() != null) {
                    ImageResponseModel imageResponse = response.body();
                    if (imageResponse.isSuccess()) {
                        String imageUrl = imageResponse.getResult().getUrl();  // Lấy URL ảnh từ kết quả

                        // Sử dụng Glide để tải ảnh
                        Glide.with(getContext())
                                .load(imageUrl)
                                .placeholder(R.drawable.icon_loading) // Ảnh hiển thị khi đang tải
                                .error(R.drawable.icon_error)           // Ảnh hiển thị khi lỗi tải
                                .into(profileImageView);

                    } else {
                        Log.e("Cloudflare", "Image fetch failed: " + imageResponse.getErrors());
                        Toast.makeText(getContext(), "Lỗi khi lấy ảnh từ Cloudflare", Toast.LENGTH_SHORT).show();
                        profileImageView.setImageResource(R.drawable.avata1);

                    }
                } else {
                    Log.e("Cloudflare", "Image fetch failed: " + response.message());
                    Toast.makeText(getContext(), "Lỗi khi gọi API Cloudflare", Toast.LENGTH_SHORT).show();
                    profileImageView.setImageResource(R.drawable.avata1);
                }
            }

            @Override
            public void onFailure(Call<ImageResponseModel> call, Throwable t) {
                Log.e("Cloudflare", "API call failed: " + t.getMessage());
                Toast.makeText(getContext(), "Lỗi kết nối Cloudflare", Toast.LENGTH_SHORT).show();
                profileImageView.setImageResource(R.drawable.avata1);
            }
        });
    }

    void openFragmentOfUser(LinearLayout linearLayout, Fragment fragment, boolean isDangXuat) {
        if (isDangXuat) {
            TrangThai.userEmail = "";
        }

        linearLayout.setOnClickListener(v -> {
            FragmentTransaction transaction = getParentFragmentManager().beginTransaction();
            transaction.replace(R.id.fragment_container, fragment);
            transaction.addToBackStack(null);
            transaction.commit();
        });
    }
    void openFragmentDangNhap() {
        DangNhapFragment fragment = new DangNhapFragment();
            FragmentTransaction transaction = getParentFragmentManager().beginTransaction();
            transaction.replace(R.id.fragment_container, fragment);
            transaction.addToBackStack(null);
            transaction.commit();
    }
}
