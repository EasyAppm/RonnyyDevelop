package com.easyapp.ronnyy.rpeasyapp;

public class UserModel {

    private final String auth;
    private final String user;
    private final String password;
    private final String token;
    private final String sha1, sha2;

    private UserModel(Builder builder) {
        this.auth = builder.auth;
        this.user = builder.user;
        this.password = builder.password;
        this.token = builder.token;
        this.sha1 = builder.sha1;
        this.sha2 = builder.sha2;
    }
    
    public static Builder builder(){
        return new Builder();
    }
    public String getAuth(){
        return auth;
    }
    public String getUser() {
        return user;
    }
    public String getPassword(){
       return password;
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

        private String auth;
        private String user;
        private String password;
        private String token;
        private String sha1, sha2;

        public Builder setAuth(String auth) {
            this.auth = auth;
            return this;
        }

        public Builder setUser(String user) {
            this.user = user;
            return this;
        }

        public Builder setPassword(String password) {
            this.password = password;
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
