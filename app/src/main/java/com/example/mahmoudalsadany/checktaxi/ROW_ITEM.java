package com.example.mahmoudalsadany.checktaxi;

/**
 * Created by mahmoudalsadany on 03/02/18.
 */

public class ROW_ITEM {
    String name, num, image_url;
    String driver_name, driver_id, driver_image_url, driver_address;

    public ROW_ITEM(String name, String num, String image_url) {
        this.name = name;
        this.num = num;
        this.image_url = image_url;
    }

    public ROW_ITEM(String driver_name, String driver_id, String driver_image_url, String driver_address) {
        this.driver_name = driver_name;
        this.driver_id = driver_id;
        this.driver_image_url = driver_image_url;
        this.driver_address = driver_address;
    }

    public String getImage_url() {
        return image_url;
    }

    public String getName() {
        return name;
    }

    public String getNum() {
        return num;
    }

    public String getDriver_address() {
        return driver_address;
    }

    public String getDriver_id() {
        return driver_id;
    }

    public String getDriver_image_url() {
        return driver_image_url;
    }

    public String getDriver_name() {
        return driver_name;
    }
}
