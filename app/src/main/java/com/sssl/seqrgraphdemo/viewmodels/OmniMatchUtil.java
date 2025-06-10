package com.sssl.seqrgraphdemo.viewmodels;

import android.util.Log;

import com.google.protobuf.ByteString;
import com.google.protobuf.InvalidProtocolBufferException;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import Tech5.OmniMatch.AuthMatcher;
import Tech5.OmniMatch.BioCommon;
import Tech5.OmniMatch.Common;
import Tech5.OmniMatch.ConvertImageFormat;
import Tech5.OmniMatch.FaceDigitalId;
import Tech5.OmniMatch.FingerCommon;
import Tech5.OmniMatch.JNI.Convert.ConvertImageFormatNative;
import Tech5.OmniMatch.JNI.DigitalId.FaceDigitalIdInstance;
import Tech5.OmniMatch.JNI.DigitalId.FaceDigitalIdNative;
import Tech5.OmniMatch.JNI.Matchers.AuthMatcherInstance;
import Tech5.OmniMatch.JNI.Matchers.AuthMatcherNative;
import Tech5.OmniMatch.JNI.OmniMatchException;
import Tech5.OmniMatch.JNI.TemplateCreators.TemplateCreatorNNInstance;
import Tech5.OmniMatch.JNI.TemplateCreators.TemplateCreatorNNNative;
import Tech5.OmniMatch.JNI.TemplateCreators.TemplateCreatorPropFingerInstance;
import Tech5.OmniMatch.JNI.TemplateCreators.TemplateCreatorPropFingerNative;
import Tech5.OmniMatch.MatcherCommon;
import Tech5.OmniMatch.TemplateCreatorCommon;
import Tech5.OmniMatch.TemplateCreatorPropFinger;

public class OmniMatchUtil {





    public byte[] compressFaceImage(FaceDigitalIdNative faceDigitalIdNative, FaceDigitalIdInstance faceDigitalIdInstance, byte[] faceImage, int compressionLevel) throws IOException, OmniMatchException {

        Log.d("TAG", "creating compressed face image");

        byte[] propData = null;

        Common.Image image = Common.Image.newBuilder().setBytes(ByteString.copyFrom(faceImage)).setFormat(Common.ImageFormat.JPEG).build();

        FaceDigitalId.FaceExtractRequest faceExtractRequest = FaceDigitalId.FaceExtractRequest.newBuilder().addImages(image).setBackgroundColor(Common.Color.newBuilder().clearBlue().build()).setCompressionType(FaceDigitalId.CompressionType.CompressionFaceProp).setCompressionLevel(compressionLevel).build();


        FaceDigitalId.FaceExtractResponse faceExtractResponse = faceDigitalIdNative.ExtractFaces(faceDigitalIdInstance, faceExtractRequest);
        for (FaceDigitalId.FaceExtractResult faceExtractResult : faceExtractResponse.getResultsList()) {
            propData = faceExtractResult.getExtracted().getFaces(0).getCompressedImage().getBytes().toByteArray();
        }

        Log.d("TAG", "compressed face image creation success");
        return propData;
    }


    public byte[] createFaceTemplate(TemplateCreatorNNNative templateCreatorNNNative, TemplateCreatorNNInstance templateCreatorNNInstance, byte[] face, Common.ImageFormat imageFormat) throws OmniMatchException, InvalidProtocolBufferException {

        Log.d("TAG", "creating face template");

        Common.Image faceImage = Common.Image.newBuilder().setBytes(ByteString.copyFrom(face)).setFormat(imageFormat).build();

        return createFaceTemplate(templateCreatorNNNative, templateCreatorNNInstance, faceImage);
    }


    public byte[] createFaceTemplate(TemplateCreatorNNNative templateCreatorNNNative, TemplateCreatorNNInstance templateCreatorNNInstance, Common.Image faceImage) throws OmniMatchException, InvalidProtocolBufferException {

        TemplateCreatorCommon.CreateTemplateRequest createTemplateRequest = TemplateCreatorCommon.CreateTemplateRequest.newBuilder().addImages(faceImage).setDoSegmentation(false).build();
        TemplateCreatorCommon.CreateTemplateResponse createTemplateResponse = templateCreatorNNNative.CreateTemplate(templateCreatorNNInstance, createTemplateRequest);


        if ((createTemplateResponse.getResultCode() == Common.ResultCode.Success) && (createTemplateResponse.getResultsCount() == 1)) {

            Log.d("TAG", "face template creation success");
            return createTemplateResponse.getResults(0).getTemplateResult().getTemplate().getData().toByteArray();
        }


        return new byte[0];
    }


