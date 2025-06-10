package com.sssl.seqrgraphdemo.models.scanview;

import com.google.gson.annotations.SerializedName;

public class InstituteAuditScanResponse {
    @SerializedName("success")
    public boolean success;

    @SerializedName("status")
    public int status;

    @SerializedName("data")
    public ResponseData data;

    public class ResponseData{
        public String serialNo;

        public String userPrinted;

        public String printingDateTime;

        public String printerUsed;

        public int printCount;

        public int scan_result;

        public String key;

        public ResponseData(String serialNo, String userPrinted, String printingDateTime, String printerUsed, int printCount, int scan_result, String key) {
            this.serialNo = serialNo;
            this.userPrinted = userPrinted;
            this.printingDateTime = printingDateTime;
            this.printerUsed = printerUsed;
            this.printCount = printCount;
            this.scan_result = scan_result;
            this.key = key;
        }

        public String getSerialNo() {
            return serialNo;
        }

        public void setSerialNo(String serialNo) {
            this.serialNo = serialNo;
        }

        public String getUserPrinted() {
            return userPrinted;
        }

        public void setUserPrinted(String userPrinted) {
            this.userPrinted = userPrinted;
        }

        public String getPrintingDateTime() {
            return printingDateTime;
        }

        public void setPrintingDateTime(String printingDateTime) {
            this.printingDateTime = printingDateTime;
        }

        public String getPrinterUsed() {
            return printerUsed;
        }

        public void setPrinterUsed(String printerUsed) {
            this.printerUsed = printerUsed;
        }

        public int getPrintCount() {
            return printCount;
        }

        public void setPrintCount(int printCount) {
            this.printCount = printCount;
        }

        public int getScan_result() {
            return scan_result;
        }

        public void setScan_result(int scan_result) {
            this.scan_result = scan_result;
        }

        public String getKey() {
            return key;
        }

        public void setKey(String key) {
            this.key = key;
        }
    }

    public InstituteAuditScanResponse(boolean success, int status, ResponseData data) {
        this.success = success;
        this.status = status;
        this.data = data;
    }

    public boolean isSuccess() {
        return success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public ResponseData getData() {
        return data;
    }

    public void setData(ResponseData data) {
        this.data = data;
    }
}
