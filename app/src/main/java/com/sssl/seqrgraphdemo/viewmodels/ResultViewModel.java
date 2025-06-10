package com.sssl.seqrgraphdemo.viewmodels;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.google.protobuf.InvalidProtocolBufferException;

import java.io.IOException;
import java.security.GeneralSecurityException;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import Tech5.OmniMatch.Common;
import Tech5.OmniMatch.JNI.Matchers.AuthMatcherInstance;
import Tech5.OmniMatch.JNI.Matchers.AuthMatcherNative;
import Tech5.OmniMatch.JNI.OmniMatchException;
import Tech5.OmniMatch.JNI.TemplateCreators.TemplateCreatorNNInstance;
import Tech5.OmniMatch.JNI.TemplateCreators.TemplateCreatorNNNative;
import Tech5.OmniMatch.JNI.TemplateCreators.TemplateCreatorPropFingerInstance;
import Tech5.OmniMatch.JNI.TemplateCreators.TemplateCreatorPropFingerNative;
import Tech5.OmniMatch.MatcherCommon;
import com.sssl.seqrgraphdemo.models.Result;
import com.sssl.seqrgraphdemo.utils.ImageUtils;
import com.sssl.seqrgraphdemo.utils.Pin;
import ai.tech5.finger.utils.Finger;

import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;

public class ResultViewModel extends ViewModel {

    private TemplateCreatorNNNative templateCreatorNNNative;
    private TemplateCreatorNNInstance templateCreatorNNInstance;
    private TemplateCreatorPropFingerNative templateCreatorPropFingerNative;
    private TemplateCreatorPropFingerInstance templateCreatorPropFingerInstance;
    private AuthMatcherNative authMatcherNative;
    private AuthMatcherInstance authMatcherInstance;

    private OmniMatchUtil omniMatchUtil;

    private CompositeDisposable compositeDisposable;

    byte[] decompressedImage = null;


    public MutableLiveData<Bitmap> getDecompressedFaceImage() {
        return decompressedFaceImage;
    }

    public MutableLiveData<Throwable> getMatchingError() {
        return matchingError;
    }

    public MutableLiveData<Boolean> getIsProcessing() {
        return isProcessing;
    }

    public MutableLiveData<MatcherCommon.RecordResult> getFaceMatchResult() {
        return faceMatchResult;
    }

    public MutableLiveData<Float> getFingerMatchResult() {
        return fingerMatchResult;
    }

    private MutableLiveData<Bitmap> decompressedFaceImage = new MutableLiveData<>();

    private MutableLiveData<Throwable> matchingError = new MutableLiveData<>();

    private MutableLiveData<Boolean> isProcessing = new MutableLiveData<>();

    private MutableLiveData<MatcherCommon.RecordResult> faceMatchResult = new MutableLiveData<>();

    private MutableLiveData<Float> fingerMatchResult = new MutableLiveData<>();


    public ResultViewModel() {

        compositeDisposable = new CompositeDisposable();
    }


    public void init(TemplateCreatorNNNative templateCreatorNNNative, TemplateCreatorNNInstance templateCreatorNNInstance, TemplateCreatorPropFingerNative templateCreatorPropFingerNative, TemplateCreatorPropFingerInstance templateCreatorPropFingerInstance, AuthMatcherNative authMatcherNative, AuthMatcherInstance authMatcherInstance) {
        this.templateCreatorNNNative = templateCreatorNNNative;
        this.templateCreatorNNInstance = templateCreatorNNInstance;
        this.templateCreatorPropFingerNative = templateCreatorPropFingerNative;
        this.templateCreatorPropFingerInstance = templateCreatorPropFingerInstance;
        this.authMatcherNative = authMatcherNative;
        this.authMatcherInstance = authMatcherInstance;


        this.omniMatchUtil = new OmniMatchUtil();

    }


