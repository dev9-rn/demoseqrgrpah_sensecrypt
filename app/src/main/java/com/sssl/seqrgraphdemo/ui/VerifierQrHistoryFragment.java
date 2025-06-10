package com.sssl.seqrgraphdemo.ui;

import static android.view.ViewGroup.LayoutParams.MATCH_PARENT;
import static com.sssl.seqrgraphdemo.utils.AppUtils.showMessage;

import android.content.Context;
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
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.sssl.seqrgraphdemo.R;
import com.sssl.seqrgraphdemo.databinding.FragmentVerifierQrHistoryBinding;
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


public class VerifierQrHistoryFragment extends Fragment {

    FragmentVerifierQrHistoryBinding binding;

    NavController navController;

    List<VerifierScanHistoryData> scanHistoryList;

    AppSharedPreference appSharedPreference;

    private Call<VerifierScanHistoryResponse> call;
    public VerifierQrHistoryFragment() {
        // Required empty public constructor
    }

    public static VerifierQrHistoryFragment newInstance(String param1, String param2) {
        VerifierQrHistoryFragment fragment = new VerifierQrHistoryFragment();

        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentVerifierQrHistoryBinding.inflate(inflater, container, false);

        appSharedPreference = new AppSharedPreference(requireContext());

        return binding.getRoot();
    }

    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        navController = NavHostFragment.findNavController(VerifierQrHistoryFragment.this);

        Log.d("Tag" , "appSharedPreference.getUserId() " +appSharedPreference.getUserId());

        VerifierScanHistoryRequest veriRequest = new VerifierScanHistoryRequest("android" , appSharedPreference.getUserId());

        Map<String, String> headers = AppUtils.getApiHeader(requireContext());

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

            if(data.getScan_result() != 1) {
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

                    scan_on.setVisibility(View.GONE);
                    device_icon.setVisibility(View.GONE);
                    doc_id.setText("Document ID : " + data.getDocument_id());

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