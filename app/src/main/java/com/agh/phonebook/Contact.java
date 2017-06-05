package com.agh.phonebook;

import java.net.URL;

/**
 * Created by Damian on 2017-06-04.
 */

public class Contact {

    private String  phoneNumber;
    private String contactName;
    private String avatarUrl;
    private String contactInfo;

    protected Contact(){

    }

    protected Contact(String newContactName, String newPhoneNumber, String newAvatarUrl,
                      String newContactInfo){
        contactName = newContactName;
        phoneNumber = newPhoneNumber;
        avatarUrl = newAvatarUrl;
        contactInfo = newContactInfo;
    }

    protected Contact(String newContactName, String newPhoneNumber, String newContactInfo){
        contactName = newContactName;
        phoneNumber = newPhoneNumber;
        avatarUrl = "google.com";
        contactInfo = newContactInfo;
    }

    public void setContactInfo(String contactInfo) {
        this.contactInfo = contactInfo;
    }

    public void setAvatarUrl(String avatarUrl) {
        this.avatarUrl = avatarUrl;
    }

    public void setContactName(String contactName) {
        this.contactName = contactName;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public String getContactName() {
        return contactName;
    }

    public String getAvatarUrl() {
        return avatarUrl;
    }

    public String getContactInfo() {
        return contactInfo;
    }
}

