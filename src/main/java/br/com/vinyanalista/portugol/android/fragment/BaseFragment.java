package br.com.vinyanalista.portugol.android.fragment;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import br.com.vinyanalista.portugol.android.util.S;

public class BaseFragment extends Fragment {
    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        S.l(this, "onActivityCreated()");
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        S.l(this, "onAttach()");
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        S.l(this, "onCreate()");
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        S.l(this, "onCreateView()");
        return super.onCreateView(inflater, container, savedInstanceState);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        S.l(this, "onDestroy()");
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        S.l(this, "onDestroyView()");
    }

    @Override
    public void onDetach() {
        super.onDetach();
        S.l(this, "onDetach()");
    }

    @Override
    public void onPause() {
        super.onPause();
        S.l(this, "onPause()");
    }

    @Override
    public void onResume() {
        super.onResume();
        S.l(this, "onResume()");
    }

    @Override
    public void onStart() {
        super.onStart();
        S.l(this, "onStart()");
    }

    @Override
    public void onStop() {
        super.onStop();
        S.l(this, "onStop()");
    }
}
