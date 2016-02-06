package recode360.spreeadminapp.models;


public class Country {
    private int id;
    private String iso_name;
    private String iso;
    private String iso3;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getIso_name() {
        return iso_name;
    }

    public void setIso_name(String iso_name) {
        this.iso_name = iso_name;
    }

    public String getIso() {
        return iso;
    }

    public void setIso(String iso) {
        this.iso = iso;
    }

    public String getIso3() {
        return iso3;
    }

    public void setIso3(String iso3) {
        this.iso3 = iso3;
    }

    public String getNumcode() {
        return numcode;
    }

    public void setNumcode(String numcode) {
        this.numcode = numcode;
    }

    private String name;
    private String numcode;
}
