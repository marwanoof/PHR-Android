package om.gov.moh.phr.adapters;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.concurrent.TimeUnit;

import om.gov.moh.phr.R;
import om.gov.moh.phr.apimodels.Notification;
import om.gov.moh.phr.interfaces.AdapterToFragmentConnectorInterface;
import om.gov.moh.phr.interfaces.MediatorInterface;
import om.gov.moh.phr.models.DBHelper;

public class NotificationsRecyclerViewAdapter extends RecyclerView.Adapter<NotificationsRecyclerViewAdapter.MyViewHolder> {
    private ArrayList<Notification> notificationsArrayList;
    private Context context;
    private AdapterToFragmentConnectorInterface mCallback;
    private DBHelper dbHelper;
    private MediatorInterface mMediatorCallback;

    public NotificationsRecyclerViewAdapter(ArrayList<Notification> notificationsArrayList, Context context, /*AdapterToFragmentConnectorInterface fragment,*/ MediatorInterface mMediatorCallback) {
        this.notificationsArrayList = notificationsArrayList;
        this.context = context;
        //  mCallback = fragment;
        this.mMediatorCallback = mMediatorCallback;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.notification_item, parent, false);
        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull final MyViewHolder holder, final int position) {
        final Notification notificationObj = notificationsArrayList.get(position);
            holder.tvTitle.setText(notificationObj.getTitle());
            dbHelper = new DBHelper(context);
            holder.tvBody.setText(notificationObj.getBody());
            switch (notificationObj.getPnsType()) {
                case "1":
                    holder.ivNotificationitem.setImageResource(R.drawable.lab_result_not);
                    break;
                case "3":
                    holder.ivNotificationitem.setImageResource(R.drawable.immunization_not);
                    break;
                case "4":
                    holder.ivNotificationitem.setImageResource(R.drawable.diagnostic_report);
                    break;
                case "5":
                    holder.ivNotificationitem.setImageResource(R.drawable.clinical_documents);
                    break;
                default:
                    holder.ivNotificationitem.setImageResource(R.drawable.about_ic);
                    holder.ibArrow.setVisibility(View.GONE);
                    holder.ibArrow.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            displayDialog(notificationObj.getKeyId());
                        }
                    });
            }

     /*   holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!notificationObj.getPnsType().equals("10"))
                    mCallback.onMyListItemClicked(notificationObj, notificationObj.getTitle(), position);
            }
        });*/

    }

    @Override
    public int getItemCount() {
        return notificationsArrayList.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        TextView tvTitle, tvBody;
        ImageView ivNotificationitem;
        ImageButton ibArrow;

        public MyViewHolder(View view) {
            super(view);
            tvTitle = view.findViewById(R.id.tv_title);
            tvBody = view.findViewById(R.id.tv_body);
            ivNotificationitem = view.findViewById(R.id.iv_notification_ic);
            ibArrow = view.findViewById(R.id.imageButton);
        }
    }

    private void displayDialog(final String keyId) {
        AlertDialog.Builder builder1 = new AlertDialog.Builder(context);
        builder1.setMessage(context.getResources().getString(R.string.delete_notification_msg));
        builder1.setCancelable(false);

        builder1.setPositiveButton(
                context.getResources().getString(R.string.yes_dialog),
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dbHelper.deleteTitle(keyId);
                        Toast.makeText(context, context.getResources().getString(R.string.success_notification_delete_msg), Toast.LENGTH_SHORT).show();
                    }
                });

        builder1.setNegativeButton(
                context.getResources().getString(R.string.no_dialog),
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        Toast.makeText(context, context.getResources().getString(R.string.cancel_done_msg), Toast.LENGTH_SHORT).show();
                        dialog.dismiss();
                    }
                });

        AlertDialog alert11 = builder1.create();
        alert11.show();
    }
}
