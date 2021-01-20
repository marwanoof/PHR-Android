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
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

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
import com.google.android.material.snackbar.Snackbar;

import org.json.JSONException;
import org.json.JSONObject;


import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

import om.gov.moh.phr.R;
import om.gov.moh.phr.apimodels.ApiUploadsDocsHolder;
import om.gov.moh.phr.interfaces.MediatorInterface;
import om.gov.moh.phr.interfaces.ToolbarControllerInterface;
import om.gov.moh.phr.models.GlobalMethodsKotlin;
import om.gov.moh.phr.models.MyProgressDialog;

import static om.gov.moh.phr.models.MyConstants.API_GET_TOKEN_BEARER;
import static om.gov.moh.phr.models.MyConstants.API_NEHR_URL;
import static om.gov.moh.phr.models.MyConstants.API_RESPONSE_CODE;
import static om.gov.moh.phr.models.MyConstants.API_RESPONSE_MESSAGE;
import static om.gov.moh.phr.models.MyConstants.API_RESPONSE_RESULT;

/**
 * A simple {@link Fragment} subclass.
 */
public class UploadedDocDetailsFragment extends Fragment implements SwipeRefreshLayout.OnRefreshListener {
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
    private ImageButton ibDelete;
    private ApiUploadsDocsHolder.ApiUploadDocInfo mUploadedDocInfo;
    private ImageView ivFileUploaded;
    private TextView tvAlert;
    private SwipeRefreshLayout swipeRefreshLayout;

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

