package om.gov.moh.phr.fragments;


import android.content.Context;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import java.util.ArrayList;

import om.gov.moh.phr.R;
import om.gov.moh.phr.adapters.NotificationsRecyclerViewAdapter;
import om.gov.moh.phr.apimodels.Notification;
import om.gov.moh.phr.interfaces.AdapterToFragmentConnectorInterface;
import om.gov.moh.phr.interfaces.MediatorInterface;
import om.gov.moh.phr.interfaces.ToolbarControllerInterface;
import om.gov.moh.phr.models.DBHelper;
import om.gov.moh.phr.models.MyProgressDialog;

/**
 * A simple {@link Fragment} subclass.
 */
public class NotificationsFragment extends Fragment implements AdapterToFragmentConnectorInterface, SwipeRefreshLayout.OnRefreshListener {
    private Context mContext;
    private MediatorInterface mMediatorCallback;
    private ToolbarControllerInterface mToolbarControllerCallback;
    private RecyclerView rvNotification;
    private DBHelper dbHelper;
    private ArrayList<Notification> notificationsList;
    private SwipeRefreshLayout swipeRefreshLayout;
    public NotificationsFragment() {
        // Required empty public constructor
    }

    public static NotificationsFragment newInstance() {
        NotificationsFragment fragment = new NotificationsFragment();
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
        tvTitle.setGravity(Gravity.CENTER);
        ImageButton ibBack = view.findViewById(R.id.ib_toolbar_back_button);
        ibBack.setVisibility(View.GONE);
        rvNotification = view.findViewById(R.id.rv_notification);
        swipeRefreshLayout = view.findViewById(R.id.swipe_refresh_layout);
        swipeRefreshLayout.setOnRefreshListener(this);
        swipeRefreshLayout.post(new Runnable() {
                                    @Override
                                    public void run() {
                                        swipeRefreshLayout.setRefreshing(true);
                                        getNotificationList();
                                    }
                                }
        );
        dbHelper = new DBHelper(mContext);
        getNotificationList();
        return view;
    }

    private void getNotificationList() {
        swipeRefreshLayout.setRefreshing(true);
        notificationsList = new ArrayList<>();
        notificationsList = dbHelper.retrieveNotificationsRecord();
        if (notificationsList.size() == 0) {
            Notification notification = new Notification();
            notification.setTitle(getResources().getString(R.string.no_notification));
            notification.setBody("");
            notification.setPnsType("10");
            notificationsList.add(notification);
        }
            setRecyclerView();
        swipeRefreshLayout.setRefreshing(false);
    }

    private void setRecyclerView() {
        NotificationsRecyclerViewAdapter mAdapter =
                new NotificationsRecyclerViewAdapter(notificationsList, mContext, NotificationsFragment.this, mMediatorCallback);
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(mContext);
        DividerItemDecoration mDividerItemDecoration = new DividerItemDecoration(rvNotification
                .getContext(),
                ((LinearLayoutManager) mLayoutManager).getOrientation());
        rvNotification.addItemDecoration(mDividerItemDecoration);
        rvNotification.setLayoutManager(mLayoutManager);
        rvNotification.setItemAnimator(new DefaultItemAnimator());
        rvNotification.setAdapter(mAdapter);
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mToolbarControllerCallback.changeSideMenuToolBarVisibility(View.VISIBLE);
    }

    @Override
    public void onResume() {
        super.onResume();
        mToolbarControllerCallback.changeSideMenuToolBarVisibility(View.GONE);
    }

    @Override
    public <T> void onMyListItemClicked(T dataToPass, String dataTitle) {

    }

    @Override
    public <T> void onMyListItemClicked(T dataToPass, String dataTitle, int position) {
        mMediatorCallback.changeFragmentContainerVisibility(View.VISIBLE, View.GONE);
        mToolbarControllerCallback.changeSideMenuToolBarVisibility(View.GONE);
        switch (notificationsList.get(position).getPnsType()) {
            case "1":
                mMediatorCallback.changeFragmentTo(LabResultDetailsFragment.newInstance(notificationsList.get(position)), LabResultDetailsFragment.class.getSimpleName());
                break;
            case "3":
                mMediatorCallback.changeFragmentTo(ImmunizationFragment.newInstance("Schedule", true), ImmunizationFragment.class.getSimpleName());
                break;
            case "4":
                mMediatorCallback.changeFragmentTo(RadFragment.newInstance(notificationsList.get(position)), RadFragment.class.getSimpleName());
                break;
            case "5":
                mMediatorCallback.changeFragmentTo(OtherDocsDetailsFragment.newInstance(notificationsList.get(position)), ProviderDocumentsFragment.class.getSimpleName());
                break;
        }
    }

    @Override
    public void onRefresh() {
        getNotificationList();
    }
}