    public void matchFace(Result result, String pin, byte[] faceImage) {


        isProcessing.postValue(true);


        Observable.create((ObservableEmitter<MatcherCommon.RecordResult> emitter) -> {
            try {
                emitter.onNext(matchFaces(result, pin, faceImage));
                emitter.onComplete();
            } catch (Exception e) {
                emitter.onError(e);
            }

        }).observeOn(AndroidSchedulers.mainThread()).subscribeOn(Schedulers.io()).subscribe(new Observer<MatcherCommon.RecordResult>() {
            @Override
            public void onSubscribe(Disposable d) {

                compositeDisposable.add(d);

            }

            @Override
            public void onNext(MatcherCommon.RecordResult recordResult) {

                isProcessing.postValue(false);
                faceMatchResult.postValue(recordResult);


            }

            @Override
            public void onError(Throwable e) {
                isProcessing.postValue(false);
                matchingError.postValue(e);

            }

            @Override
            public void onComplete() {
                isProcessing.postValue(false);
            }
        });
    }


    private MatcherCommon.RecordResult matchFaces(Result result, String pin, byte[] faceImage) throws OmniMatchException, IOException, DecryptionFailedException {


        byte[] faceTemplate = null;

        if (result.getFaceTemplate() != null) {

            try {
                faceTemplate = result.getFaceTemplate().isEncrypted() ? new Pin(pin).decrypt(result.getFaceTemplate().getContent()) :
                        result.getFaceTemplate().getContent();
            } catch (Exception e) {
                throw new DecryptionFailedException();
            }

        }


        Log.d("TAG", "template from crypto :" + (faceTemplate == null ? 0 : faceTemplate.length));


        boolean hasFaceTemplate = false;
        if (faceTemplate != null && faceTemplate.length > 0) {
            hasFaceTemplate = true;
        }


        if (result.getCompressedImage() != null && result.getCompressedImage().isEncrypted()) {
            try {

                byte[] compressedImage = new Pin(pin).decrypt(result.getCompressedImage().getContent());
                Bitmap bitmap = decompressFaceImageBytes(compressedImage);

                decompressedFaceImage.postValue(bitmap);
            } catch (Exception e) {
                throw new DecryptionFailedException();
            }
        }


        Log.d("TAG", "before create template from live :" + (faceImage == null ? 0 : faceImage.length));


        byte[] templateCapturedFace = omniMatchUtil.createFaceTemplate(templateCreatorNNNative, templateCreatorNNInstance, faceImage, Common.ImageFormat.JPEG);

        Log.d("TAG", "template from live :" + (templateCapturedFace == null ? 0 : templateCapturedFace.length));

        //because of face template is not present in cryptograph,
        // we are matching againist the compressed face image template
        if (!hasFaceTemplate) {

            byte[] decompressedFaceBytes = ImageUtils.upscaleImageToDouble(decompressedImage);
            faceTemplate = omniMatchUtil.createFaceTemplate(templateCreatorNNNative, templateCreatorNNInstance, decompressedFaceBytes, Common.ImageFormat.JPEG);

        }


        return omniMatchUtil.matchFaceTemplates(authMatcherNative, authMatcherInstance, templateCapturedFace, faceTemplate);


    }


    public void matchFingers(List<Finger> capturedFingersList, String pin, Result result, float fingerThreshold) {

        isProcessing.postValue(true);


        Observable.create((ObservableEmitter<Float> emitter) -> {
            try {
                emitter.onNext(matchWithFingers(capturedFingersList, pin, result, fingerThreshold));
                emitter.onComplete();
            } catch (Exception e) {
                emitter.onError(e);
            }

        }).observeOn(AndroidSchedulers.mainThread()).subscribeOn(Schedulers.io()).subscribe(new Observer<Float>() {
            @Override
            public void onSubscribe(Disposable d) {

                compositeDisposable.add(d);

            }

            @Override
            public void onNext(Float aFloat) {
                isProcessing.postValue(false);

                fingerMatchResult.postValue(aFloat);
            }

            @Override
            public void onError(Throwable e) {
                isProcessing.postValue(false);

                matchingError.postValue(e);
            }

            @Override
            public void onComplete() {
                isProcessing.postValue(false);

            }
        });
    }


