package recode360.spreeadminapp.models;

import java.io.Serializable;

public class Carrier implements Serializable {

    /**
     * Model class for Carrier accounts on GoShippo.
     */
    private static final long serialVersionUID = 1L;

    private String name;

    //id associated with the carrier
    private String accountId;

    //keeps track if the carrier is active or not
    private boolean isSelected;

    public Carrier() {

    }

    public Carrier(String name, String accountId) {

        this.name = name;
        this.accountId = accountId;

    }

    public Carrier(String name, String accountId, boolean isSelected) {

        this.name = name;
        this.accountId = accountId;
        this.isSelected = isSelected;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getAccountId() {
        return accountId;
    }

    public void setAccountId(String accountId) {
        this.accountId = accountId;
    }

    public boolean isSelected() {
        return isSelected;
    }

    public void setSelected(boolean isSelected) {
        this.isSelected = isSelected;
    }

}
