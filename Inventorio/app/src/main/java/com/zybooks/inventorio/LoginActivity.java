package com.zybooks.inventorio;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class LoginActivity extends AppCompatActivity {

    private UserAuth mAuthenticator;

    private EditText mUsernameText;
    private EditText mPasswordText;
    private TextView mAuthMessenger;

    private Button mRegisterButton;
    private Button mLoginButton;

    private boolean userPopulated = false;
    private boolean passPopulated = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login_activity);

        //Create an instance of the authenticator
        mAuthenticator = new UserAuth(this);

        //Get the references to the UI
        mUsernameText = findViewById(R.id.editTextUsername);
        mPasswordText = findViewById(R.id.editTextPassword);
        mRegisterButton = findViewById(R.id.registerButton);
        mLoginButton = findViewById(R.id.loginButton);
        mAuthMessenger = findViewById(R.id.authMessenger);

        //Set the buttons to inactive, will activate them when both user/pass are populated
        mRegisterButton.setEnabled(false);
        mLoginButton.setEnabled(false);

        //Register Text callbacks
        mUsernameText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                userPopulated = s.length() > 0;

                //Re-enable the buttons based on if the fields are populated
                mRegisterButton.setEnabled((userPopulated && passPopulated));
                mLoginButton.setEnabled((userPopulated && passPopulated));
            }

            @Override
            public void afterTextChanged(Editable s) {}
        });

        mPasswordText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                passPopulated = s.length() > 0;

                //Re-enable the buttons based on if the fields are populated
                mRegisterButton.setEnabled((userPopulated && passPopulated));
                mLoginButton.setEnabled((userPopulated && passPopulated));
            }

            @Override
            public void afterTextChanged(Editable s) {}
        });

        //Register the button callbacks
        mRegisterButton.setOnClickListener(this::RegisterButtonClick);
        mLoginButton.setOnClickListener(this::LoginButtonClick);

        //DEBUG - Skip sign-in
        //Intent intent = new Intent(LoginActivity.this, InventoryActivity.class);
        //startActivity(intent);
    }

    //Calls the AddUser function, and returns if successful.
    //TODO: Add support for various error codes to differentiate errors.
    public void RegisterButtonClick(View view)
    {
        //Hide the keyboard if its pulled up
        HideKeyboard();

        //Verify the fields have data
        if(mUsernameText.length() > 0 && mPasswordText.length() > 0)
        {
            boolean result = mAuthenticator.AddUser(mUsernameText.getText().toString(),
                    mPasswordText.getText().toString());

            if(result)
            {
                AuthMessengerSuccessMessage(getString(R.string.register_success));
                //Toast.makeText(this, R.string.register_success, Toast.LENGTH_SHORT).show();
            }
            else {
                AuthMessengerErrorMessage(getString(R.string.register_fail_user_exists));
                //Toast.makeText(this, R.string.register_fail_user_exists, Toast.LENGTH_SHORT).show();
            }
        }
    }

    //Checks if the user is valid and logs them in
    public void LoginButtonClick(View view)
    {
        //Hide the keyboard if its pulled up
        HideKeyboard();

        //Verify the fields have data
        if(mUsernameText.length() > 0 && mPasswordText.length() > 0)
        {
            boolean result = mAuthenticator.AuthenticateUser(mUsernameText.getText().toString(),
                    mPasswordText.getText().toString());

            if(result)
            {
                //TODO: PROCEED TO MAIN ACTIVITY
                AuthMessengerSuccessMessage("Logging in...");
                //Toast.makeText(this, "Logging in...", Toast.LENGTH_SHORT).show();

                //Start the intent
                Intent intent = new Intent(LoginActivity.this, InventoryActivity.class);
                startActivity(intent);
            }
            else {
                AuthMessengerErrorMessage(getString(R.string.auth_failed));
                //Toast.makeText(this, "FAIL", Toast.LENGTH_SHORT).show();
            }
        }
    }

    //Force hides the keyboard.
    private void HideKeyboard()
    {
        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);

        //Get the current focus
        View view = getCurrentFocus();

        //Hide the keyboard if the view exists
        if(view != null)
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
    }

    private void AuthMessengerErrorMessage(String message)
    {
        mAuthMessenger.setTextColor(ContextCompat.getColor(this, R.color.red));
        mAuthMessenger.setText(message);
        mAuthMessenger.setVisibility(View.VISIBLE);
    }

    private void AuthMessengerSuccessMessage(String message)
    {
        mAuthMessenger.setTextColor(ContextCompat.getColor(this, R.color.green));
        mAuthMessenger.setText(message);
        mAuthMessenger.setVisibility(View.VISIBLE);
    }
}