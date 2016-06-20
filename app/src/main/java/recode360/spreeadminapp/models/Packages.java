package recode360.spreeadminapp.models;


import java.io.Serializable;

/**
 * temporary model for holding the product information
 */
public class Packages implements Serializable {

    String name;
    String photo;
    String dimension_length;

    public String getDimension_width() {
        return dimension_width;
    }

    public void setDimension_width(String dimension_width) {
        this.dimension_width = dimension_width;
    }

    public String getDimension_length() {
        return dimension_length;
    }

    public void setDimension_length(String dimension_length) {
        this.dimension_length = dimension_length;
    }

    public String getDimension_height() {
        return dimension_height;
    }

    public void setDimension_height(String dimension_height) {
        this.dimension_height = dimension_height;
    }

    public String getDimension_unit() {
        return dimension_unit;
    }

    public void setDimension_unit(String dimension_unit) {
        this.dimension_unit = dimension_unit;
    }

    String dimension_width;
    String dimension_height;
    String dimension_unit;

    String seller_token;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPhoto() {
        return photo;
    }

    public void setPhoto(String photo) {
        this.photo = photo;
    }

    public String getSeller_token() {
        return seller_token;
    }

    public void setSeller_token(String seller_token) {
        this.seller_token = seller_token;
    }

}
