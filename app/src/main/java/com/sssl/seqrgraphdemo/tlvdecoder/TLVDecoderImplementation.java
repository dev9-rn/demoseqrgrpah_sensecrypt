package com.sssl.seqrgraphdemo.tlvdecoder;

import android.util.Log;

import com.sssl.seqrgraphdemo.utils.SignatureVerifier;
import com.sssl.seqrgraphdemo.utils.SignatureVerifyException;
import com.nimbusds.jose.JOSEException;

import java.io.ByteArrayInputStream;
import java.io.DataInputStream;
import java.io.IOException;
import java.text.ParseException;
import java.util.ArrayList;

import java.util.Date;
import java.util.List;
import android.util.Base64;

public class TLVDecoderImplementation implements ITLVDecoder {


    @SuppressWarnings("java:S3776")
    @Override
    public TLVDecodeResult decode(byte[] tlvEncodedData, String jwsJson) throws TLVDecodeException, IllegalArgumentException, SignatureVerifyException {




        TLVDecodeResult tlvDecodeResult = new TLVDecodeResult();

        if (tlvEncodedData == null || tlvEncodedData.length < 2) {
            throw new IllegalArgumentException("tlvEncodedData length can't be < 2");
        }


        Log.d("TAG", "decoded data size :" + tlvEncodedData.length);


        Log.d("TAG", convertBytesToHexString(tlvEncodedData));


        ByteArrayInputStream btStream = new ByteArrayInputStream(tlvEncodedData);
        DataInputStream stream = new DataInputStream(btStream);

        List<ITLVRecord> result = new ArrayList<>();

        try {


            byte signFirstByte = stream.readByte();

            byte signSecondByte = stream.readByte();


            int intFirstSignByte = signFirstByte & 0xFF;
            int intSecondSignByte = signSecondByte & 0xFF;
            boolean withSignature = intFirstSignByte == 0xFF && intSecondSignByte == 0x01;

            Log.d("TAG", "with signature : " + withSignature);


            byte openFirstByte;
            byte openSecondByte;


            if (withSignature) {

                byte keyByte = stream.readByte();
                Log.d("TAG", "key byte : " + keyByte);

                int keyId = keyByte;

                //hardcoded signature length
                //signature length needs to be calculated based on keyid and type
                int signatureLength = SignatureVerifier.getSignatureLength(keyId, jwsJson);

                Log.d("TAG", "signature  length : " + signatureLength);

                byte[] signature = new byte[signatureLength];
                stream.read(signature, 0, signatureLength);

                Log.d("TAG", convertBytesToHexString(signature));



                Log.d("TAG","signature: "+ Base64.encodeToString(signature,Base64.NO_WRAP));



                // Retrieve the signed content
                int signingInputContentLength = tlvEncodedData.length - (signatureLength + 3);

                Log.d("TAG", "signed content length " + signingInputContentLength);

                byte[] signingInputContent = new byte[signingInputContentLength];
                System.arraycopy(tlvEncodedData, signatureLength + 3, signingInputContent, 0, signingInputContentLength);


                Log.d("TAG", "input:" + convertBytesToHexString(signingInputContent));

                Log.d("TAG","signed content: "+ Base64.encodeToString(signingInputContent,Base64.NO_WRAP));



                boolean isSignatureVerified = SignatureVerifier.verifySignature(jwsJson, keyId, signingInputContent, signature);

                Log.d("TAG", "isSignatureVerified : " + isSignatureVerified);


                tlvDecodeResult.setSignatureStatus(isSignatureVerified ? "Verified" : "Not Verified");


                if (!isSignatureVerified) {
                    throw new TLVDecodeException("Signature verification failed!");
                }

                openFirstByte = stream.readByte();
                openSecondByte = stream.readByte();

            } else {

                tlvDecodeResult.setSignatureStatus("N/A");

                openFirstByte = signFirstByte;
                openSecondByte = signSecondByte;
            }


            Log.d("TAG", "first " + openFirstByte + " second :" + openSecondByte);


            boolean withoutExpTime = openFirstByte == 'P' && openSecondByte == 'K';

            int intFirstByte = openFirstByte & 0xFF;
            int intSecondByte = openSecondByte & 0x55;
            boolean withExpTime = intFirstByte == 0xFF && intSecondByte == 0x55;


            Log.d("TAG", "withoutExpTime " + withoutExpTime + " withExpTime :" + withExpTime);

            if (!withoutExpTime && !withExpTime) {
                throw new TLVDecodeException("Wrong first 2 bytes!");
            }

            if (withExpTime) {
                if (stream.available() < Integer.BYTES) {
                    throw new TLVDecodeException("Data corrupted!");
                }


                //the next 4 bytes is expiry date
                byte[] expiryDateBytes = new byte[4];

                for (int index = 0; index < 4; index++) {
                    expiryDateBytes[index] = stream.readByte();
                }

                // we have unsigned int here
                long secondsSinceEpoch = readUnsignedIntLittleEndian(expiryDateBytes, 0);
                long millisSinceEpoch = secondsSinceEpoch * 1000;
                Log.d("TAG", "expiry date in millis " + secondsSinceEpoch);

                // we create instance of the Date and pass milliseconds to the constructor
                Date expiryDate = new Date(millisSinceEpoch);
                Log.d("TAG", "expiry date " + expiryDate);

                tlvDecodeResult.setExpiryDate(expiryDate);


                if (stream.available() == 0) {
                    Log.d("TAG", "expired  ");
                    throw new TLVDecodeException("Expired!");


                }
            }

            int fullDataLength = 0;

            while (stream.available() > Short.BYTES * 2) {


                int type = stream.readShort();

                short recordDataLength = stream.readShort();
                if (stream.available() < recordDataLength) {
                    throw new TLVDecodeException("Data length corrupted!");
                }

                byte[] tmp = new byte[recordDataLength];
                stream.read(tmp, 0, recordDataLength);

                ITLVRecord.IDEncodeFieldType enumType = ITLVRecord.IDEncodeFieldType.fromInteger(type);
                if (enumType == null) {
                    throw new TLVDecodeException("Unknown TLV type!");
                }
                TLVRecord tlvRecord = new TLVRecord(enumType, tmp);
                result.add(tlvRecord);


                fullDataLength += Short.BYTES * 2 + recordDataLength;
            }


            Log.d("TAG", "full data length :" + fullDataLength );


        } catch (IOException e) {
            throw new TLVDecodeException("Data corrupted!", e);
        } catch (ParseException | JOSEException | SignatureVerifyException e) {
            throw new SignatureVerifyException(e.getLocalizedMessage());
        }


        tlvDecodeResult.setItlvRecordList(result);

        Log.d("TAG", "tlvDecodeResult :" + tlvDecodeResult );
        return tlvDecodeResult;
    }


    /**
     * Read 4 bytes in Little-endian byte order.
     *
     * @param data,  the original byte array
     * @param index, start to read from.
     * @return
     */
    public static long readUnsignedIntLittleEndian(byte[] data, int index) {
        return (data[index] & 0xFF)
                | ((data[index + 1] & 0xFF) << 8)
                | ((data[index + 2] & 0xFF) << 16)
                | ((data[index + 3] & 0xFF) << 24);

    }

    private String convertBytesToHexString(byte[] data) {
        StringBuilder sb = new StringBuilder();
        for (byte b : data) {
            sb.append(String.format("%02X ", b));
        }

        return sb.toString();
    }
}
