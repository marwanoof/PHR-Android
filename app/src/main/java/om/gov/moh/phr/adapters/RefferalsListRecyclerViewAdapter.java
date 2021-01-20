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

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

import om.gov.moh.phr.R;
import om.gov.moh.phr.apimodels.ApiAppointmentsListHolder;
import om.gov.moh.phr.apimodels.ApiHomeHolder;
import om.gov.moh.phr.interfaces.AdapterToFragmentConnectorInterface;
import om.gov.moh.phr.models.GlobalMethods;
import om.gov.moh.phr.models.GlobalMethodsKotlin;

import static om.gov.moh.phr.models.MyConstants.LANGUAGE_ARABIC;

public class RefferalsListRecyclerViewAdapter extends
        RecyclerView.Adapter<RefferalsListRecyclerViewAdapter.MyViewHolder> {
    private ArrayList<ApiAppointmentsListHolder.Referrals> mItemsArrayList = new ArrayList<>();
    private ArrayList<ApiHomeHolder.Referrals> mHomeReferralsArrayList = new ArrayList<>();
    private Context mContext;
    private AdapterToFragmentConnectorInterface mCallback;
    private boolean isButtonsShow = false;
    private boolean isFromAppointment = true;

    //the constructor of the LastRecordsRecyclerViewAdapter
    public RefferalsListRecyclerViewAdapter(AdapterToFragmentConnectorInterface fragment, Context context, boolean isFromAppointment) {
        this.mContext = context;
        mCallback = fragment;
        this.isFromAppointment = isFromAppointment;
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
        if (isFromAppointment) {
            final ApiAppointmentsListHolder.Referrals result = mItemsArrayList.get(position);
            String releasedTime;
            if(result.getSendDate()!=0){
                Date date = new Date(result.getSendDate());
                SimpleDateFormat df2 = new SimpleDateFormat("dd-MMM-yyyy", Locale.ENGLISH);
                 releasedTime = df2.format(date);
            }else
                releasedTime = "--";

            String title = releasedTime + " | " + result.getRefInstitute() /*+ ", " + result.getDescription()*/;
            holder.tvTitle.setText(title);
            if (GlobalMethods.getStoredLanguage(mContext).equals(LANGUAGE_ARABIC))
                holder.ibArrow.setImageBitmap(GlobalMethods.flipImage(mContext));
            else
                holder.ibArrow.setImageDrawable(mContext.getResources().getDrawable(R.drawable.ic_arrow_right));
            if (result.getDescription() != null) {
                holder.itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        holder.tvDescRef.setText(mContext.getResources().getString(R.string.description) + "\n \n" + result.getDescription());
                        //   mCallback.onMyListItemClicked(result.getDescription(), "REFFERAL");
                        if (isButtonsShow) {
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
                                            holder.tvDescRef.setVisibility(View.GONE);
                                            holder.tvDescRef.setAnimation(GlobalMethodsKotlin.Companion.setAnimation(mContext, R.anim.fade_out));

                                        }

                                    });
                                }
                            });
                        } else {
                            isButtonsShow = true;
                            holder.ibArrow.animate().setDuration(200).alpha(0.0f).setListener(new AnimatorListenerAdapter() {
                                @Override
                                public void onAnimationEnd(Animator animation) {
                                    super.onAnimationEnd(animation);

                                    holder.ibArrow.animate().setDuration(200).alpha(1.0f).setListener(new AnimatorListenerAdapter() {
                                        @Override
                                        public void onAnimationStart(Animator animation) {
                                            super.onAnimationStart(animation);
                                            Bitmap bitmap = BitmapFactory.decodeResource(mContext.getResources(), R.drawable.ic_arrow_down);
                                            holder.ibArrow.setImageBitmap(bitmap);

                                            holder.tvDescRef.setVisibility(View.VISIBLE);
                                            holder.tvDescRef.setAnimation(GlobalMethodsKotlin.Companion.setAnimation(mContext, R.anim.fade_in));
                                        }

                                    });
                                }
                            });
                        }
                    }
                });
            } else {
                holder.ibArrow.setVisibility(View.GONE);
                holder.tvDescRef.setVisibility(View.GONE);
            }
        } else {
            final ApiHomeHolder.Referrals result = mHomeReferralsArrayList.get(position);
            Date date = new Date(result.getSendDate());
            SimpleDateFormat df2 = new SimpleDateFormat("dd-MMM-yyyy", Locale.ENGLISH);
            String releasedTime = df2.format(date);
            String title = releasedTime + " | " + result.getRefInstitute() /*+ ", " + result.getDescription()*/;
            holder.tvTitle.setText(title);
            holder.tvTitle.setPadding(16,0,16,0);
            if (GlobalMethods.getStoredLanguage(mContext).equals(LANGUAGE_ARABIC))
                holder.ibArrow.setImageBitmap(GlobalMethods.flipImage(mContext));
            else
                holder.ibArrow.setImageDrawable(mContext.getResources().getDrawable(R.drawable.ic_arrow_right));
            if (result.getDescription() != null) {
                holder.itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        holder.tvDescRef.setText(mContext.getResources().getString(R.string.description) + "\n \n" + result.getDescription());
                        //   mCallback.onMyListItemClicked(result.getDescription(), "REFFERAL");
                        if (isButtonsShow) {
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
                                            holder.tvDescRef.setVisibility(View.GONE);
                                            holder.tvDescRef.setAnimation(GlobalMethodsKotlin.Companion.setAnimation(mContext, R.anim.fade_out));

                                        }

                                    });
                                }
                            });
                        } else {
                            isButtonsShow = true;
                            holder.ibArrow.animate().setDuration(200).alpha(0.0f).setListener(new AnimatorListenerAdapter() {
                                @Override
                                public void onAnimationEnd(Animator animation) {
                                    super.onAnimationEnd(animation);

                                    holder.ibArrow.animate().setDuration(200).alpha(1.0f).setListener(new AnimatorListenerAdapter() {
                                        @Override
                                        public void onAnimationStart(Animator animation) {
                                            super.onAnimationStart(animation);
                                            Bitmap bitmap = BitmapFactory.decodeResource(mContext.getResources(), R.drawable.ic_arrow_down);
                                            holder.ibArrow.setImageBitmap(bitmap);

                                            holder.tvDescRef.setVisibility(View.VISIBLE);
                                            holder.tvDescRef.setAnimation(GlobalMethodsKotlin.Companion.setAnimation(mContext, R.anim.fade_in));
                                        }

                                    });
                                }
                            });
                        }
                    }
                });
            } else {
                holder.ibArrow.setVisibility(View.GONE);
                holder.tvDescRef.setVisibility(View.GONE);
            }
        }

    }


    @Override
    public int getItemCount() {
        if (isFromAppointment)
            return mItemsArrayList.size();
        else
            return mHomeReferralsArrayList.size();
    }

    public void updateItemsList(ArrayList<ApiAppointmentsListHolder.Referrals> items) {
        mItemsArrayList = items;
        notifyDataSetChanged();
    }

    public void updateHomeReferralsList(ArrayList<ApiHomeHolder.Referrals> items) {
        mHomeReferralsArrayList = items;
        notifyDataSetChanged();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {

        private Button btnReschedule;
        private Button btnDelete;
        private TextView tvTitle, tvDescRef;
        private ImageButton ibArrow;

        public MyViewHolder(View view) {
            super(view);
            btnReschedule = itemView.findViewById(R.id.btn_reschedule);
            btnDelete = itemView.findViewById(R.id.btn_delete);
            tvTitle = itemView.findViewById(R.id.tv_title);
            ibArrow = itemView.findViewById(R.id.imgArrowDetailsSelf);
            tvDescRef = itemView.findViewById(R.id.tvDescRef);
            btnReschedule.setVisibility(View.GONE);
            btnDelete.setVisibility(View.GONE);
        }
    }
}
