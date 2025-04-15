package com.example.huongdannauan.fragment;

import android.os.Bundle;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.example.huongdannauan.R;

public class DisplayImageFragment extends Fragment {

    private ImageView imageView;
    private Button btn;

    public DisplayImageFragment() {
        // Required empty public constructor
    }

    public static DisplayImageFragment newInstance(String imageUrl) {
        DisplayImageFragment fragment = new DisplayImageFragment();
        Bundle args = new Bundle();
        args.putString("image_url", imageUrl);  // Chỉ cần một tham số image_url
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_display_image, container, false);

        imageView = view.findViewById(R.id.imageView2);
        btn = view.findViewById(R.id.button2);

        // Lắng nghe sự kiện click của nút
        btn.setOnClickListener(v -> openFragment(new ChinhSuaThongTinFragment()));

        // Lấy URL của hình ảnh từ arguments
        String imageUrl = getArguments() != null ? getArguments().getString("image_url") : null;

        // Hiển thị ảnh bằng Glide nếu có URL
        if (imageUrl != null && !imageUrl.isEmpty()) {
            Glide.with(this)
                    .load(imageUrl)
                    .into(imageView);
        }
        return view;
    }

    // Phương thức để mở fragment mới
    private void openFragment(Fragment fragment) {
        // Chuyển fragment trong Activity hiện tại
        FragmentTransaction transaction = requireActivity().getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.fragment_container, fragment); // fragment_container là ID của container nơi các fragment được hiển thị
        transaction.addToBackStack(null); // Để người dùng có thể quay lại fragment trước
        transaction.commit();
    }
}
