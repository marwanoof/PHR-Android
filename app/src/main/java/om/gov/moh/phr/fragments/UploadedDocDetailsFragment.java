package om.gov.moh.phr.fragments;


import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Switch;
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
import com.bumptech.glide.Glide;

import org.json.JSONException;
import org.json.JSONObject;


import java.util.HashMap;
import java.util.Map;

import om.gov.moh.phr.R;
import om.gov.moh.phr.apimodels.ApiUploadsDocsHolder;
import om.gov.moh.phr.interfaces.MediatorInterface;
import om.gov.moh.phr.interfaces.ToolbarControllerInterface;
import om.gov.moh.phr.models.MyProgressDialog;

import static om.gov.moh.phr.models.MyConstants.API_GET_TOKEN_BEARER;
import static om.gov.moh.phr.models.MyConstants.API_NEHR_URL;
import static om.gov.moh.phr.models.MyConstants.API_RESPONSE_CODE;
import static om.gov.moh.phr.models.MyConstants.API_RESPONSE_MESSAGE;
import static om.gov.moh.phr.models.MyConstants.API_RESPONSE_RESULT;

/**
 * A simple {@link Fragment} subclass.
 */
public class UploadedDocDetailsFragment extends Fragment {
    private static final String PARAM_API_UPLOADED_DOC_ITEM = "PARAM_API_UPLOADED_DOC_ITEM";
    private static final String API_DOC_INFO = API_NEHR_URL + "file/downloadBFile/";
    private static final String API_DELETE_DOC_INFO = API_NEHR_URL + "file/delete/";
    private static final String UPLOADED_FILE_KEY = "imageString";
    private RequestQueue mQueue;
    private MyProgressDialog mProgressDialog;
    private Context mContext;
    private MediatorInterface mMediatorCallback;
    private ToolbarControllerInterface mToolbarControllerCallback;
    private Switch switchPublish;
    private ImageButton ibHome, ibDelete;
    private ApiUploadsDocsHolder.ApiUploadDocInfo mUploadedDocInfo;
    private ImageView ivFileUploaded;
    private TextView tvAlert;

    public UploadedDocDetailsFragment() {
        // Required empty public constructor
    }

