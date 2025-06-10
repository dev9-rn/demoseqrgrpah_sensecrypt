package com.sssl.seqrgraphdemo.ui;

import static com.sssl.seqrgraphdemo.utils.AppUtils.showMessage;

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
import android.widget.EditText;

import com.sssl.seqrgraphdemo.R;
import com.sssl.seqrgraphdemo.databinding.FragmentInstituteLoginBinding;
import com.sssl.seqrgraphdemo.models.login.InstituteLoginRequest;

import com.sssl.seqrgraphdemo.models.login.LoginResponse;
import com.sssl.seqrgraphdemo.networks.ApiSeQRClient;
import com.sssl.seqrgraphdemo.preferences.AppSharedPreference;
import com.sssl.seqrgraphdemo.utils.AppUtils;

import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class InstituteLoginFragment extends Fragment {

    FragmentInstituteLoginBinding binding;

    AppSharedPreference appSharedPreference;
    NavController navController;
    public InstituteLoginFragment() {
        // Required empty public constructor
    }

    public static InstituteLoginFragment newInstance(String param1, String param2) {
        InstituteLoginFragment fragment = new InstituteLoginFragment();

        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentInstituteLoginBinding.inflate(inflater, container, false);


        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        appSharedPreference = new AppSharedPreference(requireContext());

        navController = NavHostFragment.findNavController(InstituteLoginFragment.this);

        binding.verifierLogin.setOnClickListener(v -> {

            binding.loadingProgress.setVisibility(View.VISIBLE);

            if(isValidData()){
                String username = String.valueOf(binding.userName.getText());
                String password = String.valueOf(binding.password.getText());

                Log.d("Tag" , "Username : " + username);
                Log.d("Tag" , "Password : " + password);

                loginAsInstitute(username , password);

            }else{
                binding.loadingProgress.setVisibility(View.GONE);
                showMessage(requireContext(), getString(R.string.fill_data));
            }
        });

    }

    private void loginAsInstitute(String username , String password){
        InstituteLoginRequest instituteLoginRequest = new InstituteLoginRequest(username , password);
        Map<String, String> headers = AppUtils.getLoginApiHeader(requireContext());

        ApiSeQRClient.getInstance().seqrCodeService.instituteLogin(headers , instituteLoginRequest).enqueue(new Callback<LoginResponse>() {
            @Override
            public void onResponse(Call<LoginResponse> call, Response<LoginResponse> response) {
                if (response.isSuccessful()) {
                    // Handle the successful response here
                    LoginResponse apiResponse = response.body();

                    if (apiResponse != null && apiResponse.getData() != null) {

                        LoginResponse.ResponseData responseData = apiResponse.getData();

                        appSharedPreference.setAccessToken(apiResponse.getAccesstoken());
                        appSharedPreference.setUserName(responseData.institute_username);
                        appSharedPreference.setUserId(responseData.id);
                        appSharedPreference.setLoginUserType(appSharedPreference.TYPE_INSTITUTE);
                        appSharedPreference.setApiKey();

                        binding.loadingProgress.setVisibility(View.GONE);

                        Log.d("Tag" , "Api Success " + apiResponse.accesstoken + responseData.institute_username + responseData.id);
                        navController.navigate(R.id.action_instituteLoginFragment_to_instituteHomeFragment);

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