package br.java.meushoponlineandroid.fragment;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;

import java.util.ArrayList;
import java.util.List;

import br.java.meushoponlineandroid.R;
import br.java.meushoponlineandroid.adapter.ImagemAdapter;
import br.java.meushoponlineandroid.model.Envio;

public class HomeFragmento extends Fragment {

    private RecyclerView mRecyclerView;
    private ImagemAdapter mAdapter;

    private ProgressBar mProgressBar;

    private FirebaseStorage mStorage;
    private DatabaseReference mDataRef;
    private ValueEventListener mListener;
    private List<Envio> mEnvios;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragmento_home, container, false);

        mRecyclerView = v.findViewById(R.id.recycler_view);
        mRecyclerView.setHasFixedSize(true);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));

        mProgressBar = v.findViewById(R.id.progress_circle);

        mEnvios = new ArrayList<>();

        mAdapter = new ImagemAdapter(getActivity(), mEnvios);

        mRecyclerView.setAdapter(mAdapter);

        mStorage = FirebaseStorage.getInstance();
        mDataRef = FirebaseDatabase.getInstance().getReference("envios");

        mListener = mDataRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                mEnvios.clear();

                for(DataSnapshot postSnapshot : dataSnapshot.getChildren()) {
                    Envio envio = postSnapshot.getValue(Envio.class);
                    envio.setKey(postSnapshot.getKey());
                    mEnvios.add(envio);
                }
                mAdapter.notifyDataSetChanged();

                mProgressBar.setVisibility(View.INVISIBLE);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

                Toast.makeText(getActivity(), error.getMessage(), Toast.LENGTH_SHORT).show();

                mProgressBar.setVisibility(View.INVISIBLE);
            }
        });

        // Inflate the layout for this fragment
        return v;
    }
}