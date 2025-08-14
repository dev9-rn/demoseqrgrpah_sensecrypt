package com.sssl.seqrgraphdemo.ui;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.fragment.NavHostFragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.sssl.seqrgraphdemo.R;
import com.sssl.seqrgraphdemo.databinding.FragmentHomeScreenBinding;
import com.sssl.seqrgraphdemo.preferences.AppSharedPreference;
import com.sssl.seqrgraphdemo.viewmodels.SharedViewModel;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link HomeScreenFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class HomeScreenFragment extends Fragment {

    AppSharedPreference appSharedPreference;

    NavController navController;

    private SharedViewModel sharedViewModel;
    private LinkedHashMap<String, Boolean> allPermissions;
    private final ArrayList<String> permissionsToRequest = new ArrayList<>();

    public HomeScreenFragment() {
        // Required empty public constructor
    }

    public static HomeScreenFragment newInstance(String param1, String param2) {
        HomeScreenFragment fragment = new HomeScreenFragment();
//        Bundle args = new Bundle();
//        args.putString(ARG_PARAM1, param1);
//        args.putString(ARG_PARAM2, param2);
//        fragment.setArguments(args);
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

        FragmentHomeScreenBinding binding = FragmentHomeScreenBinding.inflate(inflater, container, false);

        navController = NavHostFragment.findNavController(HomeScreenFragment.this);

        binding.verifierLogin.setOnClickListener(v -> navController.navigate(R.id.action_homeScreenFragment_to_verifierLoginFragment));

        binding.instituteLogin.setOnClickListener(v -> navController.navigate((R.id.action_homeScreenFragment_to_instituteLoginFragment)));

        appSharedPreference = new AppSharedPreference(requireContext());

        if (appSharedPreference.getAccessToken() != null || appSharedPreference.getAccessToken() != "") {
            if (appSharedPreference.TYPE_VERIFIER.equals(appSharedPreference.getLoginUserType())) {
                navController.navigate(R.id.action_homeScreenFragment_to_verifierHomeFragment);
            }
            if (appSharedPreference.TYPE_INSTITUTE.equals(appSharedPreference.getLoginUserType())) {
                navController.navigate(R.id.action_homeScreenFragment_to_instituteHomeFragment);
            }
        }

        Context activityContext
                = requireActivity();


        Log.d("TAG", "requireActivity() is instance of ContextThemeWrapper " + (activityContext instanceof android.view.ContextThemeWrapper));

        Context context = requireContext();


        Log.d("TAG", "requireContext is instance of ContextThemeWrapper " + (context instanceof android.view.ContextThemeWrapper));


        Context applicationContext = requireContext().getApplicationContext();


        Log.d("TAG", "applicationContext is instance of ContextThemeWrapper " + (applicationContext instanceof android.view.ContextThemeWrapper));


        sharedViewModel = new ViewModelProvider(requireActivity()).get(SharedViewModel.class);


        String[] permissions = null;


        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {

            permissions = new String[]{
                    Manifest.permission.CAMERA
            };

        } else {
            permissions = new String[]{
                    Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE
            };
        }


        allPermissions = new LinkedHashMap<>();
        for (String permission : permissions) {
            allPermissions.put(permission, false);
        }

        checkPermissionsToRequest();

        Log.d("TAG", "Permissions to request " + permissionsToRequest);

        if (!permissionsToRequest.isEmpty()) {

            requestPermissionLauncher.launch(permissionsToRequest.toArray(new String[permissionsToRequest.size()]));

        } else {

            sharedViewModel.initSDKs(requireContext());


        }


        sharedViewModel.isSDKInitialized().observe(getViewLifecycleOwner(), aBoolean -> {

            if (Boolean.TRUE.equals(aBoolean)) {

                sharedViewModel.getPublicKeys(requireContext());

            }

        });

        sharedViewModel.getJwsKeys().observe(getViewLifecycleOwner(), s -> {

            Log.d("TAG", "on jws response received");

            if (s != null && s.length() > 0) {
                appSharedPreference.setPublicKeyJson(s);
            }

        });



       return binding.getRoot();
    }

    private void checkPermissionsToRequest() {

        for (Map.Entry<String, Boolean> entry : allPermissions.entrySet()) {

            allPermissions.put(entry.getKey(), hasPermission(entry.getKey()));

            if (hasPermission(entry.getKey())) {
                allPermissions.put(entry.getKey(), true);
            } else {
                allPermissions.put(entry.getKey(), false);
                permissionsToRequest.add(entry.getKey());
            }
        }


    }


    private boolean hasPermission(String permission) {
        return (ContextCompat.checkSelfPermission(requireContext(), permission) == PackageManager.PERMISSION_GRANTED);
    }


    private final ActivityResultLauncher<String[]> requestPermissionLauncher =
            registerForActivityResult(new ActivityResultContracts.RequestMultiplePermissions(), result -> {

                boolean isAllPermissionsGranted = true;

                for (Boolean isGranted : result.values()) {

                    if (Boolean.FALSE.equals(isGranted)) {
                        isAllPermissionsGranted = false;
                        break;

                    }
                }

                if (isAllPermissionsGranted) {
                    sharedViewModel.initSDKs(requireContext());
                }


            });

}