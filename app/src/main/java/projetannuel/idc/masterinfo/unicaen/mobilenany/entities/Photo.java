package projetannuel.idc.masterinfo.unicaen.mobilenany.entities;

import android.os.Parcel;
import android.os.Parcelable;

public class Photo implements Parcelable {
    private int id;
    private String image;

    public Photo(int id, String img){
        this.id = id;
        this.image = img;
    }

    public void setImage(String img) {
        this.image = img;
    }

    public int getId() {
        return id;
    }

    public String getImage() {
        return image;
    }


    // Parcelling part
    public Photo(Parcel in){
        this.id = in.readInt();
        this.image = in.readString();
    }
    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(this.id);
        dest.writeString(this.image);;
    }

    public static final Creator CREATOR = new Creator() {
        public Photo createFromParcel(Parcel in) {
            return new Photo(in);
        }

        public Photo[] newArray(int size) {
            return new Photo[size];
        }
    };
}
