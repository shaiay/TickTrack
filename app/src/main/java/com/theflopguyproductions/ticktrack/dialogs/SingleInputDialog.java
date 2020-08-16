package com.theflopguyproductions.ticktrack.dialogs;

import android.app.Activity;
import android.app.Dialog;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.constraintlayout.widget.ConstraintLayout;

import com.theflopguyproductions.ticktrack.R;

import java.util.Objects;

public class SingleInputDialog extends Dialog {

    private Activity activity;
    public EditText inputText;
    public Button okButton, cancelButton;
    public TextView helperText, saveChangesText;
    private String currentLabel;

    public SingleInputDialog(Activity activity, String currentLabel){
        super(activity);
        this.activity = activity;
        this.currentLabel = currentLabel;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);

        View view = activity.getLayoutInflater().inflate(R.layout.dialog_text_item, new ConstraintLayout(activity), false);
        setContentView(view);
        Objects.requireNonNull(getWindow()).setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        getWindow().getAttributes().windowAnimations = R.style.createdDialog;

        initVariables(view);

    }

    private void initVariables(View view) {
        okButton = view.findViewById(R.id.labelDialogOkButton);
        cancelButton = view.findViewById(R.id.labelDialogCancelButton);
        inputText = view.findViewById(R.id.labelDialogInputText);
        helperText = view.findViewById(R.id.labelDialogHelpText);
        inputText.setHint(currentLabel);
        saveChangesText = findViewById(R.id.labelDialogSaveChangesText);
    }

}
