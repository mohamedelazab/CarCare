package com.mobile.carcare.carcare.model;

public class Service {

    private String id;
    private String serviceTitle;
    private String serviceDescription;
    private String serviceImg;
    private String servicePrice;

    public Service() {

    }

    public Service(String id, String serviceTitle, String serviceDescription, String serviceImg, String servicePrice) {
        this.id = id;
        this.serviceTitle = serviceTitle;
        this.serviceDescription = serviceDescription;
        this.serviceImg = serviceImg;
        this.servicePrice = servicePrice;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getServiceTitle() {
        return serviceTitle;
    }

    public void setServiceTitle(String serviceTitle) {
        this.serviceTitle = serviceTitle;
    }

    public String getServiceDescription() {
        return serviceDescription;
    }

    public void setServiceDescription(String serviceDescription) {
        this.serviceDescription = serviceDescription;
    }

    public String getServiceImg() {
        return serviceImg;
    }

    public void setServiceImg(String serviceImg) {
        this.serviceImg = serviceImg;
    }

    public String getServicePrice() {
        return servicePrice;
    }

    public void setServicePrice(String servicePrice) {
        this.servicePrice = servicePrice;
    }
}