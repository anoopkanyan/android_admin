package recode360.spreeadminapp.models;

import java.util.Date;

/**
 * Created by ANOOP
 */
public class Image {

    private int id;
    private int position;
    private String attachment_content_type;
    private String attachment_file_name;

    private String type;
    private Date attachment_updated_at;
    private int attachment_width;
    private int attachment_height;
    private String alt;
    private String viewable_type;

    private int viewable_id;
    private String mini_url;
    private String small_url;
    private String product_url;
    private String large_url;


    public int getViewable_id() {
        return viewable_id;
    }

    public void setViewable_id(int viewable_id) {
        this.viewable_id = viewable_id;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getPosition() {
        return position;
    }

    public void setPosition(int position) {
        this.position = position;
    }

    public String getAttachment_content_type() {
        return attachment_content_type;
    }

    public void setAttachment_content_type(String attachment_content_type) {
        this.attachment_content_type = attachment_content_type;
    }

    public String getAttachment_file_name() {
        return attachment_file_name;
    }

    public void setAttachment_file_name(String attachment_file_name) {
        this.attachment_file_name = attachment_file_name;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public Date getAttachment_updated_at() {
        return attachment_updated_at;
    }

    public void setAttachment_updated_at(Date attachment_updated_at) {
        this.attachment_updated_at = attachment_updated_at;
    }

    public int getAttachment_width() {
        return attachment_width;
    }

    public void setAttachment_width(int attachment_width) {
        this.attachment_width = attachment_width;
    }

    public int getAttachment_height() {
        return attachment_height;
    }

    public void setAttachment_height(int attachment_height) {
        this.attachment_height = attachment_height;
    }

    public String getAlt() {
        return alt;
    }

    public void setAlt(String alt) {
        this.alt = alt;
    }

    public String getViewable_type() {
        return viewable_type;
    }

    public void setViewable_type(String viewable_type) {
        this.viewable_type = viewable_type;
    }

    public String getMini_url() {
        return mini_url;
    }

    public void setMini_url(String mini_url) {
        this.mini_url = mini_url;
    }

    public String getSmall_url() {
        return small_url;
    }

    public void setSmall_url(String small_url) {
        this.small_url = small_url;
    }

    public String getProduct_url() {
        return product_url;
    }

    public void setProduct_url(String product_url) {
        this.product_url = product_url;
    }

    public String getLarge_url() {
        return large_url;
    }

    public void setLarge_url(String large_url) {
        this.large_url = large_url;
    }


}
