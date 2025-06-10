package com.sssl.seqrgraphdemo.ui;

import static com.sssl.seqrgraphdemo.utils.AppUtils.showMessage;

import android.app.Activity;
import android.app.Dialog;
import android.graphics.Color;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.fragment.NavHostFragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;

import com.sssl.seqrgraphdemo.R;
import com.sssl.seqrgraphdemo.databinding.FragmentRemoveAccountBinding;
import com.sssl.seqrgraphdemo.models.login.LoginRequest;
import com.sssl.seqrgraphdemo.models.login.LoginResponse;
import com.sssl.seqrgraphdemo.networks.ApiSeQRClient;
import com.sssl.seqrgraphdemo.preferences.AppSharedPreference;
import com.sssl.seqrgraphdemo.utils.AppUtils;

import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class RemoveAccountFragment extends Fragment {

    NavController navController;

    Dialog dialog;

    AppSharedPreference appSharedPreference;

    FragmentRemoveAccountBinding binding;
    public RemoveAccountFragment() {
        // Required empty public constructor
    }

    public static RemoveAccountFragment newInstance(String param1, String param2) {
        RemoveAccountFragment fragment = new RemoveAccountFragment();

        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentRemoveAccountBinding.inflate(inflater , container , false);

        return binding.getRoot();
    }

    @Override
    public void onAttach(@NonNull Activity activity) {
        super.onAttach(activity);
        // Ensure that the fragment is attached to an activity before accessing it
        updateToolbarText();
    }

    private void updateToolbarText() {
        // Access the parent Activity and its Toolbar
        NavHostActivity activity = (NavHostActivity) getActivity();
        if (activity != null) {
            Toolbar toolbar = activity.findViewById(R.id.toolbar);

            // Find the TextView within the Toolbar
            TextView toolbarTextView = toolbar.findViewById(R.id.toolbar_title);

            // Change the text of the TextView
            toolbarTextView.setText("Remove Account");
        }
    }

    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        navController = NavHostFragment.findNavController(RemoveAccountFragment.this);

        appSharedPreference = new AppSharedPreference(requireContext());

        CheckBox isSelectTerms = view.findViewById(R.id.terms);

        binding.removeAccount.setEnabled(false);
        binding.removeAccount.setBackgroundColor(Color.parseColor("#E8E8E8"));

        isSelectTerms.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (isChecked){
                binding.removeAccount.setBackgroundColor(Color.parseColor("#00BFFF"));
                binding.removeAccount.setEnabled(isChecked);
            }else {
                binding.removeAccount.setBackgroundColor(Color.parseColor("#E8E8E8"));
                binding.removeAccount.setEnabled(isChecked);
            }
        });

        binding.removeAccount.setOnClickListener(v->{
            showDialog();
        });
    }

    public void showDialog(){
        dialog = new Dialog(requireContext());
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setCancelable(true);

        dialog.setContentView(R.layout.remove_account_dialog);

        final EditText username = dialog.findViewById(R.id.user_name);
        final EditText password = dialog.findViewById(R.id.password);
        Button removeAccount = dialog.findViewById(R.id.remove);

        removeAccount.setOnClickListener(v->{
            dialog.findViewById(R.id.loading_progress).setVisibility(View.VISIBLE);

            if(isValidData(username , password)){
                String user_name= username.getText().toString();
                String pass = password.getText().toString();

                removeVerifierAccount(user_name , pass);
            }else{
                showMessage(requireContext(), getString(R.string.fill_data));
                dialog.findViewById(R.id.loading_progress).setVisibility(View.GONE);
            }
        });

        dialog.show();
    }

    private void removeVerifierAccount(String username , String password) {
        LoginRequest loginRequest = new LoginRequest(username, password);
        Map<String, String> headers = AppUtils.getLoginApiHeader(requireContext());

        ApiSeQRClient.getInstance().seqrCodeService.removeVerifierAccount(headers , loginRequest).enqueue(new Callback<LoginResponse>() {
            @Override
            public void onResponse(Call<LoginResponse> call, Response<LoginResponse> response) {
                if (response.isSuccessful()) {
                    // Handle the successful response here
                    LoginResponse apiResponse = response.body();

                    Log.d("Tag" , "Success "+ apiResponse.success);
                    Log.d("Tag" , "status "+ apiResponse.status);
                    Log.d("Tag" , "message "+ apiResponse.message);
                    if (apiResponse.success) {

                        appSharedPreference.setAccessToken(null);
                        appSharedPreference.setUserId(0);
                        appSharedPreference.setLoginUserType(null);
                        dialog.findViewById(R.id.loading_progress).setVisibility(View.GONE);

                        dialog.hide();
                        Log.d("Tag" , "Api Success " + apiResponse.status + apiResponse.message);
                        navController.navigate(R.id.action_removeAccountFragment_to_homeScreenFragment);

                    }else {
                        dialog.findViewById(R.id.loading_progress).setVisibility(View.GONE);
                        dialog.hide();
                        showMessage(requireContext(), apiResponse.message);
                        Log.d("Tag" , "Api Response else " + apiResponse);
                    }
                } else {
                    dialog.findViewById(R.id.loading_progress).setVisibility(View.GONE);
                    dialog.hide();
                    showMessage(requireContext(), getString(R.string.form_error));
                    Log.d("Tag" , "Api Response fail " + response);
                }
            }

            @Override
            public void onFailure(Call<LoginResponse> call, Throwable t) {
                dialog.findViewById(R.id.loading_progress).setVisibility(View.GONE);
                showMessage(requireContext(), getString(R.string.form_error));
                Log.d("Tag" , "Api Response failuare " + t.getLocalizedMessage());
                dialog.hide();
            }
        });
    }

    private boolean isValidData(EditText username , EditText password) {

        boolean isValid = true;
        if (isEmpty(username)) {
            isValid = false;
            username.setError("Name should not be empty");
        }

        if (isEmpty(password)) {
            isValid = false;
            password.setError("Password should not be empty");
        }

        return isValid;
    }

    private boolean isEmpty(EditText editText) {
        return editText.getText().toString().trim().isEmpty();
    }}