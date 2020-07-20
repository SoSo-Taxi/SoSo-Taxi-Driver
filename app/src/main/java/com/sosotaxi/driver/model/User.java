/**
 * @Author 范承祥
 * @CreateTime 2020/7/12
 * @UpdateTime 2020/7/20
 */
package com.sosotaxi.driver.model;

/**
 * 用户类
 */
public class User {
    /** 用户名 */
    private String userName;

    /**
     * 手机号
     */
    private String Phone;

    /** 密码 */
    private String password;

    /** 角色 */
    private String role;

    /** 记住我 */
    private boolean rememberMe;

    /** 令牌 */
    private String token;


    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        // 去除加号
        if(userName.startsWith("+")){
            userName=userName.substring(1);
        }
        // 去除空格
        int index=userName.indexOf(" ");
        if(index!=-1){
            String code=userName.substring(0,index);
            String phone=userName.substring(index+1);
            userName=code+phone;
        }
        this.userName = userName;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public boolean isRememberMe() {
        return rememberMe;
    }

    public void setRememberMe(boolean rememberMe) {
        this.rememberMe = rememberMe;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }
}
