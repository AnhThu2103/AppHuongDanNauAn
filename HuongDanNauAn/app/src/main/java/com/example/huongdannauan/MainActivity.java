package com.example.huongdannauan;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.Toast;

import com.example.huongdannauan.fragment.AccountFragment;
import com.example.huongdannauan.fragment.DangNhapFragment;
import com.example.huongdannauan.fragment.HomeFragment;
import com.example.huongdannauan.model.TrangThai;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class MainActivity extends AppCompatActivity {

    private ImageView buttonImage;
    private BottomNavigationView bottomNavigationView;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        bottomNavigationView = findViewById(R.id.bottom_navigation);

        // Khởi tạo Firebase
        FirebaseApp.initializeApp(this);

        loadFragment(new HomeFragment());

        // Xử lý sự kiện chọn item trong BottomNavigationView
        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                Fragment selectedFragment = null;

                int itemId = item.getItemId();
                if (itemId == R.id.nav_home) {
                    selectedFragment = new HomeFragment();
                } else if (itemId == R.id.nav_account) {
                    // Kiểm tra trạng thái người dùng để chuyển sang đúng fragment
                    if (TrangThai.userEmail.isEmpty() || TrangThai.userEmail==null) {
                        // Nếu người dùng chưa đăng nhập, chuyển đến trang đăng nhập
                        selectedFragment = new DangNhapFragment();
                    } else {
                        // Nếu người dùng đã đăng nhập, chuyển đến trang tài khoản
                        selectedFragment = new AccountFragment();
                    }
                }

                // Tải fragment tương ứng
                return loadFragment(selectedFragment);
            }
        });
    }

    private boolean loadFragment(Fragment fragment) {
        if (fragment != null) {
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.fragment_container, fragment)
                    .commit();
            return true;
        }
        return false;
    }
}