    public float matchWithFingers(List<Finger> capturedFingersList, String pin, Result result, float fingerThreshold) throws OmniMatchException,
            InvalidProtocolBufferException,
            GeneralSecurityException {


        LinkedHashMap<Integer, byte[]> capturedFingersMap = new LinkedHashMap<>();

        for (int i = 0; i < capturedFingersList.size(); i++) {
            capturedFingersMap.put(capturedFingersList.get(i).pos, capturedFingersList.get(i).fingerImage);
        }

        if (capturedFingersMap.size() > 0) {
            Map<Integer, byte[]> treeMap = new TreeMap<>(capturedFingersMap);
            Collections.reverseOrder();
            capturedFingersMap.clear();
            capturedFingersMap.putAll(treeMap);
        }


        float matchScore = 0;


        for (Map.Entry<Integer, byte[]> fingerData : capturedFingersMap.entrySet()) {
            if (result.getFingerprints().containsKey(fingerData.getKey())) {


                byte[] probTemplate = omniMatchUtil.createNISTT5FingerTemplate(templateCreatorPropFingerNative,
                        templateCreatorPropFingerInstance, fingerData.getValue(), Common.ImageFormat.WSQ,0);


                byte[] galleryTemplate = result.getFingerprints().get(fingerData.getKey()).isEncrypted() ?
                        new Pin(pin).decrypt(result.getFingerprints().get(fingerData.getKey()).getContent()) :
                        result.getFingerprints().get(fingerData.getKey()).getContent();

                matchScore = omniMatchUtil.matchFingerMinexNistT5Templates(authMatcherNative, authMatcherInstance, probTemplate, galleryTemplate);


                if (matchScore > fingerThreshold) {
                    Log.i("TAG", "Template Matched with finger position::" + fingerData.getKey() + "..So Skipping remaining...");

                    break;
                } else {
                    Log.i("TAG", "Template Not Matched with finger position::" + fingerData.getKey() + "..So continue matching...");

                }
            }
        }


        return matchScore;


    }


    public void decompressFaceImage(byte[] faceImage) {

        Log.d("TAG", "decompressFaceImage " + faceImage.length);

        decompressFaceImageObservable(faceImage).observeOn(AndroidSchedulers.mainThread()).subscribeOn(Schedulers.io()).subscribe(new Observer<Bitmap>() {
            @Override
            public void onSubscribe(Disposable d) {

                Log.d("TAG", "onSubscribe" + d);

                compositeDisposable.add(d);

            }

            @Override
            public void onNext(Bitmap bitmap) {
                Log.d("TAG", "onNext" + bitmap);
                decompressedFaceImage.postValue(bitmap);
            }

            @Override
            public void onError(Throwable e) {

                Log.d("TAG", "onError" + e.getLocalizedMessage());

            }

            @Override
            public void onComplete() {

                Log.d("TAG", "onComplete");

            }
        });

    }


    public Observable<Bitmap> decompressFaceImageObservable(byte[] compressedFaceImage) {

        Log.d("TAG", "decompressFaceImageObservable");

        return Observable.create((ObservableEmitter<Bitmap> emitter) -> {
            try {


                emitter.onNext(decompressFaceImageBytes(compressedFaceImage));


                emitter.onComplete();
            } catch (Exception e) {


                emitter.onError(e);
            }

        });

    }


    private Bitmap decompressFaceImageBytes(byte[] compressedFaceImage) throws OmniMatchException, IOException {

        decompressedImage = omniMatchUtil.decompressProprietaryFaceImage(compressedFaceImage);

        if (decompressedImage.length > 0) {

            return BitmapFactory.decodeByteArray(decompressedImage, 0, decompressedImage.length);

        } else {
            throw new IOException("Decompression failed");

        }


    }


    @Override
    protected void onCleared() {
        super.onCleared();

        if (compositeDisposable != null && !compositeDisposable.isDisposed()) {
            compositeDisposable.dispose();
        }
    }
}
