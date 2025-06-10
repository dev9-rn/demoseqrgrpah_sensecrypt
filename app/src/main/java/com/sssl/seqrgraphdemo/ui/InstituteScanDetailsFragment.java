package com.sssl.seqrgraphdemo.ui;

import android.app.Activity;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.sssl.seqrgraphdemo.R;
import com.sssl.seqrgraphdemo.databinding.FragmentInstituteScanDetailsBinding;

public class InstituteScanDetailsFragment extends Fragment {

    FragmentInstituteScanDetailsBinding binding;
    public InstituteScanDetailsFragment() {
        // Required empty public constructor
    }

    public static InstituteScanDetailsFragment newInstance(String param1, String param2) {
        InstituteScanDetailsFragment fragment = new InstituteScanDetailsFragment();

        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentInstituteScanDetailsBinding.inflate(inflater, container, false);

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
            toolbarTextView.setText("Scanned Details");
        }
    }

    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);


        binding.barcode.append(getArguments().getString("key"));
        binding.userPrinted.append(getArguments().getString("user_printed"));
        binding.printDate.append(getArguments().getString("printing_date_time"));
        binding.printTime.append(getArguments().getString("printing_date_time"));
        binding.printerUsed.append(getArguments().getString("printer_used"));
        binding.printCount.append(String.valueOf(getArguments().getInt("printCount")));
        binding.status.append(String.valueOf(getArguments().getInt("scan_result")));
    }
}