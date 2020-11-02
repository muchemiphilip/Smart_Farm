package com.muchemi.smartfarm.activities;

import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.muchemi.smartfarm.adapters.RecyclerViewAdapter;
import com.muchemi.smartfarm.util.PreferenceKeys;

public class CropsFragment extends Fragment {

    private static final String TAG = "CropsFragment";

    RecyclerView recyclerView;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(
                R.layout.fragment, container, false);
        return rootView;
    }
    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        isFirstLogin();

        String[] items = getResources().getStringArray(R.array.Crops);
        RecyclerViewAdapter adapter = new RecyclerViewAdapter(items);
        recyclerView = (RecyclerView) view.findViewById(R.id.recycler_view);
        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext());
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setAdapter(adapter);

    }

    private void isFirstLogin() {

        Log.d(TAG,"isFirstLogin: checking if this is the first time to login");
        final SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(getActivity());
        boolean isFirstLogin = preferences.getBoolean(PreferenceKeys.FIRST_TIME_LOGIN, true);

        if (isFirstLogin){
            Log.d(TAG, "isFirstLogin: launching alert dialog");

            //Launch the info dialog for first-time-users
            AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(requireActivity());
            alertDialogBuilder.setMessage(getString(R.string.first_time_user_message));
            alertDialogBuilder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    Log.d(TAG, "onClick: closing dialog");
                    // now that the user has logged in, save it to shared preferences so the dialog won't
                    // pop up again
                    SharedPreferences.Editor editor = preferences.edit();
                    editor.putBoolean(PreferenceKeys.FIRST_TIME_LOGIN, false);
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.GINGERBREAD) {
                        editor.apply();
                    }
                    dialogInterface.dismiss();
                }
            });
            alertDialogBuilder.setTitle("Welcome To SmartFarm!!!! ");
            alertDialogBuilder.setIcon(R.drawable.tabian_dating);
            AlertDialog alertDialog = alertDialogBuilder.create();
            alertDialog.show();
        }
    }

}