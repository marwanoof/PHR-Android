package om.gov.moh.phr.fragments;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import om.gov.moh.phr.R;
import om.gov.moh.phr.apimodels.ApiAppointmentsListHolder;
import om.gov.moh.phr.interfaces.MediatorInterface;
import om.gov.moh.phr.interfaces.ToolbarControllerInterface;


public class RefferalsDetailsFragment extends Fragment {

    private static final String ARG_PARAM1 = "refferal_desc";


    private String mParam1;
    private ToolbarControllerInterface mToolbarControllerCallback;
    public RefferalsDetailsFragment() {
        // Required empty public constructor
    }

    public static RefferalsDetailsFragment newInstance(String referrals) {
        RefferalsDetailsFragment fragment = new RefferalsDetailsFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, referrals);
        fragment.setArguments(args);
        return fragment;
    }



    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View parentView = inflater.inflate(R.layout.fragment_refferals_details, container, false);
        TextView tvToolbarTitle = parentView.findViewById(R.id.tv_toolbar_title);
        ImageButton ibToolbarBackButton = parentView.findViewById(R.id.ib_toolbar_back_button);
        tvToolbarTitle.setText(getResources().getString(R.string.title_vital_info));
        tvToolbarTitle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mToolbarControllerCallback.customToolbarBackButtonClicked();
            }
        });
        ibToolbarBackButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mToolbarControllerCallback.customToolbarBackButtonClicked();
            }
        });
        TextView tvRefferalsDesc = parentView.findViewById(R.id.refferal_desc);
        tvRefferalsDesc.setText(mParam1);
        return parentView;
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        mToolbarControllerCallback = (ToolbarControllerInterface) context;
    }

}
