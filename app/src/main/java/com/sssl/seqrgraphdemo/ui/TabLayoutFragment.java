package com.sssl.seqrgraphdemo.ui;

import android.app.Activity;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.viewpager2.widget.ViewPager2;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.sssl.seqrgraphdemo.R;
import com.sssl.seqrgraphdemo.databinding.FragmentTabLayoutBinding;
import com.sssl.seqrgraphdemo.utils.MyAdapter;
import com.google.android.material.tabs.TabLayout;

public class TabLayoutFragment extends Fragment {

    TabLayout tabLayout;
    ViewPager2 viewPager;

    FragmentTabLayoutBinding binding;

    public TabLayoutFragment() {
        // Required empty public constructor
    }

    public static TabLayoutFragment newInstance(String param1, String param2) {
        TabLayoutFragment fragment = new TabLayoutFragment();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


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
            toolbarTextView.setText("Scan History");
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentTabLayoutBinding.inflate(inflater, container, false);

        tabLayout= binding.tabLayout;
        viewPager= binding.viewPager;


        final MyAdapter adapter = new MyAdapter(this);
        viewPager.setAdapter(adapter);

        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                viewPager.setCurrentItem(tab.getPosition());
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });

        viewPager.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {
            @Override
            public void onPageSelected(int position) {
                super.onPageSelected(position);
                tabLayout.getTabAt(position).select();
            }
        });

        return binding.getRoot();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();

    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        // Additional cleanup tasks
    }
}