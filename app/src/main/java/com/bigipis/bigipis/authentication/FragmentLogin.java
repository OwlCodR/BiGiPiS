package com.bigipis.bigipis.authentication;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.VisibleForTesting;
import androidx.fragment.app.Fragment;

import com.bigipis.bigipis.MainActivity;
import com.bigipis.bigipis.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

public class FragmentLogin extends Fragment implements View.OnClickListener {
    private static final String TAG = "EmailPassword";

    private EditText mEmailField;
    private EditText mPasswordField;
    private View myInflatedView;
    private TextInputLayout textInputLayoutEmail, textInputLayoutPassword;

    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        myInflatedView = inflater.inflate(R.layout.fragment_login, container,false);

        mEmailField = myInflatedView.findViewById(R.id.field_email_login);
        mPasswordField = myInflatedView.findViewById(R.id.field_password_login);

        textInputLayoutEmail = myInflatedView.findViewById(R.id.textInputLayoutEmailLogin);
        textInputLayoutPassword = myInflatedView.findViewById(R.id.textInputLayoutPasswordLogin);

        myInflatedView.findViewById(R.id.buttonLogin).setOnClickListener(this);
        myInflatedView.findViewById(R.id.buttonResetPasswordLogin).setOnClickListener(this);

        return myInflatedView;
    }

    private void signIn(String email, String password) {
        Log.d(TAG, "signIn:" + email);
        if (!validateForm()) {
            return;
        }
        showProgressDialog();
        FirebaseAuth.getInstance().signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(getActivity(), new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            Log.d(TAG, "Login success");
                            ((MainActivity) getActivity()).updateUI();
                        } else {
                            Log.e(TAG, "Login fail", task.getException());
                            Toast.makeText(getActivity(), "Не удалось войти!\nПроверьте подкючение к сети", Toast.LENGTH_LONG).show();
                        }
                        hideProgressDialog();
                    }
                });
    }

    private void resetPassword(final String email) {
        if (TextUtils.isEmpty(email)) {
            textInputLayoutEmail.setErrorEnabled(true);
            textInputLayoutEmail.setError("Заполните поле");
        } else {
            textInputLayoutEmail.setErrorEnabled(false);
            showProgressDialog();
            FirebaseAuth.getInstance().sendPasswordResetEmail(email)
                    .addOnCompleteListener(getActivity(), new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful()) {
                                Snackbar.make(myInflatedView, "Запрос на сброс пароля был отправлен на почту, если она зарегистрирована!", Snackbar.LENGTH_LONG)
                                        .setAction("Ок", null).show();
                            } else {
                                Log.e(TAG, "Error reset password",
                                        task.getException());
                                Snackbar.make(myInflatedView, "Ошибка! Возможно, вы указали неверную почту!\nПопробуйте позже!", Snackbar.LENGTH_LONG)
                                        .setAction("Ок", null).show();
                            }
                            hideProgressDialog();
                        }
                    });
        }
    }

    private boolean validateForm() {
        boolean valid = true;

        String email = mEmailField.getText().toString();
        if (TextUtils.isEmpty(email)) {
            textInputLayoutEmail.setErrorEnabled(true);
            textInputLayoutEmail.setError("Заполните поле");
            valid = false;
        } else {
            textInputLayoutEmail.setErrorEnabled(false);
        }

        String password = mPasswordField.getText().toString();
        if (TextUtils.isEmpty(password)) {
            textInputLayoutPassword.setErrorEnabled(true);
            textInputLayoutPassword.setError("Заполните поле");
            valid = false;
        } else {
            textInputLayoutPassword.setErrorEnabled(false);
        }

        if (password.length() < 6) {
            textInputLayoutPassword.setErrorEnabled(true);
            textInputLayoutPassword.setError("Пароль должен содержать не менее 6 символов");
            valid = false;
        } else {
            textInputLayoutPassword.setErrorEnabled(false);
        }

        return valid;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.buttonLogin: {
                signIn(mEmailField.getText().toString(), mPasswordField.getText().toString());
                break;
            }
            case R.id.buttonResetPasswordLogin: {
                resetPassword(mEmailField.getText().toString());
                break;
            }
        }

    }

    @VisibleForTesting
    public ProgressDialog mProgressDialog;
    public void showProgressDialog() {
        if (mProgressDialog == null) {
            mProgressDialog = new ProgressDialog(getActivity());
            mProgressDialog.setMessage(getString(R.string.loading));
            mProgressDialog.setIndeterminate(true);
        }

        mProgressDialog.show();
    }

    public void hideProgressDialog() {
        if (mProgressDialog != null && mProgressDialog.isShowing()) {
            mProgressDialog.dismiss();
        }
    }

    @Override
    public void onStop() {
        super.onStop();
        hideProgressDialog();
    }
}
