/**
 * @Author 范承祥
 * @CreateTime 2020/7/23
 * @UpdateTime 2020/7/25
 */
package com.sosotaxi.driver.model;

import org.json.JSONArray;
import org.json.JSONObject;

/**
 * 乘客类
 */
public class Passenger {
    /** 用户id */
    private Long userId;

    /** 昵称 */
    private String nickname;

    /** 行业 */
    private Short industry;

    /** 公司 */
    private String company;

    /** 职业 */
    private String occupation;

    /** 紧急联系人 */
    private JSONObject urgentContact;

    /** 常用地址 */
    private JSONObject commonAddress;

    /** 乘车偏好 */
    private JSONArray preference;

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public String getNickname() {
        return nickname;
    }

    public void setNickname(String nickname) {
        this.nickname = nickname;
    }

    public Short getIndustry() {
        return industry;
    }

    public void setIndustry(Short industry) {
        this.industry = industry;
    }

    public String getCompany() {
        return company;
    }

    public void setCompany(String company) {
        this.company = company;
    }

    public String getOccupation() {
        return occupation;
    }

    public void setOccupation(String occupation) {
        this.occupation = occupation;
    }

    public JSONObject getUrgentContact() {
        return urgentContact;
    }

    public void setUrgentContact(JSONObject urgentContact) {
        this.urgentContact = urgentContact;
    }

    public JSONObject getCommonAddress() {
        return commonAddress;
    }

    public void setCommonAddress(JSONObject commonAddress) {
        this.commonAddress = commonAddress;
    }

    public JSONArray getPreference() {
        return preference;
    }

    public void setPreference(JSONArray preference) {
        this.preference = preference;
    }
}
