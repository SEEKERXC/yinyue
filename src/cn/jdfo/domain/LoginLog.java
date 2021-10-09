package cn.jdfo.domain;

import java.sql.Timestamp;
import java.util.Objects;

public class LoginLog {
    private int id;
    private int userId;
    private String ip;
    private Timestamp loginTime;
    private Timestamp logoutTime;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public String getIp() {
        return ip;
    }

    public void setIp(String ip) {
        this.ip = ip;
    }

    public Timestamp getLoginTime() {
        return loginTime;
    }

    public void setLoginTime(Timestamp loginTime) {
        this.loginTime = loginTime;
    }

    public Timestamp getLogoutTime() {
        return logoutTime;
    }

    public void setLogoutTime(Timestamp logoutTime) {
        this.logoutTime = logoutTime;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        LoginLog loginLog = (LoginLog) o;
        return id == loginLog.id &&
                userId == loginLog.userId &&
                Objects.equals(ip, loginLog.ip) &&
                Objects.equals(loginTime, loginLog.loginTime) &&
                Objects.equals(logoutTime, loginLog.logoutTime);
    }

    @Override
    public int hashCode() {

        return Objects.hash(id, userId, ip, loginTime, logoutTime);
    }
}
