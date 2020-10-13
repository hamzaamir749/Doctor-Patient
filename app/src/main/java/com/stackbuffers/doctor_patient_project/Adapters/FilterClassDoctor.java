package com.stackbuffers.doctor_patient_project.Adapters;

import com.stackbuffers.doctor_patient_project.Models.SearchDoctorConsultantModel;

import java.util.ArrayList;
import java.util.List;

import android.widget.Filter;

public class FilterClassDoctor extends Filter {

    SearchDoctorAdapter adapter;
    public List<SearchDoctorConsultantModel> filterlist;

    public FilterClassDoctor(SearchDoctorAdapter adapter, List<SearchDoctorConsultantModel> filterlist) {
        this.adapter = adapter;
        this.filterlist = filterlist;
    }

    @Override
    protected FilterResults performFiltering(CharSequence constraint) {
        FilterResults results = new FilterResults();
        if (constraint != null && constraint.length() > 0) {
            constraint = constraint.toString();
            List<SearchDoctorConsultantModel> filteredList = new ArrayList<>();
            for (SearchDoctorConsultantModel u : filterlist) {
                if (u.getName().toLowerCase().contains(constraint)) {
                    filteredList.add(u);
                }
            }
            results.count = filteredList.size();
            results.values = filteredList;
            return results;

        }
        results.count = filterlist.size();
        results.values = filterlist;
        return results;

    }

    @Override
    protected void publishResults(CharSequence constraint, FilterResults results) {
        adapter.list = (List<SearchDoctorConsultantModel>) results.values;
        adapter.notifyDataSetChanged();
    }
}
