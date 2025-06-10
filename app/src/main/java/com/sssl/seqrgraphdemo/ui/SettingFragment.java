package com.sssl.seqrgraphdemo.ui;

import static android.content.Context.MODE_PRIVATE;
import static com.sssl.seqrgraphdemo.utils.AppUtils.showMessage;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.fragment.NavHostFragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.sssl.seqrgraphdemo.R;
import com.sssl.seqrgraphdemo.databinding.FragmentSettingBinding;
import com.sssl.seqrgraphdemo.models.login.LogoutResponse;
import com.sssl.seqrgraphdemo.networks.ApiSeQRClient;
import com.sssl.seqrgraphdemo.preferences.AppSharedPreference;
import com.sssl.seqrgraphdemo.utils.AppUtils;

import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class SettingFragment extends Fragment {

    AppSharedPreference appSharedPreference;

    NavController navController;

    FragmentSettingBinding binding;

    private Call<LogoutResponse> call;
    public SettingFragment() {
        // Required empty public constructor
    }

    public static SettingFragment newInstance(String param1, String param2) {
        SettingFragment fragment = new SettingFragment();

        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentSettingBinding.inflate(inflater, container, false);

        appSharedPreference = new AppSharedPreference(requireContext());

        if(appSharedPreference.getLoginUserType().equals(appSharedPreference.TYPE_INSTITUTE)){
            binding.remove.setVisibility(View.GONE);
            binding.textView3.setVisibility(View.GONE);
            binding.divider1.setVisibility(View.GONE);
        }

        return binding.getRoot();
    }

    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);



        navController = NavHostFragment.findNavController(SettingFragment.this);


        binding.aboutUs.setOnClickListener(v ->{
            navController.navigate(R.id.action_settingFragment_to_aboutUsFragment);
        });
        binding.remove.setOnClickListener(v ->{
            navController.navigate(R.id.action_settingFragment_to_removeAccountFragment);
        });
        binding.logout.setOnClickListener(v ->{
            logoutAsVerifier();
        });
        binding.scUseBackCam.setOnClickListener(v ->{
            appSharedPreference.setUseBackCamera(binding.scUseBackCam.isChecked());
        });

        binding.faceLiveness.setOnClickListener(v ->{
            appSharedPreference.setFaceLiveness(binding.faceLiveness.isChecked());
        });
    }

    @Override
    public void onResume() {
        super.onResume();
        binding.scUseBackCam.setChecked(appSharedPreference.isUseBackCamera());
        binding.faceLiveness.setChecked(appSharedPreference.isFaceLivenessEnabled());
    }

    private void logoutAsVerifier(){

        binding.loadingProgress.setVisibility(View.VISIBLE);

        Map<String, String> headers = AppUtils.getApiHeader(requireContext());

        call = ApiSeQRClient.getInstance().seqrCodeService.logout(headers);

        call.enqueue(new Callback<LogoutResponse>() {
            @Override
            public void onResponse(Call<LogoutResponse> call, Response<LogoutResponse> response) {
                LogoutResponse logoutResponse = response.body();

                if(logoutResponse.success){
                    binding.loadingProgress.setVisibility(View.GONE);

                    requireContext().getSharedPreferences("idencode_prefs", MODE_PRIVATE).edit().clear().commit();

                    navController.navigate(R.id.action_settingFragment_to_homeScreenFragment);

                    showMessage(requireContext(), logoutResponse.message);
                }else {
                    binding.loadingProgress.setVisibility(View.GONE);
                    showMessage(requireContext(), logoutResponse.message);
                }
            }

            @Override
            public void onFailure(Call<LogoutResponse> call, Throwable t) {
//                Log.d("Tag" , "Error " + t.getLocalizedMessage());
                binding.loadingProgress.setVisibility(View.GONE);
                showErrorMessage();
            }
        });

    }
    private void showErrorMessage(){
        if(isAdded()){
            showMessage(requireContext(), getString(R.string.form_error));
        }
    }

    @Override
    public void onDestroyView() {
        if (call != null) {
            call.cancel();
        }
        super.onDestroyView();
    }
}