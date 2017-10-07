package com.apsitcoders.bus.fragment;

import android.app.Fragment;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.apsitcoders.bus.R;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;

/**
 * Created by adityathanekar on 07/10/17.
 */

public class WalletFragment extends Fragment implements View.OnClickListener {

    @BindView(R.id.current_amount_txv)
    TextView currentAmountTV;
    @BindView(R.id.add_money_value_ed)
    EditText addMoneyED;
    @BindView(R.id.debit_card_btn)
    Button debitCardBtn;
    @BindView(R.id.credit_card_btn)
    Button creditCardBtn;
    @BindView(R.id.net_banking_btn)
    Button netBankingCardBtn;
    @BindView(R.id.paytm_btn)
    Button paytmBtn;
    private String addAmountValueStr="";
    private int amountInt;

    private Unbinder unbinder;

    FirebaseDatabase firebaseDatabase;
    DatabaseReference ref;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_wallet, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        unbinder = ButterKnife.bind(this, view);
        firebaseDatabase = FirebaseDatabase.getInstance();
        ref = firebaseDatabase.getReference("money").child("wallet");

        ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if(dataSnapshot.exists() && currentAmountTV != null) {
                    amountInt = dataSnapshot.getValue(Integer.class);
                    currentAmountTV.setText(amountInt + "");
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
        initView();
    }

    private void initView() {
        debitCardBtn.setOnClickListener(this);
        creditCardBtn.setOnClickListener(this);
        netBankingCardBtn.setOnClickListener(this);
        paytmBtn.setOnClickListener(this);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.debit_card_btn:
                addAmountValueStr= addMoneyED.getText().toString();
                amountInt+=Integer.parseInt(addAmountValueStr);
                currentAmountTV.setText(""+amountInt);
                break;
            case R.id.credit_card_btn:
                addAmountValueStr= addMoneyED.getText().toString();
                amountInt+=Integer.parseInt(addAmountValueStr);
                currentAmountTV.setText(""+amountInt);
                break;
            case R.id.net_banking_btn:
                addAmountValueStr= addMoneyED.getText().toString();
                amountInt+=Integer.parseInt(addAmountValueStr);
                currentAmountTV.setText(""+amountInt);
                break;
            case R.id.paytm_btn:
                addAmountValueStr= addMoneyED.getText().toString();
                amountInt+=Integer.parseInt(addAmountValueStr);
                currentAmountTV.setText(""+amountInt);
                break;
        }
        ref.setValue(amountInt);
    }
}
