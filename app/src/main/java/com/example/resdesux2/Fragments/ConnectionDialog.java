package com.example.resdesux2.Fragments;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import com.example.resdesux2.R;
import com.example.resdesux2.Server.BoundActivity;

public class ConnectionDialog extends DialogFragment {
    public interface ConnectionDialogListener {
        void onDialogServerIPUpdated(DialogFragment dialog, String serverIP);
        String getServerIP();
    }

    private String originalServerIP;
    private ConnectionDialogListener listener;

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);

        // Verify that the host activity implements the callback interface
        try {
            // Instantiate the NoticeDialogListener so we can send events to the host
            listener = (ConnectionDialogListener) context;
            originalServerIP = ((ConnectionDialogListener) context).getServerIP();
            Log.e("TAG", "onAttach: " + originalServerIP);
        } catch (ClassCastException e) {
            // The activity doesn't implement the interface, throw exception
            throw new ClassCastException(context + " must implement NoticeDialogListener");
        }
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        // Use the Builder class for convenient dialog construction
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

        // Get the layout inflater
        LayoutInflater inflater = requireActivity().getLayoutInflater();
        final View view = inflater.inflate(R.layout.connection_dialog, null);
        ((EditText) view.findViewById(R.id.dialogServerIP)).setText(originalServerIP);

        // Inflate and set the layout for the dialog
        // Pass null as the parent view because its going in the dialog layout
            builder.setView(view)
                // Add action buttons
                .setPositiveButton("Update", (dialog, id) -> {
                    String serverIP = ((EditText) view.findViewById(R.id.dialogServerIP)).getText().toString();
                    if (!serverIP.equals(originalServerIP)) {
                        listener.onDialogServerIPUpdated(this, serverIP);
                    }
                })
                .setNegativeButton("Cancel", (dialog, id) -> {});
        return builder.create();
    }
}
