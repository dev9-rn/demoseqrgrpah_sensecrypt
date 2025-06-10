package com.sssl.seqrgraphdemo.models;

import android.text.TextUtils;
import android.util.Log;


import com.sssl.seqrgraphdemo.tlvdecoder.TLVDecodeResult;
import com.sssl.seqrgraphdemo.tlvdecoder.ITLVRecord;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;

//import ai.sssl.digitalid.tlvdecoder.TLVDecodeResult;
//import ai.sssl.digitalid.tlvdecoder.ITLVRecord;


public class Result {


    private int resultCode = -1;

    private boolean isFaceLink;

    public Result(boolean isFaceLink) {
        this.isFaceLink = isFaceLink;
    }


    private Map<String, String> demographics;
    private Map<String, String> demographicsresult;

    private String faceLinkDemographics;
    private Data compressedImage;

    private Data faceTemplate;

    private Map<Integer, Data> fingerprints;
    private String extra;


    private final DateFormat expiryDateFormat = new SimpleDateFormat("dd-MMM-yyyy", Locale.ENGLISH);

    public void setResultCode(int resultCode) {
        this.resultCode = resultCode;
    }

    public int getResultCode() {
        return resultCode;
    }

    public Map<String, String> getDemographics() {
        return demographics;
    }

    public Map<String, String> getFinalDemographics() {
        return demographicsresult;
    }

    public String getFaceLinkDemographics() {
        return faceLinkDemographics;
    }

    public void setDemographics(Map<String, String> demographics) {
        this.demographics = demographics;
    }

    public void setFinalDemographics(Map<String, String> demographicsresult) {
        this.demographicsresult = demographicsresult;
    }

    public Data getCompressedImage() {
        return compressedImage;
    }

    public void setCompressedImage(Data compressedImage) {
        this.compressedImage = compressedImage;
    }


    public Data getFaceTemplate() {
        return faceTemplate;
    }

    public void setFaceTemplate(Data faceTemplate) {
        this.faceTemplate = faceTemplate;
    }


    public Map<Integer, Data> getFingerprints() {
        return fingerprints;
    }

    public void setFingerprints(Map<Integer, Data> fingerprints) {
        this.fingerprints = fingerprints;
    }

    public String getExtra() {
        return extra;
    }

    public void setExtra(String extra) {
        this.extra = extra;
    }


    public void setResultsFromTlvRecords(TLVDecodeResult tlvDecodeResult, Date expiryTime) {

        List<ITLVRecord> recordList = tlvDecodeResult.getItlvRecordList();


        String signatureStatus = tlvDecodeResult.getSignatureStatus();


        fingerprints = new LinkedHashMap<>();

        for (ITLVRecord itlvRecord : recordList) {
            assignValuesByRecordType(itlvRecord, expiryTime, signatureStatus);
        }


        checkForEncryptedFields();


    }


    private void checkForEncryptedFields() {

        //check if any fields are encrypted
        if (TextUtils.isEmpty(extra)) {
            return;
        }

        String[] encryptedFields = extra.split(",");

        for (String field : encryptedFields) {

            if (field.equals("fi")) {
                compressedImage.setEncrypted(true);

            } else if (field.equals("ft")) {
                faceTemplate.setEncrypted(true);

            } else {

                try {
                    int fingerPos = Integer.parseInt(field);
                    if (fingerPos > 0 && fingerPos <= 10) {
                        Objects.requireNonNull(fingerprints.get(fingerPos)).setEncrypted(true);
                    }

                } catch (Exception e) {
                    Log.e("TAG", "invalid finger pos " + field);

                }
            }

        }
    }


