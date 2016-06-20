package recode360.spreeadminapp.models;

/**
 * model class for shipments
 */
public class Shipments {

    private int id;
    private String tracking;
    private String number;
    private String cost;
    private String shipped_at;
    private String state;
    private String parcel_object_id;
    private String tracking_url;
    private String transaction_obj_id;
    private String label_url;
    private String return_shipment_obj_id;
    private String return_label_url;
    private String refund_object_id;
    private String order_id;
    private String stock_location_name;

    public String getNumber() {
        return number;
    }

    public void setNumber(String number) {
        this.number = number;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getTracking() {
        return tracking;
    }

    public void setTracking(String tracking) {
        this.tracking = tracking;
    }

    public String getCost() {
        return cost;
    }

    public void setCost(String cost) {
        this.cost = cost;
    }

    public String getShipped_at() {
        return shipped_at;
    }

    public void setShipped_at(String shipped_at) {
        this.shipped_at = shipped_at;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }

    public String getParcel_object_id() {
        return parcel_object_id;
    }

    public void setParcel_object_id(String parcel_object_id) {
        this.parcel_object_id = parcel_object_id;
    }

    public String getTracking_url() {
        return tracking_url;
    }

    public void setTracking_url(String tracking_url) {
        this.tracking_url = tracking_url;
    }

    public String getTransaction_obj_id() {
        return transaction_obj_id;
    }

    public void setTransaction_obj_id(String transaction_obj_id) {
        this.transaction_obj_id = transaction_obj_id;
    }

    public String getLabel_url() {
        return label_url;
    }

    public void setLabel_url(String label_url) {
        this.label_url = label_url;
    }

    public String getReturn_shipment_obj_id() {
        return return_shipment_obj_id;
    }

    public void setReturn_shipment_obj_id(String return_shipment_obj_id) {
        this.return_shipment_obj_id = return_shipment_obj_id;
    }

    public String getReturn_label_url() {
        return return_label_url;
    }

    public void setReturn_label_url(String return_label_url) {
        this.return_label_url = return_label_url;
    }

    public String getRefund_object_id() {
        return refund_object_id;
    }

    public void setRefund_object_id(String refund_object_id) {
        this.refund_object_id = refund_object_id;
    }

    public String getOrder_id() {
        return order_id;
    }

    public void setOrder_id(String order_id) {
        this.order_id = order_id;
    }

    public String getStock_location_name() {
        return stock_location_name;
    }

    public void setStock_location_name(String stock_location_name) {
        this.stock_location_name = stock_location_name;
    }


}
