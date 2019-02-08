package projetannuel.idc.masterinfo.unicaen.mobilenany.entities;

import android.os.Parcel;
import android.os.Parcelable;

public class Child implements Parcelable {
    private long id;
    private String nom;
    private String prenom;
    private String email;
    private String adresse;
    private String tel;


    public void setNom(String nom) {
        this.nom = nom;
    }

    public void setPrenom(String prenom) {
        this.prenom = prenom;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public void setAdresse(String adresse) {
        this.adresse = adresse;
    }

    public void setTel(String tel) {
        this.tel = tel;
    }

    public long getId() {
        return id;
    }

    public String getNom() {
        return nom;
    }

    public String getPrenom() {
        return prenom;
    }

    public String getEmail() {
        return email;
    }

    public String getAdresse() {
        return adresse;
    }

    public String getTel() {
        return tel;
    }

    // Parcelling part
    public Child(Parcel in){
        this.id = in.readLong();
        this.nom = in.readString();
        this.prenom =  in.readString();
        this.email = in.readString();
        this.adresse =  in.readString();
        this.tel =  in.readString();
    }
    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeLong(this.id);
        dest.writeString(this.nom);
        dest.writeString(this.prenom);
        dest.writeString(this.email);
        dest.writeString(this.adresse);
        dest.writeString(this.tel);
    }

    public static final Parcelable.Creator CREATOR = new Parcelable.Creator() {
        public Child createFromParcel(Parcel in) {
            return new Child(in);
        }

        public Child[] newArray(int size) {
            return new Child[size];
        }
    };
}