    private void assignValuesByRecordType(ITLVRecord itlvRecord, Date expiryDate, String signatureStatus) {


        try {

            Log.d("TAG", "type " + itlvRecord.getType());


            if (itlvRecord.getType() == ITLVRecord.IDEncodeFieldType.FACE_TEMPLATE) {
                faceTemplate = new Data(itlvRecord.getData());

            } else if (itlvRecord.getType() == ITLVRecord.IDEncodeFieldType.DEMOG) {

                Log.d("TAG", "demogs " + new String(itlvRecord.getData()));


                splitDemographics(itlvRecord, expiryDate, signatureStatus, isFaceLink);


            } else if (itlvRecord.getType() == ITLVRecord.IDEncodeFieldType.EXTRA) {

                extra = new String(itlvRecord.getData());

                Log.d("TAG", "encrypted fields " + new String(itlvRecord.getData()));

            } else if (itlvRecord.getType() == ITLVRecord.IDEncodeFieldType.FACE_COMPRESSED_IMAGE) {

                compressedImage = new Data(itlvRecord.getData());

            } else if (itlvRecord.getType() == ITLVRecord.IDEncodeFieldType.FINGER_TEMPLATE_R_1) {

                fingerprints.put(1, new Data(itlvRecord.getData()));

            } else if (itlvRecord.getType() == ITLVRecord.IDEncodeFieldType.FINGER_TEMPLATE_R_2) {

                fingerprints.put(2, new Data(itlvRecord.getData()));

            } else if (itlvRecord.getType() == ITLVRecord.IDEncodeFieldType.FINGER_TEMPLATE_R_3) {

                fingerprints.put(3, new Data(itlvRecord.getData()));

            } else if (itlvRecord.getType() == ITLVRecord.IDEncodeFieldType.FINGER_TEMPLATE_R_4) {

                fingerprints.put(4, new Data(itlvRecord.getData()));

            } else if (itlvRecord.getType() == ITLVRecord.IDEncodeFieldType.FINGER_TEMPLATE_R_5) {

                fingerprints.put(5, new Data(itlvRecord.getData()));

            } else if (itlvRecord.getType() == ITLVRecord.IDEncodeFieldType.FINGER_TEMPLATE_L_1) {

                fingerprints.put(6, new Data(itlvRecord.getData()));

            } else if (itlvRecord.getType() == ITLVRecord.IDEncodeFieldType.FINGER_TEMPLATE_L_2) {

                fingerprints.put(7, new Data(itlvRecord.getData()));

            } else if (itlvRecord.getType() == ITLVRecord.IDEncodeFieldType.FINGER_TEMPLATE_L_3) {

                fingerprints.put(8, new Data(itlvRecord.getData()));

            } else if (itlvRecord.getType() == ITLVRecord.IDEncodeFieldType.FINGER_TEMPLATE_L_4) {

                fingerprints.put(9, new Data(itlvRecord.getData()));

            } else if (itlvRecord.getType() == ITLVRecord.IDEncodeFieldType.FINGER_TEMPLATE_L_5) {

                fingerprints.put(10, new Data(itlvRecord.getData()));

            }


        } catch (Exception e) {
            Log.e("TAG", "exception", e);
        }

    }

    public void convertToMap(String input) {
        Map<String, String> keyValuePairs = new LinkedHashMap<>();

        // Split the input string by the pipe character (|)
        String[] pairs = input.split("\\|");

        for (String pair : pairs) {
            // Split each pair by the colon character (:)
            String[] keyValue = pair.trim().split("\\s*:\\s*", 2);

            if (keyValue.length == 2) {
                // Add key-value pair to the map
                keyValuePairs.put(keyValue[0], keyValue[1]);
            }
        }

        setFinalDemographics(keyValuePairs);
    }

    private void splitDemographics(ITLVRecord itlvRecord, Date expiryDate, String signatureStatus, boolean isFaceLink) {

        String demogsStrings = new String(itlvRecord.getData());


        if (isFaceLink) {

            faceLinkDemographics = demogsStrings;

        } else {
            String[] values = demogsStrings.split(",");
            String[] keys = {"Name", "Date of Birth", "Email", "Ref ID", "Expiry Date", "Digital Signature"};
            demographics = new LinkedHashMap<>();
//            for (int i = 0; i < values.length; i++) {
//                demographics.put(keys[i], values[i]);
//            }
            demographics.put(keys[0] , demogsStrings);
            if (expiryDate != null) {


                Calendar calendar = new GregorianCalendar();
                calendar.setTime(expiryDate);
                int year = calendar.get(Calendar.YEAR);

                Log.d("TAG", "expiry year :" + year);

                if (year > 2000) {

                    String expiry = expiryDateFormat.format(expiryDate);

                    demographics.put(keys[4], expiry);
                } else {
                    demographics.put(keys[4], "N/A");
                }

            } else {
                demographics.put(keys[4], "N/A");
            }

            if (signatureStatus != null) {
                demographics.put(keys[5], signatureStatus);
            } else {
                demographics.put(keys[5], "N/A");
            }

        }


    }
}
