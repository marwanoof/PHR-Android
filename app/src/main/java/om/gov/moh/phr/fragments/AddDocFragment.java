package om.gov.moh.phr.fragments;


import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import android.provider.MediaStore;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.RetryPolicy;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.HurlStack;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Random;
import java.util.UUID;

import om.gov.moh.phr.R;
import om.gov.moh.phr.interfaces.MediatorInterface;
import om.gov.moh.phr.interfaces.ToolbarControllerInterface;
import om.gov.moh.phr.models.CameraUtils;
import om.gov.moh.phr.models.GlobalMethodsKotlin;
import om.gov.moh.phr.models.MyProgressDialog;

import static android.app.Activity.RESULT_CANCELED;
import static android.provider.MediaStore.Files.FileColumns.MEDIA_TYPE_IMAGE;
import static om.gov.moh.phr.models.MyConstants.API_GET_TOKEN_BEARER;
import static om.gov.moh.phr.models.MyConstants.API_NEHR_URL;
import static om.gov.moh.phr.models.MyConstants.API_RESPONSE_CODE;
import static om.gov.moh.phr.models.MyConstants.API_RESPONSE_RESULT;
import static om.gov.moh.phr.models.MyConstants.CAMERA;
import static om.gov.moh.phr.models.MyConstants.GALLERY;

/**
 * A simple {@link Fragment} subclass.
 */
public class AddDocFragment extends Fragment {
    private ImageButton ibGalleryFile;
    private ImageButton ibCameraFile;
    private MyProgressDialog mProgressDialog;
    private ToolbarControllerInterface mToolbarControllerCallback;
    private MediatorInterface mMediatorCallback;
    private Context mContext;
    private RequestQueue mQueue;
    private Spinner spnDocTypes;
    private EditText etReference, etRemark, etFileName;
    private String imageStoragePath;
    private ArrayList<String> documentsTypesCodes;
    private String mDocType;
    private ImageView ivImageView;

    public AddDocFragment() {
        // Required empty public constructor
    }

