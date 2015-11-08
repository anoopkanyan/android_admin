package recode360.spreeadminapp.models;

import java.math.BigDecimal;

/**
 *
 */
public class DetailedProduct {

    private int id;
    private String name;
    private String sku; //stock keeping unit
    private BigDecimal price;
    private BigDecimal cost_price;
    private Float weight;
    private Float height;
    private Float depth;
    private Float width;
    private String display_price;
    private String permalink;    //API response returns it as slug
    private String description;
    boolean in__stock;
    boolean is_master;
    private int shipping_category_id;
    private OptionValues option_values;
    private Image images;

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

    public String getSku() {
        return sku;
    }

    public void setSku(String sku) {
        this.sku = sku;
    }

    public BigDecimal getPrice() {
        return price;
    }

    public void setPrice(BigDecimal price) {
        this.price = price;
    }

    public BigDecimal getCost_price() {
        return cost_price;
    }

    public void setCost_price(BigDecimal cost_price) {
        this.cost_price = cost_price;
    }

    public Float getWeight() {
        return weight;
    }

    public void setWeight(Float weight) {
        this.weight = weight;
    }

    public Float getHeight() {
        return height;
    }

    public void setHeight(Float height) {
        this.height = height;
    }

    public Float getDepth() {
        return depth;
    }

    public void setDepth(Float depth) {
        this.depth = depth;
    }

    public Float getWidth() {
        return width;
    }

    public void setWidth(Float width) {
        this.width = width;
    }

    public String getDisplay_price() {
        return display_price;
    }

    public void setDisplay_price(String display_price) {
        this.display_price = display_price;
    }

    public String getPermalink() {
        return permalink;
    }

    public void setPermalink(String permalink) {
        this.permalink = permalink;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public boolean isIn__stock() {
        return in__stock;
    }

    public void setIn__stock(boolean in__stock) {
        this.in__stock = in__stock;
    }

    public boolean is_master() {
        return is_master;
    }

    public void setIs_master(boolean is_master) {
        this.is_master = is_master;
    }

    public int getShipping_category_id() {
        return shipping_category_id;
    }

    public void setShipping_category_id(int shipping_category_id) {
        this.shipping_category_id = shipping_category_id;
    }



    public Image getImages() {
        return images;
    }

    public void setImages(Image images) {
        this.images = images;
    }

    public OptionValues getOption_values() {
        return option_values;
    }

    public void setOption_values(OptionValues option_values) {
        this.option_values = option_values;
    }




}
