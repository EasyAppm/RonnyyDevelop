package com.easyapp.ronnyy.rpeasyapp;

public class UserModel {

    private final String name;
    private final String token;
    private final String sha1, sha2;

    private UserModel(Builder builder) {
        this.name = builder.name;
        this.token = builder.token;
        this.sha1 = builder.sha1;
        this.sha2 = builder.sha2;
    }
    
    public static Builder buider(){
        return new Builder();
    }

    public String getName() {
        return name;
    }

    public String getToken() {
        return token;
    }

    public String getSha1() {
        return sha1;
    }

    public String getSha2() {
        return sha2;
    }

    public static class Builder {
        private String name;
        private String token;
        private String sha1, sha2;

        public Builder setName(String name) {
            this.name = name;
            return this;
        }

        public Builder setToken(String token) {
            this.token = token;
            return this;
        }

        public Builder setSha1(String sha1) {
            this.sha1 = sha1;
            return this;
        }

        public Builder setSha2(String sha2) {
            this.sha2 = sha2;
            return this;
        }
        
        public UserModel create(){
            return new UserModel(this);
        }

    }

}
