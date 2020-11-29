package om.gov.moh.phr.adapters;

import android.content.Context;
import android.content.SharedPreferences;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Locale;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;

import org.w3c.dom.DocumentFragment;

import om.gov.moh.phr.R;
import om.gov.moh.phr.apimodels.ApiHomeHolder;
import om.gov.moh.phr.fragments.AppointmentNewFragment;
import om.gov.moh.phr.fragments.AppointmentsListFragment;
import om.gov.moh.phr.fragments.BodyMeasurementsFragment;
import om.gov.moh.phr.fragments.ChatFragment;
import om.gov.moh.phr.fragments.DemographicsFragment;
import om.gov.moh.phr.fragments.DocsContainerFragment;
import om.gov.moh.phr.fragments.HealthRecordListFragment;
import om.gov.moh.phr.fragments.ImmunizationContainerFragment;
import om.gov.moh.phr.fragments.LabResultsContainerFragment;
import om.gov.moh.phr.fragments.MedicationContainerFragment;
import om.gov.moh.phr.fragments.OrganDonationFragment;
import om.gov.moh.phr.fragments.ProceduresReportsContainerFragment;
import om.gov.moh.phr.fragments.VitalInfoFragment;
import om.gov.moh.phr.interfaces.AdapterToFragmentConnectorInterface;
import om.gov.moh.phr.models.Pagination;

import static om.gov.moh.phr.models.MyConstants.LANGUAGE_ARABIC;
import static om.gov.moh.phr.models.MyConstants.LANGUAGE_PREFS;
import static om.gov.moh.phr.models.MyConstants.LANGUAGE_SELECTED;

public class PaginationRecyclerViewAdapter extends
        RecyclerView.Adapter<PaginationRecyclerViewAdapter.MyViewHolder> {

    private ArrayList<ApiHomeHolder.ApiMainMenus> mItemsArrayList = new ArrayList<>();
    private Context mContext;
    private AdapterToFragmentConnectorInterface mCallback;

    //the constructor of the LastRecordsRecyclerViewAdapter
    public PaginationRecyclerViewAdapter(AdapterToFragmentConnectorInterface fragment, Context context) {
        this.mContext = context;
        mCallback = fragment;
    }

    //onCreateViewHolder allows you to inflate the "List items view"
//also allows to make some action during once the list created
    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.fragment_home_pagination_list_item, parent, false);
        return new MyViewHolder(itemView);
    }

    //onBindViewHolder , allows you to write the data into the fields
    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, final int position) {
        final ApiHomeHolder.ApiMainMenus result = mItemsArrayList.get(position);
        String pageTitle = "";
        if (getStoredLanguage().equals(LANGUAGE_ARABIC)) {
            pageTitle = result.getMenuNameNls();
            holder.tvDesc.setText(result.getMenuDescNls());
        } else {
            pageTitle = result.getMenuName();
            holder.tvDesc.setText(result.getMenuDesc());
        }

        final Fragment currentFragment;
        switch (result.getIconClass()) {
            case "ic_documents":
                currentFragment =  DocsContainerFragment.newInstance(pageTitle);
                holder.ibIcon.setImageResource(R.drawable.ic_documents);
                pageTitle = mContext.getResources().getString(R.string.title_other_document);
                break;
            case "ic_procedure":
               currentFragment = ProceduresReportsContainerFragment.newInstance(pageTitle);
                holder.ibIcon.setImageResource(R.drawable.ic_procedure);
                pageTitle = mContext.getResources().getString(R.string.title_procedures_reports);
                break;
            case "ic_medical_history":
                currentFragment = VitalInfoFragment.newInstance(pageTitle);
                holder.ibIcon.setImageResource(R.drawable.ic_medical_history);
                pageTitle = mContext.getResources().getString(R.string.title_vital_info);
                break;
            case "ic_vital":
               currentFragment = BodyMeasurementsFragment.newInstance(null,pageTitle);
                holder.ibIcon.setImageResource(R.drawable.ic_vital);
                pageTitle = mContext.getResources().getString(R.string.title_vital_signs);
                break;
            case "ic_demographoc":
                currentFragment =   DemographicsFragment.newInstance(null,pageTitle);
                holder.ibIcon.setImageResource(R.drawable.ic_demographoc);
                pageTitle = mContext.getResources().getString(R.string.title_demographic);
                break;
            case "ic_health_records":
                currentFragment = HealthRecordListFragment.newInstance(pageTitle);
                holder.ibIcon.setImageResource(R.drawable.ic_health_records);
                pageTitle = mContext.getResources().getString(R.string.title_health_records);
                break;
            case "ic_medication":
                currentFragment = MedicationContainerFragment.newInstance(pageTitle);
                holder.ibIcon.setImageResource(R.drawable.ic_medication);
                pageTitle = mContext.getResources().getString(R.string.title_medication);
                break;
            case "ic_organ":
                currentFragment = OrganDonationFragment.newInstance(pageTitle);
                holder.ibIcon.setImageResource(R.drawable.ic_organ);
                pageTitle = mContext.getResources().getString(R.string.title_organ);
                break;
            case "ic_appointment":
                currentFragment =  AppointmentsListFragment.newInstance(null);
                //currentFragment =  AppointmentNewFragment.newInstance(null);
                holder.ibIcon.setImageResource(R.drawable.ic_appointment);
                pageTitle = mContext.getResources().getString(R.string.title_appointments);
                break;
            case "ic_immunization":
                currentFragment = ImmunizationContainerFragment.newInstance(pageTitle);
                holder.ibIcon.setImageResource(R.drawable.ic_immunization);
                pageTitle = mContext.getResources().getString(R.string.title_immunization);
                break;
            case "ic_lab":
                currentFragment  =  LabResultsContainerFragment.newInstance(pageTitle);
                holder.ibIcon.setImageResource(R.drawable.ic_lab);
                pageTitle = mContext.getResources().getString(R.string.title_lab_results);
                break;
            default:
                currentFragment =   ChatFragment.newInstance(pageTitle);
                holder.ibIcon.setImageResource(R.drawable.ic_chat);
                pageTitle = mContext.getResources().getString(R.string.title_chat);
                break;
        }
        holder.tvTitle.setText(pageTitle);
        holder.ibIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                 mCallback.onMyListItemClicked(currentFragment, result.getMenuName());
            }
        });

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mCallback.onMyListItemClicked(currentFragment, result.getMenuName());
            }
        });
    }


    public void updateList(ArrayList<ApiHomeHolder.ApiMainMenus> items) {
        mItemsArrayList = items;
        notifyDataSetChanged();
    }

    @Override
    public int getItemCount() {
        return mItemsArrayList.size();
    }


    public class MyViewHolder extends RecyclerView.ViewHolder {
        private final TextView tvTitle, tvDesc;
        private final ImageView ibIcon;


        public MyViewHolder(View view) {
            super(view);
            tvTitle = itemView.findViewById(R.id.tv_title);
            tvDesc = itemView.findViewById(R.id.tv_desc);
            ibIcon = itemView.findViewById(R.id.ib_icon);
        }
    }

    private String getStoredLanguage() {
        SharedPreferences sharedPref = mContext.getSharedPreferences(LANGUAGE_PREFS, Context.MODE_PRIVATE);
        return sharedPref.getString(LANGUAGE_SELECTED, getDeviceLanguage());
    }

    private String getDeviceLanguage() {
        return Locale.getDefault().getLanguage();
    }
}