    public Map<Integer, byte[]> createMinexNistT5Templates(
            TemplateCreatorPropFingerNative templateCreatorPropFingerNative,
            TemplateCreatorPropFingerInstance templateCreatorPropFingerInstance,
            Map<Integer, byte[]> fingerImages,
            Common.ImageFormat fingersImageFormat, int maxTemplateSize) throws OmniMatchException, InvalidProtocolBufferException {

        long startTime = System.currentTimeMillis();

        Map<Integer, byte[]> templatesMap = new HashMap<>();

        Log.d("TAG", "creating batch NiST T5 templates  " + fingerImages.size());


        if (fingerImages.isEmpty()) {

            return templatesMap;

        }


        TemplateCreatorPropFinger.CreateMinexPropFingerTemplateRequest.Builder builder = TemplateCreatorPropFinger.CreateMinexPropFingerTemplateRequest.newBuilder();


        for (Map.Entry<Integer, byte[]> mapElement : fingerImages.entrySet()) {

            Common.Image image = Common.Image.newBuilder()
                    .setBatchIdentifier(Common.BatchIdentifier.newBuilder().setId(String.valueOf(mapElement.getKey())).build())
                    .setBytes(ByteString.copyFrom(mapElement.getValue()))
                    .setFormat(fingersImageFormat)
                    .build();

            builder.addImages(image);


        }


        TemplateCreatorPropFinger.CreateMinexPropFingerTemplateRequest createMinexFingerTemplateRequest = builder.setDoSegmentation(false)
                .setTemplateType(FingerCommon.MinexTemplateType.NistT5Template).setMaxTemplateSize(maxTemplateSize).build();


        TemplateCreatorPropFinger.CreateMinexPropFingerTemplateResponse createMinexFingerTemplateResponse = templateCreatorPropFingerNative.CreateMinexTemplate(templateCreatorPropFingerInstance, createMinexFingerTemplateRequest);


        if ((createMinexFingerTemplateResponse.getResultCode() == Common.ResultCode.Success) && (createMinexFingerTemplateResponse.getResultsCount() == fingerImages.size())) {
            List<TemplateCreatorPropFinger.CreateMinexPropFingerTemplateResult> list = createMinexFingerTemplateResponse.getResultsList();


            for (TemplateCreatorPropFinger.CreateMinexPropFingerTemplateResult result : list) {

                String batchIdentifier = result.getTemplateResult().getBatchIdentifier().getId();

                Log.d("TAG", "batch identifier : " + batchIdentifier);

                try {

                    int fingerPosition = Integer.parseInt(batchIdentifier);

                    templatesMap.put(fingerPosition, result.getTemplateResult().getTemplate().getData().toByteArray());

                } catch (Exception e) {
                    Log.e("TAG", "error in template creation " + e.getLocalizedMessage());

                }

            }

        }


        Log.d("TAG", "Time took for batch NiST T5 templates creation " + (System.currentTimeMillis() - startTime) + "ms");

        return templatesMap;


    }


    public byte[] createNISTT5FingerTemplate(TemplateCreatorPropFingerNative templateCreatorPropFingerNative, TemplateCreatorPropFingerInstance templateCreatorPropFingerInstance, byte[] fingerImage, Common.ImageFormat imageFormat, int maxTemplateSize) throws OmniMatchException, InvalidProtocolBufferException {


        Log.d("TAG", "creating NiST T5 template  ");
        long startTime = System.currentTimeMillis();

        Common.Image image = Common.Image.newBuilder().setBytes(ByteString.copyFrom(fingerImage)).setFormat(imageFormat).build();


        TemplateCreatorPropFinger.CreateMinexPropFingerTemplateRequest createMinexFingerTemplateRequest = TemplateCreatorPropFinger.CreateMinexPropFingerTemplateRequest.newBuilder().addImages(image).setDoSegmentation(false).setTemplateType(FingerCommon.MinexTemplateType.NistT5Template).setMaxTemplateSize(maxTemplateSize).build();

        TemplateCreatorPropFinger.CreateMinexPropFingerTemplateResponse createMinexFingerTemplateResponse = templateCreatorPropFingerNative.CreateMinexTemplate(templateCreatorPropFingerInstance, createMinexFingerTemplateRequest);


        Log.d("TAG", "Time took for NIST T5 template creation : " + (System.currentTimeMillis() - startTime) + "ms");


        if ((createMinexFingerTemplateResponse.getResultCode() == Common.ResultCode.Success) && (createMinexFingerTemplateResponse.getResultsCount() == 1)) {
            return createMinexFingerTemplateResponse.getResults(0).getTemplateResult().getTemplate().getData().toByteArray();
        }


        return new byte[0];
    }


