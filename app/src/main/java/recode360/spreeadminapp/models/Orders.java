package recode360.spreeadminapp.models;

import java.math.BigDecimal;

/**
 * Created by ANOOP on 11/30/2015.
 */


//don't need all the details of the Orders here, this is jut to display the orders in a list

public class Orders {

    private int id;
    private String number;
    private BigDecimal item_total;
    private String display_item_total;
    private BigDecimal total;
    private String display_total;
    private String state;
    private BigDecimal adjustment_total;
    private int user_id;
    private String created_at;
    private String updated_at;
    private String completed_at;
    private BigDecimal payment_total;
    private String shipment_state;
    private String payment_state;
    private String email;
    private int total_quantity;
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

    public String getDisplay_item_total() {
        return display_item_total;
    }

    public void setDisplay_item_total(String display_item_total) {
        this.display_item_total = display_item_total;
    }

    public BigDecimal getTotal() {
        return total;
    }

    public void setTotal(BigDecimal total) {
        this.total = total;
    }

    public String getDisplay_total() {
        return display_total;
    }

    public void setDisplay_total(String display_total) {
        this.display_total = display_total;
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

    public int getTotal_quantity() {
        return total_quantity;
    }

    public void setTotal_quantity(int total_quantity) {
        this.total_quantity = total_quantity;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }


}
