package com.uda_movie.popularmovies;

import android.app.Dialog;
import android.app.DialogFragment;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.AlertDialog;

import com.uda_movie.popularmovies.model.SortMethod;

public class SettingDialogFragment extends DialogFragment {
    private SortMethod newSortMethod;

    private SettingDialogtListener listener;

    //listener for sort method change
    public interface SettingDialogtListener {
        public void onSortMethodChange(SortMethod newSortMethod);
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        final SortMethod sortMethod = Utils.getSortMethod(getActivity().getApplicationContext());
        builder.setTitle(R.string.text_sort_by);
        builder.setSingleChoiceItems(R.array.sort_by, sortMethod.getValue(), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                newSortMethod = SortMethod.fromInt(i);
            }
        });
        builder.setPositiveButton(R.string.text_ok, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {

                if (newSortMethod != null && sortMethod != newSortMethod) {
                    updatePreference(newSortMethod);
                    //notify callback
                    listener.onSortMethodChange(newSortMethod);
                }
            }
        });

        return builder.create();

    }

    private void updatePreference(SortMethod sortMethod) {
        SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(getActivity().getApplicationContext());
        SharedPreferences.Editor editor = pref.edit();
        editor.putString(getString(R.string.sort_by), sortMethod.toString());
        editor.apply();
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        // Verify that the host activity implements the callback interface
        try {
            listener = (SettingDialogtListener) context;
        } catch (ClassCastException e) {
            throw new ClassCastException(context.toString()
                    + " must implement SettingDialogtListener");
        }
    }
}
