package com.example.huongdannauan.fragment;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;
import android.Manifest;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import com.bumptech.glide.Glide;
import com.cloudinary.utils.ObjectUtils;
import com.example.huongdannauan.Interface.CloudinaryUtils;
import com.example.huongdannauan.R;
import com.example.huongdannauan.model.TrangThai;
import com.example.huongdannauan.model.User;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.io.File;
import java.util.Map;

public class ChinhSuaThongTinFragment extends Fragment {

    private static final int PICK_IMAGE_REQUEST = 100;

    private ImageView imgView;
    private EditText edtName, edtEmail, edtAge;
    private RadioGroup radioGroupGender;
    private Button btnSave, btnSelectImage;
    String avataNOW="";
    private String uploadedImageUrl;
    private DatabaseReference mDatabase;

    @SuppressLint("MissingInflatedId")
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_chinh_sua_thong_tin, container, false);
        // Ánh xạ các thành phần
        imgView = view.findViewById(R.id.imgAnh);
        btnSelectImage = view.findViewById(R.id.btnAnh);
        edtName = view.findViewById(R.id.edtTen);
        edtEmail = view.findViewById(R.id.edMailEdit);
        edtAge = view.findViewById(R.id.edTuoi);
        radioGroupGender = view.findViewById(R.id.ttt);
        btnSave = view.findViewById(R.id.btnLuu);

        // Firebase Database reference
        mDatabase = FirebaseDatabase.getInstance().getReference("user");

        // Chọn ảnh từ thư viện
        btnSelectImage.setOnClickListener(v -> openImagePicker());

        // Lưu thông tin
        btnSave.setOnClickListener(v -> saveUserInfo());
        getUserInfoFromFirebase();
        return view;
    }

    // Mở bộ chọn ảnh
    private void openImagePicker() {
        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(intent, PICK_IMAGE_REQUEST);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PICK_IMAGE_REQUEST && resultCode == getActivity().RESULT_OK && data != null) {
            Uri selectedImageUri = data.getData();
            String filePath = getPathFromUri(selectedImageUri);

            if (filePath != null) {
                File imageFile = new File(filePath);
                uploadImageToCloudinary(imageFile);
            } else {
                Toast.makeText(getContext(), "Không thể lấy đường dẫn ảnh!", Toast.LENGTH_SHORT).show();
            }
        }
    }

    // Lấy đường dẫn từ URI
    private String getPathFromUri(Uri uri) {
        String path = null;
        String[] projection = {MediaStore.Images.Media.DATA};
        Cursor cursor = getActivity().getContentResolver().query(uri, projection, null, null, null);
        if (cursor != null) {
            int columnIndex = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
            cursor.moveToFirst();
            path = cursor.getString(columnIndex);
            cursor.close();
        }
        return path;
    }
    private void getUserInfoFromFirebase() {
        if (TrangThai.userEmail == null || TrangThai.userEmail.isEmpty()) {
            Toast.makeText(getContext(), "Email chưa được thiết lập", Toast.LENGTH_SHORT).show();
            return;
        }

        DatabaseReference userRef = FirebaseDatabase.getInstance().getReference("user");

        userRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {

                    String Emaill = snapshot.child("email").getValue(String.class);

                    if (Emaill != null && Emaill.equals(TrangThai.userEmail)) {

                        User user = snapshot.getValue(User.class);
                        if (user != null) {
                            edtName.setText(user.getName());
                            edtEmail.setText(user.getEmail());
                            edtAge.setText(user.getAge());

                            Glide.with(getContext())
                                    .load(user.getAvatar())
                                    .placeholder(R.drawable.icon_loading)
                                    .error(R.drawable.icon_error)
                                    .into(imgView);

                            avataNOW = user.getAvatar();
                            edtEmail.setEnabled(false); // Không cho phép sửa email
                            break;
                        }
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(getContext(), "Lỗi truy vấn: " + databaseError.getMessage(), Toast.LENGTH_SHORT).show();
                Log.e("FirebaseDebug", "Lỗi Firebase: ", databaseError.toException());
            }
        });
    }



    // Upload ảnh lên Cloudinary
    private void uploadImageToCloudinary(File imageFile) {
        new Thread(() -> {
            try {
                if (imageFile == null || !imageFile.exists()) {
                    Toast.makeText(getContext(), "File ảnh không tồn tại hoặc null!", Toast.LENGTH_SHORT).show();
                }
                Map uploadResult = CloudinaryUtils.getInstance().uploader().upload(imageFile, ObjectUtils.emptyMap());
                checkAndRequestPermissions();
                uploadedImageUrl = uploadResult.get("url").toString();

                // Hiển thị ảnh vừa upload
                getActivity().runOnUiThread(() -> {
                    Glide.with(getContext())
                            .load(uploadedImageUrl)
                            .into(imgView);

                    Toast.makeText(getContext(), "Upload ảnh thành công!", Toast.LENGTH_SHORT).show();
                });
            } catch (Exception e) {
                Log.e("Cloudinary", "Upload failed: " + e.getMessage());
                getActivity().runOnUiThread(() ->
                        Toast.makeText(getContext(), "Upload ảnh thất bại!", Toast.LENGTH_SHORT).show()
                );
            }
        }).start();
    }
    private void checkAndRequestPermissions() {
        if (ContextCompat.checkSelfPermission(getContext(), Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            // Yêu cầu quyền
            ActivityCompat.requestPermissions(getActivity(),
                    new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},
                    100);
        }
    }

    private void saveUserInfo() {
        String name = edtName.getText().toString().trim();
        String email = edtEmail.getText().toString().trim();
        String age = edtAge.getText().toString().trim();
        final String gender;
        int selectedGenderId = radioGroupGender.getCheckedRadioButtonId();
        if (selectedGenderId != -1) {
            RadioButton selectedGender = getView().findViewById(selectedGenderId);
            gender = selectedGender.getText().toString();  // Gán giá trị cho gender
        } else {
            Toast.makeText(getContext(), "Vui lòng chọn giới tính!", Toast.LENGTH_SHORT).show();
            return;
        }

        if (name.isEmpty() || email.isEmpty() || age.isEmpty() || imgView.getDrawable() == null) {
            Toast.makeText(getContext(), "Vui lòng nhập đủ thông tin và upload ảnh!", Toast.LENGTH_SHORT).show();
            return;
        }

        DatabaseReference userRef = FirebaseDatabase.getInstance().getReference("user");

        // Tìm người dùng theo email
        userRef.orderByChild("email").equalTo(email).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    // Cập nhật thông tin người dùng
                    for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                        User user = snapshot.getValue(User.class);
                        if (user != null) {
                            user.setName(name);
                            user.setEmail(email);
                            user.setAge(age);
                            user.setGender(gender);
                            if (uploadedImageUrl != null && !uploadedImageUrl.isEmpty()) {
                                user.setAvatar(uploadedImageUrl);
                            } else {
                                user.setAvatar(avataNOW);
                            }
                            snapshot.getRef().setValue(user).addOnCompleteListener(task -> {
                                if (task.isSuccessful()) {
                                    Toast.makeText(getContext(), "Lưu thông tin thành công!", Toast.LENGTH_SHORT).show();
                                } else {
                                    Toast.makeText(getContext(), "Lỗi khi lưu thông tin!", Toast.LENGTH_SHORT).show();
                                }
                            });
                        }
                    }
                } else {
                    // Nếu người dùng chưa tồn tại, tạo mới
                    String userId = userRef.push().getKey();
                    User newUser = new User(uploadedImageUrl, name, email, "", "", "", gender, age);
                    if (userId != null) {
                        userRef.child(userId).setValue(newUser)
                                .addOnSuccessListener(aVoid -> Toast.makeText(getContext(), "Lưu thông tin thành công!", Toast.LENGTH_SHORT).show())
                                .addOnFailureListener(e -> {
                                    Log.e("Firebase", "Lưu thất bại: " + e.getMessage());
                                    Toast.makeText(getContext(), "Lưu thất bại!", Toast.LENGTH_SHORT).show();
                                });
                    }
                }
                openAccountFragment(new AccountFragment());
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(getContext(), "Lỗi khi kiểm tra email: " + databaseError.getMessage(), Toast.LENGTH_SHORT).show();
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

}
