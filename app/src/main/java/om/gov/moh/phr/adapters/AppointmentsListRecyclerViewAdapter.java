package om.gov.moh.phr.adapters;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;

import java.util.ArrayList;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import om.gov.moh.phr.R;
import om.gov.moh.phr.apimodels.ApiAppointmentsListHolder;
import om.gov.moh.phr.interfaces.AdapterToFragmentConnectorInterface;
import om.gov.moh.phr.models.GlobalMethods;
import om.gov.moh.phr.models.GlobalMethodsKotlin;

import static om.gov.moh.phr.models.MyConstants.ACTION_CANCEL;
import static om.gov.moh.phr.models.MyConstants.ACTION_RESCHEDULE;
import static om.gov.moh.phr.models.MyConstants.LANGUAGE_ARABIC;

public class AppointmentsListRecyclerViewAdapter extends
        RecyclerView.Adapter<AppointmentsListRecyclerViewAdapter.MyViewHolder> {


    private ArrayList<ApiAppointmentsListHolder.Appointments> mItemsArrayList = new ArrayList<>();
    private Context mContext;
    private AdapterToFragmentConnectorInterface mCallback;
    private boolean isButtonsShow ;
   //the constructor of the LastRecordsRecyclerViewAdapter
    public AppointmentsListRecyclerViewAdapter(AdapterToFragmentConnectorInterface fragment, Context context) {
        this.mContext = context;
        mCallback = fragment;
    }

    //onCreateViewHolder allows you to inflate the "List items view"
//also allows to make some action during once the list created
    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.swipe_layout, parent, false);
        return new MyViewHolder(itemView);
    }

    //onBindViewHolder , allows you to write the data into the fields
    @Override
    public void onBindViewHolder(@NonNull final MyViewHolder holder, int position) {
        final ApiAppointmentsListHolder.Appointments result = mItemsArrayList.get(position);
        String title = result.getDescription() + result.getEstName();
        holder.tvTitle.setText(title);
        if (GlobalMethods.getStoredLanguage(mContext).equals(LANGUAGE_ARABIC))
            holder.ibArrow.setImageBitmap(GlobalMethods.flipImage(mContext));
        else
           holder.ibArrow.setImageDrawable(mContext.getResources().getDrawable(R.drawable.ic_arrow_right));

        isButtonsShow = false;
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isButtonsShow){
                    isButtonsShow = false;
                    holder.ibArrow.animate().setDuration(200).alpha(0.0f).setListener(new AnimatorListenerAdapter() {
                        @Override
                        public void onAnimationEnd(Animator animation) {
                            super.onAnimationEnd(animation);

                            holder.ibArrow.animate().setDuration(200).alpha(1.0f).setListener(new AnimatorListenerAdapter() {
                                @Override
                                public void onAnimationStart(Animator animation) {
                                    super.onAnimationStart(animation);
                                    if (GlobalMethods.getStoredLanguage(mContext).equals(LANGUAGE_ARABIC))
                                        holder.ibArrow.setImageBitmap(GlobalMethods.flipImage(mContext));
                                    else {

                                        holder.ibArrow.setImageDrawable(mContext.getResources().getDrawable(R.drawable.ic_arrow_right));
                                    }
                                   /* Bitmap bitmap = BitmapFactory.decodeResource(mContext.getResources(),R.drawable.ic_arrow_down);
                                    holder.ibArrow.setImageBitmap(bitmap);*/
                                    holder.btnReschedule.setVisibility(View.GONE);
                                    holder.btnDelete.setVisibility(View.GONE);


                                    //holder.btnReschedule.setVisibility(View.GONE);

                                   // holder.btnDelete.setAnimation(GlobalMethodsKotlin.Companion.setAnimation(mContext,R.anim.fade_out));
                                    //holder.btnDelete.setVisibility(View.GONE);

                                }

                            });
                        }
                    });
                    //holder.ibArrow.setImageDrawable(mContext.getResources().getDrawable(R.drawable.ic_arrow_right));


                   /* holder.btnReschedule.setVisibility(View.GONE);
                    holder.btnDelete.setVisibility(View.GONE);*/
                }else {
                    isButtonsShow = true;
                   /* Bitmap bitmap = BitmapFactory.decodeResource(mContext.getResources(),R.drawable.ic_arrow_down);
                    holder.ibArrow.setImageBitmap(bitmap);*/
                    holder.ibArrow.animate().setDuration(200).alpha(0.0f).setListener(new AnimatorListenerAdapter() {
                        @Override
                        public void onAnimationEnd(Animator animation) {
                            super.onAnimationEnd(animation);

                            holder.ibArrow.animate().setDuration(200).alpha(1.0f).setListener(new AnimatorListenerAdapter() {
                                @Override
                                public void onAnimationStart(Animator animation) {
                                    super.onAnimationStart(animation);
                                    Bitmap bitmap = BitmapFactory.decodeResource(mContext.getResources(),R.drawable.ic_arrow_down);
                                    holder.ibArrow.setImageBitmap(bitmap);

                                    holder.btnReschedule.setVisibility(View.VISIBLE);
                                    holder.btnReschedule.setAnimation(GlobalMethodsKotlin.Companion.setAnimation(mContext,R.anim.fade_in));

                                    holder.btnDelete.setVisibility(View.VISIBLE);
                                    holder.btnDelete.setAnimation(GlobalMethodsKotlin.Companion.setAnimation(mContext,R.anim.fade_in));
                                }

                            });
                            //holder.ibArrow.setAlpha(1.0f);
                        }
                    });
                    //holder.ibArrow.setVisibility(View.GONE);
                    //holder.btnReschedule.setVisibility(View.VISIBLE);
                   // holder.btnDelete.setVisibility(View.VISIBLE);
                }
               /* if(holder.btnReschedule.getVisibility()==View.VISIBLE){

                    if (GlobalMethods.getStoredLanguage(mContext).equals(LANGUAGE_ARABIC))
                        holder.ibArrow.setImageBitmap(GlobalMethods.flipImage(mContext));
                    else {

                        holder.ibArrow.setImageDrawable(mContext.getResources().getDrawable(R.drawable.ic_arrow_right));
                    }

                    holder.btnReschedule.setVisibility(View.GONE);
                    holder.btnDelete.setVisibility(View.GONE);
                }else {
                    holder.ibArrow.animate().setDuration(200).alpha(0.0f).setListener(new AnimatorListenerAdapter() {
                        @Override
                        public void onAnimationEnd(Animator animation) {
                            super.onAnimationEnd(animation);
                            Bitmap bitmap = BitmapFactory.decodeResource(mContext.getResources(),R.drawable.ic_arrow_down);
                            holder.ibArrow.setImageBitmap(bitmap);
                            holder.ibArrow.animate().alpha(1.0f);
                        }
                    });
                    //holder.ibArrow.setVisibility(View.GONE);
                    holder.btnReschedule.setVisibility(View.VISIBLE);
                    holder.btnDelete.setVisibility(View.VISIBLE);
                }*/
            }
        });
        holder.btnReschedule.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mCallback.onMyListItemClicked(result, ACTION_RESCHEDULE, holder.getAdapterPosition());
            }
        });

        holder.btnDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mCallback.onMyListItemClicked(result, ACTION_CANCEL, holder.getAdapterPosition());
            }
        });

    /*    holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mCallback.onMyListItemClicked(result, "");
            }
        });*/

    }


    @Override
    public int getItemCount() {
        return mItemsArrayList.size();
    }

    public void updateItemsList(ArrayList<ApiAppointmentsListHolder.Appointments> items) {
        mItemsArrayList = items;
        notifyDataSetChanged();
    }

    public void deleteItemAt(int position) {
        mItemsArrayList.remove(position);
        notifyItemRemoved(position);
    }


    public class MyViewHolder extends RecyclerView.ViewHolder {

        private Button btnReschedule;
        private Button btnDelete;
        private TextView tvTitle;
        private ImageButton ibArrow;

        public MyViewHolder(View view) {
            super(view);
            btnReschedule = itemView.findViewById(R.id.btn_reschedule);
            btnDelete = itemView.findViewById(R.id.btn_delete);
            tvTitle = itemView.findViewById(R.id.tv_title);
            ibArrow = itemView.findViewById(R.id.imgArrowDetailsSelf);
            btnReschedule.setVisibility(View.GONE);
            btnDelete.setVisibility(View.GONE);
            Bitmap bitmap = BitmapFactory.decodeResource(mContext.getResources(),R.drawable.ic_arrow_right);
            ibArrow.setImageBitmap(bitmap);
        }
    }
}