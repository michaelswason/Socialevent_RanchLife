package com.readinsite.ranchlife.activity;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.FacebookSdk;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;
import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.android.gms.common.api.GoogleApiClient;
import com.readinsite.ranchlife.R;
import com.readinsite.ranchlife.model.OEvent;
import com.readinsite.ranchlife.model.OUser;
import com.readinsite.ranchlife.model.PushEvent;
import com.readinsite.ranchlife.model.PushEventResponse;
import com.readinsite.ranchlife.model.UserResponse;
import com.readinsite.ranchlife.rest.ApiClient;
import com.readinsite.ranchlife.rest.ApiInterface;
import com.readinsite.ranchlife.app.UserPreferences;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Arrays;
import java.util.List;

import io.realm.Realm;
import io.realm.RealmResults;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class LoginActivity extends FragmentActivity implements TextWatcher, View.OnClickListener, GoogleApiClient.OnConnectionFailedListener {
    public static final int GOOGLE_SIGN_IN = 2;
    private static final int PLAY_SERVICES_RESOLUTION_REQUEST = 9000;

    private UserPreferences preferences;
    private LoginButton loginButton ;
    private EditText etSignEmail, etSignPwd, etJoinFName, etJoinLName, etJoinPhoneNumber, etJoinEmail, etJoinPwd, etJoinPsscode;
    private TextView btnTapSign, btnTapJoin;
    private LinearLayout tapSign, tapJoin, viewSign, viewJoin;
    private int currentTab = 0;
    private CallbackManager callbackManager ;
    private GoogleApiClient mGoogleApiClient;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        preferences = UserPreferences.getInstance();

        FacebookSdk.sdkInitialize(getApplicationContext());
        setContentView(R.layout.activity_login);
        loginButton = (LoginButton) findViewById(R.id.activity_login_btn_facebook_button);
        //Sign In Part
        viewSign = (LinearLayout)findViewById(R.id.activity_login_signin_view);
        btnTapSign = (TextView)findViewById(R.id.activity_login_tab_btn_signin);
        btnTapSign.setOnClickListener(this);
        tapSign = (LinearLayout)findViewById(R.id.activity_login_tab_sign);
        etSignEmail = (EditText)findViewById(R.id.activity_login_et_sign_email);
        etSignEmail.addTextChangedListener(this);
        etSignPwd = (EditText)findViewById(R.id.activity_login_et_sign_pwd);
        etSignPwd.addTextChangedListener(this);
        findViewById(R.id.activity_login_btn_signin).setOnClickListener(this);
        findViewById(R.id.activity_login_btn_forgot_pwd).setOnClickListener(this);
        findViewById(R.id.activity_login_btn_fb).setOnClickListener(this);
        findViewById(R.id.activity_login_btn_gl).setOnClickListener(this);

        //Join us part
        viewJoin = (LinearLayout)findViewById(R.id.activity_login_joinus_view);
        btnTapJoin = (TextView)findViewById(R.id.activity_login_tab_btn_joinus);
        btnTapJoin.setOnClickListener(this);
        tapJoin = (LinearLayout)findViewById(R.id.activity_login_tab_join);
        etJoinFName = (EditText)findViewById(R.id.activity_login_et_first_name);
        etJoinFName.addTextChangedListener(this);
        etJoinLName = (EditText)findViewById(R.id.activity_login_et_last_name);
        etJoinLName.addTextChangedListener(this);
        etJoinPhoneNumber = (EditText)findViewById(R.id.activity_login_et_number);
        etJoinPhoneNumber.addTextChangedListener(this);
        etJoinEmail = (EditText)findViewById(R.id.activity_login_et_email);
        etJoinEmail.addTextChangedListener(this);
        etJoinPwd = (EditText)findViewById(R.id.activity_login_et_password);
        etJoinPwd.addTextChangedListener(this);
        etJoinPsscode = (EditText)findViewById(R.id.activity_login_et_passcode);
        etJoinPsscode.addTextChangedListener(this);
        findViewById(R.id.activity_login_btn_sign_up).setOnClickListener(this);

        currentTab = 0;
        switchTab();
        setupUI(findViewById(R.id.parent_view));

        setupFB();
        setupGoogle();
    }

    private void setupGoogle() {
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestEmail()
                .requestId()
                .requestProfile()
                .build();
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .enableAutoManage(this, this)
                .addApi(Auth.GOOGLE_SIGN_IN_API, gso)
                .build();
    }
    private void setupFB() {
        callbackManager = CallbackManager.Factory.create();
        loginButton.setReadPermissions(Arrays.asList("public_profile","email","user_friends"));
        loginButton.registerCallback(callbackManager, new FacebookCallback<LoginResult>() {
            @Override
            public void onSuccess(LoginResult loginResult) {

                graphRequest(loginResult.getAccessToken());

            }

            @Override
            public void onCancel() {

            }

            @Override
            public void onError(FacebookException error) {

            }
        });


        LoginManager.getInstance().registerCallback(callbackManager, new FacebookCallback<LoginResult>() {
            @Override
            public void onSuccess(LoginResult loginResult) {

                graphRequest(loginResult.getAccessToken());
            }

            @Override
            public void onCancel() {
                Log.e("dima", "onCancel");
            }

            @Override
            public void onError(FacebookException error) {
                Log.e("dima", "dd -- " + error);
            }
        });
    }

    public void graphRequest(AccessToken token){
        GraphRequest request = GraphRequest.newMeRequest(token,new GraphRequest.GraphJSONObjectCallback(){

            @Override
            public void onCompleted(JSONObject object, GraphResponse response) {

                try {
                    String email = object.getString("email");
                    Toast.makeText(getApplicationContext(),object.toString(),Toast.LENGTH_LONG).show();

                    getLoginEvents(email);
                } catch (JSONException e) {
                    e.printStackTrace();
                }



            }
        });

        Bundle b = new Bundle();
        b.putString("fields","id,email,first_name,last_name,picture.type(large)");
        request.setParameters(b);
        request.executeAsync();

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == GOOGLE_SIGN_IN) { //google+
            GoogleSignInResult result = Auth.GoogleSignInApi.getSignInResultFromIntent(data);
            handleSignInResult(result);
            return;
        }

        callbackManager.onActivityResult(requestCode,resultCode,data);
    }

    private void switchTab() {
        if (currentTab == 0) { // sign in view
            viewSign.setVisibility(View.VISIBLE);
            viewJoin.setVisibility(View.GONE);
            btnTapSign.setAlpha(1.0f);
            btnTapJoin.setAlpha(0.5f);
            tapSign.setVisibility(View.VISIBLE);
            tapSign.setAlpha(0.0f);
            tapSign.animate()
                    .alpha(1.0f);
            tapJoin.setVisibility(View.GONE);
        } else if (currentTab == 1) { // join us view
            viewSign.setVisibility(View.GONE);
            viewJoin.setVisibility(View.VISIBLE);
            btnTapSign.setAlpha(0.5f);
            btnTapJoin.setAlpha(1.0f);
            tapSign.setVisibility(View.GONE);
            tapJoin.setVisibility(View.VISIBLE);
            tapJoin.setAlpha(0.0f);
            tapJoin.animate()
                    .alpha(1.0f);
        }
    }

    private void initializeView() {
        etJoinFName.setText("");
        etJoinLName.setText("");
        etJoinPhoneNumber.setText("");
        etJoinEmail.setText("");
        etJoinPwd.setText("");
        etJoinPsscode.setText("");
        etSignEmail.setText("");
        etSignPwd.setText("");

    }

    public void hideSoftKeyboard() {
        try {
            InputMethodManager inputMethodManager =
                    (InputMethodManager) LoginActivity.this.getSystemService(
                            Activity.INPUT_METHOD_SERVICE);
            inputMethodManager.hideSoftInputFromWindow(
                    LoginActivity.this.getCurrentFocus().getWindowToken(), 0);
        }catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void setupUI(View view) {
        // Set up touch listener for non-text box views to hide keyboard.
        if (!(view instanceof EditText)) {
            view.setOnTouchListener(new View.OnTouchListener() {
                public boolean onTouch(View v, MotionEvent event) {
                    hideSoftKeyboard();
                    return false;
                }
            });
        }

        //If a layout container, iterate over children and seed recursion.
        if (view instanceof ViewGroup) {
            for (int i = 0; i < ((ViewGroup) view).getChildCount(); i++) {
                View innerView = ((ViewGroup) view).getChildAt(i);
                setupUI(innerView);
            }
        }
    }
    @Override
    protected void onResume() {
        super.onResume();

        initializeView();
        currentTab = 0;
        switchTab();

        if (!preferences.getEmail().isEmpty()) {
            goToHome();
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
    }

    @Override
    public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

    }

    @Override
    public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

    }

    @Override
    public void afterTextChanged(Editable editable) {

    }

    @Override
    public void onClick(View v) {
        switch ( v.getId() ) {
            case R.id.activity_login_tab_btn_signin:
                currentTab = 0;
                switchTab();

                break;
            case R.id.activity_login_tab_btn_joinus:
                currentTab = 1;
                switchTab();

                break;
            case R.id.activity_login_btn_signin:
                signIn();
                break;
            case R.id.activity_login_btn_sign_up:
                signUp();
                break;
            case R.id.activity_login_btn_forgot_pwd:
                openPwdDialog();
                break;
            case R.id.activity_login_btn_fb:
                loginWithFB();
                break;
            case R.id.activity_login_btn_gl:
                loginWithGoogle();
                break;
            default:
                throw new RuntimeException("Unhandled action");
        }
    }

    private void loginWithFB() {
        LoginManager.getInstance().logOut();
        LoginManager.getInstance().logInWithReadPermissions(LoginActivity.this,Arrays.asList("public_profile","email","user_friends"));
    }

    private void loginWithGoogle() {
        Intent signInIntent = Auth.GoogleSignInApi.getSignInIntent(mGoogleApiClient);
        startActivityForResult(signInIntent, GOOGLE_SIGN_IN);
    }

    private void handleSignInResult(GoogleSignInResult result) {
        if (result.isSuccess()) {
            // Signed in successfully, show authenticated UI.
            GoogleSignInAccount acct = result.getSignInAccount();
            Log.e("dima", acct.getEmail() + "-" + acct.getPhotoUrl() + "-" + acct.getDisplayName() + "-" + acct.getFamilyName() + "-" + acct.getGivenName());

            getLoginEvents(acct.getEmail());
        }
    }

    private void signUp() {
        String firstName = etJoinFName.getText().toString();
        String lastName = etJoinLName.getText().toString();
        String phoneNum = etJoinPhoneNumber.getText().toString();
        final String email = etJoinEmail.getText().toString();
        final String password = etJoinPwd.getText().toString();
        String passcode = etJoinPsscode.getText().toString();

        if (firstName.isEmpty() || lastName.isEmpty() || phoneNum.isEmpty() || email.isEmpty() || password.isEmpty()) {
            showAlertDialog(LoginActivity.this, getResources().getString(R.string.loggin_alert_title), getString(R.string.check_all_fileds));
            return;
        }

        if (passcode.isEmpty()) {
            showAlertDialog(LoginActivity.this, getResources().getString(R.string.loggin_alert_title), getString(R.string.check_email_box));
            return;
        }

        startIndicator();

        ApiInterface apiService = ApiClient.getClient().create(ApiInterface.class);


        Call<UserResponse> call = apiService.doRegister(firstName, lastName, email, password, phoneNum, passcode, preferences.getDeviceId(), "Android");
        call.enqueue(new Callback<UserResponse>() {
            @Override
            public void onResponse(Call<UserResponse> call, Response<UserResponse> response) {
                stopIndicator();

                if (!response.isSuccessful()){

                    onConnectionFailed(LoginActivity.this);

                    return;
                }

                if (response.body().isSuccess == 0) {
                    String message = response.body().message;
                    showAlertDialog(LoginActivity.this, getResources().getString(R.string.loggin_alert_title), message);
                    return;
                }

                OUser users = response.body().users.get(0);
                preferences.setEmail(email);
                preferences.setPwd(password);
                preferences.setName(users.firstName);
                preferences.setSurname(users.lastName);


                Toast.makeText(LoginActivity.this, getString(R.string.sign_up_successfully), Toast.LENGTH_LONG).show();
                getLoginEvents(email);
            }

            @Override
            public void onFailure(Call<UserResponse> call, Throwable t) {
                stopIndicator();

                onNetworkFailed(LoginActivity.this);
            }
        });
    }

    private void signIn (){
        final String email = etSignEmail.getText().toString();
        final String password = etSignPwd.getText().toString();
        if (email.equals("") || password.equals("")) {
            showAlertDialog(LoginActivity.this, getResources().getString(R.string.loggin_alert_title), getString(R.string.check_email_pwd));
            return;
        }

        startIndicator();

        ApiInterface apiService = ApiClient.getClient().create(ApiInterface.class);

        Call<UserResponse> call = apiService.doLogin(email, password, preferences.getDeviceId(), "Android");
        call.enqueue(new Callback<UserResponse>() {
            @Override
            public void onResponse(Call<UserResponse> call, Response<UserResponse> response) {
                stopIndicator();

                if (!response.isSuccessful()){

                    onConnectionFailed(LoginActivity.this);

                    return;
                }

                if (response.body().isSuccess == 0) {
                    String message = response.body().message;
                    showAlertDialog(LoginActivity.this, getResources().getString(R.string.loggin_alert_title), message);
                    return;
                }

                OUser users = response.body().users.get(0);

                preferences.setEmail(email);
                preferences.setPwd(password);
                preferences.setName(users.firstName);
                preferences.setSurname(users.lastName);
                preferences.setProfilePicture(users.picture);
                preferences.setUserType(users.userType);

                Toast.makeText(LoginActivity.this, String.format(getString(R.string.logged_in_successfully), users.email), Toast.LENGTH_LONG).show();
                getLoginEvents(email);
            }

            @Override
            public void onFailure(Call<UserResponse> call, Throwable t) {
                stopIndicator();

                onNetworkFailed(LoginActivity.this);
            }
        });
    }

    private void openPwdDialog(){
        LayoutInflater inflater = LayoutInflater.from(LoginActivity.this);
        @SuppressLint("InflateParams") View subView = inflater.inflate(R.layout.dialog_forgot_pwd, null);
        final EditText subEditText = (EditText)subView.findViewById(R.id.dialog_forgot_pwd_et_email);

        AlertDialog.Builder builder = new AlertDialog.Builder(this, R.style.MyAlertDialogStyle);

        builder.setTitle("Forgot Password?");
        builder.setView(subView);

        builder.setPositiveButton("OK",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        String email = subEditText.getText().toString();
                        if (!email.isEmpty()) {
                            startIndicator();

                            ApiInterface apiService = ApiClient.getClient().create(ApiInterface.class);
                            Call<UserResponse> call = apiService.doForgot(email);
                            call.enqueue(new Callback<UserResponse>() {
                                @Override
                                public void onResponse(Call<UserResponse> call, Response<UserResponse> response) {
                                    stopIndicator();

                                    if (!response.isSuccessful()){
                                        onConnectionFailed(LoginActivity.this);

                                        return;
                                    }

                                    String message = response.body().message;
                                    showAlertDialog(LoginActivity.this, getResources().getString(R.string.loggin_alert_title), message);
                                }

                                @Override
                                public void onFailure(Call<UserResponse> call, Throwable t) {
                                    stopIndicator();

                                    onNetworkFailed(LoginActivity.this);
                                }
                            });
                        }
                        dialog.dismiss();
                    }
                });
        builder.show();
    }


    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }

    private void goToHome(){

        startActivity(new Intent(this, MainActivity.class));
        overridePendingTransition(R.anim.slide_left_to_right_in, R.anim.slide_left_to_right_out);
    }

    private boolean checkPlayServices() {
        GoogleApiAvailability apiAvailability = GoogleApiAvailability.getInstance();
        int resultCode = apiAvailability.isGooglePlayServicesAvailable(this);
        if (resultCode != ConnectionResult.SUCCESS) {
            if (apiAvailability.isUserResolvableError(resultCode)) {
                apiAvailability.getErrorDialog(this, resultCode, PLAY_SERVICES_RESOLUTION_REQUEST)
                        .show();
            } else {
                Log.i("Dima", "This device is not supported.");
                finish();
            }
            return false;
        }
        return true;
    }

    private void getLoginEvents(String email) {
        startIndicator();

        ApiInterface apiService = ApiClient.getClient().create(ApiInterface.class);

        Call<PushEventResponse> call = apiService.getLoginEvents(email);
        call.enqueue(new Callback<PushEventResponse>() {
            @Override
            public void onResponse(Call<PushEventResponse> call, Response<PushEventResponse> response) {
                stopIndicator();

                if (!response.isSuccessful()) {
                    return;
                }

                if (response.body().isSuccess == 0) {
                    return;
                }
                saveEvents(response.body().events);

            }

            @Override
            public void onFailure(Call<PushEventResponse> call, Throwable t) {
                stopIndicator();

                onNetworkFailed(LoginActivity.this);
            }
        });


    }

    public void saveEvents(final List<PushEvent> lsEvent) {
        Realm realm = Realm.getDefaultInstance();

        try {
            realm.executeTransactionAsync(new Realm.Transaction() {
                @Override
                public void execute(Realm bgRealm) {
                    for (PushEvent pEvent : lsEvent) {
                        if (!isExistEvent(pEvent, bgRealm)) {
                            OEvent event = pEvent.eventInfo;
                            OEvent oEvent = bgRealm.createObject(OEvent.class);

                            oEvent.eventId = event.eventId;
                            oEvent.name = event.name;
                            oEvent.title = event.title;
                            oEvent.details = event.details;
                            oEvent.latitude = event.latitude;
                            oEvent.longitude = event.longitude;
                            oEvent.triggerId = event.triggerId;
                            oEvent.paId = event.paId;
                            oEvent.iconPath = event.iconPath;
                            oEvent.imagePath = event.imagePath;
                            oEvent.slideImages = event.slideImages;
                            oEvent.videourl = event.videourl;
                            oEvent.maxReservableCount = event.maxReservableCount;
                            oEvent.currentReservedCount = event.currentReservedCount;
                            oEvent.eventDate = event.eventDate;
                            oEvent.reservation = event.reservation;
                            oEvent.payment = event.payment;
                            oEvent.amount = event.amount;
                            oEvent.claimeoffer = event.claimeoffer;
                            oEvent.pushable = event.pushable;
                            oEvent.location = event.location;
                            oEvent.category = event.category;

                            oEvent.followerEmail = pEvent.email;
                            oEvent.firstName = pEvent.firstname;
                            oEvent.lastName = pEvent.lastname;
                            Long tsLong = System.currentTimeMillis()/1000;
                            oEvent.readDate = tsLong.toString();
                        }
                    }
                }
            }, new Realm.Transaction.OnSuccess() {
                @Override
                public void onSuccess() {
                    goToHome();
                }
            }, new Realm.Transaction.OnError() {
                @Override
                public void onError(Throwable error) {
                    goToHome();
                }
            });
        } finally {
            realm.close();
        }

    }

    private boolean isExistEvent(PushEvent pushEvent, Realm bgRealm) {

        RealmResults<OEvent> lsEvent = bgRealm.where(OEvent.class).equalTo("eventId", pushEvent.eventInfo.eventId).findAll();

        if (lsEvent == null || lsEvent.size() == 0) {
            return false;
        }

        OEvent event = pushEvent.eventInfo;
        Boolean found = false;
        for (OEvent oEvent : lsEvent) {
            if (!pushEvent.email.isEmpty() && !pushEvent.email.equals(oEvent.followerEmail)) {
                continue;
            }

            oEvent.eventId = event.eventId;
            oEvent.name = event.name;
            oEvent.title = event.title;
            oEvent.details = event.details;
            oEvent.latitude = event.latitude;
            oEvent.longitude = event.longitude;
            oEvent.triggerId = event.triggerId;
            oEvent.paId = event.paId;
            oEvent.iconPath = event.iconPath;
            oEvent.imagePath = event.imagePath;
            oEvent.slideImages = event.slideImages;
            oEvent.videourl = event.videourl;
            oEvent.maxReservableCount = event.maxReservableCount;
            oEvent.currentReservedCount = event.currentReservedCount;
            oEvent.eventDate = event.eventDate;
            oEvent.reservation = event.reservation;
            oEvent.payment = event.payment;
            oEvent.amount = event.amount;
            oEvent.claimeoffer = event.claimeoffer;
            oEvent.pushable = event.pushable;
            oEvent.location = event.location;
            oEvent.category = event.category;

            oEvent.followerEmail = event.followerEmail;
            oEvent.firstName = event.firstName;
            oEvent.lastName = event.lastName;

            found = true;
        }

        return found;
    }

}
