package com.stackbuffers.doctor_patient_project.Activities.Doctor;

import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager.widget.ViewPager;

import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;

import com.google.android.material.tabs.TabLayout;
import com.stackbuffers.doctor_patient_project.Activities.Fragments.ConsultationCompleteFragment;
import com.stackbuffers.doctor_patient_project.Activities.Fragments.ConsultationRequestFragment;
import com.stackbuffers.doctor_patient_project.Adapters.FragmentViewPagerAdapter;
import com.stackbuffers.doctor_patient_project.R;

public class ConsultationMenuDoctorActivity extends AppCompatActivity {
    TabLayout tabLayout;
    ViewPager viewPager;
    FragmentViewPagerAdapter adapter;
    ImageView back;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_consultation_menu_doctor);
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
        tabLayout = findViewById(R.id.consultationMenudoctor_tablayout);
        viewPager = findViewById(R.id.consultationMenudoctor_viewpager);
        back = findViewById(R.id.consultationMenudoctor_back);
        adapter = new FragmentViewPagerAdapter(getSupportFragmentManager());
        adapter.AddFragments(new ConsultationRequestFragment(), getResources().getString(R.string.fRequests));
        adapter.AddFragments(new ConsultationCompleteFragment(), getResources().getString(R.string.fCompleted));
        viewPager.setAdapter(adapter);
        tabLayout.setupWithViewPager(viewPager);
    }

}
