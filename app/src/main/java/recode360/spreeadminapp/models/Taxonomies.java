package recode360.spreeadminapp.models;

/**
 * Hold details related to taxonomies
 */
public class Taxonomies {

    private int id;
    private String name;

    //every taxonomy has a root taxon
    private Taxon root;

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

    public Taxon getRoot() {
        return root;
    }

    public void setRoot(Taxon root) {
        this.root = root;
    }

}