    public byte[] decompressProprietaryFaceImage(byte[] compressedFace) throws IOException, OmniMatchException {
        ConvertImageFormatNative convertImageFormatNative = new ConvertImageFormatNative();
        Common.Image image = Common.Image.newBuilder().setBytes(ByteString.copyFrom(compressedFace)).setFormat(Common.ImageFormat.FaceProp).build();

        ConvertImageFormat.ConvertImageFormatRequest convertImageFormatRequest = ConvertImageFormat.ConvertImageFormatRequest.newBuilder()
                .setSourceImage(image)
                .setTargetFormat(Common.ImageFormat.JPEG)
                .setBioType(BioCommon.BioType.Face)
                .build();

        ConvertImageFormat.ConvertImageFormatResponse convertImageFormatResponse = convertImageFormatNative.Convert(convertImageFormatRequest);

        if (convertImageFormatResponse.getResultCode() == Common.ResultCode.Success) {

            return convertImageFormatResponse.getTargetImage().getBytes().toByteArray();
        }


        return new byte[0];

    }


    public MatcherCommon.RecordResult matchFaceTemplates(AuthMatcherNative authMatcherNative, AuthMatcherInstance authMatcherInstance, byte[] template1, byte[] template2) throws OmniMatchException, IOException {
        ByteString faceTemplate1 = ByteString.copyFrom(template1);
        ByteString faceTemplate2 = ByteString.copyFrom(template2);
        AuthMatcher.AuthVerifyRecordRequest authVerifyRecordRequest = AuthMatcher.AuthVerifyRecordRequest.newBuilder().setReferenceRecord(MatcherCommon.Record.newBuilder().setFace(BioCommon.MatcherTemplate.newBuilder().setTemplateData(BioCommon.Template.newBuilder().setData(faceTemplate1).setQuality(100).build()).build()).build()).setCapturedRecord(MatcherCommon.Record.newBuilder().setFace(BioCommon.MatcherTemplate.newBuilder().setTemplateData(BioCommon.Template.newBuilder().setData(faceTemplate2).setQuality(100).build()).build()).build()).setVerifyMode(MatcherCommon.VerifyMode.NN_ONLY).build();
        return authMatcherNative.VerifyRecord(authMatcherInstance, authVerifyRecordRequest);


    }


    public float matchFingerMinexNistT5Templates(AuthMatcherNative authMatcherNative, AuthMatcherInstance authMatcherInstance, byte[] template1, byte[] template2) throws OmniMatchException, InvalidProtocolBufferException {

        ByteString nistT5template1 = ByteString.copyFrom(template1);
        ByteString nistT5template2 = ByteString.copyFrom(template2);


        AuthMatcher.AuthVerifyMinexRecordRequest authVerifyMinexRecordRequest = AuthMatcher.AuthVerifyMinexRecordRequest.newBuilder().setCapturedTemplate(BioCommon.Template.newBuilder().setData(nistT5template1).setQuality(100).build()).setReferenceTemplate(BioCommon.Template.newBuilder().setData(nistT5template2).setQuality(100).build()).setMinexTemplateType(FingerCommon.MinexTemplateType.NistT5Template).build();
        MatcherCommon.BioScoresResult bioScoresResult = authMatcherNative.VerifyMinexRecord(authMatcherInstance, authVerifyMinexRecordRequest);

        if (bioScoresResult != null && bioScoresResult.hasScores()) {

            return bioScoresResult.getScores().getLogFAR();

        }

        return 0f;

    }

    private Common.Image deserializeImage(byte[] image, Common.ImageFormat imageFormat, String batchIdentifier) {
        Common.Image.Builder builder = Common.Image.newBuilder();
        if (batchIdentifier != null) {
            builder.setBatchIdentifier(Common.BatchIdentifier.newBuilder().setId(batchIdentifier).build());
        }


        return builder.setBytes(ByteString.copyFrom(image)).setFormat(imageFormat).build();
    }




}