        setToolBarItemsVisibility();
        swipeRefreshLayout = view.findViewById(R.id.swipe_refresh_layout);
        swipeRefreshLayout.setOnRefreshListener(this);
        swipeRefreshLayout.post(new Runnable() {
                                    @Override
                                    public void run() {
                                        swipeRefreshLayout.setRefreshing(true);
                                        String fileUrl = API_DOC_INFO + mUploadedDocInfo.getDocId();
                                        getFileUploaded(fileUrl);
                                    }
                                }
        );
        ivFileUploaded = view.findViewById(R.id.iv_uploadedFile);
        tvAlert = view.findViewById(R.id.tv_alert);
        mQueue = Volley.newRequestQueue(mContext, new HurlStack(null, mMediatorCallback.getSocketFactory()));
        mProgressDialog = new MyProgressDialog(mContext);
        String fileUrl = API_DOC_INFO + mUploadedDocInfo.getDocId();
        if (mUploadedDocInfo.getStatus().equals("P"))
            switchPublish.setChecked(true);
        switchPublish.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked)
                    displayPublishDialog();
                if (!isChecked)
                    displayUnPublishDialog();
            }
        });
        if (mMediatorCallback.isConnected()) {
            getFileUploaded(fileUrl);
        } else {
            GlobalMethodsKotlin.Companion.showAlertDialog(mContext, getResources().getString(R.string.no_internet_title), getResources().getString(R.string.alert_no_connection), getResources().getString(R.string.ok), R.drawable.ic_error);
        }
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
                        String fileUrl = API_NEHR_URL + "file/publish/" + mUploadedDocInfo.getDocId();
                        publishDocRequest(fileUrl);
                    }
                });

        builder1.setNegativeButton(
                getResources().getString(R.string.no_dialog),
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        Snackbar.make(Objects.requireNonNull(getActivity()).findViewById(android.R.id.content), getResources().getString(R.string.cancel_done_msg), Snackbar.LENGTH_SHORT)
                                .setBackgroundTint(getResources().getColor(R.color.colorPrimary))
                                .show();
                        mToolbarControllerCallback.customToolbarBackButtonClicked();
                        dialog.dismiss();
                    }
                });

        AlertDialog alert11 = builder1.create();
        alert11.show();
    }

    private void displayUnPublishDialog() {
        AlertDialog.Builder builder1 = new AlertDialog.Builder(mContext);
        builder1.setMessage(getResources().getString(R.string.unpublish_doc_msg));
        builder1.setCancelable(false);

        builder1.setPositiveButton(
                getResources().getString(R.string.yes_dialog),
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        String fileUrl = API_NEHR_URL + "file/unPublish/" + mUploadedDocInfo.getDocId();
                        publishDocRequest(fileUrl);
                    }
                });

        builder1.setNegativeButton(
                getResources().getString(R.string.no_dialog),
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        Snackbar.make(Objects.requireNonNull(getActivity()).findViewById(android.R.id.content), getResources().getString(R.string.cancel_done_msg), Snackbar.LENGTH_SHORT)
                                .setBackgroundTint(getResources().getColor(R.color.colorPrimary))
                                .show();
                        mToolbarControllerCallback.customToolbarBackButtonClicked();
                        dialog.dismiss();
                    }
                });

        AlertDialog alert11 = builder1.create();
        alert11.show();
    }

    private void publishDocRequest(final String fullUrl) {
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, fullUrl, null
                , new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                if (mContext != null && isAdded()) {
                    try {
                        if (response.getInt(API_RESPONSE_CODE) == 0) {
                            if (fullUrl.contains("file/unPublish/"))
                            Snackbar.make(Objects.requireNonNull(getActivity()).findViewById(android.R.id.content), getResources().getString(R.string.success_unpublish_msg), Snackbar.LENGTH_SHORT).setBackgroundTint(getResources().getColor(R.color.colorPrimary)).show();
                            else
                            Snackbar.make(Objects.requireNonNull(getActivity()).findViewById(android.R.id.content), getResources().getString(R.string.success_publish_msg), Snackbar.LENGTH_SHORT).setBackgroundTint(getResources().getColor(R.color.colorPrimary)).show();
                            mToolbarControllerCallback.customToolbarBackButtonClicked();
                        } else {

                            mProgressDialog.dismissDialog();
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                    mProgressDialog.dismissDialog();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                if (mContext != null && isAdded()) {
                    error.printStackTrace();
                    Snackbar.make(getActivity().findViewById(android.R.id.content), error.toString(), Snackbar.LENGTH_SHORT)
                            .setBackgroundTint(getResources().getColor(R.color.colorPrimary))
                            .show();
                    mProgressDialog.dismissDialog();
                }
            }
        }) {
            //
            @Override
            public Map<String, String> getHeaders() {
                HashMap<String, String> headers = new HashMap<>();
                headers.put("Content-Type", "application/json");
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

    private void setToolBarItemsVisibility() {
        switchPublish.setVisibility(View.VISIBLE);
        ibDelete.setVisibility(View.VISIBLE);
    }

    private void getFileUploaded(String fileUrl) {
        mProgressDialog.showDialog();
        swipeRefreshLayout.setRefreshing(true);
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, fileUrl, null
                , new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                if (mContext != null && isAdded()) {
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
                            displayAlert(getResources().getString(R.string.no_record_found));

                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                    mProgressDialog.dismissDialog();
                    swipeRefreshLayout.setRefreshing(false);
                }
            }

        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                if (mContext != null && isAdded()) {
                    error.printStackTrace();
                    mProgressDialog.dismissDialog();
                    swipeRefreshLayout.setRefreshing(false);
                }
            }
        }) {
            //
            @Override
            public Map<String, String> getHeaders() {
                HashMap<String, String> headers = new HashMap<>();
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
                if (mContext != null && isAdded()) {
                    try {
                        if (response.getInt(API_RESPONSE_CODE) == 0) {
                            Snackbar.make(getActivity().findViewById(android.R.id.content), getResources().getString(R.string.success_delete_msg), Snackbar.LENGTH_SHORT)
                                    .setBackgroundTint(getResources().getColor(R.color.colorPrimary))
                                    .show();
                            mToolbarControllerCallback.customToolbarBackButtonClicked();
                        } else {
                            mProgressDialog.dismissDialog();
                            displayAlert(getResources().getString(R.string.no_record_found));

                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                    mProgressDialog.dismissDialog();
                }
            }

        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                if (mContext != null && isAdded()) {
                    error.printStackTrace();
                    mProgressDialog.dismissDialog();
                }
            }
        }) {
            //
            @Override
            public Map<String, String> getHeaders() {
                HashMap<String, String> headers = new HashMap<>();
                headers.put("Content-Type", "application/json");
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

    @Override
    public void onRefresh() {
        String fileUrl = API_DOC_INFO + mUploadedDocInfo.getDocId();
        getFileUploaded(fileUrl);
    }
}
