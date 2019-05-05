package com.example.finalproject489;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
public class loginFrag extends Fragment {
    public EditText usr, pass;
    public CheckBox saveOrNot;
    public boolean saveLogin;
    public Button loginButton, createAccountButton;
    public loginFrag() {
        // Required empty public constructor
    }
    public loginInterface login;
    public interface loginInterface {
        void loginPress(String user, String password, boolean save);
        void createPress(String user, String password, boolean save);
    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view =  inflater.inflate(R.layout.fragment_login, container, false);
        usr= (EditText) view.findViewById(R.id.username);
        pass= (EditText) view.findViewById(R.id.pass);
        saveOrNot= (CheckBox) view.findViewById(R.id.checkBox);
        loginButton= (Button) view.findViewById(R.id.login);
        createAccountButton= (Button) view.findViewById(R.id.create);
        return view;
    }
    @Override
    public void onActivityCreated(Bundle savedState) {
        super.onActivityCreated(savedState);
        saveOrNot.setOnClickListener(new View.OnClickListener(){
            public void onClick(View v){
                saveLogin=saveOrNot.isChecked();
            }
        });
        loginButton.setOnClickListener(new View.OnClickListener(){
            public void onClick(View v){
                login.loginPress(usr.getText().toString(),pass.getText().toString(),saveLogin);
            }
        });
        createAccountButton.setOnClickListener(new View.OnClickListener(){
            public void onClick(View v){
                login.createPress(usr.getText().toString(),pass.getText().toString(),saveLogin);
            }
        });
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof loginInterface)
            login= (loginInterface) context;
    }
    @Override
    public void onDetach() {
        super.onDetach();
        login = null;
    }
    public String getEmail() {return usr.getText().toString();}
    public String getPass() {return pass.getText().toString();}
    public void setEmail(String s) {usr.setText(s);}
    public void setPass(String s) {pass.setText(s);}
}