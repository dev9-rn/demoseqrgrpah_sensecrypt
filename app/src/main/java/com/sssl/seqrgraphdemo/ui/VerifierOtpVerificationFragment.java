package com.sssl.seqrgraphdemo.ui;

import static com.sssl.seqrgraphdemo.utils.AppUtils.showMessage;

import android.graphics.Color;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.fragment.NavHostFragment;

import android.os.CountDownTimer;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

import com.sssl.seqrgraphdemo.R;
import com.sssl.seqrgraphdemo.databinding.FragmentVerifierOtpVerificationBinding;
import com.sssl.seqrgraphdemo.models.verifyOtp.ResenOtpResponse;
import com.sssl.seqrgraphdemo.models.verifyOtp.ResendOtpRequest;
import com.sssl.seqrgraphdemo.models.verifyOtp.VerifyOtpRequest;
import com.sssl.seqrgraphdemo.models.verifyOtp.VerifyOtpResponse;
import com.sssl.seqrgraphdemo.networks.ApiSeQRClient;
import com.sssl.seqrgraphdemo.preferences.AppSharedPreference;
import com.sssl.seqrgraphdemo.utils.AppUtils;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class VerifierOtpVerificationFragment extends Fragment {

    FragmentVerifierOtpVerificationBinding binding;
    AppSharedPreference appSharedPreference;
    NavController navController;
    String phoneNumber;
    int OTP;
    String verifyBy;
    int id;

    public VerifierOtpVerificationFragment() {
        // Required empty public constructor
    }

    public static VerifierOtpVerificationFragment newInstance(String param1, String param2) {
        VerifierOtpVerificationFragment fragment = new VerifierOtpVerificationFragment();

        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        id = getArguments().getInt("id");
        OTP = getArguments().getInt("otp");
        phoneNumber = getArguments().getString("phoneNumber");
        verifyBy = getArguments().getString("verifyBy");

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        binding = FragmentVerifierOtpVerificationBinding.inflate(inflater , container , false);

        return binding.getRoot();
    }

    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        navController = NavHostFragment.findNavController(VerifierOtpVerificationFragment.this);

        appSharedPreference = new AppSharedPreference(requireContext());

        resetTimer();

        disableVerifierButton();

        binding.otpET1.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                binding.otpET2.requestFocus();
                validateInput();
            }
        });
        binding.otpET2.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                binding.otpET3.requestFocus();
                validateInput();
            }
        });
        binding.otpET3.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                binding.otpET4.requestFocus();
                validateInput();
            }
        });
        binding.otpET4.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                binding.otpET5.requestFocus();
                validateInput();
            }
        });
        binding.otpET5.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                validateInput();
            }
        });

        binding.verifyOtp.setOnClickListener(v ->{
            binding.loadingProgress.setVisibility(View.VISIBLE);
            verifyUser();
        });

        binding.resendOtp.setOnClickListener(v->{
            binding.loadingProgress.setVisibility(View.GONE);
            resendOtp();
        });

        Log.d("Tag" , "OTP verification Data " + id + " " + OTP + " " + phoneNumber + " " + verifyBy);
    }

    private void verifyUser(){
        String OTP = binding.otpET1.getText().toString()+binding.otpET2.getText().toString()+binding.otpET3.getText().toString()+binding.otpET4.getText().toString()+binding.otpET5.getText().toString();

        VerifyOtpRequest verifyOtpRequest = new VerifyOtpRequest(phoneNumber , OTP);
        Map<String, String> headers = AppUtils.getLoginApiHeader(requireContext());

        ApiSeQRClient.getInstance().seqrCodeService.verifyOtp(headers , verifyOtpRequest).enqueue(new Callback<VerifyOtpResponse>() {
            @Override
            public void onResponse(Call<VerifyOtpResponse> call, Response<VerifyOtpResponse> response) {
                if (response.isSuccessful()) {
                    // Handle the successful response here
                    VerifyOtpResponse apiResponse = response.body();
                    Log.d("Tag" , "Header access token" + response.headers().get("accesstoken"));
                    if (apiResponse != null && apiResponse.getData() != null) {

                        VerifyOtpResponse.ResponseData responseData = apiResponse.getData();

                        binding.loadingProgress.setVisibility(View.GONE);

                        appSharedPreference.setAccessToken(response.headers().get("accesstoken"));
                        appSharedPreference.setUserName(responseData.username);
                        appSharedPreference.setUserId(responseData.id);
                        appSharedPreference.setLoginUserType(appSharedPreference.TYPE_VERIFIER);
                        appSharedPreference.setApiKey();

                        navController.navigate(R.id.action_verifierOtpVerificationFragment_to_verifierHomeFragment);

                        Log.d("Tag" , "Api Success " + responseData.id + " " + responseData.verify_by + " " + responseData.username + " " + responseData.access_token);
                        showMessage(requireContext(), apiResponse.message);

                    }else {
                        binding.loadingProgress.setVisibility(View.GONE);
                        showMessage(requireContext(), apiResponse.message);
                        Log.d("Tag" , "Api Response else " + response);
                    }
                } else {
                    binding.loadingProgress.setVisibility(View.GONE);
                    showMessage(requireContext(), getString(R.string.form_error));
                    Log.d("Tag" , "Api Response fail " + response);
                }
            }

            @Override
            public void onFailure(Call<VerifyOtpResponse> call, Throwable t) {
                binding.loadingProgress.setVisibility(View.GONE);
                showMessage(requireContext(), getString(R.string.form_error));
                Log.d("Tag" , "Api Response failure " + t.getLocalizedMessage());
            }
        });

    }

    private void resendOtp(){
        ResendOtpRequest resendOtpRequest = new ResendOtpRequest(phoneNumber);
        Map<String, String> headers = AppUtils.getLoginApiHeader(requireContext());

        ApiSeQRClient.getInstance().seqrCodeService.resendOtp(headers , resendOtpRequest).enqueue(new Callback<ResenOtpResponse>() {
            @Override
            public void onResponse(Call<ResenOtpResponse> call, Response<ResenOtpResponse> response) {
                if (response.isSuccessful()) {
                    // Handle the successful response here
                    ResenOtpResponse apiResponse = response.body();

                    if (apiResponse.success) {

                        binding.loadingProgress.setVisibility(View.GONE);

                        resetTimer();

                        Log.d("Tag" , "Api Success " + apiResponse);
                        showMessage(requireContext(), apiResponse.message);

                    }else {
                        binding.loadingProgress.setVisibility(View.GONE);
                        showMessage(requireContext(), apiResponse.message);
                        Log.d("Tag" , "Api Response else " + response);
                    }
                } else {
                    binding.loadingProgress.setVisibility(View.GONE);
                    showMessage(requireContext(), getString(R.string.form_error));
                    Log.d("Tag" , "Api Response fail " + response);
                }
            }

            @Override
            public void onFailure(Call<ResenOtpResponse> call, Throwable t) {
                binding.loadingProgress.setVisibility(View.GONE);
                showMessage(requireContext(), getString(R.string.form_error));
                Log.d("Tag" , "Api Response failure " + t.getLocalizedMessage());
            }
        });
    }

    private void validateInput(){
        boolean isValid = true;
        if(isEmpty(binding.otpET1)){
            isValid = false;
        }
        if(isEmpty(binding.otpET2)){
            isValid = false;
        }
        if(isEmpty(binding.otpET3)){
            isValid = false;
        }
        if(isEmpty(binding.otpET4)){
            isValid = false;
        }
        if(isEmpty(binding.otpET5)){
            isValid = false;
        }
       if(isValid){
           enabledVerifierButton();
       }else{
           disableVerifierButton();
       }
    }

    private void enabledVerifierButton(){
        binding.verifyOtp.setBackgroundColor(Color.parseColor("#00BFFF"));
        binding.verifyOtp.setEnabled(true);
    }

    private void disableVerifierButton(){
        binding.verifyOtp.setBackgroundColor(Color.parseColor("#D3D3D3"));
        binding.verifyOtp.setEnabled(false);
    }

    private void resetTimer(){
        binding.resendOtp.setBackgroundColor(Color.parseColor("#D3D3D3"));
        binding.resendOtp.setEnabled(false);
        new CountDownTimer(60000, 1000) {
            public void onTick(long millisUntilFinished) {
                // Used for formatting digit to be in 2 digits only
                NumberFormat f = new DecimalFormat("00");
                long min = (millisUntilFinished / 60000) % 60;
                long sec = (millisUntilFinished / 1000) % 60;
                binding.resendOtp.setText("Resend OTP : "+ f.format(min) + ":" + f.format(sec));
            }
            // When the task is over it will print 00:00:00 there
            public void onFinish() {
                binding.resendOtp.setEnabled(true);
                binding.resendOtp.setBackgroundColor(Color.parseColor("#00BFFF"));
                binding.resendOtp.setText("Resend OTP");
            }
        }.start();
    }

    private boolean isEmpty(EditText editText) {
        return editText.getText().toString().trim().isEmpty();
    }
}