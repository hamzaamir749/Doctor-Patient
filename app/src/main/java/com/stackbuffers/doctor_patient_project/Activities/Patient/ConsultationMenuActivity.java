package com.stackbuffers.doctor_patient_project.Activities.Patient;

import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager.widget.ViewPager;

import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;

import com.google.android.material.tabs.TabLayout;
import com.stackbuffers.doctor_patient_project.Activities.Fragments.ConsultationMenuApprovedFragment;
import com.stackbuffers.doctor_patient_project.Activities.Fragments.ConsultationMenuCompleteFragment;
import com.stackbuffers.doctor_patient_project.Activities.Fragments.ConsultationMenuPendingFragment;
import com.stackbuffers.doctor_patient_project.Adapters.FragmentViewPagerAdapter;
import com.stackbuffers.doctor_patient_project.R;

public class ConsultationMenuActivity extends AppCompatActivity {
    TabLayout tabLayout;
    ViewPager viewPager;
    FragmentViewPagerAdapter adapter;
    ImageView back;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_consultation_menu);
        getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_STABLE | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN);
        initViews();
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
    }

    private void initViews() {
        tabLayout = findViewById(R.id.consultationMenu_tablayout);
        viewPager = findViewById(R.id.consultationMenu_viewpager);
        back = findViewById(R.id.consultationMenu_back);
        adapter = new FragmentViewPagerAdapter(getSupportFragmentManager());
        adapter.AddFragments(new ConsultationMenuPendingFragment(), getResources().getString(R.string.fPending));
        adapter.AddFragments(new ConsultationMenuApprovedFragment(), getResources().getString(R.string.fApproved));
        adapter.AddFragments(new ConsultationMenuCompleteFragment(), getResources().getString(R.string.fCompleted));
        viewPager.setAdapter(adapter);
        tabLayout.setupWithViewPager(viewPager);
    }


}