    public static AddDocFragment newInstance() {
        AddDocFragment fragment = new AddDocFragment();
        Bundle args = new Bundle();
        //  args.putString(ARG_PARAM1, param1);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        mContext = context;

        mMediatorCallback = (MediatorInterface) context;
        mToolbarControllerCallback = (ToolbarControllerInterface) context;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_add_doc, container, false);
        TextView tvToolbarTitle = view.findViewById(R.id.tv_toolbar_title);
        tvToolbarTitle.setText(getResources().getString(R.string.upload_an_image));
        ImageButton ibToolbarBackButton = view.findViewById(R.id.ib_toolbar_back_button);
        tvToolbarTitle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mToolbarControllerCallback.customToolbarBackButtonClicked();
            }
        });
        ibToolbarBackButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mToolbarControllerCallback.customToolbarBackButtonClicked();
            }
        });
        enableHomeandRefresh(view);
        mQueue = Volley.newRequestQueue(mContext, new HurlStack(null, mMediatorCallback.getSocketFactory()));
        mProgressDialog = new MyProgressDialog(mContext);
        spnDocTypes = view.findViewById(R.id.spnr_DocType);
        etReference = view.findViewById(R.id.et_reference);
        etRemark = view.findViewById(R.id.et_remark);
        etFileName = view.findViewById(R.id.et_fileName);
        ibGalleryFile = view.findViewById(R.id.ib_galleryFile);
        ibGalleryFile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                choosePhotoFromGallery();
            }
        });
        ibCameraFile = view.findViewById(R.id.ib_cameraFile);
        ibCameraFile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                captureImageFromCamera();
            }
        });
        Button btnUpload = view.findViewById(R.id.btn_upload);
        getPermissionForAccessingCameraAndGallery();
        btnUpload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                uploadFile();
            }
        });
        Button btnCancel = view.findViewById(R.id.btn_cancel);
        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                spnDocTypes.setSelection(0);
                etReference.setText(null);
                etRemark.setText(null);
                etFileName.setText(null);
                imageStoragePath = null;
                ivImageView.setImageBitmap(null);
            }
        });
        ivImageView = view.findViewById(R.id.imageView);
        getDocType();
        return view;
    }

    private void enableHomeandRefresh(View view) {
        ImageButton ibRefresh = view.findViewById(R.id.ib_refresh);
        ibRefresh.setVisibility(View.GONE);
    }


    private void getDocType() {
        mProgressDialog.showDialog();

        String fullUrl = API_NEHR_URL + "master/getDocType";
        Log.d("fullURL", fullUrl);

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, fullUrl, null
                , new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                try {
                    if (mContext != null) {
                        if (response.getInt(API_RESPONSE_CODE) == 0) {
                            JSONArray documentsTypesArray = response.getJSONArray(API_RESPONSE_RESULT);
                            ArrayList<String> documentsTypes = new ArrayList<>();
                            documentsTypesCodes = new ArrayList<>();
                            String defaultText = getResources().getString(R.string.select_doc_type_msg);
                            documentsTypes.add(defaultText);
                            documentsTypesCodes.add("None");
                            for (int i = 0; i < documentsTypesArray.length(); i++) {
                                JSONObject documentTypeObj = documentsTypesArray.getJSONObject(i);
                                documentsTypes.add(documentTypeObj.getString("typeName"));
                                documentsTypesCodes.add(documentTypeObj.getString("typeCode"));
                            }
                            Log.d("AddDocFrag", "-Demo" + response.getJSONArray(API_RESPONSE_RESULT));
                            setupSelectDocTypeSpinner(documentsTypes, documentsTypesCodes);


                        } else {
                            noDocTypeFound();
                            mProgressDialog.dismissDialog();
                        }
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }

                mProgressDialog.dismissDialog();

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                if (mContext != null && isAdded()) {
                    Log.d("add_doc", error.toString());
                    error.printStackTrace();
                    mProgressDialog.dismissDialog();
                }
            }
        }) {
            //
            @Override
            public Map<String, String> getHeaders() {
                HashMap<String, String> headers = new HashMap<>();
//                headers.put("Accept", "application/json");
                headers.put("Content-Type", "application/json");
                headers.put("Authorization", API_GET_TOKEN_BEARER + mMediatorCallback.getAccessToken().getAccessTokenString());
                return headers;
            }

        };
        int socketTimeout = 30000;//30 seconds - change to what you want
        RetryPolicy policy = new DefaultRetryPolicy(socketTimeout, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT);
        jsonObjectRequest.setRetryPolicy(policy);

        mQueue.add(jsonObjectRequest);
    }

    private void noDocTypeFound() {
        String title = mContext.getResources().getString(R.string.alert_error_title);
        String body = mContext.getResources().getString(R.string.no_doc_type_found);
        String buttonText = mContext.getResources().getString(R.string.ok);
        GlobalMethodsKotlin.Companion.showAlertDialog(mContext,title,body,buttonText,R.drawable.ic_error);
    }

    private void setupSelectDocTypeSpinner(ArrayList<String> documentsTypes, ArrayList<String> documentsTypesCodes) {
        // Initializing an ArrayAdapter
        final ArrayAdapter<String> spinnerArrayAdapter = new ArrayAdapter<String>(
                mContext, android.R.layout.simple_spinner_dropdown_item, documentsTypes) {
            @NonNull
            @Override
            public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
                View v = super.getView(position, convertView, parent);

                Typeface externalFont=Typeface.createFromAsset(mContext.getAssets(), "sky.ttf");
                ((TextView) v).setTypeface(externalFont);

                return v;
            }

            @Override
            public boolean isEnabled(int position) {

                if (position == 0) {
                    // Disable the first item from Spinner
                    // First item will be use for hint
                    return false;
                } else {
                    return true;
                }
            }

            @Override
            public View getDropDownView(int position, View convertView,
                                        @NonNull ViewGroup parent) {
                View view = super.getDropDownView(position, convertView, parent);
                TextView tv = (TextView) view;
                Typeface externalFont = Typeface.createFromAsset(mContext.getAssets(), "sky.ttf");
                tv.setTypeface(externalFont);
                if (position == 0) {
                    // Set the hint text color gray
                    tv.setTextColor(Color.GRAY);
                } else {
                    tv.setTextColor(Color.BLACK);
                }
                return view;
            }
        };
        spinnerArrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spnDocTypes.setAdapter(spinnerArrayAdapter);
    }

    private void uploadFile() {
        if (spnDocTypes.getSelectedItemPosition() == 0) {
            Toast.makeText(mContext, getResources().getString(R.string.select_doc_type_msg), Toast.LENGTH_SHORT).show();
        } else if (etReference.getText().toString().isEmpty()) {
            Toast.makeText(mContext, getResources().getString(R.string.enter_reference_msg), Toast.LENGTH_SHORT).show();
        } else if (etRemark.getText().toString().isEmpty()) {
            Toast.makeText(mContext, getResources().getString(R.string.enter_remark_msg), Toast.LENGTH_SHORT).show();
        } else if (etFileName.getText().toString().isEmpty()) {
            Toast.makeText(mContext, getResources().getString(R.string.select_file_msg), Toast.LENGTH_SHORT).show();
        } else if (imageStoragePath != null) {
            File img = new File(imageStoragePath);
            long length = img.length();
            if (length > 5 * 1000 * 1000) {
                Toast.makeText(mContext, getResources().getString(R.string.too_large_file), Toast.LENGTH_SHORT).show();
            } else {
                getDocTypeCode();
                mProgressDialog.showDialog();

                String fullUrl = API_NEHR_URL + "file/uploadAndroid";
                Log.d("fileUpload", fullUrl);
                JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, fullUrl, getJSONRequestParams()
                        , new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        Toast.makeText(mContext, getResources().getString(R.string.success_upload_msg), Toast.LENGTH_SHORT).show();
                        mToolbarControllerCallback.customToolbarBackButtonClicked();
                        mProgressDialog.dismissDialog();
                    }
                }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        if(mContext!=null&&isAdded()) {
                            Log.d("upload_error", error.toString());
                            Log.i("jsonObjectRequest", "Error, Status Code " + error.networkResponse.statusCode);
                      //      Log.i("jsonObjectRequest", "URL: " + payOp.getURL());
                     //       Log.i("jsonObjectRequest", "Payload: " + payOp.getJson().toString());
                            Log.i("jsonObjectRequest", "Net Response to String: " + error.networkResponse.toString());
                            Log.i("jsonObjectRequest", "Error bytes: " + new String(error.networkResponse.data));
                            error.printStackTrace();
                            Toast.makeText(mContext, error.toString(), Toast.LENGTH_SHORT).show();
                            mProgressDialog.dismissDialog();
                        }
                    }
                }) {
                    //
                    @Override
                    public Map<String, String> getHeaders() {
                        HashMap<String, String> headers = new HashMap<>();
//                headers.put("Accept", "application/json");
                        headers.put("Content-Type", "application/json");
                        //         headers.put("Authorization", API_GET_TOKEN_BEARER + mMediatorCallback.getAccessToken().getAccessTokenString());


                        return headers;
                    }

                };
                int socketTimeout = 30000;//30 seconds - change to what you want
                RetryPolicy policy = new DefaultRetryPolicy(socketTimeout, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT);
                jsonObjectRequest.setRetryPolicy(policy);

                mQueue.add(jsonObjectRequest);
            }
        }
    }

    private void getDocTypeCode() {
        for (int i = 0; i < documentsTypesCodes.size(); i++) {
            if (spnDocTypes.getSelectedItemPosition() == i) {
                spnDocTypes.setSelection(i);
                mDocType = documentsTypesCodes.get(i);

            }
        }
    }

    private JSONObject getJSONRequestParams() {
        Map<String, Object> params = new HashMap<>();
        params.put("civilId", Long.parseLong(mMediatorCallback.getCurrentUser().getCivilId()));
        params.put("docType", mDocType);
        params.put("fileName", etFileName.getText().toString());
        params.put("imageString", convertPic(imageStoragePath));
        params.put("remarks", etRemark.getText().toString());
        params.put("sourceRef", etReference.getText().toString());
        Log.d("fileUpload", new JSONObject(params).toString());
        return new JSONObject(params);
    }

    private void choosePhotoFromGallery() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "Choose " +
                "Picture"), GALLERY);
    }

    private void captureImageFromCamera() {
        Intent cameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        File file = CameraUtils.getOutputMediaFile(MEDIA_TYPE_IMAGE);
        if (file != null) {
            imageStoragePath = file.getAbsolutePath();
        }
        Uri fileUri = CameraUtils.getOutputMediaFileUri(mContext, file);
        cameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, fileUri);
        // start the image capture Intent
        startActivityForResult(cameraIntent, CAMERA);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {

        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_CANCELED) {
            return;
        }
        if (requestCode == GALLERY) {
            if (data != null) {
                try {
                    final Uri imageUri = data.getData();
                    assert imageUri != null;
                    final InputStream imageStream = mContext.getContentResolver().openInputStream
                            (imageUri);

                    final Bitmap selectedImage = BitmapFactory.decodeStream(imageStream);
                    Uri imageRealUri = getImageUri(mContext, selectedImage);
                    Bitmap bitmap = CameraUtils.optimizeBitmap(12,
                            getRealPathFromURI(imageRealUri));
                    imageStoragePath = getRealPathFromURI(imageRealUri);
                    ivImageView.setImageBitmap(selectedImage);
                    ibCameraFile.setEnabled(false);
                } catch (IOException e) {
                    e.printStackTrace();
                }

            }
        } else if (requestCode == CAMERA) {
            Bitmap cameraBitmap = CameraUtils.optimizeBitmap(8,
                    imageStoragePath);
            Uri imageRealUri = getImageUri(mContext, cameraBitmap);
            imageStoragePath=getRealPathFromURI(imageRealUri);
            ivImageView.setImageBitmap(cameraBitmap);
            ibGalleryFile.setEnabled(false);
        }

        char[] chars = "abcdefghijklmnopqrstuvwxyz".toCharArray();
        StringBuilder sb = new StringBuilder(3);
        Random random = new Random();
        for (int i = 0; i < 3; i++) {
            char c = chars[random.nextInt(chars.length)];
            sb.append(c);
        }
        String output = "File" + mMediatorCallback.getCurrentUser().getCivilId() + sb.toString() + ".jpg";
        etFileName.setText(output);


    }

    private String getRealPathFromURI(Uri uri) {
        Cursor cursor = mContext.getContentResolver().query(uri, null, null, null, null);
        assert cursor != null;
        cursor.moveToFirst();
        int idx = cursor.getColumnIndex(MediaStore.Images.ImageColumns.DATA);
        String realPath = cursor.getString(idx);
        cursor.close();
        return realPath;
    }

    private Uri getImageUri(Context inContext, Bitmap inImage) {
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        inImage.compress(Bitmap.CompressFormat.JPEG, 100, bytes);
        String path = MediaStore.Images.Media.insertImage(inContext.getContentResolver(), inImage, "IMG_" + Calendar.getInstance().getTimeInMillis(), null);
        return Uri.parse(path);
    }

    private void getPermissionForAccessingCameraAndGallery() {

        if (ContextCompat.checkSelfPermission(mContext, android.Manifest.permission.CAMERA) !=
                PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(Objects.requireNonNull(getActivity()), new String[]{android.Manifest
                    .permission.CAMERA, Manifest
                    .permission.WRITE_EXTERNAL_STORAGE}, 1);
        }
    }

    private String convertPic(String path) {
        Bitmap bitmap = BitmapFactory.decodeFile(path);
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG, 90, baos);
        byte[] b = baos.toByteArray();
        return Base64.encodeToString(b, Base64.DEFAULT);
    }
}
