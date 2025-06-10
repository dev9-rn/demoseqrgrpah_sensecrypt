package com.sssl.seqrgraphdemo.ui;

import android.app.Activity;
import android.os.AsyncTask;
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
import com.sssl.seqrgraphdemo.databinding.FragmentViewCertificateBinding;
import com.github.barteksc.pdfviewer.listener.OnLoadCompleteListener;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

import javax.net.ssl.HttpsURLConnection;

public class ViewCertificateFragment extends Fragment {

    FragmentViewCertificateBinding binding;
    InputStream inputStream = null;
    public String pdfurl;
    public String key;
    public String scanResult;
    public String serialNo;

    public ViewCertificateFragment() {
        // Required empty public constructor
    }

    public static ViewCertificateFragment newInstance(String param1, String param2) {
        ViewCertificateFragment fragment = new ViewCertificateFragment();

        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        pdfurl = getArguments().getString("pdfUrl");
        key = getArguments().getString("key");
        serialNo = getArguments().getString("serialNo");
        scanResult = getArguments().getString("scanResult");
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        binding = FragmentViewCertificateBinding.inflate(inflater, container, false);

        return binding.getRoot();
//        return inflater.inflate(R.layout.fragment_view_certificate, container, false);
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
            toolbarTextView.setText("Scanned Certificate");
        }
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

//        pdfurl = "https://pjlce.seqrdoc.com/pjlce/backend/pdf_file/202401231446401.pdf";


        binding.documentId.append(serialNo);
        binding.status.append(scanResult.equals("1") ? " Active" : " Inactive");

        new RetrivePDFfromUrl().execute(pdfurl);
    }

    class RetrivePDFfromUrl extends AsyncTask<String, Void, InputStream> {
        @Override
        protected InputStream doInBackground(String... strings) {
            // we are using inputstream
            // for getting out PDF.
            try {
                URL url = new URL(strings[0]);
                // below is the step where we are
                // creating our connection.
                HttpURLConnection urlConnection = (HttpsURLConnection) url.openConnection();
                if (urlConnection.getResponseCode() == 200) {
                    // response is success.
                    // we are getting input stream from url
                    // and storing it in our variable.
                    inputStream = new BufferedInputStream(urlConnection.getInputStream());

                }

            } catch (IOException e) {
                // this is the method
                // to handle errors.
                e.printStackTrace();
                return null;
            }
            return inputStream;
        }

        @Override
        protected void onPostExecute(InputStream inputStream) {
            // after the execution of our async
            // task we are loading our pdf in our pdf view.
            binding.viewPdf.fromStream(inputStream).onLoad(new OnLoadCompleteListener() {
                @Override
                public void loadComplete(int nbPages) {
                    binding.loadingProgress.setVisibility(View.GONE);
                }
            }).load();
        }
    }
}