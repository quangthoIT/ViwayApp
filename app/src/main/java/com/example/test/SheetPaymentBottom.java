package com.example.test;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.google.android.material.bottomsheet.BottomSheetDialogFragment;

import org.jetbrains.annotations.Nullable;

public class SheetPaymentBottom extends BottomSheetDialogFragment {

    private RadioButton radioMomo, radioVnpay, radioBank;
    private LinearLayout layoutMomo, layoutVnpay,layoutBank;
    private Button btnThanhToan;

    private OnPaymentSelectedListener listener;

    public interface OnPaymentSelectedListener {
        void onMethodSelected(String method);
    }

    public SheetPaymentBottom(OnPaymentSelectedListener listener) {
        this.listener = listener;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.bottom_sheet_payment, container, false);

        radioMomo = view.findViewById(R.id.radio_Momo);
        radioVnpay = view.findViewById(R.id.radio_Vnpay);
        radioBank = view.findViewById(R.id.radio_Bank);

        layoutMomo = view.findViewById(R.id.payment_momo);
        layoutVnpay = view.findViewById(R.id.payment_vnpay);
        layoutBank = view.findViewById(R.id.payment_bank);

        RadioButton[] allRadios = {radioMomo, radioVnpay, radioBank};

        View.OnClickListener selectRadio = clicked -> {
            for (RadioButton rb : allRadios) rb.setChecked(false);

            if (clicked instanceof RadioButton) {
                ((RadioButton) clicked).setChecked(true);
            } else if (clicked instanceof LinearLayout) {
                RadioButton rb = (RadioButton) ((LinearLayout) clicked).getChildAt(0);
                rb.setChecked(true);
            }
        };

        layoutMomo.setOnClickListener(selectRadio);
        radioMomo.setOnClickListener(selectRadio);

        layoutVnpay.setOnClickListener(selectRadio);
        radioVnpay.setOnClickListener(selectRadio);

        layoutBank.setOnClickListener(selectRadio);
        radioBank.setOnClickListener(selectRadio);

        btnThanhToan = view.findViewById(R.id.btn_SubmitPayment);
        btnThanhToan.setOnClickListener(v -> {
            String method = null;
            if (radioMomo.isChecked()) method = "Momo";
            else if (radioVnpay.isChecked()) method = "VNPay";
            else if (radioBank.isChecked()) method = "Bank";

            if (method != null) {
                listener.onMethodSelected(method);
                dismiss();
            } else {
                Toast.makeText(getContext(), "Vui lòng chọn phương thức thanh toán", Toast.LENGTH_SHORT).show();
            }
        });

        return view;
    }

}
