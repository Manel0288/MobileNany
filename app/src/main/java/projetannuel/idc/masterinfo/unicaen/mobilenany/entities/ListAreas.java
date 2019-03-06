package projetannuel.idc.masterinfo.unicaen.mobilenany.entities;

import java.util.List;

public class ListAreas {
    List<Area> data;

    public ListAreas(List<Area> areas) {
        this.data = areas;
    }

    public void setListLieux(List<Area> listLieux) {
        this.data = listLieux;
    }

    public List<Area> getListLieux() {
        return data;
    }
}
