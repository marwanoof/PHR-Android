package om.gov.moh.phr.adapters;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.text.TextUtils;
import android.util.Base64;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;

import java.util.ArrayList;

import de.hdodenhof.circleimageview.CircleImageView;
import om.gov.moh.phr.R;
import om.gov.moh.phr.activities.MainActivity;
import om.gov.moh.phr.apimodels.ApiGetRecentSearch;

public class RecentlySearchedPatientAdapter extends RecyclerView.Adapter<RecentlySearchedPatientAdapter.MyView> {
    // List with String type
    private ArrayList<ApiGetRecentSearch.Result> recentlySearchedList;
    private Context mContext;

    // View Holder class which
    // extends RecyclerView.ViewHolder
    public class MyView
            extends RecyclerView.ViewHolder {

        // Text View
        TextView tvCivilId;
        CircleImageView ivProfileImg;

        // parameterised constructor for View Holder class
        // which takes the view as a parameter
        public MyView(View view) {
            super(view);

            // initialise TextView with id
            tvCivilId = (TextView) view
                    .findViewById(R.id.tvCivilId);
            ivProfileImg = view.findViewById(R.id.profile_image);
        }
    }

    // Constructor for adapter class
    // which takes a list of String type
    public RecentlySearchedPatientAdapter(Context context, ArrayList<ApiGetRecentSearch.Result> horizontalList) {
        this.mContext = context;
        this.recentlySearchedList = horizontalList;
    }

    // Override onCreateViewHolder which deals
    // with the inflation of the card layout
    // as an item for the RecyclerView.
    @NonNull
    @Override
    public MyView onCreateViewHolder(ViewGroup parent,
                                     int viewType) {
        // Inflate item.xml using LayoutInflator
        View itemView
                = LayoutInflater
                .from(parent.getContext())
                .inflate(R.layout.recent_searched_item,
                        parent,
                        false);

        // return itemView
        return new MyView(itemView);
    }

    // Override onBindViewHolder which deals
    // with the setting of different data
    // and methods related to clicks on
    // particular items of the RecyclerView.
    @Override
    public void onBindViewHolder(@NonNull final MyView holder,
                                 final int position) {
        // Set the text of each item of
        // Recycler view with the list items
        if (recentlySearchedList.get(position).getCivilId() != null)
            holder.tvCivilId.setText(String.valueOf(recentlySearchedList.get(position).getCivilId()));
        else
            holder.tvCivilId.setText("--");
        Glide.with(mContext).load(getPersonPhoto(mContext, recentlySearchedList.get(position))).into(holder.ivProfileImg);
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent openPhr = new Intent(mContext, MainActivity.class);
                openPhr.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK & Intent.FLAG_ACTIVITY_CLEAR_TASK);
                openPhr.putExtra("civilId", String.valueOf(recentlySearchedList.get(position).getCivilId()));
                mContext.startActivity(openPhr);
            }
        });
    }

    // Override getItemCount which Returns
    // the length of the RecyclerView.
    @Override
    public int getItemCount() {
        return recentlySearchedList.size();
    }

    private Bitmap getPersonPhoto(Context context, ApiGetRecentSearch.Result personObj) {
        if (personObj.getPersonPhoto() == null || TextUtils.isEmpty(personObj.getPersonPhoto())) {
            if (personObj.getSex().equalsIgnoreCase("m"))
                return BitmapFactory.decodeResource(context.getResources(),
                        R.drawable.ic_patient_male);
            else
                return BitmapFactory.decodeResource(context.getResources(),
                        R.drawable.ic_patient_female);
        } else {
            //decode base64 string to image
            byte[] imageBytes = Base64.decode(personObj.getPersonPhoto(), Base64.NO_WRAP);
            Bitmap decodedImage = BitmapFactory.decodeByteArray(imageBytes, 0, imageBytes.length);
            return decodedImage;
        }
    }
}
