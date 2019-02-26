package projetannuel.idc.masterinfo.unicaen.mobilenany.entities;

public class Area {
    private int id;
    private int location_id;
    private String label;
    private String adresse;
    private String category;
    private String longitude;
    private String latitude;

    public void setId(int id) {
        this.id = id;
    }

    public void setLocation_id(int location_id) {
        this.location_id = location_id;
    }

    public void setLabel(String label) {
        this.label = label;
    }

    public void setAdresse(String adresse) {
        this.adresse = adresse;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public void setLongitude(String longitude) {
        this.longitude = longitude;
    }

    public void setLatitude(String latitude) {
        this.latitude = latitude;
    }

    public long getId() {
        return id;
    }

    public long getLocation_id() {
        return location_id;
    }

    public String getLabel() {
        return label;
    }

    public String getAdresse() {
        return adresse;
    }

    public String getCategory() {
        return category;
    }

    public String getLongitude() {
        return longitude;
    }

    public String getLatitude() {
        return latitude;
    }
}
