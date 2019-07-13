package com.hcrpurdue.jason.hcrhousepoints.Fragments;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatDialogFragment;

import com.hcrpurdue.jason.hcrhousepoints.R;

public class PasswordResetDialogFragment extends AppCompatDialogFragment {

    private EditText passwordEntryEditText;
    private String userEmail = "";
    private boolean isValidEmail = false;

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

        LayoutInflater inflater = getActivity().getLayoutInflater();
        View view = inflater.inflate(R.layout.fragment_dialog_layout_reset_pass, null);

        builder.setView(view)
                .setTitle("Reset Password")
                .setNegativeButton("Cancel", (dialog, which) -> {
                    dismiss();
                })
                .setPositiveButton("Ok", (dialog, which) -> {

                    userEmail = passwordEntryEditText.getText().toString().trim();
                    if(TextUtils.isEmpty(userEmail)) {
                        Toast.makeText(getContext(), "Please enter an email address", Toast.LENGTH_SHORT).show();
                        return;
                    }

                    isValidEmail = true;
                    dismiss();
                    /* TODO: Implement check to see if email is in Firebase */
                });
        return builder.create();
    }

    public boolean emailValid() {
        return isValidEmail;
    }

    public String getUserEmail() {
        return userEmail;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return super.onCreateView(inflater, container, savedInstanceState);
    }
}
