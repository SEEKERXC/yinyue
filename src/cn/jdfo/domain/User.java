package cn.jdfo.domain;

import cn.jdfo.tool.View;
import com.fasterxml.jackson.annotation.JsonView;

import java.sql.Timestamp;
import java.util.Collection;
import java.util.List;
import java.util.Objects;

public class User {
    @JsonView(View.SimpleUser.class)
    private int id;
    @JsonView(View.SimpleUser.class)
    private String email;
    private String password;
    @JsonView(View.SimpleUser.class)
    private String name;
    @JsonView(View.SimpleUser.class)
    private Timestamp lastVisit;
    @JsonView(View.SimpleUser.class)
    private String lastIp;
    private Collection<Songlist> songlists;

    public User() {
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Timestamp getLastVisit() {
        return lastVisit;
    }

    public void setLastVisit(Timestamp lastVisit) {
        this.lastVisit = lastVisit;
    }

    public String getLastIp() {
        return lastIp;
    }

    public void setLastIp(String lastIp) {
        this.lastIp = lastIp;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        User user = (User) o;
        return id == user.id &&
                Objects.equals(email, user.email) &&
                Objects.equals(password, user.password) &&
                Objects.equals(name, user.name) &&
                Objects.equals(lastVisit, user.lastVisit) &&
                Objects.equals(lastIp, user.lastIp);
    }

    @Override
    public int hashCode() {

        return Objects.hash(id, email, password, name, lastVisit, lastIp);
    }

    public Collection<Songlist> getSonglists() {
        return songlists;
    }

    public void setSonglists(Collection<Songlist> songlists) {
        this.songlists = songlists;
    }
}
