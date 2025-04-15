    package com.example.huongdannauan.model;

    public class User {
        private String avatar;
        private String name;
        private String email;
        private String monAnDaLuu;
        private String monAnDaXem;
        private String tinTucDaLuu;
        private String gender;
        private String Age;


        // Constructor
        public User() {
            // Trống cho Firestore
        }

        public User(String avatar, String name, String email, String monAnDaLuu, String monAnDaXem, String tinTucDaLuu, String gender, String age) {
            this.avatar = avatar;
            this.name = name;
            this.email = email;
            this.monAnDaLuu = monAnDaLuu;
            this.monAnDaXem = monAnDaXem;
            this.tinTucDaLuu = tinTucDaLuu;
            this.gender = gender;
            Age = age;
        }

        public String getAvatar() {
            return avatar;
        }

        public void setAvatar(String avatar) {
            this.avatar = avatar;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getEmail() {
            return email;
        }

        public void setEmail(String email) {
            this.email = email;
        }

        public String getMonAnDaLuu() {
            return monAnDaLuu;
        }

        public void setMonAnDaLuu(String monAnDaLuu) {
            this.monAnDaLuu = monAnDaLuu;
        }

        public String getMonAnDaXem() {
            return monAnDaXem;
        }

        public void setMonAnDaXem(String monAnDaXem) {
            this.monAnDaXem = monAnDaXem;
        }

        public String getTinTucDaLuu() {
            return tinTucDaLuu;
        }

        public void setTinTucDaLuu(String tinTucDaLuu) {
            this.tinTucDaLuu = tinTucDaLuu;
        }

        public String getGender() {
            return gender;
        }

        public void setGender(String gender) {
            this.gender = gender;
        }

        public String getAge() {
            return Age;
        }

        public void setAge(String age) {
            Age = age;
        }
    }
