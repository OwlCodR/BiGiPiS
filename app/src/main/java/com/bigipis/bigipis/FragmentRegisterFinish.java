package com.bigipis.bigipis;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.VisibleForTesting;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.Objects;

import static com.bigipis.bigipis.MainActivity.fDatabase;
import static com.bigipis.bigipis.MainActivity.firebaseAuth;
import static com.bigipis.bigipis.MainActivity.updateUI;
import static com.bigipis.bigipis.MainActivity.user;

public class FragmentRegisterFinish extends Fragment implements View.OnClickListener, SeekBar.OnSeekBarChangeListener  {
    private static final String TAG = "EmailPassword";
    public static final String TAG_PASSWORD = "Password";
    public static final String TAG_EMAIL = "Email";
    public static final String TAG_ISNAKERS = "IsNakers";

    private AlertDialog.Builder builder;
    private View myInflateView;
    private Button buttonRegister;
    private SeekBar seekBarWeight, seekBarHeight;
    private TextInputLayout textInputLayoutNickname;
    private EditText editTextNickname;
    private ImageView imageViewUserIcon;
    private TextView textViewHeight, textViewWeight;
    private int iconId;
    private View dialogView;
    private ImageButton imageButton1, imageButton2, imageButton3, imageButton4, imageButton5, imageButton6;


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        myInflateView = inflater.inflate(R.layout.fragment_register_finish, container, false);

        textViewHeight = myInflateView.findViewById(R.id.textViewHeight);
        textViewWeight = myInflateView.findViewById(R.id.textViewWeight);

        textInputLayoutNickname = myInflateView.findViewById(R.id.textInputLayoutNicknameRegister);
        editTextNickname = myInflateView.findViewById(R.id.field_nickname_register);

        seekBarWeight = myInflateView.findViewById(R.id.seekBarWeight);
        seekBarWeight.setOnSeekBarChangeListener(this);
        seekBarHeight = myInflateView.findViewById(R.id.seekBarHeight);
        seekBarHeight.setOnSeekBarChangeListener(this);

        buttonRegister = myInflateView.findViewById(R.id.buttonRegister);
        buttonRegister.setOnClickListener(this);

        imageViewUserIcon = myInflateView.findViewById(R.id.imageViewNavUserIcon);
        imageViewUserIcon.setOnClickListener(this);

        LayoutInflater dialogInflater = (LayoutInflater) getActivity().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        if (dialogInflater != null)
            dialogView = dialogInflater.inflate(R.layout.dialog_change_default_icon, (ViewGroup) getActivity().findViewById(R.id.linearLayoutChangeIcon));
        else Log.d(TAG, "dialogInflater == null");

        imageButton1 = dialogView.findViewById(R.id.imageButton1);
        imageButton1.setOnClickListener(this);
        imageButton2 = dialogView.findViewById(R.id.imageButton2);
        imageButton2.setOnClickListener(this);
        imageButton3 = dialogView.findViewById(R.id.imageButton3);
        imageButton3.setOnClickListener(this);
        imageButton4 = dialogView.findViewById(R.id.imageButton4);
        imageButton4.setOnClickListener(this);
        imageButton5 = dialogView.findViewById(R.id.imageButton5);
        imageButton5.setOnClickListener(this);
        imageButton6 = dialogView.findViewById(R.id.imageButton6);
        imageButton6.setOnClickListener(this);

