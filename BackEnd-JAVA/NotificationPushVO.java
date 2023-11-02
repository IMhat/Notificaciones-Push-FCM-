package cl.mpsoft.txm.vo;

import java.util.Date;

public class NotificationPushVO extends IdentityVO {
    private int userId;
    private String token;
    // Constructor, getters y setters

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

}


