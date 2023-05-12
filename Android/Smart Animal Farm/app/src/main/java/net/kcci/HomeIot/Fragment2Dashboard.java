package net.kcci.HomeIot;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;
import androidx.fragment.app.Fragment;


public class Fragment2Dashboard extends Fragment {
    MainActivity mainActivity;
    Switch switchLed;
    Switch switchFood;
    Switch switchAir;
    TextView textViewIllumination;
    TextView textViewTemp;
    TextView textViewDis;
    ImageView imageViewLed;
    ImageView imageViewFood;
    ImageView imageViewAir;
    Button buttonCondition;
    Button buttonControl;
    boolean switchLedFlag = false;
    boolean switchFoodFlag = false;
    boolean switchAirFlag = false;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment2dashboard, container, false);
        mainActivity = (MainActivity) getActivity();
        textViewIllumination = view.findViewById(R.id.textViewIllumination);
        textViewTemp = view.findViewById(R.id.textViewTemp);
        textViewDis = view.findViewById(R.id.textViewDis);
        imageViewLed = view.findViewById(R.id.imageViewLed);
        imageViewFood = view.findViewById(R.id.imageViewFood);
        imageViewAir = view.findViewById(R.id.imageViewAir);

        buttonCondition = view.findViewById(R.id.buttonCondition);
        buttonCondition.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (ClientThread.socket != null) {
                    mainActivity.clientThread.sendData(ClientThread.arduinoId + "GETSENSOR");
                } else
                    Toast.makeText(getActivity(),"login 확인", Toast.LENGTH_SHORT).show();
            }
        });
        buttonControl = view.findViewById(R.id.buttonControl);
        buttonControl.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (ClientThread.socket != null) {
                    mainActivity.clientThread.sendData(ClientThread.arduinoId + "GETSW");
                } else
                    Toast.makeText(getActivity(), "login 확인", Toast.LENGTH_SHORT).show();
            }
        });
        switchLed = view.findViewById(R.id.switchLed);
        switchLed.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (ClientThread.socket != null) {
                    if (switchLed.isChecked()) {
                        mainActivity.clientThread.sendData(ClientThread.arduinoId + "LED@ON@3");
                        switchLed.setChecked(false);
                    } else {
                        mainActivity.clientThread.sendData(ClientThread.arduinoId + "LED@OFF@3");
                        switchLed.setChecked(true);
                    }
                } else
                    Toast.makeText(getActivity(), "login 확인", Toast.LENGTH_SHORT).show();
            }
        });

        switchFood = view.findViewById(R.id.switchFood);
        switchFood.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (ClientThread.socket != null) {
                    if (switchFood.isChecked()) {
                        mainActivity.clientThread.sendData(ClientThread.arduinoId + "SMOTOR@ON");
                        switchFood.setChecked(false);
                    } else {
                        mainActivity.clientThread.sendData(ClientThread.arduinoId + "SMOTOR@STOP");
                        switchFood.setChecked(true);
                    }
                }else
                    Toast.makeText(getActivity(), "login 확인", Toast.LENGTH_SHORT).show();
            }
        });
        switchAir = view.findViewById(R.id.switchAir);
        switchAir.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (ClientThread.socket != null) {
                    if (switchAir.isChecked()) {
                        mainActivity.clientThread.sendData(ClientThread.arduinoId + "MOTOR@ON");
                        switchAir.setChecked(false);
                    } else {
                        mainActivity.clientThread.sendData(ClientThread.arduinoId + "MOTOR@STOP");
                        switchAir.setChecked(true);
                    }
                }else
                    Toast.makeText(getActivity(), "login 확인", Toast.LENGTH_SHORT).show();
            }
        });
        return view;
    }
    public void recvDataProcess(String recvData) {
        String[] splitLists = recvData.toString().split("\\[|]|@|\\n");
        if(splitLists[2].equals("GETSW")) {
            if(splitLists[3].equals("0"))
                imageButtonUpdate("LEDOFF");
            else if(splitLists[3].equals("1")) {
                imageButtonUpdate("LEDON");
            }if(splitLists[4].equals("0")) {
                imageButtonUpdate("MOTORSTOP");
            }else if(splitLists[4].equals("1")) {
                imageButtonUpdate("MOTORON");
            }if(splitLists[5].equals("0")) {
                imageButtonUpdate("MOTORSTOP");
            }else if(splitLists[5].equals("1")) {
                imageButtonUpdate("MOTORON");
            }
        }
        else if(splitLists[2].equals("SENSOR"))
            updateTextView(splitLists[3],splitLists[4],splitLists[5]);
        else {
            imageButtonUpdate(splitLists[2]+splitLists[3]);
        }
    }
    public void imageButtonUpdate(String cmd) {
        if (cmd.equals("LEDON")) {
            imageViewLed.setImageResource(R.drawable.led_on);
            switchLed.setChecked(true);
            switchLedFlag = true;
        } else if(cmd.equals("LEDOFF")) {
            imageViewLed.setImageResource(R.drawable.led_off);
            switchLed.setChecked(false);
            switchLedFlag = false;
        } else if(cmd.equals("SMOTORON")) {
            imageViewFood.setImageResource(R.drawable.food_on);
            switchFood.setChecked(true);
            switchFoodFlag = true;
        } else if(cmd.equals("SMOTORSTOP")) {
            imageViewFood.setImageResource(R.drawable.food_off);
            switchFood.setChecked(false);
            switchFoodFlag = false;
        } else if(cmd.equals("MOTORON")) {
            imageViewAir.setImageResource(R.drawable.air_on);
            switchAir.setChecked(true);
            switchAirFlag = true;
        } else if(cmd.equals("MOTORSTOP")) {
            imageViewAir.setImageResource(R.drawable.air_off);
            switchAir.setChecked(false);
            switchAirFlag = true;
        }
    }
    public void updateTextView(String cds, String temp, String dis) {
        textViewIllumination.setText(cds+"%");    //Illumination
        textViewTemp.setText(temp+"℃");          //Temperature
        textViewDis.setText(dis+"cm");          //Distance
    }
}
