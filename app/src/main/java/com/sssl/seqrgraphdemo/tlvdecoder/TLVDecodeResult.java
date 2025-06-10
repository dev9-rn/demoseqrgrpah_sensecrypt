package com.sssl.seqrgraphdemo.tlvdecoder;

import java.util.Date;
import java.util.List;

public class TLVDecodeResult {

    private Date expiryDate;

    private String signatureStatus;

    public String getSignatureStatus() {
        return signatureStatus;
    }

    public void setSignatureStatus(String signatureStatus) {
        this.signatureStatus = signatureStatus;
    }

    private List<ITLVRecord> itlvRecordList;

    public Date getExpiryDate() {
        return expiryDate;
    }

    public void setExpiryDate(Date expiryDate) {
        this.expiryDate = expiryDate;
    }

    public List<ITLVRecord> getItlvRecordList() {
        return itlvRecordList;
    }

    public void setItlvRecordList(List<ITLVRecord> itlvRecordList) {
        this.itlvRecordList = itlvRecordList;
    }
}
