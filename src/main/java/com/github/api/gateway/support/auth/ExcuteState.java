package com.github.api.gateway.support.auth;

/**
 * Created by chdyan on 16/8/3.
 */
public enum ExcuteState {

    NOEXCUTE, //未执行
    EXCUTE; //执行


    public boolean isExcute() {
        return EXCUTE.equals(this);
    }

}
