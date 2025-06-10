package com.sssl.seqrgraphdemo.viewmodels;

import android.content.Context;
import android.content.res.AssetManager;
import android.util.Log;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.sssl.seqrgraphdemo.models.Result;
import com.sssl.seqrgraphdemo.networks.ApiClient;
import com.sssl.seqrgraphdemo.networks.IdencodeService;
import com.sssl.seqrgraphdemo.preferences.AppSharedPreference;
import com.sssl.seqrgraphdemo.utils.UiText;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

import Tech5.OmniMatch.AuthMatcher;
import Tech5.OmniMatch.BioCommon;
import Tech5.OmniMatch.Common;
import Tech5.OmniMatch.FaceCommon;
import Tech5.OmniMatch.FaceDetector;
import Tech5.OmniMatch.FaceDigitalId;
import Tech5.OmniMatch.FingerCommon;
import Tech5.OmniMatch.JNI.Android.AndroidNative;
import Tech5.OmniMatch.JNI.CoreNative;
import Tech5.OmniMatch.JNI.Detectors.FaceDetectorInstance;
import Tech5.OmniMatch.JNI.Detectors.FaceDetectorNative;
import Tech5.OmniMatch.JNI.DigitalId.FaceDigitalIdInstance;
import Tech5.OmniMatch.JNI.DigitalId.FaceDigitalIdNative;
import Tech5.OmniMatch.JNI.Matchers.AuthMatcherInstance;
import Tech5.OmniMatch.JNI.Matchers.AuthMatcherNative;
import Tech5.OmniMatch.JNI.OmniMatchException;
import Tech5.OmniMatch.JNI.TemplateCreators.TemplateCreatorNNInstance;
import Tech5.OmniMatch.JNI.TemplateCreators.TemplateCreatorNNNative;
import Tech5.OmniMatch.JNI.TemplateCreators.TemplateCreatorPropFingerInstance;
import Tech5.OmniMatch.JNI.TemplateCreators.TemplateCreatorPropFingerNative;
import Tech5.OmniMatch.TemplateCreatorNn;

import ai.tech5.sdk.abis.cryptograph.T5CryptoClient;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;


public class SharedViewModel extends ViewModel {

    private static final String TAG = SharedViewModel.class.getSimpleName();


    private final MutableLiveData<Boolean> isSDKInitialized = new MutableLiveData<>();

    private final MutableLiveData<String> jwsKeys = new MutableLiveData<>();

    private final MutableLiveData<Boolean> isLoadingSDK = new MutableLiveData<>();


    private final MutableLiveData<UiText> status = new MutableLiveData<>();


    private TemplateCreatorNNNative templateCreatorNNNative = null;
    private TemplateCreatorNNInstance templateCreatorNNFaceLightInstance = null;

    TemplateCreatorPropFingerNative templateCreatorPropFingerNative;
    TemplateCreatorPropFingerInstance templateCreatorPropFingerInstance;


    private TemplateCreatorNNInstance templateCreatorNNFingerLightInstance = null;
    private AuthMatcherInstance authMatcherInstance = null;
    private AuthMatcherNative authMatcherNative = null;

    private FaceDigitalIdNative faceDigitalIdNative;
    private FaceDigitalIdInstance faceDigitalIdLightInstance;

    private FaceDetectorNative faceDetectorNative;
    private FaceDetectorInstance faceDetectorInstance;

    private T5CryptoClient t5CryptoClient;


    boolean isSDKsInitialized;

    public T5CryptoClient getT5CryptoClient() {
        return t5CryptoClient;
    }

    byte[] keyData = null;

    public Result getDecodedCryptographResult() {
        return decodedCryptographResult;
    }

    public void setDecodedCryptographResult(Result decodedCryptographResult) {
        this.decodedCryptographResult = decodedCryptographResult;
    }

    private Result decodedCryptographResult;


    public LiveData<Boolean> isSDKInitialized() {
        return isSDKInitialized;
    }

    public LiveData<Boolean> isSDkLoading() {

        return isLoadingSDK;
    }


    public LiveData<UiText> getStatus() {
        return status;
    }


