package com.easyapp.ronnyy.rpeasyapp;
public enum ErrorCode {

    SERVER(454545355,"Não foi possível conectar ao servidor"),
    DNS(12123312,"URL inválida DNS"),
    ACCOUNT(505060,"Informe usuário e senha corretamente"),
    EMPTY(20241,"Sem resultado na consultar"),
    TOKEN(101276,"Token_ID nao existe no firebase"),
    HEADER(35352355,"O cabeçalho RPEASYAPP não foi enviado");
    public final long code;
    public final String message;

    ErrorCode(long code, String message){
        this.code = code;
        this.message = message;
    };

    public static ErrorCode valueOf(long code){
        for(ErrorCode errorCode : values()){
            if(code == errorCode.code){
                return errorCode;
            }
        }
        throw new IllegalArgumentException("The code: " + code + " invalid");
    }

}
