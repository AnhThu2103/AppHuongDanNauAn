package com.example.huongdannauan.Interface;

import com.example.huongdannauan.model.ImageResponseModel;

import okhttp3.MultipartBody;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;
import retrofit2.http.Path;

public interface CloudflareApi {

    // Phương thức verify API token
    @GET("client/v4/user/tokens/verify")
    Call<Void> verifyToken(@Header("Authorization") String authHeader);

    // Phương thức upload ảnh lên Cloudflare
    @Multipart
    @POST("client/v4/accounts/{account_id}/images/v1")
    Call<ImageResponseModel> uploadImage(
            @Header("Authorization") String authHeader, // Thêm header Authorization
            @Part MultipartBody.Part image // Phần ảnh sẽ được gửi lên
    );

    @GET("client/v4/accounts/{accountId}/images/v1/{imageId}")
    Call<ImageResponseModel> getImageDetails(
            @Header("Authorization") String authHeader,
            @Path("imageId") String imageId
    );
}
