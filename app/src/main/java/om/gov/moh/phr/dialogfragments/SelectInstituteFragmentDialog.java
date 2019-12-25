package om.gov.moh.phr.dialogfragments;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.DialogFragment;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.textfield.TextInputEditText;

import java.util.ArrayList;

import om.gov.moh.phr.R;
import om.gov.moh.phr.adapters.InstitutesRecyclerViewAdapter;
import om.gov.moh.phr.interfaces.AdapterToFragmentConnectorInterface;

import static om.gov.moh.phr.models.MyConstants.PARAM_SELECTED_INSTITUTE;
import static om.gov.moh.phr.models.MyConstants.REQUEST_CODE_SELECTED_INSTITUTE;

public class SelectInstituteFragmentDialog extends DialogFragment {


    private static final String PARAM_ITEMS_ARRAY = "PARAM_ITEMS_ARRAY";
    private String mSelectedInstitute;
    private RecyclerView mRecyclerView;
    private InstitutesRecyclerViewAdapter mAdapter;
    private ArrayList<String> mInstitutesNames = new ArrayList<>();
    private Context mContext;

    public SelectInstituteFragmentDialog() {
        // Required empty public constructor
    }


    public static SelectInstituteFragmentDialog newInstance(ArrayList<String> items) {
        SelectInstituteFragmentDialog fragment = new SelectInstituteFragmentDialog();
        Bundle args = new Bundle();
        args.putSerializable(PARAM_ITEMS_ARRAY, items);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        mContext = context;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mInstitutesNames = (ArrayList<String>) getArguments().getSerializable(PARAM_ITEMS_ARRAY);
        }
    }

    @Override
    public void onStart() {
        super.onStart();
        Dialog dialog = getDialog();
        if (dialog != null) {
            int width = ViewGroup.LayoutParams.MATCH_PARENT;
            int height = ViewGroup.LayoutParams.WRAP_CONTENT;
            dialog.getWindow().setLayout(width, height);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View parentView = inflater.inflate(R.layout.fragment_select_institute_fragment_dialog, container, false);

        mRecyclerView = parentView.findViewById(R.id.recycler_view);
        mAdapter = new InstitutesRecyclerViewAdapter();
        LinearLayoutManager layoutManager
                = new LinearLayoutManager(mContext, RecyclerView.VERTICAL, false);
        mRecyclerView.setLayoutManager(layoutManager);
        mRecyclerView.setItemAnimator(new DefaultItemAnimator());
        mRecyclerView.setAdapter(mAdapter);

        mAdapter.setOnItemClickListener(new AdapterToFragmentConnectorInterface() {
            @Override
            public <T> void onMyListItemClicked(T dataToPass, String dataTitle) {
                Intent intent = new Intent();
                intent.putExtra(PARAM_SELECTED_INSTITUTE, (String) dataToPass);
                getTargetFragment().onActivityResult(getTargetRequestCode(), REQUEST_CODE_SELECTED_INSTITUTE, intent);

            }

            @Override
            public <T> void onMyListItemClicked(T dataToPass, String dataTitle, int position) {

            }
        });

        TextInputEditText tietSearchKey = parentView.findViewById(R.id.tiet_search_key);
        tietSearchKey.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void afterTextChanged(Editable content) {
                filter(content.toString());
            }
        });

        setupRecyclerView(mInstitutesNames);


        return parentView;
    }


    private void filter(String searchKey) {
        ArrayList<String> filteredList = new ArrayList<>();
        for (String instituteName : mInstitutesNames) {
            if (instituteName.toLowerCase().contains(searchKey.toLowerCase())) {
                filteredList.add(instituteName);
            }
        }
        setupRecyclerView(filteredList);
    }

    private void setupRecyclerView(ArrayList<String> institutesNames) {
        mAdapter.submitList(institutesNames);
    }

}
