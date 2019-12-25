package om.gov.moh.phr.fragments;


import android.content.Context;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import om.gov.moh.phr.R;
import om.gov.moh.phr.interfaces.MediatorInterface;
import om.gov.moh.phr.interfaces.ToolbarControllerInterface;

/**
 * A simple {@link Fragment} subclass.
 */
public class NotificationsFragment extends Fragment {
    private Context mContext;
    private MediatorInterface mMediatorCallback;
    private ToolbarControllerInterface mToolbarControllerCallback;

    public NotificationsFragment() {
        // Required empty public constructor
    }

    public static NotificationsFragment newInstance() {
        NotificationsFragment fragment = new NotificationsFragment();
        Bundle args = new Bundle();
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
        View view = inflater.inflate(R.layout.fragment_notifications, container, false);
        TextView tvTitle = view.findViewById(R.id.tv_toolbar_title);
        tvTitle.setText(getResources().getString(R.string.title_notification));
        ImageButton ibBack = view.findViewById(R.id.ib_toolbar_back_button);
        tvTitle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mMediatorCallback.slideTo(0);
            }
        });
        ibBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mMediatorCallback.slideTo(0);
            }
        });
        return view;
    }
    @Override
    public void onDetach() {
        super.onDetach();
        mMediatorCallback.changeFragmentContainerVisibility(View.GONE, View.VISIBLE);
        mToolbarControllerCallback.changeSideMenuToolBarVisibility(View.VISIBLE);
    }

    @Override
    public void onResume() {
        super.onResume();
        mToolbarControllerCallback.changeSideMenuToolBarVisibility(View.GONE);
    }
}
