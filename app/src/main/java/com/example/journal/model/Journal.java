package com.example.journal.model;

import com.google.firebase.Timestamp;

public class Journal {

        private String title;
        private String thought;
        private String imageUrl;
        private String userId;
        private Timestamp TimeAdd;
        private String userName;

        public Journal() {
        }

        public Journal(String title, String thought, String imageUrl, String userId, Timestamp timestamp, String userName) {
            this.title = title;
            this.thought = thought;
            this.imageUrl = imageUrl;
            this.userId = userId;
            this.TimeAdd = timestamp;
            this.userName = userName;
        }

        public String getTitle() {
            return title;
        }

        public void setTitle(String title) {
            this.title = title;
        }

        public String getThought() {
            return thought;
        }

        public void setThought(String thought) {
            this.thought = thought;
        }

        public String getImageUrl() {
            return imageUrl;
        }

        public void setImageUrl(String imageUrl) {
            this.imageUrl = imageUrl;
        }

        public String getUserId() {
            return userId;
        }

        public void setUserId(String userId) {
            this.userId = userId;
        }

        public Timestamp getTimeAdd() {
        return TimeAdd;
        }

        public void setTimeAdd(Timestamp timestamp) {
            this.TimeAdd = timestamp;
        }

        public String getUserName() {
            return userName;
        }

        public void setUserName(String userName) {
            this.userName = userName;
        }


}
