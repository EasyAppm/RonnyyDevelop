package com.easyapp.ronnyy.rpeasyapp;
import com.easyapp.core.TypeValidator;
import com.http.ceas.ClientFactory;
import com.http.ceas.callback.RestCallback;
import com.http.ceas.core.HttpHeaders;
import com.http.ceas.core.HttpStatus;

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

    public void login(final RpCallback rpCallback) {
        client.login(userModel.getName(), userModel.getToken()).then(new ClientCallback(rpCallback));
    }

    public void banner(final RpCallback rpCallback) {
        client.banner(userModel.getName(), userModel.getToken()).then(new ClientCallback(rpCallback));
    }

    public void changeCipher(final RpCallback rpCallback) {
        client.changeCipher(userModel.getName(), userModel.getToken()).then(new ClientCallback(rpCallback));
    }

    private final class ClientCallback extends RestCallback {

        private final RpCallback rpCallback;

        public ClientCallback(RpCallback rpCallback) {
            this.rpCallback = TypeValidator.nonNull(rpCallback, "RpCallback cannot be null.");
        }

        @Override
        public void onFailure(Exception p1) {
            rpCallback.onException(p1);
        }

        @Override
        public void onResponse(String body, HttpStatus p2, HttpHeaders p3) throws Exception {
            String sha1 = userModel.getSha1();
            String sha2 = userModel.getSha2();
            ErrorCode errorCode = null;
            try {
                if (body.trim().matches("[0-9]+"))
                    errorCode = ErrorCode.valueOf(Long.valueOf(body));
            } catch (Exception ignore) {}

            if (errorCode == null) {
                body = new String(new CipherApi(sha1, sha2).decrypt(body));
            }

            rpCallback.onResponse(
                body,
                errorCode
            );
        }

    }

}
