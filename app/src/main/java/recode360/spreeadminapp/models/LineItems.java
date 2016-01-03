package recode360.spreeadminapp.models;

//considering LineItems model same as Product model during testing purposes
//to be edited in future depending upon our needs

public class LineItems extends DetailedProduct {


    private String single_display_amount;
    private int quantity;

    public String getTemp_img() {
        return temp_img;
    }

    public void setTemp_img(String temp_img) {
        this.temp_img = temp_img;
    }

    private String temp_img;

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }


    public String getSingle_display_amount() {
        return single_display_amount;
    }

    public void setSingle_display_amount(String single_display_amount) {
        this.single_display_amount = single_display_amount;
    }

}
