package com.sssl.seqrgraphdemo.viewmodels;

import android.util.Base64;
import android.util.Log;

import com.sssl.seqrgraphdemo.models.Result;
import com.sssl.seqrgraphdemo.tlvdecoder.TLVDecodeException;
import com.sssl.seqrgraphdemo.tlvdecoder.TLVDecodeResult;
import com.sssl.seqrgraphdemo.tlvdecoder.TLVDecoderImplementation;
import com.sssl.seqrgraphdemo.utils.SignatureVerifyException;

import java.util.Date;

import ai.tech5.sdk.abis.cryptograph.T5CryptoClient;

public class CryptographDecodeUtil {

    byte[] decodedData = null;
    T5CryptoClient t5CryptoClient;

    private static final String UNIQUE_ID = null;
    TLVDecoderImplementation tlvDecoderImplementation;

    private boolean isFaceLink;


    public CryptographDecodeUtil(T5CryptoClient t5CryptoClient, boolean isFaceLink) {
        this.t5CryptoClient = t5CryptoClient;
        tlvDecoderImplementation = new TLVDecoderImplementation();
        this.isFaceLink = isFaceLink;
    }


    @SuppressWarnings({"java:S2129", "java:S1874", "java:S5738"})
   public Result decodeBarcodeImage(byte[] imageBarcode, String jwsJson) throws TLVDecodeException, SignatureVerifyException {

        Log.d("TAG", "decodeBarcodeImage " + t5CryptoClient);

        Result result = new Result(isFaceLink);


        int errorCode = 0;
        if (t5CryptoClient != null) {
            Integer decodedDataSize = new Integer(0);
            Integer errorFailingToDecode = new Integer(0);

            decodedData = new byte[10 * 1024];
            long startDecode = System.currentTimeMillis();

            Date expiryTime = new Date();

            int[] versionData = new int[2];


           // byte[] encodedPNG, String uniqueID, byte[] decodedData, Integer decodedData_Size, Date expiry_time, Integer ErrorBeforeFailingToDecode
            errorCode = t5CryptoClient.decode(imageBarcode, UNIQUE_ID, decodedData, decodedDataSize, expiryTime, versionData, errorFailingToDecode);
           // errorCode = t5CryptoClient.decode(imageBarcode, UNIQUE_ID, decodedData, decodedDataSize, expiryTime,  errorFailingToDecode);
            Log.d("TAG", "is errorCode kunal shirsat : " + errorCode );
            result.setResultCode( errorCode);


            long decodeTime = System.currentTimeMillis() - startDecode;


            byte[] decodedDataNew = new byte[decodedDataSize];
            System.arraycopy(decodedData, 0, decodedDataNew, 0, decodedDataSize);

            Log.d("TAG", "is decoded : " + (errorCode == 0) + " decoded time : " + decodeTime);
            Log.d("TAG", "is decoded : " + errorCode + " decoded time : " + decodeTime);


            if (errorCode == 0) {

                Log.d("TAG", "expiry time : " + expiryTime);
                Log.d("TAG", "version info : " + versionData[0] + "." + versionData[1]);


                Log.d("TAG", "decodedData size : " + decodedDataNew.length);

                Log.d("TAG", "data->" + Base64.encodeToString(decodedDataNew, Base64.NO_WRAP));
                Log.d("TAG", "data->" + new String(decodedDataNew));


                TLVDecodeResult tlvDecodeResult = tlvDecoderImplementation.decode(decodedDataNew, jwsJson);
                result.setResultsFromTlvRecords(tlvDecodeResult, expiryTime);




            }

        }

        return result;

    }


}
