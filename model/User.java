package sushchak.bohdan.curabitur.model;


public class User {
    private String userId;
    private String name;
    private String email;
    private String avatar;
    private String status;
    private String phone;

    public User() {
    }

    public User(String userId, String name, String email, String avatar, String status, String phone) {
        this.userId = userId;
        this.name = name;
        this.email = email;
        this.avatar = avatar;
        this.status = status;
        this.phone = phone;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getAvatar() {
        return avatar;
    }

    public void setAvatar(String avatar) {
        this.avatar = avatar;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }
}
