package com.cs.wasselha.Customer;

public class Notifications {
    int id,user_id;
    int image;
    String titleNotification, descriptionNotification, notificationTime,notificationDate,user_type;

    public Notifications(int id, int user_id, int image, String titleNotification, String descriptionNotification, String notificationTime, String notificationDate, String user_type) {
        this.id = id;
        this.user_id = user_id;
        this.image = image;
        this.titleNotification = titleNotification;
        this.descriptionNotification = descriptionNotification;
        this.notificationTime = notificationTime;
        this.notificationDate = notificationDate;
        this.user_type = user_type;
    }

    public Notifications(int id, int user_id, String titleNotification, String descriptionNotification, String notificationTime, String notificationDate, String user_type) {
        this.id = id;
        this.user_id = user_id;
        this.titleNotification = titleNotification;
        this.descriptionNotification = descriptionNotification;
        this.notificationTime = notificationTime;
        this.notificationDate = notificationDate;
        this.user_type = user_type;
    }

    public Notifications(int id, int image, String titleNotification, String descriptionNotification, String notificationTime, String notificationDate) {
        this.id = id;
        this.image = image;
        this.titleNotification = titleNotification;
        this.descriptionNotification = descriptionNotification;
        this.notificationTime = notificationTime;
        this.notificationDate = notificationDate;
    }

    public Notifications(int image, String titleNotification, String descriptionNotification, String notificationTime) {
        this.image = image;
        this.titleNotification = titleNotification;
        this.descriptionNotification = descriptionNotification;
        this.notificationTime = notificationTime;
    }

    public int getUser_id() {
        return user_id;
    }

    public void setUser_id(int user_id) {
        this.user_id = user_id;
    }

    public String getUser_type() {
        return user_type;
    }

    public void setUser_type(String user_type) {
        this.user_type = user_type;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getNotificationDate() {
        return notificationDate;
    }

    public void setNotificationDate(String notificationDate) {
        this.notificationDate = notificationDate;
    }

    public int getImage() {
        return image;
    }

    public void setImage(int image) {
        this.image = image;
    }

    public String getTitleNotification() {
        return titleNotification;
    }

    public void setTitleNotification(String titleNotification) {
        this.titleNotification = titleNotification;
    }

    public String getDescriptionNotification() {
        return descriptionNotification;
    }

    public void setDescriptionNotification(String descriptionNotification) {
        this.descriptionNotification = descriptionNotification;
    }

    public String getNotificationTime() {
        return notificationTime;
    }

    public void setNotificationTime(String notificationTime) {
        this.notificationTime = notificationTime;
    }
}
