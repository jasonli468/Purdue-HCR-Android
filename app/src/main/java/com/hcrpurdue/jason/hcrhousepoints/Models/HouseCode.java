/**
 * Model Class for the House Code object on Firestore
 */

package com.hcrpurdue.jason.hcrhousepoints.Models;

import androidx.annotation.NonNull;

import java.util.Map;

public class HouseCode {

    public final String CODE_KEY = "Code";
    public final String CODE_NAME_KEY = "CodeName";
    public final String PERMISSION_LEVEL = "PermissionLevel";
    public final String FLOOR_ID_KEY = "FloorID";
    public final String HOUSE_NAME_KEY = "House";

    private String code;
    private String codeName;
    private int permissionLevel;
    private String floorId;
    private String houseName;


    public HouseCode(Map<String,Object> dataMap){
        this.code = (String) dataMap.get(CODE_KEY);
        this.codeName = (String) dataMap.get(CODE_NAME_KEY);
        this.permissionLevel = (int) dataMap.get(PERMISSION_LEVEL);
        this.floorId = (String) dataMap.get(FLOOR_ID_KEY);
        this.houseName = (String) dataMap.get(HOUSE_NAME_KEY);
    }

    public String getCode() {
        return code;
    }

    public String getCodeName() {
        return codeName;
    }

    public int getPermissionLevel() {
        return permissionLevel;
    }

    public String getFloorId() {
        return floorId;
    }

    public String getHouseName() {
        return houseName;
    }

}
