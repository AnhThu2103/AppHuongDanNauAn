package com.example.huongdannauan.Interface;
import android.os.AsyncTask;
import android.util.Log;

import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;

import java.io.File;
import java.util.Map;

public class UploadTask extends AsyncTask<File, Void, String> {

    private final OnUploadListener listener;

    public interface OnUploadListener {
        void onUploadSuccess(String imageUrl);
        void onUploadFailure(String errorMessage);
    }

    public UploadTask(OnUploadListener listener) {
        this.listener = listener;
    }

    @Override
    protected String doInBackground(File... files) {
        try {
            File imageFile = files[0];

            // Khởi tạo Cloudinary
            Cloudinary cloudinary = new Cloudinary(ObjectUtils.asMap(
                    "cloud_name", "your_cloud_name",
                    "api_key", "your_api_key",
                    "api_secret", "your_api_secret"
            ));

            // Upload ảnh lên Cloudinary
            Map uploadResult = cloudinary.uploader().upload(imageFile, ObjectUtils.emptyMap());
            return uploadResult.get("url").toString(); // URL của ảnh trên Cloudinary
        } catch (Exception e) {
            Log.e("Cloudinary", "Error uploading image: " + e.getMessage());
            return null;
        }
    }

    @Override
    protected void onPostExecute(String result) {
        if (result != null) {
            listener.onUploadSuccess(result);
        } else {
            listener.onUploadFailure("Không thể upload ảnh!");
        }
    }
}

