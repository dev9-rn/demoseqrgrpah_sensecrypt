package com.sssl.seqrgraphdemo.ui;

import static com.sssl.seqrgraphdemo.utils.AppUtils.showMessage;

import android.app.Activity;
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
import com.sssl.seqrgraphdemo.databinding.FragmentInstituteBarCodeScanBinding;
import com.sssl.seqrgraphdemo.models.CaptureAct;
import com.sssl.seqrgraphdemo.models.scanview.InstituteAuditScanResponse;
import com.sssl.seqrgraphdemo.models.scanview.ViewCertificateRequest;
import com.sssl.seqrgraphdemo.networks.ApiSeQRClient;
import com.sssl.seqrgraphdemo.preferences.AppSharedPreference;
import com.sssl.seqrgraphdemo.utils.AppUtils;
import com.journeyapps.barcodescanner.ScanContract;
import com.journeyapps.barcodescanner.ScanOptions;

import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class InstituteBarCodeScanFragment extends Fragment {

    FragmentInstituteBarCodeScanBinding binding;

    AppSharedPreference appSharedPreference;

    NavController navController;
    public InstituteBarCodeScanFragment() {
        // Required empty public constructor
    }

    public static InstituteBarCodeScanFragment newInstance(String param1, String param2) {
        InstituteBarCodeScanFragment fragment = new InstituteBarCodeScanFragment();

        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentInstituteBarCodeScanBinding.inflate(inflater, container, false);

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
            toolbarTextView.setText("Scan Barcode");
        }
    }

    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        navController = NavHostFragment.findNavController(InstituteBarCodeScanFragment.this);

        appSharedPreference = new AppSharedPreference(requireContext());

        scanCode();

        binding.scanAgain.setOnClickListener(v->{
            scanCode();
        });

    }

    public void scanCode(){
        ScanOptions options = new ScanOptions();
//        options.setDesiredBarcodeFormats(ScanOptions.QR_CODE);
        options.setPrompt("Scan Barcode");
//        options.setCameraId(0);  // Use a specific camera of the device
        options.setBeepEnabled(true);
//        options.setBarcodeImageEnabled(true);
        options.setOrientationLocked(true);
        options.setCaptureActivity(CaptureAct.class);
        barcodeLauncher.launch(options);
    }
    private final ActivityResultLauncher<ScanOptions> barcodeLauncher = registerForActivityResult(new ScanContract(),
        result -> {
            if(result.getContents() == null) {
                Log.d("Tag", "Canceled: ");
            } else {
                binding.loadingProgress.setVisibility(View.VISIBLE);
                binding.scanAgain.setVisibility(View.GONE);
                getScanAPI(AppUtils.getCertificateKey(result.getContents()));
            }
        });

    private void getScanAPI(String key){
        ViewCertificateRequest verifierViewCertificateRequest = new ViewCertificateRequest(key , "android" ,appSharedPreference.getUserName() ,appSharedPreference.getUserId());

        Map<String, String> headers = AppUtils.getApiHeader(requireContext());

        ApiSeQRClient.getInstance().seqrCodeService.instituteBarCodeViewDetails(headers , verifierViewCertificateRequest).enqueue(new Callback<InstituteAuditScanResponse>() {
            @Override
            public void onResponse(Call<InstituteAuditScanResponse> call, Response<InstituteAuditScanResponse> response) {
                if (response.isSuccessful()) {
                    // Handle the successful response here

                    InstituteAuditScanResponse apiResponse = response.body();

                    if(apiResponse.success){
                        if (apiResponse != null && apiResponse.getData() != null) {

                            InstituteAuditScanResponse.ResponseData responseData = apiResponse.getData();

                            if(responseData.scan_result == 0 || responseData.scan_result == 1) {
                                if(responseData.scan_result == 0){
                                    showMessage(requireContext(),getString(R.string.certificate_inactive));
                                }
                                Bundle args = new Bundle();
                                args.putString("serial_no", responseData.serialNo);
                                args.putString("user_printed", responseData.userPrinted);
                                args.putString("printing_date_time", responseData.printingDateTime);
                                args.putString("printer_used", responseData.printerUsed);
                                args.putInt("printCount", responseData.printCount);
                                args.putInt("scan_result", responseData.scan_result);
                                args.putString("key", responseData.key);

                                navController.navigate(R.id.action_instituteBarCodeScanFragment_to_instituteScanDetailsFragment, args);
                            }

                            if(responseData.scan_result ==2){
                                showMessage(requireContext(), getString(R.string.proper_barcode));
                            }
                            binding.loadingProgress.setVisibility(View.GONE);
                        }else {
                            binding.loadingProgress.setVisibility(View.GONE);
                            binding.scanAgain.setVisibility(View.VISIBLE);
                            showMessage(requireContext(), getString(R.string.form_error));
                        }
                    }
                    else {
                        binding.loadingProgress.setVisibility(View.GONE);
                        binding.scanAgain.setVisibility(View.VISIBLE);
                        showMessage(requireContext(), getString(R.string.form_error));
                    }
                } else {
                    binding.loadingProgress.setVisibility(View.GONE);
                    binding.scanAgain.setVisibility(View.VISIBLE);
                    showMessage(requireContext(), getString(R.string.form_error));
                }
            }

            @Override
            public void onFailure(Call<InstituteAuditScanResponse> call, Throwable t) {
                binding.loadingProgress.setVisibility(View.GONE);
                binding.scanAgain.setVisibility(View.VISIBLE);
                showMessage(requireContext(), getString(R.string.form_error));
            }
        });
    }
}