    public void initSDKs(Context context) {


        new Thread(() -> {


            if (!isSDKsInitialized) {


                keyData = readFileFromAssets(context.getAssets(), "encryptionKey.txt");

                Log.d("TAG", "initSDKs keyData :" + readFileFromAssets(context.getAssets(), "encryptionKey.txt"));


                isLoadingSDK.postValue(true);
                isSDKInitialized.postValue(false);
                status.postValue(UiText.emptyString());


                isSDKsInitialized = loadLicense(context);

                Log.d(TAG, "isSDKInitilized 1 :" + isSDKsInitialized);

                if (isSDKsInitialized) {

                    isSDKsInitialized = initOmniMatchInstances();


                }


                Log.d(TAG, "isSDKInitilized 2 :" + isSDKsInitialized);

                if (isSDKsInitialized) {

                    int retCode = initCrypto(context);

                    isSDKsInitialized = retCode == Common.ResultCode.Success_VALUE;

                }

                Log.d(TAG, "isSDKInitilized 3 :" + isSDKsInitialized);


            } else {
                isSDKsInitialized = true;
            }


            isLoadingSDK.postValue(false);
            isSDKInitialized.postValue(isSDKsInitialized);


        }).start();

    }


    public void getPublicKeys(Context context) {

        AppSharedPreference appSharedPreference = new AppSharedPreference(context);

        Call<ResponseBody> call = ApiClient.getRetrofitInstance().create(IdencodeService.class).getPublicKeys(appSharedPreference.getCryptoApiBaseUrl() + AppSharedPreference.GET_PUBLIC_KEYS_URL);


        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, retrofit2.Response<ResponseBody> response) {

                if (response.isSuccessful()) {
                    Log.d("TAG", "response : " + response);

                    try {
                        String keys = response.body().string();

                        jwsKeys.postValue(keys);

                    } catch (Exception e) {
                        Log.d("TAG", "unable to get public keys : " + e.getLocalizedMessage());

                        jwsKeys.postValue(null);
                    }


                } else {
                    jwsKeys.postValue(null);
                }


            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                Log.e("TAG", "response : " + t.getLocalizedMessage());
                jwsKeys.postValue(null);
            }
        });


    }


    public byte[] readFileFromAssets(AssetManager assetManager, String fileName) {


        try (InputStream inputStream = assetManager.open(fileName); ByteArrayOutputStream output = new ByteArrayOutputStream()) {


            byte[] buffer = new byte[8192];
            int bytesRead;

            while ((bytesRead = inputStream.read(buffer)) != -1) {
                output.write(buffer, 0, bytesRead);
            }

            return output.toByteArray();


        } catch (Exception e) {
            return new byte[0];
        }

    }


    private int initCrypto(Context context) {
        int errcode = 0;
        if (t5CryptoClient == null) {
            t5CryptoClient = new T5CryptoClient(context);


            Log.d("TAG", "Cryptograph SDK version keyData :" + t5CryptoClient.getVersion());
            Log.d("TAG", "Cryptograph SDK version keyData :" + keyData);

            //base64 decoded encryption key to be passed here
            errcode = t5CryptoClient.create(keyData);
            if (errcode != 0) {

                t5CryptoClient = null;
            }
        }
        return errcode;
    }


    private boolean initOmniMatchInstances() {

        try {


            templateCreatorNNNative = new TemplateCreatorNNNative();

            TemplateCreatorNn.TemplateCreatorNnConfiguration templateCreatorNnFaceLightConfiguration = TemplateCreatorNn.TemplateCreatorNnConfiguration.newBuilder()
                    .setAlgorithm(BioCommon.Algorithm.newBuilder()
                            .setFace(FaceCommon.FaceAlgorithm.DetFace_100Light))
                    .setBase(Common.BaseConfiguration.newBuilder().setDebugMode(false).setThreadsNumber(1).build())
                    .setBatch(Common.BatchConfiguration.newBuilder().setBatchSize(1).build())
                    .setBioType(BioCommon.BioType.Face).build();

            templateCreatorNNFaceLightInstance = templateCreatorNNNative.CreateInstance(templateCreatorNnFaceLightConfiguration);



            TemplateCreatorNn.TemplateCreatorNnConfiguration templateCreatorNnFingerConfiguration = TemplateCreatorNn.TemplateCreatorNnConfiguration.newBuilder().
                    setAlgorithm(BioCommon.Algorithm.newBuilder().setFinger(FingerCommon.FingerAlgorithm.ChiFinger_100Light)).
                    setBase(Common.BaseConfiguration.newBuilder().setDebugMode(false).setThreadsNumber(1).build()).
                    setBatch(Common.BatchConfiguration.newBuilder().setBatchSize(1).build()).
                    setBioType(BioCommon.BioType.Finger).build();


            templateCreatorNNFingerLightInstance = templateCreatorNNNative.CreateInstance(templateCreatorNnFingerConfiguration);


            templateCreatorPropFingerNative = new TemplateCreatorPropFingerNative();
            Common.BaseConfiguration configuration = Common.BaseConfiguration.newBuilder().
                    setDebugMode(false).setThreadsNumber(0).build();
            templateCreatorPropFingerInstance = templateCreatorPropFingerNative.CreateInstance(configuration);


            authMatcherNative = new AuthMatcherNative();
            AuthMatcher.AuthMatcherConfiguration authMatcherConfiguration = AuthMatcher.AuthMatcherConfiguration.newBuilder()
                    .setAlgorithms(BioCommon.Algorithms.newBuilder()
                            .setFace(FaceCommon.FaceAlgorithm.DetFace_100Light)
                            .setFinger(FingerCommon.FingerAlgorithm.ChiFinger_100Light).build())
                    .setDebugMode(true).build();
            authMatcherInstance = authMatcherNative.CreateInstance(authMatcherConfiguration);


            faceDigitalIdNative = new FaceDigitalIdNative();

            FaceDigitalId.FaceDigitalIdConfiguration faceDigitalIdLightConfiguration = FaceDigitalId.FaceDigitalIdConfiguration.newBuilder()
                    .setAlgorithm(FaceCommon.FaceAlgorithm.DetFace_100Light).setBase(Common.BaseConfiguration.newBuilder().setDebugMode(false).setThreadsNumber(1).build())
                    .setBatch(Common.BatchConfiguration.newBuilder().setBatchSize(1).build()).setBackgroundRemoval(false).setConfidenceThreshold(0.2f).build();

            faceDigitalIdLightInstance = faceDigitalIdNative.CreateInstance(faceDigitalIdLightConfiguration);


            faceDetectorNative = new FaceDetectorNative();

            FaceDetector.FaceDetectorConfiguration faceDetectorConfigurationLight =
                    FaceDetector.FaceDetectorConfiguration.newBuilder().setAlgorithm(FaceCommon.FaceAlgorithm.DetFace_100Light).
                            setBase(Common.BaseConfiguration.newBuilder().setDebugMode(false).setThreadsNumber(1).build()).
                            setBatch(Common.BatchConfiguration.newBuilder().setBatchSize(1).build()).
                            setSingleFace(true).setConfidenceThreshold(0.2f).build();
            faceDetectorInstance = faceDetectorNative.CreateInstance(faceDetectorConfigurationLight);


            return true;

        } catch (OmniMatchException ome) {


            Log.d(TAG, "templateCreatorNNNative : " + templateCreatorNNNative + " templateCreatorNNFaceLightInstance : " + templateCreatorNNFaceLightInstance);
            Log.d(TAG, "authMatcherNative : " + authMatcherNative + " authMatcherInstance : " + authMatcherInstance);


            Log.d("TAG", "unable to create templateCreatorNNFaceLightInstance " + ome.getResultCode());
        }

        return false;

    }


    private boolean loadLicense(Context context) {


        int resultCode;
        try {


            CoreNative coreNative = new CoreNative();


            Log.d("TAG", "version : " + coreNative.GetVersion().getValue());


            Log.d("TAG", " context : " + context);


            AndroidNative<Context> androidNative = new AndroidNative<>();


            Log.d("TAG", "context.getApplicationContext() " + (context instanceof android.view.ContextThemeWrapper));


            resultCode = androidNative.SetLicense(context, "");
            Log.d("ATG", "set License result code " + resultCode);


            if (resultCode < 0) {


                boolean isInstanceOfContextThemeWrapper =  (context instanceof android.view.ContextThemeWrapper);


                String url = "https://pheonix-lic.tech5.tech/license/" + context.getApplicationContext().getPackageName() + "/" + Math.abs(resultCode);


                String token = sendHttpRequest(url);

                androidNative.SetLicense(context, token);
                resultCode = androidNative.SetLicense(context, token);


                Context ds = context.getApplicationContext();


                Log.d("TAG", "token from server" + token);


                Log.d("ATG", "set License result code2 " + resultCode);

            }


        } catch (OmniMatchException | IOException ome) {

            resultCode = -1;

        }


        return (resultCode == 0);

    }


    private String sendHttpRequest(String urlString) throws IOException {

        HttpURLConnection urlConnection;
        StringBuilder result = new StringBuilder();

        URL url = new URL(urlString);
        urlConnection = (HttpURLConnection) url.openConnection();

        int code = urlConnection.getResponseCode();

        if (code == 200) {

            try (InputStream in = new BufferedInputStream(urlConnection.getInputStream());

                 BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(in))) {


                String line = "";

                while ((line = bufferedReader.readLine()) != null) result.append(line);

                return result.toString();
            } catch (IOException e) {
                Log.e(TAG, "error  " + e.getLocalizedMessage());

            }

        }


        return result.toString();

    }


    @Override
    protected void onCleared() {
        super.onCleared();

        Log.d(TAG, "sharedview model onCleared " + this);

        try {
            if (templateCreatorNNNative != null) {
                templateCreatorNNNative.DeleteInstance(templateCreatorNNFaceLightInstance);
            }

            if (templateCreatorPropFingerNative != null) {
                templateCreatorPropFingerNative.DeleteInstance(templateCreatorPropFingerInstance);
            }

            if (authMatcherNative != null) {
                authMatcherNative.DeleteInstance(authMatcherInstance);
            }


            if (faceDigitalIdNative != null) {
                faceDigitalIdNative.DeleteInstance(faceDigitalIdLightInstance);
            }


            if (faceDetectorNative != null) {
                faceDetectorNative.DeleteInstance(faceDetectorInstance);
            }


            if (t5CryptoClient != null) {
                t5CryptoClient.cancel();
            }


        } catch (Exception e) {
            Log.d("TAG", "Unable to delete instances");
        }
    }


    public TemplateCreatorNNNative getTemplateCreatorNNNative() {
        return templateCreatorNNNative;
    }

    public TemplateCreatorNNInstance getTemplateCreatorNNFaceLightInstance() {
        return templateCreatorNNFaceLightInstance;
    }


    public TemplateCreatorNNInstance getTemplateCreatorNNFingerLightInstance() {
        return templateCreatorNNFingerLightInstance;
    }


    public TemplateCreatorPropFingerNative getTemplateCreatorPropFingerNative() {
        return templateCreatorPropFingerNative;
    }

    public TemplateCreatorPropFingerInstance getTemplateCreatorPropFingerInstance() {
        return templateCreatorPropFingerInstance;
    }

    public AuthMatcherInstance getAuthMatcherInstance() {
        return authMatcherInstance;
    }

    public AuthMatcherNative getAuthMatcherNative() {
        return authMatcherNative;
    }


    public FaceDigitalIdNative getFaceDigitalIdNative() {
        return faceDigitalIdNative;
    }

    public FaceDigitalIdInstance getFaceDigitalIdInstance() {
        return faceDigitalIdLightInstance;
    }

    public FaceDetectorInstance getFaceDetectorInstance() {
        return faceDetectorInstance;
    }

    public FaceDetectorNative getFaceDetectorNative() {
        return faceDetectorNative;
    }


    public MutableLiveData<String> getJwsKeys() {
        return jwsKeys;
    }
}
