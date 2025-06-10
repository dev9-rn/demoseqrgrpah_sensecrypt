package com.sssl.seqrgraphdemo.ui;

import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.camera.core.ExperimentalGetImage;
import androidx.camera.view.TransformExperimental;
import androidx.core.content.res.ResourcesCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.fragment.NavHostFragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.sssl.seqrgraphdemo.R;
import com.sssl.seqrgraphdemo.camera.CameraException;
import com.sssl.seqrgraphdemo.databinding.CameraxLayoutBinding;
import com.sssl.seqrgraphdemo.dialog.GifProgressDialog;
import com.sssl.seqrgraphdemo.preferences.AppSharedPreference;
import com.sssl.seqrgraphdemo.utils.Constants;
import com.sssl.seqrgraphdemo.utils.CryptographResultCode;
import com.sssl.seqrgraphdemo.viewmodels.CameraViewModel;
import com.sssl.seqrgraphdemo.viewmodels.SharedViewModel;

import ai.tech5.sdk.abis.cryptograph.ResultCode;

public class ScanCryptographFragment extends Fragment {

    CameraxLayoutBinding binding;
    AppSharedPreference appSharedPreference;

    boolean isAutoCapture;

    private SharedViewModel sharedViewModel;
    private CameraViewModel cameraViewModel;

    Bitmap capturedImage;
    private GifProgressDialog gifProgressDialog;
    public ScanCryptographFragment() {
        // Required empty public constructor
    }


    public static ScanCryptographFragment newInstance(String param1, String param2) {
        ScanCryptographFragment fragment = new ScanCryptographFragment();

        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    @TransformExperimental
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        binding = CameraxLayoutBinding.inflate(inflater, container, false);

        sharedViewModel = new ViewModelProvider(requireActivity()).get(SharedViewModel.class);
        cameraViewModel = new ViewModelProvider(ScanCryptographFragment.this).get(CameraViewModel.class);

        cameraViewModel.init(sharedViewModel.getT5CryptoClient());
        appSharedPreference = new AppSharedPreference(requireContext());


        binding.photoControlsLayout.setVisibility(View.GONE);

        isAutoCapture = new AppSharedPreference(requireContext()).isAutoCapture();

        binding.ivCaptureImage.setVisibility(isAutoCapture ? View.GONE : View.VISIBLE);

        return binding.getRoot();
    }

    @Override
    @ExperimentalGetImage
    @TransformExperimental
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {

        binding.capturedHand.setText("Searching for Cryptograph...");
        gifProgressDialog = new GifProgressDialog(requireActivity());

        if(appSharedPreference.getLoginUserType().equals(appSharedPreference.TYPE_VERIFIER)){
            binding.close.setOnClickListener(v -> NavHostFragment.findNavController(ScanCryptographFragment.this)
                    .navigate(R.id.action_scanCryptographFragment_to_verifierHomeFragment));
        }else {

            binding.close.setOnClickListener(v -> NavHostFragment.findNavController(ScanCryptographFragment.this)
                    .navigate(R.id.action_scanCryptographFragment_to_instituteHomeFragment));
        }


        binding.viewFinder.post(() -> {
            Log.e("TAG", "binding.viewFinder : ");
            try {
                cameraViewModel.startCapture(getViewLifecycleOwner(), binding.viewFinder, appSharedPreference.isAutoCapture());
            } catch (CameraException e) {

                Log.e("TAG", "unable to start camera : " + e.getLocalizedMessage());
            }

        });


        binding.ivCaptureImage.setOnClickListener(v -> {

            Log.e("TAG", "ivCaptureImage : ");
            capturedImage = null;
            cameraViewModel.takePicture();

        });

        binding.flashOnOff.setOnClickListener(v -> cameraViewModel.toggleFlash());

        binding.btnRetake.setOnClickListener(v -> {

            binding.photoControlsLayout.setVisibility(View.GONE);
            binding.cameraControlsLayout.setVisibility(View.VISIBLE);
            capturedImage = null;
            try {
                cameraViewModel.startCapture(getViewLifecycleOwner(), binding.viewFinder, appSharedPreference.isAutoCapture());
            } catch (CameraException e) {
                Log.e("TAG", "unable to start camera : " + e.getLocalizedMessage());
            }

        });

        binding.btnDecode.setOnClickListener(v -> {
            Log.e("TAG", "binding.btnDecode.setOnClickListener kunal: " + capturedImage);

            if (capturedImage != null) {

                Log.e("TAG", "binding.btnDecode.setOnClickListener kunal: " + capturedImage);

                cameraViewModel.decodeCryptographImage(capturedImage);
            }

        });


        cameraViewModel.getDetectionResult().observe(getViewLifecycleOwner(), result -> {

            if (result != null && result.getResultCode() == ResultCode.successful) {
                cameraViewModel.stopCamera();


                sharedViewModel.setDecodedCryptographResult(result);

                Bundle bundle = new Bundle();
                bundle.putString(Constants.FROM_WHERE, Constants.FROM_SCAN_SCREEN);

                NavHostFragment.findNavController(ScanCryptographFragment.this)
                        .navigate(R.id.action_scanCryptographFragment_to_resultFragment, bundle);
            } else {

                CryptographResultCode cryptographResultCode = new CryptographResultCode(result.getResultCode());
                showAlertDialog(cryptographResultCode.getErrorDescription().asString(requireContext()));
            }

        });


        cameraViewModel.getErrorMessage().observe(getViewLifecycleOwner(), s -> {

            cameraViewModel.stopCamera();
            showAlertDialog(getString(R.string.temp_unavalible));

        });


        cameraViewModel.getFlashState().observe(getViewLifecycleOwner(), state -> {

            if (state == 0) {
                binding.flashOnOff.setBackground(ResourcesCompat.getDrawable(getResources(), R.drawable.ic_flash_off_black_24dp, null));
            } else if (state == 1) {
                binding.flashOnOff.setBackground(ResourcesCompat.getDrawable(getResources(), R.drawable.ic_flash_on_black_24dp, null));
            }

        });


        cameraViewModel.getCapturedBitmapLiveData().observe(getViewLifecycleOwner(), bitmap -> {

            if (bitmap != null) {

                capturedImage = bitmap;

                binding.cameraControlsLayout.setVisibility(View.GONE);
                binding.photoControlsLayout.setVisibility(View.VISIBLE);

                cameraViewModel.stopCamera();
            }

        });


        cameraViewModel.getIsProcessing().observe(getViewLifecycleOwner(), aBoolean -> showProgressDialog(Boolean.TRUE.equals(aBoolean)));
    }


    private void showAlertDialog(String message) {

        androidx.appcompat.app.AlertDialog.Builder builder = new androidx.appcompat.app.AlertDialog.Builder(requireContext());
        builder.setTitle(getResources().getString(R.string.app_name));
        builder.setMessage(message);
        builder.setCancelable(true);
        builder.setNeutralButton("Ok", (DialogInterface dialog, int which) -> dialog.dismiss());
        builder.show();

    }


    public void showProgressDialog(boolean isSShow) {

        Log.d("TAG", "show progress dialog here " + isSShow);
        if (isSShow) {
            gifProgressDialog.showDialog(R.raw.load, null);
        } else {
            gifProgressDialog.hideDialog();
        }
    }


    @Override
    public void onDestroyView() {
        super.onDestroyView();

        showProgressDialog(false);
    }

}