        iconId = 1;
        return myInflateView;
    }

    private void createAccount(final String nickname) {
        Log.d(TAG, "Start creating Account");
        if (!validateNickname(nickname)) {
            return;
        }
        showProgressDialog();

        Bundle bundle = getArguments();
        user.setIconId(iconId);
        user.setNickname(nickname);

        String email = bundle.getString(TAG_EMAIL);
        String password = bundle.getString(TAG_PASSWORD);

        if (email != null && password != null) {
            firebaseAuth = FirebaseAuth.getInstance();
            firebaseAuth.createUserWithEmailAndPassword(email, password)
                    .addOnCompleteListener(getActivity(), new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if (task.isSuccessful()) {
                                Log.d(TAG, "Account has been created");
                                fDatabase = FirebaseFirestore.getInstance();
                                fDatabase.collection("users").document(firebaseAuth.getUid()).set(user);
                                updateUI();
                                // @TODO Go somewhere after registration
                            } else {
                                Toast.makeText(getActivity(), Objects.requireNonNull(task.getException()).getMessage() + "", Toast.LENGTH_LONG).show();
                                Log.d(TAG, "Register account failed", task.getException());
                            }
                            hideProgressDialog();
                        }
                    });
        }
    }

    private boolean isNicknameOriginal(String nickname) {
        final boolean[] valid = {true};
        fDatabase = FirebaseFirestore.getInstance();
        CollectionReference docRef = fDatabase.collection("users");
        docRef.whereEqualTo("nickname", nickname)
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            valid[0] = false;
                        } else {
                            Log.d(TAG, "Error getting documents: ", task.getException());
                        }
                    }
                });
        return valid[0];
    }

    private boolean validateNickname(String nickname) {
        if (TextUtils.isEmpty(nickname)) {
            textInputLayoutNickname.setErrorEnabled(true);
            textInputLayoutNickname.setError("Заполните поле");
            return false;
        } else {
            textInputLayoutNickname.setErrorEnabled(false);
        }

        if (nickname.length() < 3) {
            textInputLayoutNickname.setErrorEnabled(true);
            textInputLayoutNickname.setError("Ник должен содержать не менее 3 символов");
            return false;
        } else {
            textInputLayoutNickname.setErrorEnabled(false);
        }

        if (!isNicknameOriginal(nickname)) {
            textInputLayoutNickname.setErrorEnabled(true);
            textInputLayoutNickname.setError("Этот ник уже занят. Попробуйте другой.");
            return false;
        } else {
            textInputLayoutNickname.setErrorEnabled(false);
        }

        return true;
    }

    @Override
    public void onClick(View view) {
        switch(view.getId()) {
            case R.id.buttonRegister: {
                createAccount(editTextNickname.getText().toString());
                break;
            }
            case R.id.imageViewNavUserIcon:
            {
                builder = new AlertDialog.Builder(getActivity());
                builder.setView(dialogView);

                builder.setNegativeButton("Отмена", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                });

                builder.setPositiveButton("Ок", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                    }
                }).create();
                builder.show();
                break;
            }
            case R.id.imageButton1: {
                iconId = 1;
                break;
            }
            case R.id.imageButton2: {
                iconId = 2;
                break;
            }
            case R.id.imageButton3: {
                iconId = 3;
                break;
            }
            case R.id.imageButton4: {
                iconId = 4;
                break;
            }
            case R.id.imageButton5: {
                iconId = 5;
                break;
            }
            case R.id.imageButton6: {
                iconId = 6;
                break;
            }
        }
        setIconActiveColor(iconId);
    }

    private void setIconActiveColor(int numberButton) {
        imageButton1.setColorFilter(ContextCompat.getColor(getActivity(),
                R.color.common_google_signin_btn_text_light_default));
        imageButton2.setColorFilter(ContextCompat.getColor(getActivity(),
                R.color.common_google_signin_btn_text_light_default));
        imageButton3.setColorFilter(ContextCompat.getColor(getActivity(),
                R.color.common_google_signin_btn_text_light_default));
        imageButton4.setColorFilter(ContextCompat.getColor(getActivity(),
                R.color.common_google_signin_btn_text_light_default));
        imageButton5.setColorFilter(ContextCompat.getColor(getActivity(),
                R.color.common_google_signin_btn_text_light_default));
        imageButton6.setColorFilter(ContextCompat.getColor(getActivity(),
                R.color.common_google_signin_btn_text_light_default));

        switch (numberButton){
            case 1: {
                imageButton1.setColorFilter(ContextCompat.getColor(getActivity(),
                        android.R.color.transparent));
                break;
            }
            case 2: {
                imageButton2.setColorFilter(ContextCompat.getColor(getActivity(),
                        android.R.color.transparent));
                break;
            }
            case 3: {
                imageButton3.setColorFilter(ContextCompat.getColor(getActivity(),
                        android.R.color.transparent));
                break;
            }
            case 4: {
                imageButton4.setColorFilter(ContextCompat.getColor(getActivity(),
                        android.R.color.transparent));
                break;
            }
            case 5: {
                imageButton5.setColorFilter(ContextCompat.getColor(getActivity(),
                        android.R.color.transparent));
                break;
            }
            case 6: {
                imageButton6.setColorFilter(ContextCompat.getColor(getActivity(),
                        android.R.color.transparent));
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
    public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
        switch (seekBar.getId()) {
            case R.id.seekBarHeight: {
                textViewHeight.setText("Рост: " + (i+100) + " см");
                user.setHeight(i+100);
                Log.d("Seekbar", "Changed seekBarHeight: " + i);
                break;
            }
            case R.id.seekBarWeight: {
                textViewWeight.setText("Вес: " + (i+20) + " кг");
                user.setWeight(i+20);
                Log.d("Seekbar", "Changed textViewWeight: " + i);
                break;
            }
            default: Log.d("Seekbar", "\nonProgressChanged");
        }
    }

    @Override
    public void onStartTrackingTouch(SeekBar seekBar) {

    }

    @Override
    public void onStopTrackingTouch(SeekBar seekBar) {

    }

    @Override
    public void onResume() {
        super.onResume();
        ((AppCompatActivity) getActivity()).getSupportActionBar()
                .setTitle(R.string.register);
    }
}
