package recode360.spreeadminapp.models;


import java.io.Serializable;

/**
 * Tracks all the items present the cart, contains a list of products.
 */
public class CartItem implements Serializable {

    private int qunatity;
    private Product product;


    public int getQunatity() {
        return qunatity;
    }

    public void setQunatity(int qunatity) {
        this.qunatity = qunatity;
    }

    public Product getProduct() {
        return product;
    }

    public void setProduct(Product product) {
        this.product = product;
    }

}
