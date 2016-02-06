package recode360.spreeadminapp.models;


public class Licenses {

    private String title;
    private String description;
    private String url;     //not added right now, maybe later

    public Licenses(String title, String description, String url) {
        this.title = title;
        this.description = description;
        this.url = url;

    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }
    
}
