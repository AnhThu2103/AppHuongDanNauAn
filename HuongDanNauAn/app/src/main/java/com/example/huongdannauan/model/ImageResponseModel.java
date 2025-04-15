package com.example.huongdannauan.model;

public class ImageResponseModel {
    private boolean success;
    private String errors;
    private CloudflareImageResult result;

    public static class CloudflareImageResult {
        private String id;
        private String url;

        public String getUrl() {
            return url;
        }

        public String getId() {
            return id;
        }
    }

    public CloudflareImageResult getResult() {
        return result;
    }

    public boolean isSuccess() {
        return success;
    }

    public String getErrors() {
        return errors;
    }
}
