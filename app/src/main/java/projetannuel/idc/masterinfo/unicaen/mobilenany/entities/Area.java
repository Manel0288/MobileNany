package projetannuel.idc.masterinfo.unicaen.mobilenany.entities;

import android.os.Parcel;
import android.os.Parcelable;

public class Area implements Parcelable {
    private int id;
    private int location_id;
    private String label;
    private String adresse;
    private String category;
    private String longitude;
    private String latitude;
    private String from;
    private String to;

    protected Area(Parcel in) {
        id = in.readInt();
        location_id = in.readInt();
        label = in.readString();
        adresse = in.readString();
        category = in.readString();
        longitude = in.readString();
        latitude = in.readString();
        from = in.readString();
        to = in.readString();
    }

    public static final Creator<Area> CREATOR = new Creator<Area>() {
        @Override
        public Area createFromParcel(Parcel in) {
            return new Area(in);
        }

        @Override
        public Area[] newArray(int size) {
            return new Area[size];
        }
    };

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

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeInt(id);
        parcel.writeInt(location_id);
        parcel.writeString(label);
        parcel.writeString(adresse);
        parcel.writeString(category);
        parcel.writeString(longitude);
        parcel.writeString(latitude);
        parcel.writeString(from);
        parcel.writeString(to);
    }
}
