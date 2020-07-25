/**
 * @Author 范承祥
 * @CreateTime 2020/7/21
 * @UpdateTime 2020/7/21
 */
package com.sosotaxi.driver.model;

import android.os.Parcel;
import android.os.Parcelable;

import java.io.Serializable;

/**
 * 司机类
 */
public class Driver extends User implements Parcelable{


    private long driverId;
    private double accountFlow;
    private int workSeconds;
    private int orderNum;
    private int serviceScore;
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

    /**
     * 司机是否接单
     */
    private boolean isAvailable;

    /**
     * 当前位置
     */
    private LocationPoint currentPoint;

    /**
     * 是否听单
     */
    private boolean startListening;

    private boolean isDispatched;

    public Driver(){}

    protected Driver(Parcel in) {
        carBrand = in.readString();
        carModel = in.readString();
        carColor = in.readString();
        licensePlate = in.readString();
        int tmpServiceType = in.readInt();
        serviceType = tmpServiceType != Integer.MAX_VALUE ? (short) tmpServiceType : null;
        driverLicenseNumber = in.readString();
        vin = in.readString();
        isAvailable = in.readByte() != 0;
        currentPoint = in.readParcelable(LocationPoint.class.getClassLoader());
        startListening = in.readByte() != 0;
        isDispatched = in.readByte() != 0;
    }

    public static final Creator<Driver> CREATOR = new Creator<Driver>() {
        @Override
        public Driver createFromParcel(Parcel in) {
            return new Driver(in);
        }

        @Override
        public Driver[] newArray(int size) {
            return new Driver[size];
        }
    };

    @Override
    public Long getUserId() {
        return super.getUserId();
    }

    @Override
    public void setUserId(Long userId) {
        super.setUserId(userId);
        this.driverId = userId;
    }

    public double getAccountFlow() {
        return accountFlow;
    }

    public void setAccountFlow(double accountFlow) {
        this.accountFlow = accountFlow;
    }

    public int getWorkSeconds() {
        return workSeconds;
    }

    public void setWorkSeconds(int workSeconds) {
        this.workSeconds = workSeconds;
    }

    public int getOrderNum() {
        return orderNum;
    }

    public void setOrderNum(int orderNum) {
        this.orderNum = orderNum;
    }

    public int getServiceScore() {
        return serviceScore;
    }

    public void setServiceScore(int serviceScore) {
        this.serviceScore = serviceScore;
    }

    public boolean getAvailable() {
        return isAvailable;
    }

    public void setAvailable(boolean available) {
        isAvailable = available;
    }

    public LocationPoint getCurrentPoint() {
        return currentPoint;
    }

    public void setCurrentPoint(LocationPoint currentPoint) {
        this.currentPoint = currentPoint;
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

    public boolean getStartListening() {
        return startListening;
    }

    public void setStartListening(boolean startListening) {
        this.startListening = startListening;
    }

    public boolean getDispatched() {
        return isDispatched;
    }

    public void setDispatched(boolean dispatched) {
        isDispatched = dispatched;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(carBrand);
        dest.writeString(carModel);
        dest.writeString(carColor);
        dest.writeString(licensePlate);
        dest.writeInt(serviceType != null ? (int) serviceType : Integer.MAX_VALUE);
        dest.writeString(driverLicenseNumber);
        dest.writeString(vin);
        dest.writeByte((byte) (isAvailable ? 1 : 0));
        dest.writeParcelable(currentPoint, flags);
        dest.writeByte((byte) (startListening ? 1 : 0));
        dest.writeByte((byte) (isDispatched ? 1 : 0));
    }
}
