package projetannuel.idc.masterinfo.unicaen.mobilenany.entities;

public class ChildLocation {
    private String longitude;
    private  String latitude;


    public ChildLocation(String longitude, String latitude) {
        this.longitude = longitude;
        this.latitude = latitude;
    }

    public void setLongitude(String longitude) {
        this.longitude = longitude;
    }

    public void setLatitude(String latitude) {
        this.latitude = latitude;
    }

    public String getLongitude() {
        return longitude;
    }

    public String getLatitude() {
        return latitude;
    }
}

