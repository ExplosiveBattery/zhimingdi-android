package cn.hellovega.zhimingdi.model;

import cn.hellovega.zhimingdi.model.network.NetworkClient;
import cn.hellovega.zhimingdi.model.network.NetworkDefine;

/**
 * Created by vega on 3/20/18.
 */

public class Marker {
    double longitude;
    double latitude;
    String name;
    String iconUrl;

    public double getLongitude() {
        return longitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }

    public double getLatitude() {
        return latitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getIconUrl() {
        return NetworkDefine.ZHIMINGDI_BASE_URL+"pic/"+iconUrl+".jpg";
    }

    public void setIconUrl(String iconUrl) {
        this.iconUrl = iconUrl;
    }
}
