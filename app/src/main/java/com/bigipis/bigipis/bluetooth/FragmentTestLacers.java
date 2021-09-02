package com.bigipis.bigipis.bluetooth;

import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.SeekBar;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.bigipis.bigipis.R;
import com.github.douglasjunior.bluetoothclassiclibrary.BluetoothWriter;

public class FragmentTestLacers extends Fragment implements SeekBar.OnSeekBarChangeListener, View.OnClickListener {
    private View view;
    private SeekBar seekBarPower, seekBarCount, seekBarPause1, seekBarPause2, seekBarPause3, seekBarTime1, seekBarTime2, seekBarTime3;
    private TextView textViewPower, textViewCount, textViewPause1, textViewPause2, textViewPause3, textViewTime1, textViewTime2, textViewTime3;
    private LinearLayout linearLayout1, linearLayout2, linearLayout3;
    private Button buttonTest;
    private String BLUETOOTH_ASK;
    private static Handler sentMessageHandler;
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_test, container,false);
        seekBarPower = view.findViewById(R.id.seekBarSignalPower);
        seekBarCount = view.findViewById(R.id.seekBarSignalCount);
        seekBarPause1 = view.findViewById(R.id.seekBarPause1);
        seekBarPause2 = view.findViewById(R.id.seekBarPause2);
        seekBarPause3 = view.findViewById(R.id.seekBarPause3);
        seekBarTime1 = view.findViewById(R.id.seekBarTime1);
        seekBarTime2 = view.findViewById(R.id.seekBarTime2);
        seekBarTime3 = view.findViewById(R.id.seekBarTime3);

        textViewPower = view.findViewById(R.id.textViewSignalPower);
        textViewCount = view.findViewById(R.id.textViewSignalCount);
        textViewPause1 = view.findViewById(R.id.textViewPause1);
        textViewPause2 = view.findViewById(R.id.textViewPause2);
        textViewPause3 = view.findViewById(R.id.textViewPause3);
        textViewTime1 = view.findViewById(R.id.textViewTime1);
        textViewTime2 = view.findViewById(R.id.textViewTime2);
        textViewTime3 = view.findViewById(R.id.textViewTime3);

        buttonTest = view.findViewById(R.id.buttonTest);

        buttonTest.setOnClickListener(this);

        seekBarPower.setOnSeekBarChangeListener(this);
        seekBarCount.setOnSeekBarChangeListener(this);
        seekBarPause1.setOnSeekBarChangeListener(this);
        seekBarPause2.setOnSeekBarChangeListener(this);
        seekBarPause3.setOnSeekBarChangeListener(this);
        seekBarTime1.setOnSeekBarChangeListener(this);
        seekBarTime2.setOnSeekBarChangeListener(this);
        seekBarTime3.setOnSeekBarChangeListener(this);

        linearLayout1 = view.findViewById(R.id.linearLayoutSignal1);
        linearLayout2 = view.findViewById(R.id.linearLayoutSignal2);
        linearLayout3 = view.findViewById(R.id.linearLayoutSignal3);

        sentMessageHandler = new Handler(){
            @Override
            public synchronized void handleMessage(Message msg) {
                if (msg.what < BLUETOOTH_ASK.length()) {
                    SendMessageTask task = new SendMessageTask(BLUETOOTH_ASK);
                    task.execute(msg.what);
                }
            }
        };

        return view;
    }

    @Override
    public void onStart() {

        super.onStart();
    }

    private void setCommand() {
        int count = seekBarCount.getProgress() + 1;
        BLUETOOTH_ASK = String.valueOf(count);
        BLUETOOTH_ASK += seekBarPower.getProgress() + 1;
        if (count >= 1) {
            BLUETOOTH_ASK += seekBarTime1.getProgress() + 1;
            BLUETOOTH_ASK += seekBarPause1.getProgress() + 1;
        }
        if (count >= 2) {
            BLUETOOTH_ASK += seekBarTime2.getProgress() + 1;
            BLUETOOTH_ASK += seekBarPause2.getProgress() + 1;
        }
        if (count == 3) {
            BLUETOOTH_ASK += seekBarTime3.getProgress() + 1;
            BLUETOOTH_ASK += seekBarPause3.getProgress() + 1;
        }
        BLUETOOTH_ASK = "A" + BLUETOOTH_ASK + "Z";
    }

    @Override
    public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
        switch (seekBar.getId()) {
            case R.id.seekBarSignalPower: {
                textViewPower.setText(String.valueOf(seekBarPower.getProgress() + 1));
                setCommand();
                break;
            }
            case R.id.seekBarSignalCount: {
                textViewCount.setText(String.valueOf(seekBarCount.getProgress() + 1));
                if (seekBarCount.getProgress() + 1 == 1) {
                    linearLayout1.setVisibility(View.VISIBLE);
                    linearLayout2.setVisibility(View.GONE);
                    linearLayout3.setVisibility(View.GONE);
                } else if (seekBarCount.getProgress() + 1 == 2) {
                    linearLayout1.setVisibility(View.VISIBLE);
                    linearLayout2.setVisibility(View.VISIBLE);
                    linearLayout3.setVisibility(View.GONE);
                } else if (seekBarCount.getProgress() + 1 == 3) {
                    linearLayout1.setVisibility(View.VISIBLE);
                    linearLayout2.setVisibility(View.VISIBLE);
                    linearLayout3.setVisibility(View.VISIBLE);
                }
                setCommand();
                break;
            }
            case R.id.seekBarPause1: {
                textViewPause1.setText(String.valueOf((seekBarPause1.getProgress() + 1) / 2.0));
                setCommand();
                break;
            }
            case R.id.seekBarPause2: {
                textViewPause2.setText(String.valueOf((seekBarPause2.getProgress() + 1) / 2.0));
                setCommand();
                break;
            }
            case R.id.seekBarPause3: {
                textViewPause3.setText(String.valueOf((seekBarPause3.getProgress() + 1) / 2.0));
                setCommand();
                break;
            }
            case R.id.seekBarTime1: {
                textViewTime1.setText(String.valueOf((seekBarTime1.getProgress() + 1) / 2.0));
                setCommand();
                break;
            }
            case R.id.seekBarTime2: {
                textViewTime2.setText(String.valueOf((seekBarTime2.getProgress() + 1) / 2.0));
                setCommand();
                break;
            }
            case R.id.seekBarTime3: {
                textViewTime3.setText(String.valueOf((seekBarTime3.getProgress() + 1) / 2.0));
                setCommand();
                break;
            }
        }
    }

    @Override
    public void onStartTrackingTouch(SeekBar seekBar) {

    }

    @Override
    public void onStopTrackingTouch(SeekBar seekBar) {

    }

    @Override
    public void onClick(View view) {
        setCommand();
        sentMessageHandler.sendEmptyMessage(0);
    }

    private static class SendMessageTask extends AsyncTask<Integer, Void, Integer> {
        private String bluetoothCommand;

        SendMessageTask(String bluetoothCommand) {
            this.bluetoothCommand = bluetoothCommand;
        }

        @Override
        protected synchronized Integer doInBackground(Integer... index) {
            BluetoothWriter writer = new BluetoothWriter(BluetoothLeService.getDefaultInstance());
            writer.write(bluetoothCommand.charAt(index[0]));
            return index[0];
        }

        protected synchronized void onPostExecute(Integer i) {
            Log.d("BluetoothLeService", "\nSENDED CHAR:" + bluetoothCommand.charAt(i) + "\n");
            try {
                Thread.sleep(200);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            sentMessageHandler.sendEmptyMessage(i + 1);
        }
    }
}


