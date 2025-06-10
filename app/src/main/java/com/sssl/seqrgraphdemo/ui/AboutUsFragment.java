package com.sssl.seqrgraphdemo.ui;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
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
import com.sssl.seqrgraphdemo.databinding.FragmentAboutUsBinding;

public class AboutUsFragment extends Fragment {

    FragmentAboutUsBinding binding;
    public AboutUsFragment() {
        // Required empty public constructor
    }


    public static AboutUsFragment newInstance(String param1, String param2) {
        AboutUsFragment fragment = new AboutUsFragment();

        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment

        binding = FragmentAboutUsBinding.inflate(inflater, container, false);

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
            toolbarTextView.setText("About Us");
        }
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        binding.email.setOnClickListener(v ->{
            Intent browserIntent = new Intent(Intent.ACTION_SENDTO, Uri.parse("mailto:software@scube.net.in?subject=Enquiry regarding SeQR scan."));
            startActivity(browserIntent);
        });

        binding.domain.setOnClickListener(v ->{
            Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://scube.net.in"));
            startActivity(browserIntent);
        });
    }
}