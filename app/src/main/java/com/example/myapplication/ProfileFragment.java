package com.example.myapplication;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.List;

public class ProfileFragment extends Fragment {

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_profile, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View v, @Nullable Bundle savedInstanceState) {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        String name = user != null && user.getDisplayName() != null ? user.getDisplayName() : "User";
        String email = user != null ? user.getEmail() : "";

        ((TextView) v.findViewById(R.id.profile_name)).setText(name);
        ((TextView) v.findViewById(R.id.profile_email)).setText(email);
        ((TextView) v.findViewById(R.id.profile_initials)).setText(initials(name));

        LinearLayout box = v.findViewById(R.id.profile_settings);
        List<DemoData.Setting> settings = DemoData.settings();
        for (int i = 0; i < settings.size(); i++) {
            DemoData.Setting s = settings.get(i);
            View row = getLayoutInflater().inflate(R.layout.item_setting, box, false);
            ((ImageView) row.findViewById(R.id.set_icon)).setImageResource(s.iconRes);
            ((TextView) row.findViewById(R.id.set_label)).setText(s.label);
            box.addView(row);
            if (i < settings.size() - 1) {
                View d = new View(requireContext());
                d.setLayoutParams(new LinearLayout.LayoutParams(
                        ViewGroup.LayoutParams.MATCH_PARENT, Ui.dp(requireContext(), 1)));
                d.setBackgroundColor(ContextCompat.getColor(requireContext(), R.color.divider));
                box.addView(d);
            }
        }

        v.findViewById(R.id.profile_logout).setOnClickListener(x -> {
            FirebaseAuth.getInstance().signOut();
            new SessionManager(requireContext()).clear();
            Intent i = new Intent(requireContext(), LoginActivity.class);
            i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(i);
            requireActivity().finish();
        });
    }

    private String initials(String name) {
        if (name == null || name.trim().isEmpty()) return "U";
        String[] parts = name.trim().split("\\s+");
        String s = parts[0].substring(0, 1);
        if (parts.length > 1) s += parts[parts.length - 1].substring(0, 1);
        return s.toUpperCase();
    }
}
