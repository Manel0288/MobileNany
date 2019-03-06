package projetannuel.idc.masterinfo.unicaen.mobilenany.entities;

import android.os.Parcel;
import android.os.Parcelable;

public class Child implements Parcelable {
    private int id;
    private String nom;
    private String prenom;
    private String email;
    private String adresse;
    private String tel;
    private String image_url;

    public Child(int id, String nom, String prenom, String email, String adresse, String tel, String image_url){
        this.id = id;
        this.nom = nom;
        this.prenom = prenom;
        this.email = email;
        this.adresse = adresse;
        this.tel = tel;
        this.image_url = image_url;
    }

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

    public void setImage(String img) {
        this.image_url = img;
    }

    public void setTel(String tel) {
        this.tel = tel;
    }

    public int getId() {
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

    public String getImage() {
        return image_url;
    }

    // Parcelling part
    public Child(Parcel in){
        //this.image = (Photo) in.readParcelable(Photo.class.getClassLoader());
        this.id = in.readInt();
        this.nom = in.readString();
        this.prenom =  in.readString();
        this.email = in.readString();
        this.adresse =  in.readString();
        this.tel =  in.readString();
        this.image_url = in.readString();
    }
    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        //dest.writeParcelable(this.image, flags);
        dest.writeInt(this.id);
        dest.writeString(this.nom);
        dest.writeString(this.prenom);
        dest.writeString(this.email);
        dest.writeString(this.adresse);
        dest.writeString(this.tel);
        dest.writeString(this.image_url);
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
