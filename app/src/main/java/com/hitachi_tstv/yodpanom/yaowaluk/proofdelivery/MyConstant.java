package com.hitachi_tstv.yodpanom.yaowaluk.proofdelivery;

import android.app.Application;
import android.content.Context;

/**
 * Created by musz on 10/11/2016.
 */

public class MyConstant{
    //Explicit
    private int iconAnInt = R.drawable.warning48; // Image for icon 48x48
    private String titleHaveSpaceString = "มีช่องว่าง";
    private String messageHaveeSpaceString = "กรุณากรอกข้อมูลให้ครบทุกช่อง!!";
    private String urlUserString = "http://service.eternity.co.th/TmsPXD/app/CenterService/getUser.php";
    private String[] columLogin = new String[]{"drv_id", "drv_name", "drv_username","drv_pic","gender"};
    private String titleUserFalesString = "ชื่อผู้ใช้ผิดพลาด";
    private String messageUserFalesString = "ไม่มีชื่อผู้ใช้ในฐานข้อมูลของเรา";
    private String titlePasswordFalse = "รหัสผ่านผิดพลาด";
    private String messagePasswordFalse = "กรุณาลองใหม่ คุณใส่รหัสผ่านผิด";
    private String urlDataWhereDriverID = "http://service.eternity.co.th/TmsPXD/app/CenterService/getPlan.php";
    private String urlDataWhereDriverIDanDate = "http://service.eternity.co.th/TmsPXD/app/CenterService/getPlanDtl.php";
    private String urlDetailWherePlanId = "http://service.eternity.co.th/TmsPXD/app/CenterService/getTripDtl1.php";
    private String urlContainerList = "http://service.eternity.co.th/TmsPXD/app/CenterService/getTripDtl_Listview.php";
    private String urlArrivalGPS = "http://service.eternity.co.th/TmsPXD/app/CenterService/updateArrivalFromDriver.php";
    private String urlSaveImage = "http://service.eternity.co.th/TmsPXD/app/CenterService/uploadPicture.php";
    private String urlSaveImagePath = "http://service.eternity.co.th/TmsPXD/app/CenterService/setPicturePath.php";
    private String urlSaveSignPath = "http://service.eternity.co.th/TmsPXD/app/CenterService/setSignPath.php";
    private String urlUpdateStatus = "http://service.eternity.co.th/TmsPXD/app/CenterService/updateStatusConfirm.php";
    private String urlUpdateLoad = "http://service.eternity.co.th/TmsPXD/app/CenterService/updateLoad.php";
    private String urlDriverPicture = "http://service.eternity.co.th/TmsPXD/app/MasterData/driver/avatar/";

    public String getUrlUpdateLoad() {
        return urlUpdateLoad;
    }

    public String getUrlDriverPicture() {
        return urlDriverPicture;
    }

    public String getUrlUpdateStatus() {
        return urlUpdateStatus;
    }

    public String getUrlSaveSignPath() {
        return urlSaveSignPath;
    }

    public String getUrlSaveImagePath() {
        return urlSaveImagePath;
    }

    public String getUrlSaveImage() {
        return urlSaveImage;
    }

    public String getUrlArrivalGPS() {
        return urlArrivalGPS;
    }

    public String getUrlContainerList() {
        return urlContainerList;
    }

    public String getUrlDetailWherePlanId() {
        return urlDetailWherePlanId;
    }

    public String getUrlDataWhereDriverIDanDate() {
        return urlDataWhereDriverIDanDate;
    }

    public String getUrlDataWhereDriverID() {
        return urlDataWhereDriverID;
    }

    public String getTitlePasswordFalse() {
        return titlePasswordFalse;
    }

    public String getMessagePasswordFalse() {
        return messagePasswordFalse;
    }

    public String getTitleUserFalesString() {
        return titleUserFalesString;
    }

    public String getMessageUserFalesString() {
        return messageUserFalesString;
    }

    public String[] getColumLogin() {
        return columLogin;
    }

    public String getUrlUserString() {
        return urlUserString;
    }

    public int getIconAnInt() {
        return iconAnInt;
    }

    public String getTitleHaveSpaceString() {
        return titleHaveSpaceString;
    }

    public String getMessageHaveeSpaceString() {
        return messageHaveeSpaceString;
    }
}//Main Class