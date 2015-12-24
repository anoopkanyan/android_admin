package recode360.spreeadminapp.models;

import java.math.BigDecimal;

/**
 * Model to store the details of an order in ready for shipping state
 */
public class ShipOrders {
    private int id;
    private String number;
    private BigDecimal item_total;
    private BigDecimal total;
    private BigDecimal ship_total;
    private String state;   //Order state such as cart,address or complete
    private BigDecimal adjustment_total;
    private int user_id;
    private String created_at;
    private String updated_at;
    private String completed_at;
    private BigDecimal payment_total;
    private String shipment_state;
    private String payment_state;
    private String email;
    private String special_instructions;
    private String channel;
    private BigDecimal included_tax_total;
    private BigDecimal additional_tax_total;
    private String display_included_tax_total;
    private String display_additional_tax_total;
    private BigDecimal tax_total;
    private String currency;
    private String display_item_total;
    private int total_quantity;
    private String display_total;
    private String display_ship_total;
    private String display_tax_total;
    private String token;


    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getNumber() {
        return number;
    }

    public void setNumber(String number) {
        this.number = number;
    }

    public BigDecimal getItem_total() {
        return item_total;
    }

    public void setItem_total(BigDecimal item_total) {
        this.item_total = item_total;
    }

    public BigDecimal getTotal() {
        return total;
    }

    public void setTotal(BigDecimal total) {
        this.total = total;
    }

    public BigDecimal getShip_total() {
        return ship_total;
    }

    public void setShip_total(BigDecimal ship_total) {
        this.ship_total = ship_total;
    }

    public BigDecimal getAdjustment_total() {
        return adjustment_total;
    }

    public void setAdjustment_total(BigDecimal adjustment_total) {
        this.adjustment_total = adjustment_total;
    }

    public int getUser_id() {
        return user_id;
    }

    public void setUser_id(int user_id) {
        this.user_id = user_id;
    }

    public String getCreated_at() {
        return created_at;
    }

    public void setCreated_at(String created_at) {
        this.created_at = created_at;
    }

    public String getUpdated_at() {
        return updated_at;
    }

    public void setUpdated_at(String updated_at) {
        this.updated_at = updated_at;
    }

    public String getCompleted_at() {
        return completed_at;
    }

    public void setCompleted_at(String completed_at) {
        this.completed_at = completed_at;
    }

    public BigDecimal getPayment_total() {
        return payment_total;
    }

    public void setPayment_total(BigDecimal payment_total) {
        this.payment_total = payment_total;
    }

    public String getShipment_state() {
        return shipment_state;
    }

    public void setShipment_state(String shipment_state) {
        this.shipment_state = shipment_state;
    }

    public String getPayment_state() {
        return payment_state;
    }

    public void setPayment_state(String payment_state) {
        this.payment_state = payment_state;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getSpecial_instructions() {
        return special_instructions;
    }

    public void setSpecial_instructions(String special_instructions) {
        this.special_instructions = special_instructions;
    }

    public String getChannel() {
        return channel;
    }

    public void setChannel(String channel) {
        this.channel = channel;
    }

    public BigDecimal getIncluded_tax_total() {
        return included_tax_total;
    }

    public void setIncluded_tax_total(BigDecimal included_tax_total) {
        this.included_tax_total = included_tax_total;
    }

    public BigDecimal getAdditional_tax_total() {
        return additional_tax_total;
    }

    public void setAdditional_tax_total(BigDecimal additional_tax_total) {
        this.additional_tax_total = additional_tax_total;
    }

    public String getDisplay_included_tax_total() {
        return display_included_tax_total;
    }

    public void setDisplay_included_tax_total(String display_included_tax_total) {
        this.display_included_tax_total = display_included_tax_total;
    }

    public String getDisplay_additional_tax_total() {
        return display_additional_tax_total;
    }

    public void setDisplay_additional_tax_total(String display_additional_tax_total) {
        this.display_additional_tax_total = display_additional_tax_total;
    }

    public BigDecimal getTax_total() {
        return tax_total;
    }

    public void setTax_total(BigDecimal tax_total) {
        this.tax_total = tax_total;
    }

    public String getCurrency() {
        return currency;
    }

    public void setCurrency(String currency) {
        this.currency = currency;
    }

    public String getDisplay_item_total() {
        return display_item_total;
    }

    public void setDisplay_item_total(String display_item_total) {
        this.display_item_total = display_item_total;
    }

    public int getTotal_quantity() {
        return total_quantity;
    }

    public void setTotal_quantity(int total_quantity) {
        this.total_quantity = total_quantity;
    }

    public String getDisplay_total() {
        return display_total;
    }

    public void setDisplay_total(String display_total) {
        this.display_total = display_total;
    }

    public String getDisplay_ship_total() {
        return display_ship_total;
    }

    public void setDisplay_ship_total(String display_ship_total) {
        this.display_ship_total = display_ship_total;
    }

    public String getDisplay_tax_total() {
        return display_tax_total;
    }

    public void setDisplay_tax_total(String display_tax_total) {
        this.display_tax_total = display_tax_total;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

}
