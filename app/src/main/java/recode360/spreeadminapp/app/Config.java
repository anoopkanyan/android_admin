package recode360.spreeadminapp.app;

import com.paypal.android.sdk.payments.PayPalConfiguration;
import com.paypal.android.sdk.payments.PayPalPayment;

/**
 * Holds the URLs, API keys etc.
 */
public class Config {

    // server URL configuration

    //URL to the store
    public static String URL_STORE = "";


    //API key for the admin, to be extracted using LoginActivity
    public static String API_KEY = "";

    //FULL name of the user, is extracted from the JSON requests by the LoginActivity
    public static String USER_FULL_NAME = "";

    public static String USER_EMAIL = "";

    //temporarily storing password, until we figure out the image upload API
    public static String USER_PASSWORD = "";

    //Shippo API Key
    public static String USER_SHIPPO_KEY = "";


    public static final String PAYPAL_CLIENT_ID = "Aew0IbP79FaIFh2I3jffx8Vj5MfSo74a1oytcoq69I-LoCOE4JjHIU_tKhEBB06bhK-9qglK33H29te6";
    public static final String PAYPAL_CLIENT_SECRET = "";

    public static final String PAYPAL_ENVIRONMENT = PayPalConfiguration.ENVIRONMENT_SANDBOX;
    public static final String PAYMENT_INTENT = PayPalPayment.PAYMENT_INTENT_SALE;
    public static final String DEFAULT_CURRENCY = "USD";

}
