package recode360.spreeadminapp.models;

/**
 * Created by ANOOP on
 */
public class OptionValues {


    private int id;
    private String name;
    private String presentation;
    private String option_type_name;

    public String getOption_type_id() {
        return option_type_id;
    }

    public void setOption_type_id(String option_type_id) {
        this.option_type_id = option_type_id;
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

    public String getPresentation() {
        return presentation;
    }

    public void setPresentation(String presentation) {
        this.presentation = presentation;
    }

    public String getOption_type_name() {
        return option_type_name;
    }

    public void setOption_type_name(String option_type_name) {
        this.option_type_name = option_type_name;
    }

    private String option_type_id;


}
