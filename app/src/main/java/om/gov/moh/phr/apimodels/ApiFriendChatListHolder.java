package om.gov.moh.phr.apimodels;

import android.text.TextUtils;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.ArrayList;

public class ApiFriendChatListHolder implements Serializable{
    public ArrayList<ApiFriendListInfo> getmResult() {
        return mResult;
    }

    @SerializedName("result")
    private ArrayList<ApiFriendChatListHolder.ApiFriendListInfo> mResult;

    public class ApiFriendListInfo implements Serializable {

        public ApiFriendListInfo() {
        }

        public void setMessageId(String messageId) {
            this.messageId = messageId;
        }

        public void setCreatedBy(String createdBy) {
            this.createdBy = createdBy;
        }

        public void setUnreadCount(int unreadCount) {
            this.unreadCount = unreadCount;
        }

        public void setCreatedName(String createdName) {
            this.createdName = createdName;
        }

        public void setCreatedDate(String createdDate) {
            this.createdDate = createdDate;
        }

        public void setSubject(String subject) {
            this.subject = subject;
        }

        public void setReceivedBy(String receivedBy) {
            this.receivedBy = receivedBy;
        }

        public void setPrevMessageId(long prevMessageId) {
            this.prevMessageId = prevMessageId;
        }

        public void setMessageBody(String messageBody) {
            this.messageBody = messageBody;
        }

        @SerializedName("messageId")
        private String messageId;

        @SerializedName("createdBy")
        private String createdBy;

        @SerializedName("unreadCount")
        private int unreadCount;

        @SerializedName("createdName")
        private String createdName;

        @SerializedName("createdDate")
        private String createdDate;

        @SerializedName("subject")
        private String subject;

        @SerializedName("receivedBy")
        private String receivedBy;

        @SerializedName("isNew")
        private boolean isNew;

        public boolean isNew() {
            return isNew;
        }

        public void setNew(boolean aNew) {
            isNew = aNew;
        }

        public String getSubject() {
            return subject;
        }

        public String getReceivedBy() {
            return receivedBy;
        }

        public long getPrevMessageId() {
            return prevMessageId;
        }

        public String getMessageBody() {
            if(messageBody==null||TextUtils.isEmpty(messageBody))
                return "";
            else
            return messageBody;
        }

        @SerializedName("prevMessageId")
        private long prevMessageId;

        @SerializedName("messageBody")
        private String messageBody;

        public String getMessageId() {
            return messageId;
        }

        public String getCreatedBy() {
            if (createdBy == null || TextUtils.isEmpty(createdBy))
                return "";
            else
                return createdBy;
        }

        public int getUnreadCount() {
            return unreadCount;
        }

        public String getCreatedName() {
            if (createdName == null || TextUtils.isEmpty(createdName))
                return "";
            else
                return createdName;
        }

        public String getCreatedDate() {
            if (createdDate == null || TextUtils.isEmpty(createdDate))
                return "";
            else
                return createdDate;
        }
    }
}