    public static UploadedDocDetailsFragment newInstance(ApiUploadsDocsHolder.ApiUploadDocInfo docObj) {
        UploadedDocDetailsFragment fragment = new UploadedDocDetailsFragment();
        Bundle args = new Bundle();
        args.putSerializable(PARAM_API_UPLOADED_DOC_ITEM, docObj);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        mContext = context;
        mMediatorCallback = (MediatorInterface) context;
        mToolbarControllerCallback = (ToolbarControllerInterface) context;
        //  mListener = (DialogPdfViewer.OnDialogPdfViewerListener) context;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mUploadedDocInfo = (ApiUploadsDocsHolder.ApiUploadDocInfo) getArguments().getSerializable(PARAM_API_UPLOADED_DOC_ITEM);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_uploaded_doc_details, container, false);
        ImageButton ibToolbarBackButton = view.findViewById(R.id.ib_toolbar_back_button);
        TextView tvToolbarTitle = view.findViewById(R.id.tv_toolbar_title);
        tvToolbarTitle.setText(getResources().getString(R.string.uploaded_docs));
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
        switchPublish = view.findViewById(R.id.ib_publish);
        ibDelete = view.findViewById(R.id.ib_delete);
        ibDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showAlertDialog();
            }
        });
        ibHome = view.findViewById(R.id.ib_home);
        ibHome.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                backToHome();
            }
        });
        ivFileUploaded = view.findViewById(R.id.iv_uploadedFile);
        tvAlert = view.findViewById(R.id.tv_alert);
        setToolBarItemsVisibility(view);
        mQueue = Volley.newRequestQueue(mContext, new HurlStack(null, mMediatorCallback.getSocketFactory()));
        mProgressDialog = new MyProgressDialog(mContext);
        String fileUrl = API_DOC_INFO + mUploadedDocInfo.getDocId();
        if(mUploadedDocInfo.getStatus().equals("P"))
            switchPublish.setChecked(true);
        switchPublish.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked)
                    displayPublishDialog();
                if(!isChecked)
                    displayUnPublishDialog();
            }
        });
        getFileUploaded(fileUrl);
        return view;
    }

    private void displayPublishDialog() {
        AlertDialog.Builder builder1 = new AlertDialog.Builder(mContext);
        builder1.setMessage(getResources().getString(R.string.publish_doc_msg));
        builder1.setCancelable(false);

        builder1.setPositiveButton(
                getResources().getString(R.string.yes_dialog),
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        String fileUrl = "http://10.99.9.36:9000/nehrapi/file/publish/" + mUploadedDocInfo.getDocId();
                        publishDocRequest(fileUrl);
                    }
                });

        builder1.setNegativeButton(
                getResources().getString(R.string.no_dialog),
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.cancel();
                    }
                });

        AlertDialog alert11 = builder1.create();
        alert11.show();
    }
    private void displayUnPublishDialog() {
        AlertDialog.Builder builder1 = new AlertDialog.Builder(mContext);
        builder1.setMessage(getResources().getString(R.string.unpublish_doc_msg));
        builder1.setCancelable(true);

        builder1.setPositiveButton(
                getResources().getString(R.string.yes_dialog),
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        String fileUrl = "http://10.99.9.36:9000/nehrapi/file/unPublish/" + mUploadedDocInfo.getDocId();
                        publishDocRequest(fileUrl);
                    }
                });

        builder1.setNegativeButton(
                getResources().getString(R.string.no_dialog),
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.cancel();
                    }
                });

        AlertDialog alert11 = builder1.create();
        alert11.show();
    }

    private void publishDocRequest(String fullUrl) {
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, fullUrl, null
                , new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                try {
                    if (response.getInt(API_RESPONSE_CODE) == 0) {
                        Log.d("publish", response.getString(API_RESPONSE_MESSAGE));
                        Toast.makeText(mContext, response.getString(API_RESPONSE_MESSAGE), Toast.LENGTH_SHORT).show();
                        mToolbarControllerCallback.customToolbarBackButtonClicked();
                    } else {

                        mProgressDialog.dismissDialog();
                    }
                } catch (JSONException e) {
                    Log.d("publish", e.getMessage());
                    e.printStackTrace();
                }

                mProgressDialog.dismissDialog();

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.d("publish", error.toString());
                error.printStackTrace();
                Toast.makeText(mContext, error.toString(), Toast.LENGTH_SHORT).show();
                mProgressDialog.dismissDialog();
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

    private void showAlertDialog() {
        AlertDialog.Builder builder1 = new AlertDialog.Builder(mContext);
        builder1.setMessage(getResources().getString(R.string.delete_doc_msg));
        builder1.setCancelable(true);

        builder1.setPositiveButton(
                getResources().getString(R.string.yes_dialog),
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        String fileUrl = API_DELETE_DOC_INFO + mUploadedDocInfo.getDocId();
                        deleteRequest(fileUrl);
                    }
                });

        builder1.setNegativeButton(
                getResources().getString(R.string.no_dialog),
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.cancel();
                    }
                });

        AlertDialog alert11 = builder1.create();
        alert11.show();
    }

    private void backToHome() {
        FragmentManager fm = getActivity().getSupportFragmentManager();
        for (int i = 0; i < fm.getBackStackEntryCount(); ++i) {
            fm.popBackStack();
        }
    }

    private void setToolBarItemsVisibility(View view) {
        switchPublish.setVisibility(View.VISIBLE);
        ibDelete.setVisibility(View.VISIBLE);
        ibHome.setVisibility(View.VISIBLE);
    }

    private void getFileUploaded(String fileUrl) {
        mProgressDialog.showDialog();

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, fileUrl, null
                , new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                try {
                   if (response.getInt(API_RESPONSE_CODE) == 0) {
                        String uploadedFile = response.getJSONObject(API_RESPONSE_RESULT).getString(UPLOADED_FILE_KEY);
                        byte[] decodedString = Base64.decode(uploadedFile.getBytes(), Base64.DEFAULT);
                        if (response.getJSONObject(API_RESPONSE_RESULT).getString("contentType").contains("pdf")) {
                            ivFileUploaded.setVisibility(View.GONE);
                        } else {
                            Bitmap decodedByte = BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length);
                            Glide.with(mContext).load(decodedByte).into(ivFileUploaded);
                            //      }
                        }
                    } else {
                        mProgressDialog.dismissDialog();
                        displayAlert(response.getString(API_RESPONSE_MESSAGE));

                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }

                mProgressDialog.dismissDialog();

            }

        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.d("getUploadedFiles", error.toString());
                error.printStackTrace();
                mProgressDialog.dismissDialog();
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

    private void deleteRequest(String fileUrl) {
        mProgressDialog.showDialog();

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, fileUrl, null
                , new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                 try {
                    if (response.getInt(API_RESPONSE_CODE) == 0) {
                        Toast.makeText(mContext, response.getString(API_RESPONSE_MESSAGE).toString(), Toast.LENGTH_SHORT).show();
                        mToolbarControllerCallback.customToolbarBackButtonClicked();
                    } else {
                        mProgressDialog.dismissDialog();
                        displayAlert(response.getString(API_RESPONSE_MESSAGE));

                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }

                mProgressDialog.dismissDialog();

            }

        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.d("deleteDoc", error.toString());
                error.printStackTrace();
                mProgressDialog.dismissDialog();
            }
        }) {
            //
            @Override
            public Map<String, String> getHeaders() {
                HashMap<String, String> headers = new HashMap<>();
//                headers.put("Accept", "application/json");
                headers.put("Content-Type", "application/json");
                //       headers.put("Authorization", API_GET_TOKEN_BEARER + mMediatorCallback.getAccessToken().getAccessTokenString());
                return headers;
            }

        };

        int socketTimeout = 30000;//30 seconds - change to what you want
        RetryPolicy policy = new DefaultRetryPolicy(socketTimeout, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT);
        jsonObjectRequest.setRetryPolicy(policy);

        mQueue.add(jsonObjectRequest);

    }

    private void displayAlert(String msg) {
        ivFileUploaded.setVisibility(View.GONE);
        tvAlert.setVisibility(View.VISIBLE);
        tvAlert.setText(msg);
    }
}
