package om.gov.moh.phr.fragments;


import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.SearchView;
import androidx.cardview.widget.CardView;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;

import om.gov.moh.phr.R;
import om.gov.moh.phr.adapters.ImmunizationRecyclerViewAdapter;
import om.gov.moh.phr.apimodels.ApiImmunizationHolder;
import om.gov.moh.phr.interfaces.ToolbarControllerInterface;
import om.gov.moh.phr.models.GlobalMethodsKotlin;

/**
 * A simple {@link Fragment} subclass.
 */
public class ImmunizationFragment extends Fragment implements SearchView.OnQueryTextListener {
    private static final String ARG_PARAM1 = "ARG_PARAM1";
    private static final String ARG_PARAM2 = "ARG_PARAM2";
    private Context mContext;
    private ToolbarControllerInterface mToolbarControllerCallback;
    private RecyclerView rvImmunizationList;
    private TextView tvAlert;
    private ImmunizationRecyclerViewAdapter mAdapter;
    private boolean isSchedule = false;
    //private SwipeRefreshLayout swipeRefreshLayout;
    private CardView noRecordCardView;
    private SearchView searchView;
    private ArrayList<ApiImmunizationHolder.ApiImmunizationInfo> immunizationList;

    public ImmunizationFragment() {
        // Required empty public constructor
    }

    public static ImmunizationFragment newInstance(String type, ArrayList<ApiImmunizationHolder.ApiImmunizationInfo> apiImmunizationArrayList) {
        ImmunizationFragment fragment = new ImmunizationFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, type);
        args.putSerializable(ARG_PARAM2, apiImmunizationArrayList);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        mContext = context;
        mToolbarControllerCallback = (ToolbarControllerInterface) context;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            String immunizationType = getArguments().getString(ARG_PARAM1);
            isSchedule = !immunizationType.equals("Given");
            immunizationList = (ArrayList<ApiImmunizationHolder.ApiImmunizationInfo>) getArguments().getSerializable(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_immunization, container, false);

        tvAlert = view.findViewById(R.id.tv_alert);
        rvImmunizationList = view.findViewById(R.id.rv_immunization);
        searchView = (SearchView) view.findViewById(R.id.sv_searchView);
        searchView.setOnQueryTextListener(this);
        //swipeRefreshLayout = view.findViewById(R.id.swipe_refresh_layout);
        noRecordCardView = view.findViewById(R.id.cardViewNoRecords);
        noRecordCardView.setVisibility(View.GONE);
        if (immunizationList!=null)
            setupRecyclerView(immunizationList);
        else {
            if (isSchedule)
                displayAlert(getResources().getString(R.string.no_records_imm_schedule));
            else
                displayAlert(getResources().getString(R.string.no_records_imm_given));
        }
        return view;
    }


    private void setupRecyclerView(ArrayList<ApiImmunizationHolder.ApiImmunizationInfo> getmResult) {
        mAdapter =
                new ImmunizationRecyclerViewAdapter(getmResult, mContext, isSchedule, checkCalendarPermission());
        LinearLayoutManager layoutManager
                = new LinearLayoutManager(mContext, RecyclerView.VERTICAL, false);
        DividerItemDecoration mDividerItemDecoration = new DividerItemDecoration(rvImmunizationList.getContext(),
                layoutManager.getOrientation());
        //rvImmunizationList.addItemDecoration(mDividerItemDecoration);
        rvImmunizationList.setLayoutManager(layoutManager);
        rvImmunizationList.setItemAnimator(new DefaultItemAnimator());
        rvImmunizationList.setAdapter(mAdapter);
    }
    private boolean checkCalendarPermission() {
        // check permission
        return ContextCompat.checkSelfPermission(mContext,
                Manifest.permission.READ_CALENDAR) == PackageManager.PERMISSION_GRANTED || ContextCompat.checkSelfPermission(mContext,
                Manifest.permission.WRITE_CALENDAR) == PackageManager.PERMISSION_GRANTED;
    }
    private void displayAlert(String msg) {
        searchView.clearFocus();
        searchView.setEnabled(false);
        searchView.setVisibility(View.GONE);
        rvImmunizationList.setVisibility(View.GONE);
        noRecordCardView.setVisibility(View.VISIBLE);
        tvAlert.setVisibility(View.VISIBLE);
        tvAlert.setText(msg);
        ;
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mToolbarControllerCallback.changeSideMenuToolBarVisibility(View.VISIBLE);
    }

    @Override
    public boolean onQueryTextSubmit(String query) {
        return false;
    }

    @Override
    public boolean onQueryTextChange(String newText) {
        if (mAdapter != null)
            mAdapter.filter(newText);
        return false;
    }
    @Override
    public void onResume() {
        super.onResume();
        if (mAdapter != null) {
            mAdapter.updateItemsList();
        }


    }
    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.clear();
    }
}
