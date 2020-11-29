package om.gov.moh.phr.fragments;


import android.content.Context;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ImageButton;
import android.widget.TextView;

import om.gov.moh.phr.R;
import om.gov.moh.phr.interfaces.MediatorInterface;
import om.gov.moh.phr.interfaces.ToolbarControllerInterface;

/**
 * A simple {@link Fragment} subclass.
 */
public class WebSideMenuFragment extends Fragment {
    private static final String ARG_SIDEMENU_URL = "ARG_PARAM1";
    private static final String ARG_SIDEMENU_NAME = "ARG_PARAM1";
    private Context mContext;
    private MediatorInterface mMediatorCallback;
    private ToolbarControllerInterface mToolbarControllerCallback;
    private WebView wvMoHWebsite;
    private String SideMenuUrl, sideMenuName;
    private TextView tvAlert;
    private TextView tvToolbarTitle;

    public WebSideMenuFragment() {
        // Required empty public constructor
    }

    public static WebSideMenuFragment newInstance(String sideMenuItem, String sideMenuName) {
        WebSideMenuFragment fragment = new WebSideMenuFragment();
        Bundle args = new Bundle();
        args.putString(ARG_SIDEMENU_URL, sideMenuItem);
        args.putString(ARG_SIDEMENU_NAME, sideMenuName);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        mContext = context;
        mMediatorCallback = (MediatorInterface) context;
        mToolbarControllerCallback = (ToolbarControllerInterface) context;
        mToolbarControllerCallback.changeSideMenuToolBarVisibility(View.GONE);
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            SideMenuUrl = getArguments().getString(ARG_SIDEMENU_URL);
            sideMenuName = getArguments().getString(ARG_SIDEMENU_NAME);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View parentView = inflater.inflate(R.layout.fragment_web_side_menu, container, false);
        tvToolbarTitle = parentView.findViewById(R.id.tv_toolbar_title);
        ImageButton ibToolbarBackButton = parentView.findViewById(R.id.ib_toolbar_back_button);
        tvToolbarTitle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mToolbarControllerCallback.changeSideMenuToolBarVisibility(View.VISIBLE);
                mToolbarControllerCallback.customToolbarBackButtonClicked();
            }
        });
        ibToolbarBackButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mToolbarControllerCallback.changeSideMenuToolBarVisibility(View.VISIBLE);
                mToolbarControllerCallback.customToolbarBackButtonClicked();
            }
        });
        tvAlert = parentView.findViewById(R.id.tv_alert);
        wvMoHWebsite = parentView.findViewById(R.id.wv_mohWebsite);
        if (mMediatorCallback.isConnected()) {

            wvMoHWebsite.getSettings().setJavaScriptEnabled(true); // enable javascript

            wvMoHWebsite.setWebViewClient(new WebViewClient() {

                @Override
                public boolean shouldOverrideUrlLoading(WebView view, String url) {
                    view.loadUrl(url);
                    return true;
                }
            });
            checkSideMenuItem();
        } else {
            displayAlert(getString(R.string.alert_no_connection));
        }

        return parentView;
    }

    private void checkSideMenuItem() {
        tvToolbarTitle.setText(sideMenuName);
        wvMoHWebsite.loadUrl(SideMenuUrl);
    }

    private void displayAlert(String msg) {
        wvMoHWebsite.setVisibility(View.GONE);
        tvAlert.setVisibility(View.VISIBLE);
        tvAlert.setText(msg);
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mToolbarControllerCallback.changeSideMenuToolBarVisibility(View.VISIBLE);
    }
}
