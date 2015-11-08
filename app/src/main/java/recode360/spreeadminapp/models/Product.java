package recode360.spreeadminapp.models;


import java.math.BigDecimal;
import java.util.List;

/**
 * To work on API response for the display in the ListView
 * Not for master/variant, DetailedProduct to deal with master/variants.
 */

public class Product {
    private int id;
    private String name;
    private String description;
    private BigDecimal price;
    private String display_price;
    private String available_on;
    private String permalink;    //API response returns it as slug
    private int shipping_category_id;
    private List<Integer> taxon_ids;
    String image;   //to get the url of master product image's URL to display in the ListView
    private int total_on_hand;
    private boolean has_variants;

    private ProductProperties product_properties;
    private OptionTypes option_types;


    public OptionTypes getOption_types() {
        return option_types;
    }

    public void setOption_types(OptionTypes option_types) {
        this.option_types = option_types;
    }

    public ProductProperties getProduct_properties() {
        return product_properties;
    }

    public void setProduct_properties(ProductProperties product_properties) {
        this.product_properties = product_properties;
    }


    public int getTotal_on_hand() {
        return total_on_hand;
    }

    public void setTotal_on_hand(int total_on_hand) {
        this.total_on_hand = total_on_hand;
    }



    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }



    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public BigDecimal getPrice() {
        return price;
    }

    public void setPrice(BigDecimal price) {
        this.price = price;
    }

    public String getDisplay_price() {
        return display_price;
    }

    public void setDisplay_price(String display_price) {
        this.display_price = display_price;
    }

    public String getAvailable_on() {
        return available_on;
    }

    public void setAvailable_on(String available_on) {
        this.available_on = available_on;
    }

    public String getPermalink() {
        return permalink;
    }

    public void setPermalink(String permalink) {
        this.permalink = permalink;
    }

    public List<Integer> getTaxon_ids() {
        return taxon_ids;
    }

    public void setTaxon_ids(List<Integer> taxon_ids) {
        this.taxon_ids = taxon_ids;
    }

    public int getShipping_category_id() {
        return shipping_category_id;
    }

    public void setShipping_category_id(int shipping_category_id) {
        this.shipping_category_id = shipping_category_id;
    }

    public boolean isHas_variants() {
        return has_variants;
    }

    public void setHas_variants(boolean has_variants) {
        this.has_variants = has_variants;
    }

}
