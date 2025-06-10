package com.sssl.seqrgraphdemo.ui;

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
import com.sssl.seqrgraphdemo.databinding.FragmentInstituteHomeBinding;
import com.sssl.seqrgraphdemo.preferences.AppSharedPreference;

public class InstituteHomeFragment extends Fragment {

    FragmentInstituteHomeBinding binding;
    AppSharedPreference appSharedPreference;

//    private SharedViewModel sharedViewModel;
//    private LinkedHashMap<String, Boolean> allPermissions;
//    private final ArrayList<String> permissionsToRequest = new ArrayList<>();

    public InstituteHomeFragment() {
        // Required empty public constructor
    }

    public static InstituteHomeFragment newInstance(String param1, String param2) {
        InstituteHomeFragment fragment = new InstituteHomeFragment();

        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentInstituteHomeBinding.inflate(inflater, container, false);

        return binding.getRoot();
    }

    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        appSharedPreference = new AppSharedPreference(requireContext());

        NavController navController = NavHostFragment.findNavController(InstituteHomeFragment.this);

        binding.welcome.setText("Welcome : " + appSharedPreference.getUserName());

        binding.scan.setOnClickListener(v -> navController.navigate(R.id.action_instituteHomeFragment_to_instituteScanFragment));

        binding.scanCryptograph.setOnClickListener(v -> navController.navigate(R.id.action_instituteHomeFragment_to_scanCryptographFragment));

        binding.scanBarcode.setOnClickListener(v -> navController.navigate(R.id.action_instituteHomeFragment_to_instituteBarCodeScanFragment));

//        Context activityContext
//                = requireActivity();
//
//
//        Log.d("TAG", "requireActivity() is instance of ContextThemeWrapper " + (activityContext instanceof android.view.ContextThemeWrapper));
//
//        Context context = requireContext();
//
//
//        Log.d("TAG", "requireContext is instance of ContextThemeWrapper " + (context instanceof android.view.ContextThemeWrapper));
//
//
//        Context applicationContext = requireContext().getApplicationContext();
//
//
//        Log.d("TAG", "applicationContext is instance of ContextThemeWrapper " + (applicationContext instanceof android.view.ContextThemeWrapper));
//
//
//        sharedViewModel = new ViewModelProvider(requireActivity()).get(SharedViewModel.class);
//
//
//        String[] permissions = null;
//
//
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
//
//            permissions = new String[]{
//                    Manifest.permission.CAMERA
//            };
//
//        } else {
//            permissions = new String[]{
//                    Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE
//            };
//        }


//        allPermissions = new LinkedHashMap<>();
//        for (String permission : permissions) {
//            allPermissions.put(permission, false);
//        }
//
//        checkPermissionsToRequest();
//
//        Log.d("TAG", "Permissions to request " + permissionsToRequest);
//
//        if (!permissionsToRequest.isEmpty()) {
//
//            requestPermissionLauncher.launch(permissionsToRequest.toArray(new String[permissionsToRequest.size()]));
//        } else {
//            sharedViewModel.initSDKs(requireContext());
//        }


//        sharedViewModel.isSDKInitialized().observe(getViewLifecycleOwner(), aBoolean -> {
//
//            if (Boolean.TRUE.equals(aBoolean)) {
//
//                sharedViewModel.getPublicKeys(requireContext());
//
//            }
//
//        });
//
//        sharedViewModel.getJwsKeys().observe(getViewLifecycleOwner(), s -> {
//
//            Log.d("TAG", "on jws response received");
//
//            if (s != null && s.length() > 0) {
//                appSharedPreference.setPublicKeyJson(s);
//            }
//
//        });
    }


//    private void checkPermissionsToRequest() {
//
//        for (Map.Entry<String, Boolean> entry : allPermissions.entrySet()) {
//
//            allPermissions.put(entry.getKey(), hasPermission(entry.getKey()));
//
//            if (hasPermission(entry.getKey())) {
//                allPermissions.put(entry.getKey(), true);
//            } else {
//                allPermissions.put(entry.getKey(), false);
//                permissionsToRequest.add(entry.getKey());
//            }
//        }
//
//
//    }


//    private boolean hasPermission(String permission) {
//        return (ContextCompat.checkSelfPermission(requireContext(), permission) == PackageManager.PERMISSION_GRANTED);
//    }
//
//
//    private final ActivityResultLauncher<String[]> requestPermissionLauncher =
//            registerForActivityResult(new ActivityResultContracts.RequestMultiplePermissions(), result -> {
//
//                boolean isAllPermissionsGranted = true;
//
//                for (Boolean isGranted : result.values()) {
//
//                    if (Boolean.FALSE.equals(isGranted)) {
//                        isAllPermissionsGranted = false;
//                        break;
//
//                    }
//                }
//
//                if (isAllPermissionsGranted) {
//                    sharedViewModel.initSDKs(requireContext());
//                }
//
//
//            });
}