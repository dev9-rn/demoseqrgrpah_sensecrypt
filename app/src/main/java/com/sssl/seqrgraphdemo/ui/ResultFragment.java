package com.sssl.seqrgraphdemo.ui;

import static android.view.ViewGroup.LayoutParams.MATCH_PARENT;

import static com.sssl.seqrgraphdemo.networks.liveness.ApiUrl.getCheckLivenessUrl;
import static com.sssl.seqrgraphdemo.utils.AppUtils.showMessage;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.core.content.ContextCompat;
import androidx.core.content.res.ResourcesCompat;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.fragment.NavHostFragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.phoenixcapture.camerakit.FaceBox;
import com.sssl.seqrgraphdemo.R;
import com.sssl.seqrgraphdemo.databinding.FragmentResultBinding;
import com.sssl.seqrgraphdemo.dialog.PinDialog;
import com.sssl.seqrgraphdemo.networks.liveness.GetDataService;
import com.sssl.seqrgraphdemo.networks.liveness.LivenessResponseModel;
import com.sssl.seqrgraphdemo.networks.liveness.ServiceGenerator;
import com.sssl.seqrgraphdemo.preferences.AppSharedPreference;
import com.sssl.seqrgraphdemo.utils.Constants;
import com.sssl.seqrgraphdemo.viewmodels.ResultViewModel;
import com.sssl.seqrgraphdemo.viewmodels.SharedViewModel;
import com.sssl.seqrgraphdemo.models.Result;

import Tech5.OmniMatch.MatcherCommon;
import ai.tech5.finger.utils.FingerCaptureResult;
import ai.tech5.finger.utils.SegmentationMode;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ResultFragment extends BaseFaceAndFingerCaptureFragment {

    private static final String TAG = ResultFragment.class.getSimpleName();
    FragmentResultBinding binding;

    private ResultViewModel resultViewModel;
    private AlertDialog verifyOptionsAlertDialog = null;
    private Result result;

    private SegmentationMode mode = null;
    boolean leftFingersFound = false;
    boolean rightFingersFound = false;
    boolean rightThumbFound = false;
    boolean leftThumbFound = false;

    private boolean leftFingersVerified = false;
    private boolean rightFingersVerified = false;
    private boolean rightThumbVerified = false;
    private boolean leftThumbVerified = false;

    private AppSharedPreference appSharedPreference;
    private String pin = null;
    private ImageView verifyWithFingerprint;
    private ImageView verifyWithFace;

    private byte[] capturedFaceBytes;
    private int failedCount = 0;

    private String fromWhere;


//    private CameraManager mCameraManager;
//    private String mCameraId;

    public ResultFragment() {
        // Required empty public constructor
    }


    public static ResultFragment newInstance(String param1, String param2) {
        ResultFragment fragment = new ResultFragment();

        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentResultBinding.inflate(inflater, container, false);

        SharedViewModel sharedViewModel = new ViewModelProvider(requireActivity()).get(SharedViewModel.class);
        resultViewModel = new ViewModelProvider(ResultFragment.this).get(ResultViewModel.class);

        resultViewModel.init(sharedViewModel.getTemplateCreatorNNNative(), sharedViewModel.getTemplateCreatorNNFaceLightInstance(), sharedViewModel.getTemplateCreatorPropFingerNative(), sharedViewModel.getTemplateCreatorPropFingerInstance(), sharedViewModel.getAuthMatcherNative(), sharedViewModel.getAuthMatcherInstance());
        result = sharedViewModel.getDecodedCryptographResult();

        if (result != null && result.getCompressedImage() != null && !result.getCompressedImage().isEncrypted()) {
            resultViewModel.decompressFaceImage(result.getCompressedImage().getContent());
        }

        appSharedPreference = new AppSharedPreference(requireContext());

        if (getArguments() != null) {
            fromWhere = getArguments().getString(Constants.FROM_WHERE);
        }


        return binding.getRoot();
    }


    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        binding.matchWithTemplate.setOnClickListener(v -> onVerifyClicked());

        binding.scanAgain.setOnClickListener(v -> onScanAgainClicked());

        binding.scUseBackCam.setOnClickListener(v ->{
            appSharedPreference.setUseBackCamera(binding.scUseBackCam.isChecked());
        });

        resultViewModel.getDecompressedFaceImage().observe(getViewLifecycleOwner(), bitmap -> {
            if (bitmap != null) {
                binding.face.setImageBitmap(bitmap);
            }
        });


        resultViewModel.getIsProcessing().observe(getViewLifecycleOwner(), aBoolean -> showProgressDialog(Boolean.TRUE.equals(aBoolean)));

        resultViewModel.getFaceMatchResult().observe(getViewLifecycleOwner(), this::onFaceMatched);

        resultViewModel.getFingerMatchResult().observe(getViewLifecycleOwner(), this::onFingersMatched);


        resultViewModel.getMatchingError().observe(getViewLifecycleOwner(), throwable -> {

            showDemographics(false);
            binding.matchResult.setVisibility(View.VISIBLE);

            binding.matchResult.setTextColor(ResourcesCompat.getColor(getResources(), R.color.light_red, null));

            binding.matchResult.setText(getResources().getString(R.string.verification_failed) + ": " + throwable.getLocalizedMessage());


        });


        prepareVerifyOptionsDialog();

        checkFingersInCrypto();

//        boolean isFlashAvailable = getActivity().getApplicationContext().getPackageManager()
//                .hasSystemFeature(PackageManager.FEATURE_CAMERA_FRONT);
//
//        if (!isFlashAvailable) {
//            Log.d("Tag" , "flash not available");
//        }
//
//        mCameraManager = (CameraManager) getActivity().getSystemService(Context.CAMERA_SERVICE);
//        try {
//            mCameraId = mCameraManager.getCameraIdList()[0];
//        } catch (CameraAccessException e) {
//            e.printStackTrace();
//        }

    }

