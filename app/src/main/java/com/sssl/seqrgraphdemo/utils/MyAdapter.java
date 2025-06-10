package com.sssl.seqrgraphdemo.utils;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentManager;
import androidx.lifecycle.Lifecycle;
import androidx.viewpager2.adapter.FragmentStateAdapter;

import com.sssl.seqrgraphdemo.ui.VerifierQrHistoryFragment;
import com.sssl.seqrgraphdemo.ui.VerifierScanHistoryFragment;


public class MyAdapter extends FragmentStateAdapter {

    public MyAdapter(@NonNull FragmentActivity fragmentActivity) {
        super(fragmentActivity);
    }

    public MyAdapter(@NonNull Fragment fragment) {
        super(fragment);
    }

    public MyAdapter(@NonNull FragmentManager fragmentManager, @NonNull Lifecycle lifecycle) {
        super(fragmentManager, lifecycle);
    }

    @NonNull
    @Override
    public Fragment createFragment(int position) {
        switch (position) {
            case 0:
                VerifierScanHistoryFragment verifierScanHistoryFragment = new VerifierScanHistoryFragment();
                return verifierScanHistoryFragment;
            case 1:
                VerifierQrHistoryFragment verifierQrHistoryFragment = new VerifierQrHistoryFragment();
                return verifierQrHistoryFragment;
            default:
                return null;
        }
    }

    @Override
    public int getItemCount() {
        return 2;
    }
}
