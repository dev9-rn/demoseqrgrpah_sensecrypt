package com.sssl.seqrgraphdemo.ui;

import static com.sssl.seqrgraphdemo.utils.AppUtils.showMessage;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.fragment.NavHostFragment;

import android.util.Log;
import android.util.Patterns;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;

import com.sssl.seqrgraphdemo.R;
import com.sssl.seqrgraphdemo.databinding.FragmentVerifierSignUpBinding;
import com.sssl.seqrgraphdemo.models.verifierRegister.VerifierRegisterResponse;
import com.sssl.seqrgraphdemo.models.verifierRegister.VerifierSignUpRequest;
import com.sssl.seqrgraphdemo.networks.ApiSeQRClient;
import com.sssl.seqrgraphdemo.preferences.AppSharedPreference;
import com.sssl.seqrgraphdemo.utils.AppUtils;

import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class VerifierSignUpFragment extends Fragment {

    FragmentVerifierSignUpBinding binding;

    NavController navController;

    AppSharedPreference appSharedPreference;

    private RadioGroup radioGroup;

    public int verificationType = 1;

    public VerifierSignUpFragment() {
        // Required empty public constructor
    }

    public static VerifierSignUpFragment newInstance(String param1, String param2) {
        VerifierSignUpFragment fragment = new VerifierSignUpFragment();

        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
       binding = FragmentVerifierSignUpBinding.inflate(inflater , container , false);

       return binding.getRoot();
    }

    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        navController = NavHostFragment.findNavController(VerifierSignUpFragment.this);

        appSharedPreference = new AppSharedPreference(requireContext());

        // on below line we are initializing our variables.
        radioGroup = view.findViewById(R.id.verification_toggle);

        radioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup radioGroup, int checkedId) {

                RadioButton rb=(RadioButton)view.findViewById(checkedId);
                if(rb.getText().toString().equals("OTP")){
                    verificationType = 1;
                }else{
                    verificationType = 2;
                }
                Log.d("Tag" , "radio " + rb.getText());
            }
        });

        binding.verifierSignup.setOnClickListener(v ->{
            if(isValidData()){
                binding.loadingProgress.setVisibility(View.VISIBLE);
                registerAsVerifier();
            }else{
                showMessage(requireContext(), getString(R.string.fill_data));
            }
        });

    }

    private void registerAsVerifier(){
        String user_name = binding.userName.getText().toString();
        String full_name = binding.name.getText().toString();
        String pass = binding.password.getText().toString();
        String email = binding.email.getText().toString();
        String phone = binding.phoneNumber.getText().toString();
        int verify_by = verificationType;

        VerifierSignUpRequest verifierSignUpRequest = new VerifierSignUpRequest(user_name,full_name,pass,email,phone,verify_by);
        Map<String, String> headers = AppUtils.getLoginApiHeader(requireContext());

        ApiSeQRClient.getInstance().seqrCodeService.registerVerifier(headers , verifierSignUpRequest).enqueue(new Callback<VerifierRegisterResponse>() {
            @Override
            public void onResponse(Call<VerifierRegisterResponse> call, Response<VerifierRegisterResponse> response) {
                if (response.isSuccessful()) {
                    // Handle the successful response here
                    VerifierRegisterResponse apiResponse = response.body();

                    if (apiResponse != null && apiResponse.getData() != null) {

                        VerifierRegisterResponse.ResponseData responseData = apiResponse.getData();

                        binding.loadingProgress.setVisibility(View.GONE);

                        if(responseData.verify_by.equals("1")){
                            Log.d("Tag" , "========= if " + responseData.verify_by);
                            Bundle args = new Bundle();
                            args.putInt("id", responseData.id);
                            args.putInt("otp", responseData.OTP);
                            args.putString("verifyBy", responseData.verify_by);
                            args.putString("phoneNumber", responseData.mobile_no);
//
                            navController.navigate(R.id.action_verifierSignUpFragment_to_verifierOtpVerificationFragment,args);
                        }else{
                            Log.d("Tag" , "========= else " + responseData.verify_by);
                            navController.navigate(R.id.action_verifierSignUpFragment_to_verifierLoginFragment);
                        }

                        Log.d("Tag" , "Api Success " + responseData.email_id + " " + responseData.id + " " + responseData.OTP + " " + responseData.verify_by + " ");
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
            public void onFailure(Call<VerifierRegisterResponse> call, Throwable t) {
                binding.loadingProgress.setVisibility(View.GONE);
                showMessage(requireContext(), getString(R.string.form_error));
                Log.d("Tag" , "Api Response failure " + t.getLocalizedMessage());
            }
        });
    }

    private boolean isValidData() {

        boolean isValid = true;

        if (isEmpty(binding.name)) {
            isValid = false;
            binding.name.setError("Name should not be empty");
        }

        if (isEmpty(binding.userName)) {
            isValid = false;
            binding.userName.setError("UserName should not be empty");
        }

        if (isEmpty(binding.password)) {
            isValid = false;
            binding.password.setError("Password should not be empty");
        }

        String confPassword = binding.confPassword.getText().toString();
        if (isEmpty(binding.password) && !confPassword.equals(binding.password.toString())) {
            isValid = false;
            binding.confPassword.setError("Password mismatch");
        }

        String emailToText = binding.email.getText().toString();
        if(emailToText.isEmpty() && !Patterns.EMAIL_ADDRESS.matcher(emailToText).matches()){
            isValid = false;
            binding.email.setError("Email address is invalid");
        }

        String phoneNumber = binding.phoneNumber.getText().toString();
        if(phoneNumber.isEmpty() && !Patterns.PHONE.matcher(phoneNumber).matches()){
            isValid = false;
            binding.phoneNumber.setError("Phone number is invalid");
        }

        return isValid;
    }

    private boolean isEmpty(EditText editText) {
        return editText.getText().toString().trim().isEmpty();
    }
}