//    public void switchFlashLight(boolean status) {
//        try {
//            mCameraManager.setTorchMode(mCameraId, status);
//        } catch (CameraAccessException e) {
//            e.printStackTrace();
//        }
//    }


    @Override
    public void onResume() {
        super.onResume();
        binding.scUseBackCam.setChecked(appSharedPreference.isUseBackCamera());
    }

    private void onFaceMatched(MatcherCommon.RecordResult recordResult) {

        if (recordResult != null && recordResult.getCandidate().getScores().getFace().getLogFAR() >= 4) {

            showDemographics(true);
        } else {

            showDemographics(false);
            binding.matchResult.setVisibility(View.VISIBLE);

            binding.matchResult.setTextColor(ResourcesCompat.getColor(getResources(), R.color.light_red, null));
            binding.matchResult.setText(getResources().getString(R.string.verification_failed));
        }
    }


    private void onFingersMatched(Float matchScore) {

        markVerifiedFingers();

        if (matchScore > Constants.FINGER_THRESHOLD) {

            failedCount = 0;
            showDemographics(true);
        } else {

            if (isAllFingersVerified()) {


                showDemographics(false);
                binding.matchResult.setVisibility(View.VISIBLE);
                binding.matchResult.setTextColor(ContextCompat.getColor(requireContext(), R.color.light_red));
                binding.matchResult.setText(getResources().getString(R.string.verification_failed) + "(score: " + matchScore + ")");

                if (failedCount < 4) {
                    failedCount++;
                } else {
                    failedCount = 0;
                    showAlert("Please try with face verification or scan cryptograph again.");
                }

            } else {
                startFingerCapture();
            }
        }
    }


    private void checkFingersInCrypto() {

        for (Integer finger : result.getFingerprints().keySet()) {

            if (finger <= 10 && finger > 6) {
                leftFingersFound = true;

            } else if (finger <= 5 && finger > 1) {
                rightFingersFound = true;

            } else if (finger == 1) {
                rightThumbFound = true;

            } else if (finger == 6) {
                leftThumbFound = true;
            }
        }
    }


    public void prepareVerifyOptionsDialog() {

        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(requireContext());
        LayoutInflater inflater = getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.verify_options_layout, null);
        verifyWithFingerprint = dialogView.findViewById(R.id.verify_with_fingerprint);
        verifyWithFace = dialogView.findViewById(R.id.verify_with_face);

        verifyWithFingerprint.setOnClickListener((View view) -> {
            Log.d(TAG, "verify_with_fingerprint() clicked");

            verifyOptionsAlertDialog.dismiss();
            verifyWithFingerprint.setEnabled(false);
            verifyWithFace.setEnabled(false);

            resetResultView();
            startFingerCapture();
        });

        verifyWithFace.setOnClickListener((View view) -> {

            Log.d(TAG, "verify_with_face() clicked");
            verifyOptionsAlertDialog.dismiss();
            verifyWithFingerprint.setEnabled(false);
            verifyWithFace.setEnabled(false);

            resetResultView();
            captureFace();
        });

        dialogBuilder.setView(dialogView);
        verifyOptionsAlertDialog = dialogBuilder.create();
        verifyOptionsAlertDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.WHITE));


    }


    private void onScanAgainClicked() {

        Log.d(TAG, "from where " + fromWhere);

        if (Constants.FROM_DECODE_SCREEN.equals(fromWhere)) {

//            NavHostFragment.findNavController(ResultFragment.this)
//                    .navigate(R.id.action_result_fragment_to_decode_fragment);
            NavHostFragment.findNavController(ResultFragment.this)
                    .navigate(R.id.action_resultFragment_to_scanCryptographFragment);

        } else if (Constants.FROM_SCAN_SCREEN.equals(fromWhere)) {


            NavHostFragment.findNavController(ResultFragment.this)
                    .navigate(R.id.action_resultFragment_to_scanCryptographFragment);
        }
    }


    private void onVerifyClicked() {

        pin = null;


        if ((null != result.getFaceTemplate() || null != result.getCompressedImage()) &&
                (null != result.getFingerprints() && result.getFingerprints().size() > 0)) {

            verifyOptions();
        } else if ((null != result.getFaceTemplate() || null != result.getCompressedImage())) {
            resetResultView();
            captureFace();

        } else if (null != result.getFingerprints() && result.getFingerprints().size() > 0) {
            resetResultView();
            startFingerCapture();
        } else {

            showAlert("Cryptograph does't contain face or fingerprint template !");
        }


    }

    public void verifyOptions() {
        if (verifyOptionsAlertDialog != null) {

            if (verifyOptionsAlertDialog.isShowing()) {
                verifyOptionsAlertDialog.dismiss();
            }

            verifyWithFingerprint.setEnabled(true);
            verifyWithFace.setEnabled(true);
            verifyOptionsAlertDialog.show();
        }
    }


    private void resetResultView() {
        showDemographics(false);
        binding.matchResult.setVisibility(View.GONE);
        binding.imgIndicator.setImageDrawable(ResourcesCompat.getDrawable(getResources(), R.drawable.ic_baseline_help_24, null));
    }


    @SuppressLint("SetTextI18n")
    private void showDemographics(boolean show) {
        binding.resultScrollView.removeAllViews();


        if (!show) {
            binding.name.setVisibility(View.GONE);
            binding.resultScrollView.setVisibility(View.GONE);
            binding.imgIndicator.setImageDrawable(ResourcesCompat.getDrawable(getResources(), R.drawable.ic_baseline_cancel_24, null));
            return;
        }
        binding.imgIndicator.setImageDrawable(ResourcesCompat.getDrawable(getResources(), R.drawable.ic_baseline_check_circle_24, null));

        if (null == result.getDemographics() || 0 == result.getDemographics().size()) {
            binding.resultScrollView.setVisibility(View.GONE);
            return;

        }

        if (null != result.getDemographics().get("Name")) {
            binding.name.setVisibility(View.VISIBLE);
            result.convertToMap(result.getDemographics().get("Name"));
            binding.name.setText(result.getFinalDemographics().get("Name"));
        } else {
            binding.name.setVisibility(View.GONE);
        }


        Log.d("TAG","Demographics : "+result.getDemographics());

        binding.resultScrollView.setVisibility(View.VISIBLE);
        binding.resultScrollView.addView(createDemographicsLayout());

    }


    private LinearLayout createDemographicsLayout() {

        LinearLayout outerLayout = new LinearLayout(requireContext());
        outerLayout.setOrientation(LinearLayout.VERTICAL);

        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(MATCH_PARENT, MATCH_PARENT);
        outerLayout.setLayoutParams(layoutParams);
        LayoutInflater inflater;

        Log.d("TAG","convertToMap getFinalDemographics : "+ result.getFinalDemographics());

        // for (String keyText : result.getDemographics().keySet()) {
        for (String keyText : result.getFinalDemographics().keySet()) {
            LinearLayout innerLayout = new LinearLayout(requireContext());

            innerLayout.setOrientation(LinearLayout.VERTICAL);
            innerLayout.setLayoutParams(layoutParams);
            inflater = (LayoutInflater) requireContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            View resultView = inflater.inflate(R.layout.result_template, null);

            TextView key = resultView.findViewById(R.id.key);
            TextView value = resultView.findViewById(R.id.value);

            key.setText(keyText);
            // value.setText(result.getDemographics().get(keyText));
            value.setText(result.getFinalDemographics().get(keyText));

            // Log.d("TAG","createDemographicsLayout get(keyText): "+result.getDemographics().get(keyText));
            // Log.d("TAG","createDemographicsLayout keyText : "+ keyText);

            Log.d("TAG","createDemographicsLayout get(keyText): "+ result.getFinalDemographics().get(keyText));
            Log.d("TAG","createDemographicsLayout keyText : "+ keyText);


            innerLayout.addView(resultView);
            outerLayout.addView(innerLayout);
        }

        return outerLayout;

    }

    private void startFingerCapture() {

        Log.d(TAG, "startFingerCapture() called");
        binding.matchResult.setVisibility(View.GONE);
        if (leftFingersFound && !leftFingersVerified) {
            mode = SegmentationMode.SEGMENTATION_MODE_LEFT_SLAP;
        } else if (rightFingersFound && !rightFingersVerified) {
            mode = SegmentationMode.SEGMENTATION_MODE_RIGHT_SLAP;
        } else if (leftThumbFound && !leftThumbVerified) {
            mode = SegmentationMode.SEGMENTATION_MODE_LEFT_THUMB;
        } else if (rightThumbFound && !rightThumbVerified) {
            mode = SegmentationMode.SEGMENTATION_MODE_RIGHT_THUMB;
        }

        captureFingers(mode);


    }


    private void markVerifiedFingers() {

        if (mode == SegmentationMode.SEGMENTATION_MODE_LEFT_SLAP) {
            leftFingersVerified = true;
        } else if (mode == SegmentationMode.SEGMENTATION_MODE_RIGHT_SLAP) {
            rightFingersVerified = true;
        } else if (mode == SegmentationMode.SEGMENTATION_MODE_LEFT_THUMB) {
            leftThumbVerified = true;
        } else if (mode == SegmentationMode.SEGMENTATION_MODE_RIGHT_THUMB) {
            rightThumbVerified = true;
        }
    }


    private boolean isAllFingersVerified() {

        if (leftFingersFound && !leftFingersVerified) {
            return false;
        } else if (rightFingersFound && !rightFingersVerified) {
            return false;
        } else if (leftThumbFound && !leftThumbVerified) {
            return false;
        } else return !rightThumbFound || rightThumbVerified;


    }


    private void captureFace() {

        if ((result.getFaceTemplate() != null && result.getFaceTemplate().isEncrypted()) || (result.getCompressedImage() != null && result.getCompressedImage().isEncrypted())) {
            showPinDialog(true);
        } else {
            startFaceCapture();
//            switchFlashLight(false);
        }

    }


    private void showPinDialog(boolean face) {
        PinDialog pinDialog = new PinDialog();
        pinDialog.showPinDialog(requireActivity(), (boolean isValidPin, String passcode) -> {

            if (isValidPin) {
                pin = passcode;
                if (face) {
                    startFaceCapture();
                } else {
                    startFingerCapture();
                }
            } else {
                Toast.makeText(requireContext(), getResources().getString(R.string.invalid_pin), Toast.LENGTH_LONG).show();
            }
        });
    }


    @Override
    public void onFaceCaptured(byte[] bytes, FaceBox faceBox) {


        Log.d("TAG", "face captured......");


        if (bytes != null && bytes.length > 0) {

            capturedFaceBytes = bytes;

            String livenessMessage = "";
            boolean isLivenessPassed = true;

            Log.d("Tag" , "liveness ===== " + faceBox.mLiveness +" " + appSharedPreference.isFaceLivenessEnabled());

//            if (appSharedPreference.isFaceLivenessEnabled() && faceBox.mLiveness < 0.5) {
//
//                livenessMessage = "Face liveness failed (" + faceBox.mLiveness + ")";
//                isLivenessPassed = false;
//
//            }

            if(appSharedPreference.isFaceLivenessEnabled()){
                checkLiveness(bytes);
            }else{
                matchFaces();
            }

        }
    }

    private void checkLiveness(byte[] imageData) {
        try {
            binding.loadingProgress.setVisibility(View.VISIBLE);

            /*Create handle for the RetrofitInstance interface*/
            // GetDataService service = RetrofitClientInstance.getRetrofitInstance().create(GetDataService.class);

            GetDataService service = ServiceGenerator.createService(GetDataService.class, "tech5", "WeAreTech5!@#$");


            RequestBody reqFile = RequestBody.create(MediaType.parse("application/x-www-form-urlencoded"), imageData);
            MultipartBody.Part filePart = MultipartBody.Part.createFormData("data", "data", reqFile);
            Call<LivenessResponseModel> call = service.checkLiveness(getCheckLivenessUrl(), filePart);

            call.enqueue(new Callback<LivenessResponseModel>() {
                @Override
                public void onResponse(Call<LivenessResponseModel> call, Response<LivenessResponseModel> response) {

                    binding.loadingProgress.setVisibility(View.GONE);

                    if (response != null && response.body() != null) {
                        try {

                            Log.d("TAG", "response " + response.body());

                            LivenessResponseModel livenessResponseModel = response.body();

                            Log.d("TAG", "livenessResponseModel " + livenessResponseModel);
                            if (livenessResponseModel != null) {
                                if ((livenessResponseModel.getProbability() * 100) > 50) {
                                    matchFaces();
                                }
                                else{
                                    isInvalid();
                                }
//                              Toast.makeText(requireContext(), livenessMessage, Toast.LENGTH_LONG).show();
                            } else {
                                isInvalid();
                                showMessage(requireContext(), getString(R.string.response_error));
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                            isInvalid();

                            showMessage(requireContext(), getString(R.string.response_error));
                        }

                    } else {
                        isInvalid();
                        showMessage(requireContext(), getString(R.string.response_error));
                    }
                }

                @Override
                public void onFailure(Call<LivenessResponseModel> call, Throwable t) {
                    binding.loadingProgress.setVisibility(View.GONE);
                    isInvalid();
                    Log.d("TAG", "error " + t.getLocalizedMessage());
                    showMessage(requireContext(), getString(R.string.network_activity_error));
                }
            });
        } catch (Exception e) {
            binding.loadingProgress.setVisibility(View.GONE);
            isInvalid();
            Log.i("ERROR ", e.getMessage());
        }
    }

    private void isInvalid(){
        showDemographics(false);
        binding.matchResult.setVisibility(View.VISIBLE);
        binding.matchResult.setTextColor(ResourcesCompat.getColor(getResources(), R.color.light_red, null));
        binding.matchResult.setText(getResources().getString(R.string.verification_failed_face_liveness));
    }

    private void matchFaces() {
        binding.matchResult.setText("");
        if (null == capturedFaceBytes) {
            Toast.makeText(requireContext(), "Please capture face image from Camera", Toast.LENGTH_SHORT).show();
            return;
        }
        resultViewModel.matchFace(result, pin, capturedFaceBytes);
    }


    @Override
    public void onSuccess(FingerCaptureResult fingerCaptureResult) {


        if (fingerCaptureResult != null && !fingerCaptureResult.fingers.isEmpty()) {

            resultViewModel.matchFingers(fingerCaptureResult.fingers, pin, result, 4.0f);

        }

    }

    private void showAlert(String message) {

        AlertDialog alertDialog = new AlertDialog.Builder(requireContext()).create();
        alertDialog.setTitle("Tech5 IDencode");
        alertDialog.setMessage(message);
        alertDialog.setButton(DialogInterface.BUTTON_NEUTRAL, "OK", (DialogInterface dialog, int which) -> dialog.dismiss());
        alertDialog.show();

    }
}