package om.gov.moh.phr.apimodels;

import android.text.TextUtils;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.ArrayList;

public class ApigetMessages {
    @SerializedName("result")
    private ArrayList<Result> result = new ArrayList<>();

    public ArrayList<Result> getResult() {
        return result;
    }

    public class Result implements Serializable {
        @SerializedName("messageId")
        private Long messageId;

        @SerializedName("createdBy")
        private String createdBy;

        @SerializedName("unreadCount")
        private Integer unreadCount;

        @SerializedName("createdName")
        private String createdName;

        @SerializedName("createdDate")
        private String createdDate;

        @SerializedName("messageBody")
        private String messageBody;

        public String getMessageBody() {
            return messageBody;
        }

        public Long getMessageId() {
            return messageId;
        }

        public String getCreatedBy() {
            if(createdBy==null||TextUtils.isEmpty(createdBy))
                return "--";
            else
            return createdBy;
        }

        public Integer getUnreadCount() {
            return unreadCount;
        }

        public String getCreatedName() {
            if(createdName==null||TextUtils.isEmpty(createdName))
                return "--";
            else
            return createdName;
        }

        public String getCreatedDate() {
            if (createdDate == null || TextUtils.isEmpty(createdDate))
                return "--";
            else
                return createdDate;
        }

        public void setMessageId(Long messageId) {
            this.messageId = messageId;
        }

        public void setCreatedBy(String createdBy) {
            this.createdBy = createdBy;
        }

        public void setUnreadCount(Integer unreadCount) {
            this.unreadCount = unreadCount;
        }

        public void setCreatedName(String createdName) {
            this.createdName = createdName;
        }

        public void setCreatedDate(String createdDate) {
            this.createdDate = createdDate;
        }

        public void setMessageBody(String messageBody) {
            this.messageBody = messageBody;
        }
    }
}
