package recode360.spreeadminapp.utils;

import android.content.Context;
import android.text.Html;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.math.BigDecimal;

import recode360.spreeadminapp.R;
import recode360.spreeadminapp.models.DetailedProduct;
import recode360.spreeadminapp.models.Image;

/*
Helper static methods and methods to parse JSON data or HTML data or setting some properties at more than one place in the app.
 */


public class Utils {

    public static int getToolbarHeight(Context context) {
        int height = (int) context.getResources().getDimension(R.dimen.abc_action_bar_default_height_material);
        return height;
    }

    public static int getStatusBarHeight(Context context) {
        int height = (int) context.getResources().getDimension(R.dimen.statusbar_size);
        return height;
    }

    /*
    parse JSONObject response and return a DetailedProduct
     which can either be a master or a variant.
     */
    public static DetailedProduct parseProduct(JSONObject obj) {
        DetailedProduct tempProduct = new DetailedProduct();
        obj = obj.optJSONObject("master");        //extract the master into obj itself

        try {
            tempProduct.setId(obj.getInt("id"));
            tempProduct.setName(obj.getString("name"));
            tempProduct.setSku(obj.getString("sku"));
            tempProduct.setPrice(new BigDecimal(obj.getString("price")));

            if (obj.getString("cost_price") != "null") {
                tempProduct.setCost_price(new BigDecimal(obj.getString("cost_price")));
            }
            if (obj.getString("weight") != "null") {
                tempProduct.setWeight(Float.parseFloat(obj.getString("weight")));
            }
            if (obj.getString("height") != "null") {
                tempProduct.setHeight(Float.parseFloat(obj.getString("height")));
            }
            if (obj.getString("depth") != "null") {
                tempProduct.setDepth(Float.parseFloat(obj.getString("depth")));
            }
            if (obj.getString("width") != "null") {
                tempProduct.setWidth(Float.parseFloat(obj.getString("width")));
            }

            tempProduct.setDisplay_price(obj.getString("display_price"));
            tempProduct.setPermalink(obj.getString("slug"));            //returned as slug in the API response
            tempProduct.setDescription(obj.getString("description"));
            tempProduct.setIn__stock(obj.getBoolean("in_stock"));
            tempProduct.setIs_master(obj.getBoolean("is_master"));

            //create an image object associated with the product to store all the information related to the image

            JSONArray imageArray = obj.getJSONArray("images");
            Image array_image[] = new Image[imageArray.length()];
            for (int i = 0; i < imageArray.length(); i++) {
                JSONObject imageObject = imageArray.getJSONObject(i);
                Image img = new Image();
                img.setId(imageObject.getInt("id"));
                img.setPosition(imageObject.getInt("position"));
                img.setMini_url(imageObject.getString("mini_url"));
                img.setSmall_url(imageObject.getString("small_url"));
                img.setProduct_url(imageObject.getString("product_url"));
                img.setLarge_url(imageObject.getString("large_url"));
                array_image[i] = img;
                tempProduct.setImages(array_image);
                //    Log.d("image",tempProduct.getImages()[0].getMini_url());
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return tempProduct;
    }

    //Returns the combination of first letters of FirstName and a SirName
    public static String parseName(String s) {

        if (s != null) {
            int i = 0;
            while (s.charAt(i) != ' ') {
                i++;
            }
            while (s.charAt(i) == ' ') {
                i++;
            }

            return "" + s.charAt(0) + s.charAt(i);
        }
        return "";
    }

    //Removes the HTML tags if found in any response
    public static String stripHtml(String html) {
        return Html.fromHtml(html).toString();
    }

}