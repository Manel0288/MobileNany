package projetannuel.idc.masterinfo.unicaen.mobilenany.entities;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.List;

public class ChildAndHisAreas implements Parcelable {
    private Child child;
    private Area[] areas;

    public ChildAndHisAreas(Child child, Area[] areas){
        this.child = child;
        this.areas = areas;
    }

    public void setChild(Child child) {
        this.child = child;
    }

    public void setAreas(Area[] areas) {
        this.areas = areas;
    }

    public Child getChild() {
        return child;
    }

    public Area[] getAreas() {
        return areas;
    }

    // Parcelling part
    public ChildAndHisAreas(Parcel in){
        this.child = in.readParcelable(Child.class.getClassLoader());
        this.areas = in.createTypedArray(Area.CREATOR);
    }
    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeParcelable(this.child, flags);
        dest.writeTypedArray(areas, flags);
    }

    public static final Creator CREATOR = new Creator() {
        public ChildAndHisAreas createFromParcel(Parcel in) {
            return new ChildAndHisAreas(in);
        }

        public ChildAndHisAreas[] newArray(int size) {
            return new ChildAndHisAreas[size];
        }
    };
}
