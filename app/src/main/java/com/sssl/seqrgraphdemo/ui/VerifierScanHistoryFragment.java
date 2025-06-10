package com.sssl.seqrgraphdemo.ui;

import static android.view.ViewGroup.LayoutParams.MATCH_PARENT;

import static com.sssl.seqrgraphdemo.utils.AppUtils.showMessage;

import android.content.Context;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.fragment.NavHostFragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.sssl.seqrgraphdemo.R;
import com.sssl.seqrgraphdemo.databinding.FragmentVerifierScanHistoryBinding;
import com.sssl.seqrgraphdemo.models.scanHistory.VerifierScanHistoryData;
import com.sssl.seqrgraphdemo.models.scanHistory.VerifierScanHistoryRequest;
import com.sssl.seqrgraphdemo.models.scanHistory.VerifierScanHistoryResponse;
import com.sssl.seqrgraphdemo.networks.ApiSeQRClient;
import com.sssl.seqrgraphdemo.preferences.AppSharedPreference;
import com.sssl.seqrgraphdemo.utils.AppUtils;

import java.util.List;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class VerifierScanHistoryFragment extends Fragment {

    FragmentVerifierScanHistoryBinding binding;

    NavController navController;

    List<VerifierScanHistoryData> scanHistoryList;

    VerifierScanHistoryRequest veriRequest;

    AppSharedPreference appSharedPreference;

    Map<String, String> headers;

    AlertDialog alertDialog;

    private LinearLayout linearLayout;

    private Call<VerifierScanHistoryResponse> call;


    public VerifierScanHistoryFragment() {
        // Required empty public constructor
    }


    public static VerifierScanHistoryFragment newInstance(String param1, String param2) {
        VerifierScanHistoryFragment fragment = new VerifierScanHistoryFragment();

        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentVerifierScanHistoryBinding.inflate(inflater, container, false);

        appSharedPreference = new AppSharedPreference(requireContext());

        return binding.getRoot();
    }

    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        navController = NavHostFragment.findNavController(VerifierScanHistoryFragment.this);

        veriRequest = new VerifierScanHistoryRequest("android" , appSharedPreference.getUserId());

        headers = AppUtils.getApiHeader(requireContext());

        call = ApiSeQRClient.getInstance().seqrCodeService.getVerifierScanHistory(headers, veriRequest);

        call.enqueue(new Callback<VerifierScanHistoryResponse>() {
            @Override
            public void onResponse(Call<VerifierScanHistoryResponse> call, Response<VerifierScanHistoryResponse> response) {
                if (response.isSuccessful()) {
                    // Handle the successful response here
                    VerifierScanHistoryResponse apiResponse = response.body();

                    if (apiResponse != null && apiResponse.getData() != null) {
                        scanHistoryList = apiResponse.getData();

                        binding.resultScrollView.removeAllViews();

                        if(isAdded()){
                            binding.resultScrollView.addView(createScanHistoryLayout());
                        }

                        binding.loadingProgress.setVisibility(View.GONE);

                        Log.d("Tag" , "Api Response Success" + scanHistoryList);

                    }else {
                        binding.loadingProgress.setVisibility(View.GONE);
                        binding.noRecord.setVisibility(View.VISIBLE);
                        binding.resultScrollView.setVisibility(View.GONE);
                        showMessage(requireContext(), apiResponse.message);
                        Log.d("Tag" , "Api Response " + response);
                    }
                } else {
                    binding.loadingProgress.setVisibility(View.GONE);
                    binding.noRecord.setVisibility(View.VISIBLE);
                    binding.resultScrollView.setVisibility(View.GONE);
                    showErrorMessage();
                    Log.d("Tag" , "Api Response else " + response);
                }
            }

            @Override
            public void onFailure(Call<VerifierScanHistoryResponse> call, Throwable t) {
                binding.loadingProgress.setVisibility(View.GONE);
                binding.noRecord.setVisibility(View.VISIBLE);
                binding.resultScrollView.setVisibility(View.GONE);
                showErrorMessage();
                Log.d("Tag" , "Api Response failure " + t.getLocalizedMessage());
            }
        });
    }

    private void showErrorMessage(){
        if(isAdded()){
            showMessage(requireContext(), getString(R.string.form_error));
        }
    }

    private LinearLayout createScanHistoryLayout() {

        int recordCount = 0;
        LinearLayout outerLayout = new LinearLayout(requireContext());
        outerLayout.setOrientation(LinearLayout.VERTICAL);

        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(MATCH_PARENT, MATCH_PARENT);
        outerLayout.setLayoutParams(layoutParams);
        LayoutInflater inflater;

        for (VerifierScanHistoryData data : scanHistoryList) {

            if(data.getScan_result() == 1) {
                if (data.getDocument_id() != null || data.getDocument_id() == "") {
                    recordCount++;
                    LinearLayout innerLayout = new LinearLayout(requireContext());

                    innerLayout.setOrientation(LinearLayout.VERTICAL);
                    innerLayout.setLayoutParams(layoutParams);
                    inflater = (LayoutInflater) requireContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                    View resultView = inflater.inflate(R.layout.scan_history_result_template, null);

                    TextView doc_id = resultView.findViewById(R.id.doc_id);
                    TextView scan_on = resultView.findViewById(R.id.scan_on);
                    ImageView device_icon = resultView.findViewById(R.id.device_icon);

                    if (data.device_type.toLowerCase().equals("android")) {
                        device_icon.setImageResource(R.drawable.android);
                    } else if (data.device_type.toLowerCase().equals("ios")) {
                        device_icon.setImageResource(R.drawable.ios);
                    } else {
                        device_icon.setImageResource(R.drawable.windows);
                    }

                    doc_id.setText("Document ID : " + data.getDocument_id());
                    scan_on.setText("Scanned On : " + data.getDate_time());

                    resultView.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {

                            Bundle args = new Bundle();
                            args.putString("pdfUrl", data.pdf_url);
                            args.putString("serialNo", data.document_id);
                            args.putString("key", data.scanned_data);
                            args.putString("scanResult", String.valueOf(data.scan_result));

                            navController.navigate(R.id.action_tabLayoutFragment_to_viewCertificateFragment, args);
                        }
                    });

                    innerLayout.addView(resultView);
                    outerLayout.addView(innerLayout);
                }
            }
        }

        if(recordCount == 0){
            binding.noRecord.setVisibility(View.VISIBLE);
            binding.resultScrollView.setVisibility(View.GONE);
        }else {
            binding.noRecord.setVisibility(View.GONE);
            binding.resultScrollView.setVisibility(View.VISIBLE);
        }

        return outerLayout;

    }

    @Override
    public void onDestroyView() {
        if (call != null) {
            call.cancel();
        }
        super.onDestroyView();
    }
}