/**
 * @Author 范承祥
 * @CreateTime 2020/7/23
 * @UpdateTime 2020/7/23
 */
package com.sosotaxi.driver.model;

import android.os.Parcel;
import android.os.Parcelable;

import java.io.Serializable;

/**
 * 司机VO类
 */
public class DriverVo implements Parcelable {
    /** 用户id */
    private Long userId;

    /** 所在城市 */
    private String city;

    /** 车品牌 */
    private String carBrand;

    /** 车型 */
    private String carModel;

    /** 车辆颜色 */
    private String carColor;

    /** 车牌号码 */
    private String licensePlate;

    /** 司机能提供的服务类型 */
    private Short serviceType;

    /** 驾驶证号码 */
    private String driverLicenseNumber;

    /** 行驶证中的车辆识别代码 */
    private String vin;

    public DriverVo(){}

    protected DriverVo(Parcel in) {
        if (in.readByte() == 0) {
            userId = null;
        } else {
            userId = in.readLong();
        }
        city = in.readString();
        carBrand = in.readString();
        carModel = in.readString();
        carColor = in.readString();
        licensePlate = in.readString();
        int tmpServiceType = in.readInt();
        serviceType = tmpServiceType != Integer.MAX_VALUE ? (short) tmpServiceType : null;
        driverLicenseNumber = in.readString();
        vin = in.readString();
    }

    public static final Creator<DriverVo> CREATOR = new Creator<DriverVo>() {
        @Override
        public DriverVo createFromParcel(Parcel in) {
            return new DriverVo(in);
        }

        @Override
        public DriverVo[] newArray(int size) {
            return new DriverVo[size];
        }
    };

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public String getCarBrand() {
        return carBrand;
    }

    public void setCarBrand(String carBrand) {
        this.carBrand = carBrand;
    }

    public String getCarModel() {
        return carModel;
    }

    public void setCarModel(String carModel) {
        this.carModel = carModel;
    }

    public String getCarColor() {
        return carColor;
    }

    public void setCarColor(String carColor) {
        this.carColor = carColor;
    }

    public String getLicensePlate() {
        return licensePlate;
    }

    public void setLicensePlate(String licensePlate) {
        this.licensePlate = licensePlate;
    }

    public Short getServiceType() {
        return serviceType;
    }

    public void setServiceType(Short serviceType) {
        this.serviceType = serviceType;
    }

    public String getDriverLicenseNumber() {
        return driverLicenseNumber;
    }

    public void setDriverLicenseNumber(String driverLicenseNumber) {
        this.driverLicenseNumber = driverLicenseNumber;
    }

    public String getVin() {
        return vin;
    }

    public void setVin(String vin) {
        this.vin = vin;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        if (userId == null) {
            dest.writeByte((byte) 0);
        } else {
            dest.writeByte((byte) 1);
            dest.writeLong(userId);
        }
        dest.writeString(city);
        dest.writeString(carBrand);
        dest.writeString(carModel);
        dest.writeString(carColor);
        dest.writeString(licensePlate);
        dest.writeInt(serviceType != null ? (int) serviceType : Integer.MAX_VALUE);
        dest.writeString(driverLicenseNumber);
        dest.writeString(vin);
    }
}
