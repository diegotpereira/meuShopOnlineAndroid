package br.java.meushoponlineandroid.fragment;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;

import br.java.meushoponlineandroid.R;
import br.java.meushoponlineandroid.adapter.ImagemAdapter;

public class HomeFragmento extends Fragment {

    private RecyclerView mRecyclerView;
    private ImagemAdapter mAdapter;

    private ProgressBar mProgressBar;

    private FirebaseStorage mStorage;
    private DatabaseReference mDataRef;
    private ValueEventListener mListener;



    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    private String mParam1;
    private String mParam2;

    public HomeFragmento() {
        // Required empty public constructor
    }

    public static HomeFragmento newInstance(String param1, String param2) {
        HomeFragmento fragment = new HomeFragmento();
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
        return inflater.inflate(R.layout.fragmento_home, container, false);
    }
}