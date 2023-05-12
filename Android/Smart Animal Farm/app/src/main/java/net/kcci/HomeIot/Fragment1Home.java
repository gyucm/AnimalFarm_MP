package net.kcci.HomeIot;

import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;

import androidx.fragment.app.Fragment;


public class Fragment1Home extends Fragment {
    MainActivity mainActivity;
    ImageButton imageButtonLamp;
    ImageButton imageButtonPlug;
    boolean imageButtonLampCheck;
    boolean imageButtonPlugCheck;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment1home, container, false);
        imageButtonLamp = view.findViewById(R.id.imageButtonLamp);
        imageButtonPlug = view.findViewById(R.id.imageButtonPlug);
        mainActivity = (MainActivity)getActivity();
        imageButtonLamp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(ClientThread.socket != null) {
                    if (imageButtonLampCheck)
                        mainActivity.clientThread.sendData(ClientThread.arduinoId + "LAMPOFF");
                    else
                        mainActivity.clientThread.sendData(ClientThread.arduinoId + "LAMPON");
                }
            }
        });
        imageButtonPlug = view.findViewById(R.id.imageButtonPlug);
        imageButtonPlug.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(ClientThread.socket != null) {
                    if (imageButtonPlugCheck)
                        mainActivity.clientThread.sendData(ClientThread.arduinoId + "PLUGOFF");
                    else
                        mainActivity.clientThread.sendData(ClientThread.arduinoId + "PLUGON");
                }
            }
        });
        return view;
    }
    void recvDataProcess(String strRecvData)
    {
        String[] splitLists = strRecvData.toString().split("\\[|]|@|\\n") ;
        for(int i=0; i< splitLists.length; i++)
            Log.d("recvDataProcess"," i: " + i + ", value: " +splitLists[i]);
        buttonUpdate(splitLists[2]);
        if(splitLists.length == 3){
            String[] insertArgs = new String[3];
            int index = splitLists[2].indexOf('O');//O에 있는 인덱스 리턴
            String sensorId = splitLists[2].substring(0,index);
            insertArgs[0] = splitLists[1]; //LHF_ARD
            insertArgs[1] = sensorId; //LAMP
            insertArgs[2] = splitLists[2].substring(index); //ON
          //  mainActivity.database.updateRecordParam(mainActivity.SQLdb,insertArgs);
        }
    }
    public void buttonUpdate(String cmd)
    {
        if(cmd.equals("LAMPON")) {
            imageButtonLamp.setImageResource(R.drawable.lamp_on);
            imageButtonLamp.setBackgroundColor(Color.GREEN);
            imageButtonLampCheck = true;
        } else if(cmd.equals("LAMPOFF")) {
            imageButtonLamp.setImageResource(R.drawable.lamp_off);
            imageButtonLamp.setBackgroundColor(Color.LTGRAY);
            imageButtonLampCheck = false;
        } else if(cmd.equals("PLUGON")) {
            imageButtonPlug.setImageResource(R.drawable.air_on);
            imageButtonPlug.setBackgroundColor(Color.GREEN);
            imageButtonPlugCheck = true;
        } else if(cmd.equals("PLUGOFF")) {
            imageButtonPlug.setImageResource(R.drawable.air_off);
            imageButtonPlug.setBackgroundColor(Color.LTGRAY);
            imageButtonPlugCheck = false;
        }
    }

  /*  @Override
    public void onStart() { //화면이 다시 가려졌다가 보여지면 ON/OFF 보이기
        super.onStart();
        String strSensor[] = mainActivity.database.executeRawQueryParam(mainActivity.SQLdb,"LAMP");
        buttonUpdate(strSensor[3]+strSensor[4]);
        strSensor = mainActivity.database.executeRawQueryParam(mainActivity.SQLdb,"PLUG");
        buttonUpdate(strSensor[3]+strSensor[4]);
    }*/
}
