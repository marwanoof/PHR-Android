package om.gov.moh.phr.adapters;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.ArrayList;

import om.gov.moh.phr.R;
import om.gov.moh.phr.models.CustomSlot;

public class DateItemsGridViewAdapter extends BaseAdapter {
    private ArrayList<CustomSlot> mSlotsHolderArrayList;
    //private ArrayList<CustomSlot> newmSlotsHolderArrayList =new ArrayList<>();
    private Context mContext;

    public DateItemsGridViewAdapter(ArrayList<CustomSlot> items, Context context) {
        mSlotsHolderArrayList = items;
      /*  for(CustomSlot item : mSlotsHolderArrayList){
            if (item.getAppointmentDay() != null && !item.getAppointmentDay().isEmpty()) {
                newmSlotsHolderArrayList.add(item);
            }
        }*/

        mContext = context;
    }

    public int getCount() {
        return mSlotsHolderArrayList.size();
    }

    public Object getItem(int position) {
        return mSlotsHolderArrayList.get(position);
    }

    public long getItemId(int position) {
        return position;
    }

    // create a new ImageView for each item referenced by the Adapter
    public View getView(int position, View holder, ViewGroup parent) {

        if (holder == null) {
            holder = LayoutInflater.from(mContext).
                    inflate(R.layout.fragment_appointment_date_list_item, parent, false);
        }

        // get current item to be displayed
        CustomSlot item = (CustomSlot) getItem(position);

        if (item.getAppointmentDay() == null || item.getAppointmentDay().isEmpty()) {
            holder.setVisibility(View.GONE);

        }
        TextView tvMonth = holder.findViewById(R.id.tv_month);
        TextView tvDay = holder.findViewById(R.id.tv_title);

        tvDay.setText(item.getAppointmentDay());
        tvMonth.setText(item.getAppointmentMonth());

        Log.d("re-runId", " getView:" + item.getTimeBlock() + " / " + item.getRunIdArrayList());


        return holder;
    }


}