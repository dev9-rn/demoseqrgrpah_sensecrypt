package com.sssl.seqrgraphdemo.ui;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;

import com.sssl.seqrgraphdemo.R;
import com.sssl.seqrgraphdemo.databinding.ActivityNavHostBinding;

public class NavHostActivity extends AppCompatActivity {

    ActivityNavHostBinding binding;

    NavController navController;

    String user_type;

    Toolbar toolbar;

    ActionBar actionBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        setContentView(R.layout.activity_nav_host);

        binding = ActivityNavHostBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());


        toolbar = findViewById(R.id.toolbar);

        setSupportActionBar(binding.getRoot().findViewById(R.id.toolbar));


        navController = Navigation.findNavController(this, R.id.nav_host_fragment_content_main);
        AppBarConfiguration appBarConfiguration = new AppBarConfiguration.Builder(navController.getGraph()).build();
        NavigationUI.setupActionBarWithNavController(this, navController, appBarConfiguration);


    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu, menu);


        handleNavDestinationChange(menu);

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        user_type = (String) getSharedPreferences("idencode_prefs", MODE_PRIVATE).getAll().get("LOGIN_USER_TYPE");
        int id = item.getItemId();

        if(id == R.id.action_settings){
            if(user_type.equals("VERIFIER")){
                navController.navigate(R.id.action_verifierHomeFragment_to_settingFragment);
            }else{
                navController.navigate(R.id.action_instituteHomeFragment_to_settingFragment);
            }
        }

//        if (id == R.id.about_us) {
//            if(user_type.equals("VERIFIER")){
//                navController.navigate(R.id.action_verifierHomeFragment_to_aboutUsFragment);
//            }else{
//                navController.navigate(R.id.action_instituteHomeFragment_to_aboutUsFragment);
//            }
//        }else if(id == R.id.logout){
//            String accessToken = (String) getSharedPreferences("idencode_prefs", MODE_PRIVATE).getAll().get("ACCESS_TOKEN");
//            String apiKey = (String) getSharedPreferences("idencode_prefs", MODE_PRIVATE).getAll().get("API_KEY");
//
//            logoutAsVerifier(accessToken, apiKey);
//
//        }else if(id == R.id.remove_account){
//            navController.navigate(R.id.action_verifierHomeFragment_to_removeAccountFragment);
//        }else {
//
//        }
        return super.onOptionsItemSelected(item);
    }

    private void handleNavDestinationChange(Menu menu) {

        navController.addOnDestinationChangedListener((navController2, navDestination, bundle) -> {

            TextView toolbarTextView = toolbar.findViewById(R.id.toolbar_title);
            toolbarTextView.setText(R.string.app_name);

            actionBar = getSupportActionBar();
            if (actionBar == null) {
                return;
            }

            if (navDestination.getId() == R.id.homeScreenFragment) {

                actionBar.hide();

            } else {
                if(navDestination.getId() == R.id.verifierLoginFragment || navDestination.getId() == R.id.verifierHomeFragment){
                    int toolbarColor = ContextCompat.getColor(this, R.color.primary);
                    toolbar.setBackgroundColor(toolbarColor);
                }else if(navDestination.getId() == R.id.instituteLoginFragment || navDestination.getId() == R.id.instituteHomeFragment){
                    int toolbarColor = ContextCompat.getColor(this, R.color.secondary);
                    toolbar.setBackgroundColor(toolbarColor);
                }else{

                }
                if(navDestination.getId() == R.id.verifierHomeFragment || navDestination.getId() == R.id.instituteHomeFragment){
                    menu.setGroupVisible(0 , true);
                }else {
                    menu.setGroupVisible(0 , false);
                }
//                if(navDestination.getId() == R.id.verifierHomeFragment){
//                    menu.setGroupVisible(0 , true);
//                }else if(navDestination.getId() == R.id.instituteHomeFragment){
//                    menu.setGroupVisible(0 , true);
//                    menu.findItem(R.id.remove_account).setVisible(false);
//                }else {
//                    menu.setGroupVisible(0 , false);
//                }
                actionBar.show();
            }

            actionBar.setHomeButtonEnabled(false); // disable the button
            actionBar.setDisplayHomeAsUpEnabled(false); // remove the left caret
            actionBar.setDisplayShowHomeEnabled(false); // remove the icon
            actionBar.setTitle(null);
        });

    }

//    private void logoutAsVerifier(String access_token, String api_key){
//
//        findViewById(R.id.loading_progress).setVisibility(View.VISIBLE);
//
//        Map<String, String> headers = new HashMap<>();
//        headers.put("Accept", "application/json");
//        headers.put("Content-Type", "application/json");
//        headers.put("accesstoken", access_token);
//        headers.put("apikey", api_key);
//
//        ApiSeQRClient.getInstance().seqrCodeService.logout(headers).enqueue(new Callback<LogoutResponse>() {
//            @Override
//            public void onResponse(Call<LogoutResponse> call, Response<LogoutResponse> response) {
//                LogoutResponse logoutResponse = response.body();
//
//                if(logoutResponse.success){
//                    findViewById(R.id.loading_progress).setVisibility(View.GONE);
//                    getSharedPreferences("idencode_prefs", MODE_PRIVATE).edit().clear().commit();
//                    if(user_type.equals("VERIFIER")){
//                        navController.navigate(R.id.action_verifierHomeFragment_to_homeScreenFragment);
//                    }else{
//                        navController.navigate(R.id.action_instituteHomeFragment_to_homeScreenFragment);
//                    }
//                    Toast.makeText(getApplicationContext(),logoutResponse.message,Toast.LENGTH_SHORT).show();
//                }else {
//                    findViewById(R.id.loading_progress).setVisibility(View.GONE);
//                    Toast.makeText(getApplicationContext(),logoutResponse.message,Toast.LENGTH_SHORT).show();
//                }
//            }
//
//            @Override
//            public void onFailure(Call<LogoutResponse> call, Throwable t) {
////                Log.d("Tag" , "Error " + t.getLocalizedMessage());
//                findViewById(R.id.loading_progress).setVisibility(View.GONE);
//                Toast.makeText(getApplicationContext(),R.string.form_error,Toast.LENGTH_SHORT).show();
//            }
//        });
//
//    }

}