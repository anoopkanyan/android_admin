package recode360.spreeadminapp.Fragments;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Response;
import com.android.volley.VolleyError;

import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

import recode360.spreeadminapp.R;
import recode360.spreeadminapp.app.AppController;
import recode360.spreeadminapp.utils.PhotoMultipartRequest;

/**
 * Created by ANOOP on 11/6/2015.
 */

public class UploadImageFragment extends Fragment {

    private Button buttonChoose;
    private Button buttonUpload;

    private ImageView imageView;

    private EditText editTextName;

    private Bitmap bitmap;
    private File file;

    private int PICK_IMAGE_REQUEST = 1;

    private String UPLOAD_URL ="http://upload.photobox.com/en/";

    private String KEY_IMAGE = "image";
    private String KEY_NAME = "name";




    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.image_upload_layout, container, false);

        buttonChoose = (Button) view.findViewById(R.id.buttonChoose);
        buttonUpload = (Button) view.findViewById(R.id.buttonUpload);

        editTextName = (EditText) view.findViewById(R.id.editText);

        imageView  = (ImageView) view.findViewById(R.id.imageView);


        buttonChoose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                showFileChooser();
            }
        });


        buttonUpload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {



                PhotoMultipartRequest imageUploadReq = new PhotoMultipartRequest(UPLOAD_URL, new Response.ErrorListener() {

                    @Override
                    public void onErrorResponse(VolleyError error) {
                      //  VolleyLog.d(TAG, "Error: " + error.getMessage());
                        Toast.makeText(getActivity(),
                                error.getMessage(), Toast.LENGTH_SHORT).show();
                        // hide the progress dialog

                    }} ,new  Response.Listener< JSONObject >() {

                    @Override
                    public void onResponse(JSONObject response) {
                        Log.d("phas", response.toString());
                    }
                }
                        ,file );

                imageUploadReq.setRetryPolicy(new DefaultRetryPolicy(
                        50000,
                        DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                        DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));



                AppController.getInstance().addToRequestQueue(imageUploadReq);

            }
        });



        return view;

    }



    //method to choose file from gallery
    private void showFileChooser() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "Select Picture"), PICK_IMAGE_REQUEST);
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == PICK_IMAGE_REQUEST && resultCode == Activity.RESULT_OK && data != null && data.getData() != null) {
            Uri filePath = data.getData();
            try {
                //Getting the Bitmap from Gallery
                bitmap = MediaStore.Images.Media.getBitmap(getActivity().getContentResolver(), filePath);
                //Setting the Bitmap to ImageView
                imageView.setImageBitmap(bitmap);


                file = new File(getActivity().getCacheDir(), "file");
                file.createNewFile();

                //Convert bitmap to byte array

                ByteArrayOutputStream bos = new ByteArrayOutputStream();
                bitmap.compress(Bitmap.CompressFormat.PNG, 0 /*ignored for PNG*/, bos);
                byte[] bitmapdata = bos.toByteArray();

                //write the bytes in file
                FileOutputStream fos = new FileOutputStream(file);
                fos.write(bitmapdata);
                fos.flush();
                fos.close();



            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }



}
