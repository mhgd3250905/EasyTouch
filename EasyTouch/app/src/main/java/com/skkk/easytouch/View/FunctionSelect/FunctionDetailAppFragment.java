package com.skkk.easytouch.View.FunctionSelect;


import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.skkk.easytouch.Bean.AppInfoBean;
import com.skkk.easytouch.R;
import com.skkk.easytouch.Utils.PackageUtils;
import com.skkk.easytouch.View.AppSelect.AppAdapter;
import com.skkk.easytouch.View.BaseAdapter;
import com.skkk.easytouch.View.ScaleRecyclerView;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link FunctionDetailAppFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class FunctionDetailAppFragment extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    @Bind(R.id.rv_apps)
    ScaleRecyclerView rvApps;

    private AppAdapter adapter;
    private LinearLayoutManager layoutManager;

    private List<AppInfoBean> allApps;

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;


    public FunctionDetailAppFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment FunctionDetailBaseFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static FunctionDetailAppFragment newInstance(String param1, String param2) {
        FunctionDetailAppFragment fragment = new FunctionDetailAppFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_function_detail_app, container, false);
        ButterKnife.bind(this, view);
        return view;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        allApps = new ArrayList<>();
        allApps = PackageUtils.getInstance(getContext().getApplicationContext()).getAllApps();
//            allApps = PackageUtils.getInstance(getContext().getApplicationContext()).getAllShortCuts();
        adapter = new AppAdapter(getContext(), allApps);
        layoutManager = new LinearLayoutManager(getContext());
        rvApps.setAdapter(adapter);
        rvApps.setLayoutManager(layoutManager);
        initEvent();
    }

    private void initEvent() {
        adapter.setOnItemClickListener(new BaseAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(View v, int pos) {

            }
        });
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
    }

    @Override
    public void onDetach() {
        super.onDetach();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        ButterKnife.unbind(this);
    }
}
