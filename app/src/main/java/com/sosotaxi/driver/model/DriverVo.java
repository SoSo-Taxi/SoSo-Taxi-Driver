package com.sosotaxi.driver.model;

/**
 * @Author 范承祥
 * @CreateTime 2020/7/23
 * @UpdateTime 2020/7/23
 */
public class DriverVo {
    /** 用户id */
    private Long userId;

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
}
