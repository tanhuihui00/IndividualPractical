package my.edu.utar.individualpractical;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

public class Top25PlayerDialogForm extends DialogFragment {

    public interface CustomListener{
        void onMyCustomAction(CustomObject co);
    }

    private CustomListener mListener;

    public void setMyCustomListener(CustomListener listener){
        mListener = listener;
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

        LayoutInflater inflater = getActivity().getLayoutInflater();
        View formView = inflater.inflate(R.layout.activity_top25_player_form, null);

        // Inflate and set the layout for the dialog
        // Pass null as the parent view because its going in the dialog layout
        builder.setView(formView)
                .setPositiveButton("Submit", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        EditText name = formView.findViewById(R.id.winnerName);
                        if(name.getText().toString().matches("")){
                            Toast.makeText(getActivity(), "Enter your name", Toast.LENGTH_SHORT).show();
                        }else{
                            ActionPanel.winnerName = name.getText().toString();
                            Top25PlayerDialogForm.this.getDialog().dismiss();
                        }
                    }
                });

        return builder.create();
    }

    @Override
    public void onDismiss(DialogInterface dialog) {
        if(mListener != null){
            CustomObject o = new CustomObject();
            mListener.onMyCustomAction(o);
        }
        super.onDismiss(dialog);
    }
}
