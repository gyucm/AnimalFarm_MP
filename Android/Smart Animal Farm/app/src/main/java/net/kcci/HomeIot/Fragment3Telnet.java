package net.kcci.HomeIot;
/*
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.ToggleButton;

import androidx.fragment.app.Fragment;


import java.text.SimpleDateFormat;
import java.util.Date;

public class Fragment3Telnet extends Fragment {
    ClientThread clientThread;
    TextView textView;
    ScrollView scrollViewRecv;
    ToggleButton toggleButtonStart;
    SimpleDateFormat dataFormat = new SimpleDateFormat("yy.MM.dd HH:mm:ss");
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment3telnet, container, false);
        EditText editTextIp = view.findViewById(R.id.editTextIp);
        EditText editTextPort = view.findViewById(R.id.editTextPort);
        toggleButtonStart = view.findViewById(R.id.toggleButtonStart);
        Button buttonSend = view.findViewById(R.id.buttonSend);
        EditText editTextSend = view.findViewById(R.id.editTextSend);
        buttonSend.setEnabled(false);
        textView = view.findViewById(R.id.textViewRecv);
        scrollViewRecv = view.findViewById(R.id.scrollViewRecv);
        if(ClientThread.socket != null)
            toggleButtonStart.setEnabled(false);
        toggleButtonStart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(toggleButtonStart.isChecked()) {
                    String strIp = editTextIp.getText().toString();
                    int intPort = Integer.parseInt(editTextPort.getText().toString());
                    clientThread = new ClientThread(strIp,intPort);
                    clientThread.start();
                    buttonSend.setEnabled(true);
                } else {
                    clientThread.stopClient();
                    buttonSend.setEnabled(false);
                }
            }
        });


        buttonSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String strSend = editTextSend.getText().toString();
                clientThread.sendData(strSend);
                editTextSend.setText("");
            }
        });
        return  view;
    }

    void recvDataProcess(String data) {
        Date date = new Date();
        String strDate = dataFormat.format(date);
        data += '\n';
        strDate = strDate + " " + data;
        textView.append(strDate);
        scrollViewRecv.fullScroll(View.FOCUS_DOWN);
    }
}
*/
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ProgressBar;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import androidx.fragment.app.Fragment;
import java.text.SimpleDateFormat;
import java.util.Date;
public class Fragment3Telnet extends Fragment {

    private WebView webView;


    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment3telnet, container, false);
        webView = view.findViewById(R.id.web_view);
        webView.setWebViewClient(new WebViewClient());
        webView.getSettings().setJavaScriptEnabled(true);
        webView.loadUrl("http://10.10.141.81/");
        return view;
    }

    private class LoadWebPageTask extends AsyncTask<String, Void, String> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected String doInBackground(String... urls) {
            String response = "";
            HttpURLConnection connection = null;
            try {
                URL url = new URL(urls[0]);
                connection = (HttpURLConnection) url.openConnection();
                connection.connect();

                // 서버 응답을 가져옴
                BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
                String line;
                while ((line = reader.readLine()) != null) {
                    response += line;
                }
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                if (connection != null) {
                    connection.disconnect();
                }
            }
            return response;
        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);


            // WebView에 가져온 웹 페이지를 보여줌
            webView.loadData(result, "text/html", null);
        }

    }

}
