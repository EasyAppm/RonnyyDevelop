package com.easyapp.ronnyy.rpeasyapp;

import android.util.Log;

import com.easyapp.core.TypeValidator;
import com.http.ceas.ClientFactory;
import com.http.ceas.callback.HttpCallback;
import com.http.ceas.callback.RestCallback;
import com.http.ceas.core.HttpHeaders;
import com.http.ceas.core.HttpStatus;
import com.http.ceas.entity.Response;

public class RpService {

    private final RpClient client;
    private final UserModel userModel;

    private RpService(UserModel userModel) {
        this.userModel = TypeValidator.nonNull(userModel, "UserModel cannot be null.");
        this.client = ClientFactory.newInstance().create(RpClient.class);
    }

    public static RpService newRpService(UserModel userModel) {
        return new RpService(userModel);
    }

    public void login(RpCallback rpCallback) {
        login(rpCallback, false);
    }
    public void login(RpCallback rpCallback, boolean ignoreErrorStatus) {
        client.login(
                userModel.getAuth(),
                userModel.getUser(),
                userModel.getPassword(),
                userModel.getToken()
        ).then(new Callback(rpCallback, ignoreErrorStatus));
    }

    public void categoriaTV(RpCallback rpCallback) {
        categoriaTV(rpCallback, false);
    }

    public void categoriaTV(RpCallback rpCallback, boolean ignoreErrorStatus) {
        client.categoriaTV(
                userModel.getAuth(),
                userModel.getUser(),
                userModel.getPassword(),
                userModel.getToken()
        ).then(new Callback(rpCallback, ignoreErrorStatus));
    }

    public void categoriaFilmes(RpCallback rpCallback) {
        categoriaFilmes(rpCallback, false);
    }

    public void categoriaFilmes(RpCallback rpCallback, boolean ignoreErrorStatus) {
        client.categoriaFilmes(
                userModel.getAuth(),
                userModel.getUser(),
                userModel.getPassword(),
                userModel.getToken()
        ).then(new Callback(rpCallback, ignoreErrorStatus));
    }

    public void categoriaSeries(RpCallback rpCallback){
        categoriaSeries(rpCallback, false);
    }
    public void categoriaSeries(RpCallback rpCallback, boolean ignoreErrorStatus) {
        client.categoriaSeries(
                userModel.getAuth(),
                userModel.getUser(),
                userModel.getPassword(),
                userModel.getToken()
        ).then(new Callback(rpCallback, ignoreErrorStatus));
    }


    public void banner(RpCallback rpCallback) {
        banner(rpCallback, false);
    }
    public void banner(RpCallback rpCallback, boolean ignoreErrorStatus) {
        client.banner(
                userModel.getAuth(),
                userModel.getToken()
        ).then(new Callback(rpCallback, ignoreErrorStatus));
    }
    public void changeCipher(RpCallback rpCallback) {
        changeCipher(rpCallback, false);
    }

    public void changeCipher(RpCallback rpCallback, boolean ignoreErrorStatus) {
        client.changeCipher(
                userModel.getAuth(),
                userModel.getToken()
        ).then(new Callback(rpCallback, ignoreErrorStatus, false));
    }

    private final class Callback extends RestCallback {

        private final RpCallback rpCallback;
        private final boolean ignoreErrorStatus;
        private final boolean useDecrypt;

        public Callback(RpCallback rpCallback, boolean ignoreErrorStatus, boolean useDecrypt) {
            this.rpCallback = TypeValidator.nonNull(rpCallback, "RpCallback cannot be null.");
            this.ignoreErrorStatus = ignoreErrorStatus;
            this.useDecrypt = useDecrypt;
        }

        public Callback(RpCallback rpCallback, boolean ignoreErrorStatus){
            this(rpCallback, ignoreErrorStatus, true);
        }


        @Override
        public void onResponse(String body, HttpStatus status, HttpHeaders headers) throws Exception {
            if (!status.isSuccess() && !ignoreErrorStatus) {
                throw new IllegalArgumentException(
                        String.format("The status code %s, not is valid", status)
                );
            }
            if (isNumber(body)) {
                rpCallback.onResponse(
                        body,
                        ErrorCode.valueOf(Long.parseLong(body))
                );
            } else {
                if (useDecrypt) {
                    body = new CipherApi(
                            userModel.getSha1(),
                            userModel.getSha2()
                    ).decryptToString(body);
                }
                rpCallback.onResponse(body, null);
            }
        }

        @Override
        public void onFailure(Exception e) {
            rpCallback.onException(e);
        }

        private boolean isNumber(String text) {
            return text != null && text.trim().matches("[0-9]+");
        }
    }
}
