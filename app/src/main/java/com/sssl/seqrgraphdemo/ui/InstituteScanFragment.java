package com.sssl.seqrgraphdemo.ui;

import static com.sssl.seqrgraphdemo.utils.AppUtils.showMessage;

import android.os.Bundle;

import androidx.activity.result.ActivityResultLauncher;
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
import android.widget.TextView;

import com.sssl.seqrgraphdemo.R;
import com.sssl.seqrgraphdemo.databinding.FragmentInstituteScanBinding;
import com.sssl.seqrgraphdemo.models.CaptureAct;
import com.sssl.seqrgraphdemo.models.scanview.ViewCertificateRequest;
import com.sssl.seqrgraphdemo.models.scanview.ViewCertificateResponse;
import com.sssl.seqrgraphdemo.networks.ApiSeQRClient;
import com.sssl.seqrgraphdemo.preferences.AppSharedPreference;
import com.sssl.seqrgraphdemo.utils.AppUtils;
import com.journeyapps.barcodescanner.ScanContract;
import com.journeyapps.barcodescanner.ScanOptions;

import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class InstituteScanFragment extends Fragment {

    AppSharedPreference appSharedPreference;

    FragmentInstituteScanBinding binding;

    NavController navController;
    public InstituteScanFragment() {
        // Required empty public constructor
    }

    public static InstituteScanFragment newInstance(String param1, String param2) {
        InstituteScanFragment fragment = new InstituteScanFragment();

        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        binding = FragmentInstituteScanBinding.inflate(inflater, container, false);


        return binding.getRoot();
    }

    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        appSharedPreference = new AppSharedPreference(requireContext());

        NavHostActivity activity = (NavHostActivity) getActivity();
        if (activity != null) {
            Toolbar toolbar = activity.findViewById(R.id.toolbar);
            TextView toolbarTextView = toolbar.findViewById(R.id.toolbar_title);
            toolbarTextView.setText("Scan QR");
        }

        navController = NavHostFragment.findNavController(InstituteScanFragment.this);

        scanCode();

        binding.scanAgain.setOnClickListener(v->{
            scanCode();
        });

    }

    public void scanCode(){
        ScanOptions options = new ScanOptions();
        options.setDesiredBarcodeFormats(ScanOptions.QR_CODE);
        options.setPrompt("Scan QR Code");
//        options.setCameraId(0);  // Use a specific camera of the device
        options.setBeepEnabled(true);
        options.setBarcodeImageEnabled(true);
        options.setOrientationLocked(true);
        options.setCaptureActivity(CaptureAct.class);
        barcodeLauncher.launch(options);
    }
    private final ActivityResultLauncher<ScanOptions> barcodeLauncher = registerForActivityResult(new ScanContract(),
    result -> {
        if(result.getContents() != null) {
            binding.loadingProgress.setVisibility(View.VISIBLE);
            binding.scanAgain.setVisibility(View.GONE);
            getScanAPI(AppUtils.getCertificateKey(result.getContents()));
        }
    });

    private void getScanAPI(String key){
        ViewCertificateRequest instituteViewCertificateRequest = new ViewCertificateRequest(key , "android" ,appSharedPreference.getUserName() ,appSharedPreference.getUserId());
        Map<String, String> headers = AppUtils.getApiHeader(requireContext());

        ApiSeQRClient.getInstance().seqrCodeService.instituteScanViewCertificate(headers , instituteViewCertificateRequest).enqueue(new Callback<ViewCertificateResponse>() {
            @Override
            public void onResponse(Call<ViewCertificateResponse> call, Response<ViewCertificateResponse> response) {
                if (response.isSuccessful()) {
                    // Handle the successful response here

                    ViewCertificateResponse apiResponse = response.body();

                    if(apiResponse.success){
                        if (apiResponse != null && apiResponse.getData() != null) {

                            ViewCertificateResponse.ResponseData responseData = apiResponse.getData();

                            Bundle args = new Bundle();
                            args.putString("pdfUrl", responseData.fileUrl);
                            args.putString("serialNo", responseData.serialNo);
                            args.putString("key", responseData.key);
                            args.putString("scanResult", responseData.scan_result);

                            binding.loadingProgress.setVisibility(View.GONE);
                            navController.navigate(R.id.action_instituteScanFragment_to_viewCertificateFragment , args);

                        }else {
                            binding.loadingProgress.setVisibility(View.GONE);
                            binding.scanAgain.setVisibility(View.VISIBLE);
                            showMessage(requireContext(), apiResponse.message);
                        }
                    }else {
                        binding.loadingProgress.setVisibility(View.GONE);
                        binding.scanAgain.setVisibility(View.VISIBLE);
                        Log.d("Tag" , "Error " + response);
                        showMessage(requireContext(), apiResponse.message);
                    }
                } else {
                    binding.loadingProgress.setVisibility(View.GONE);
                    binding.scanAgain.setVisibility(View.VISIBLE);
                    Log.d("Tag" , "Error " + response);
                    showMessage(requireContext(), getString(R.string.form_error));
                }
            }

            @Override
            public void onFailure(Call<ViewCertificateResponse> call, Throwable t) {
                binding.loadingProgress.setVisibility(View.GONE);
                binding.scanAgain.setVisibility(View.VISIBLE);
                Log.d("Tag" , "Error " + t.getLocalizedMessage());
                showMessage(requireContext(), getString(R.string.form_error));
            }
        });
    }
}