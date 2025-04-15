package com.example.huongdannauan.Interface;

import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;

import java.util.Map;

public class CloudinaryUtils {
    private static Cloudinary cloudinary;

    public static Cloudinary getInstance() {
        if (cloudinary == null) {
            // Cấu hình Cloudinary với thông tin từ Dashboard
//            Map config = ObjectUtils.asMap(
//                    "cloud_name", "dbxnsagoz",  // Thay bằng "cloud_name" của bạn
//                    "api_key", "176659354887277",       // Thay bằng API Key
//                    "api_secret", "dsNoUR9jl1VdEGSOvR9NWF4XS3I"  // Thay bằng API Secret
//            );
            Map config = ObjectUtils.asMap(
                    "cloud_name", "den6glhqc",  // Thay bằng "cloud_name" của bạn
                    "api_key", "885864395487874",       // Thay bằng API Key
                    "api_secret", "BGmqQ6GsJv7bWfX2nUwXuEw9CII"  // Thay bằng API Secret
            );
            cloudinary = new Cloudinary(config);
        }
        return cloudinary;
    }
}
