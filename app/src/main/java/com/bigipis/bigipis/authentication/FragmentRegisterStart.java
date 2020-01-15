package com.bigipis.bigipis.authentication;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import com.bigipis.bigipis.MainActivity;
import com.bigipis.bigipis.R;
import com.bigipis.bigipis.bluetooth.FragmentBluetoothList;
import com.google.android.material.textfield.TextInputLayout;

import static com.bigipis.bigipis.authentication.FragmentRegisterFinish.TAG_EMAIL;
import static com.bigipis.bigipis.authentication.FragmentRegisterFinish.TAG_PASSWORD;

public class FragmentRegisterStart extends Fragment implements View.OnClickListener {

    private View myInflateView;
    private TextInputLayout textInputLayoutEmail, textInputLayoutPassword, textInputLayoutRepeatPassword;
    private EditText mEmailField, mPasswordField, mRepeatPasswordField;
    private Button buttonContinue;
    private CheckBox checkBoxIsNakers;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        myInflateView = inflater.inflate(R.layout.fragment_register_start, container,false);

        textInputLayoutEmail = myInflateView.findViewById(R.id.textInputLayoutEmailRegister);
        textInputLayoutPassword = myInflateView.findViewById(R.id.textInputLayoutPasswordRegister);
        textInputLayoutRepeatPassword = myInflateView.findViewById(R.id.textInputLayoutRepeatPasswordRegister);

        mEmailField = myInflateView.findViewById(R.id.field_email_register);
        mPasswordField = myInflateView.findViewById(R.id.field_password_register);
        mRepeatPasswordField = myInflateView.findViewById(R.id.field_repeat_password_register);

        buttonContinue = myInflateView.findViewById(R.id.buttonContinue);
        buttonContinue.setOnClickListener(this);

        checkBoxIsNakers = myInflateView.findViewById(R.id.checkBoxIsNakers);
        // Create Bluetooth connection with Arduino if checkBoxIsNakers.isChecked()!

        return myInflateView;
    }

    @Override
    public void onClick(View view) {
        continueRegistration(mEmailField.getText().toString(), mPasswordField.getText().toString());
    }

    private void continueRegistration(String email, String password) {
        if (!validateForm()) {
            return;
        }
        final Bundle bundle = new Bundle();
        bundle.putString(TAG_EMAIL,email);
        bundle.putString(TAG_PASSWORD, password);
        if (!((MainActivity) getActivity()).isNakersConnected && checkBoxIsNakers.isChecked()) {
            AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
            builder.setTitle("Хотите подключить Nakers прямо сейчас?");
            builder.setPositiveButton("ОК", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int arg1) {
                    dialog.cancel();
                    FragmentTransaction ft = getActivity().getSupportFragmentManager().beginTransaction();
                    Fragment fragment = new FragmentBluetoothList();
                    ft.replace(R.id.content_frame, fragment);
                    ft.addToBackStack(null);
                    ft.commit();
                }
            });
            builder.setNegativeButton("Отмена", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int arg1) {
                    dialog.cancel();
                    FragmentTransaction ft = getActivity().getSupportFragmentManager().beginTransaction();
                    Fragment fragment = new FragmentRegisterFinish();
                    fragment.setArguments(bundle);
                    ft.replace(R.id.content_frame, fragment);
                    ft.addToBackStack(null);
                    ft.commit();
                }
            });
            builder.setCancelable(false);
            builder.show();
        }
        else {
            FragmentTransaction ft2 = getActivity().getSupportFragmentManager().beginTransaction();
            Fragment fragment2 = new FragmentRegisterFinish();
            fragment2.setArguments(bundle);
            ft2.replace(R.id.content_frame, fragment2)
                .addToBackStack(null)
                .commit();
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

        String repeatPassword = mRepeatPasswordField.getText().toString();
        if (!repeatPassword.equals(password)) {
            textInputLayoutRepeatPassword.setErrorEnabled(true);
            textInputLayoutRepeatPassword.setError("Пароли не совпадают");
            valid = false;
        } else {
            textInputLayoutRepeatPassword.setErrorEnabled(false);
        }
        return valid;
    }
}
