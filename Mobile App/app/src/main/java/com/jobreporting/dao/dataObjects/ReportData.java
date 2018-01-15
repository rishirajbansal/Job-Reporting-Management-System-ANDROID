/*
 * Licensed To: ThoughtExecution & 9sistemes
 * Authored By: Rishi Raj Bansal
 * Developed in: 2016
 *
 * ===========================================================================
 * This is FULLY owned and COPYRIGHTED by ThoughtExecution
 * This code may NOT be RESOLD or REDISTRIBUTED under any circumstances, and is only to be used with this application
 * Using the code from this application in another application is strictly PROHIBITED and not PERMISSIBLE
 * ===========================================================================
 */

package com.jobreporting.dao.dataObjects;


public class ReportData {

    private int id;

    private String data;

    private byte[] photo;

    private String photoName;

    private byte[] sign;


    public String getData() {

        return data;
    }

    public void setData(String data) {

        this.data = data;
    }

    public int getId() {

        return id;
    }

    public void setId(int id) {

        this.id = id;
    }

    public byte[] getPhoto() {

        return photo;
    }

    public void setPhoto(byte[] photo) {

        this.photo = photo;
    }

    public String getPhotoName() {

        return photoName;
    }

    public void setPhotoName(String photoName) {

        this.photoName = photoName;
    }

    public byte[] getSign() {
        return sign;
    }

    public void setSign(byte[] sign) {
        this.sign = sign;
    }
}
