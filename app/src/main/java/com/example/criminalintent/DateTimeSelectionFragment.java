package com.example.criminalintent;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import androidx.fragment.app.DialogFragment;

import java.util.Date;

public class DateTimeSelectionFragment extends DialogFragment {
    private Button Date_Button;
    private Button Time_Button;

    private boolean hasChooseDate;

    //DateTimeSelectionListener listener;

    /* The activity that creates an instance of this dialog fragment must
     * implement this interface in order to receive event callbacks.
     * Each method passes the DialogFragment in case the host needs to query it. */
    /*public interface DateTimeSelectionListener {
        public void onDialogTimeButtonClick(DialogFragment dialog);
        public void onDialogDateButtonClick(DialogFragment dialog);
    }
    // Override the Fragment.onAttach() method to instantiate the NoticeDialogListener
    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        // Verify that the host activity implements the callback interface
        try {
            // Instantiate the NoticeDialogListener so we can send events to the host
            listener = (DateTimeSelectionListener) context;
        } catch (ClassCastException e) {
            // The activity doesn't implement the interface, throw exception
            throw new ClassCastException(getParentFragment().toString()
                    + " must implement NoticeDialogListener");
        }
    }*/
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        //mDate = (Date)getArguments().getSerializable(CrimeFragment.EXTRA_DATE);
        View v = getActivity().getLayoutInflater().inflate(R.layout.selection_dialog, null);
        Date_Button = (Button)v.findViewById(R.id.Change_Date_Button);
        Date_Button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                hasChooseDate = true;
                sendResult();
            }
        });
        Time_Button = (Button)v.findViewById(R.id.Change_Time_Button);
        Time_Button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                hasChooseDate = false;
                sendResult();
            }
        });
        return new AlertDialog.Builder(getActivity())
                .setView(v)
                .create();
    }

    private void sendResult(){
        Intent i = new Intent();
        i.putExtra("hasChooseDate", hasChooseDate);
        if(getTargetFragment()!=null){
            getTargetFragment().onActivityResult(getTargetRequestCode(), -1, i);
            dismiss();
        }
    }
}
