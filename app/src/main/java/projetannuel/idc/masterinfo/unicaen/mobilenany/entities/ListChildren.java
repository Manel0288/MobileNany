package projetannuel.idc.masterinfo.unicaen.mobilenany.entities;

import java.util.ArrayList;
import java.util.List;

public class ListChildren {
    List<Child> data;

    public ListChildren(List<Child> listEnfant) {
        this.data = listEnfant;
    }

    public void setListEnfant(List<Child> listEnfant) {
        this.data = listEnfant;
    }

    public List<Child> getListEnfant() {
        return data;
    }
}
