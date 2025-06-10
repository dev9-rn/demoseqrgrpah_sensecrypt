package com.sssl.seqrgraphdemo.ui;

import static com.sssl.seqrgraphdemo.utils.AppUtils.showMessage;

import android.app.Dialog;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.fragment.NavHostFragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;

import com.sssl.seqrgraphdemo.R;
import com.sssl.seqrgraphdemo.databinding.FragmentVerifierLoginBinding;
import com.sssl.seqrgraphdemo.models.login.ForgotPasswordRequest;
import com.sssl.seqrgraphdemo.models.login.ForgotPasswordResponse;
import com.sssl.seqrgraphdemo.models.login.LoginRequest;
import com.sssl.seqrgraphdemo.models.login.LoginResponse;
import com.sssl.seqrgraphdemo.networks.ApiSeQRClient;
import com.sssl.seqrgraphdemo.preferences.AppSharedPreference;
import com.sssl.seqrgraphdemo.utils.AppUtils;

import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class VerifierLoginFragment extends Fragment {

    FragmentVerifierLoginBinding binding;

    AppSharedPreference appSharedPreference;
    NavController navController;

    Dialog dialog;
    public VerifierLoginFragment() {
        // Required empty public constructor
    }


    public static VerifierLoginFragment newInstance(String param1, String param2) {
        VerifierLoginFragment fragment = new VerifierLoginFragment();

        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        binding = FragmentVerifierLoginBinding.inflate(inflater, container, false);


        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        appSharedPreference = new AppSharedPreference(requireContext());

        navController = NavHostFragment.findNavController(VerifierLoginFragment.this);

        binding.verifierLogin.setOnClickListener(v -> {

            binding.loadingProgress.setVisibility(View.VISIBLE);

            if(isValidData()){
                String username = String.valueOf(binding.userName.getText());
                String password = String.valueOf(binding.password.getText());

                loginAsVerifier(username , password);

            }else{
                binding.loadingProgress.setVisibility(View.GONE);
                showMessage(requireContext(), getString(R.string.fill_data));
            }
        });

        binding.signUp.setOnClickListener(v->{
            navController.navigate(R.id.action_verifierLoginFragment_to_verifierSignUpFragment);
        });

        binding.resetPassword.setOnClickListener(v->{
            showDialog();
        });

    }

    private void loginAsVerifier(String username , String password){
        LoginRequest verifierloginRequest = new LoginRequest(username , password);
        Map<String, String> headers = AppUtils.getLoginApiHeader(requireContext());

        ApiSeQRClient.getInstance().seqrCodeService.verifierLogin(headers , verifierloginRequest).enqueue(new Callback<LoginResponse>() {
            @Override
            public void onResponse(Call<LoginResponse> call, Response<LoginResponse> response) {
                if (response.isSuccessful()) {
                    // Handle the successful response here
                    LoginResponse apiResponse = response.body();

                    if (apiResponse != null && apiResponse.getData() != null) {

                        LoginResponse.ResponseData responseData = apiResponse.getData();

                        appSharedPreference.setAccessToken(responseData.access_token);
                        appSharedPreference.setUserName(responseData.username);
                        appSharedPreference.setUserId(responseData.id);
                        appSharedPreference.setLoginUserType(appSharedPreference.TYPE_VERIFIER);
                        appSharedPreference.setApiKey();

                        binding.loadingProgress.setVisibility(View.GONE);
                        Log.d("Tag" , "Api Success " + responseData.access_token + responseData.username + responseData.id);
                        navController.navigate(R.id.action_verifierLoginFragment_to_verifierHomeFragment);

                    }else {
                        binding.loadingProgress.setVisibility(View.GONE);
                        showMessage(requireContext(), getString(R.string.fill_data));
                        Log.d("Tag" , "Api Response else " + response);
                    }
                } else {
                    binding.loadingProgress.setVisibility(View.GONE);
                    Log.d("Tag" , "Api Response fail " + response);
                }
            }

            @Override
            public void onFailure(Call<LoginResponse> call, Throwable t) {
                binding.loadingProgress.setVisibility(View.GONE);
                Log.d("Tag" , "Api Response failure " + t.getLocalizedMessage());
            }
        });
    }

    public void showDialog(){


        dialog = new Dialog(requireContext());
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setCancelable(true);


        dialog.setContentView(R.layout.forgot_password_dialog);

        final EditText emailid = dialog.findViewById(R.id.emailid);

        Button forgotPassword = dialog.findViewById(R.id.forgot_password);

        forgotPassword.setOnClickListener(v->{
            dialog.findViewById(R.id.loading_progress).setVisibility(View.VISIBLE);

            if (isEmpty(emailid)) {
                emailid.setError("Email ID should not be empty");
                showMessage(requireContext(), getString(R.string.fill_data));
                dialog.findViewById(R.id.loading_progress).setVisibility(View.GONE);
            }else{
                String email_id= emailid.getText().toString();
                verifierForgotPassword(email_id);
            }

        });

        dialog.show();
    }

    private void verifierForgotPassword(String emailid) {
        ForgotPasswordRequest forgotPasswordRequest = new ForgotPasswordRequest("forgotPassword" , emailid , 1);
        Map<String, String> headers = AppUtils.getLoginApiHeader(requireContext());

        ApiSeQRClient.getInstance().seqrCodeService.forgotVerifierPassword(headers , forgotPasswordRequest).enqueue(new Callback<ForgotPasswordResponse>() {
            @Override
            public void onResponse(Call<ForgotPasswordResponse> call, Response<ForgotPasswordResponse> response) {
                if (response.isSuccessful()) {
                    // Handle the successful response here
                    ForgotPasswordResponse apiResponse = response.body();

                    Log.d("Tag" , "Success "+ apiResponse.success);
                    Log.d("Tag" , "status "+ apiResponse.status);
                    Log.d("Tag" , "message "+ apiResponse.message);

                    dialog.findViewById(R.id.loading_progress).setVisibility(View.GONE);
                    dialog.hide();
                    Log.d("Tag" , "Api Success " + apiResponse.status + apiResponse.message);

                } else {
                    dialog.findViewById(R.id.loading_progress).setVisibility(View.GONE);
                    dialog.hide();
                    showMessage(requireContext(), getString(R.string.form_error));
                    Log.d("Tag" , "Api Response fail " + response);
                }
            }

            @Override
            public void onFailure(Call<ForgotPasswordResponse> call, Throwable t) {
                dialog.findViewById(R.id.loading_progress).setVisibility(View.GONE);
                showMessage(requireContext(), getString(R.string.form_error));
                Log.d("Tag" , "Api Response failuare " + t.getLocalizedMessage());
                dialog.hide();
            }
        });
    }


    private boolean isValidData() {

        boolean isValid = true;
        if (isEmpty(binding.userName)) {
            isValid = false;
            binding.userName.setError("Name should not be empty");
        }

        if (isEmpty(binding.password)) {
            isValid = false;
            binding.password.setError("Password should not be empty");
        }

        return isValid;
    }

    private boolean isEmpty(EditText editText) {
        return editText.getText().toString().trim().isEmpty();
    }

}