
#define DEBUG
#define DEBUG_WIFI
#define USE_ARDUINO_INTERRUPTS true
#include <PulseSensorPlayground.h>
#include "HX711.h"
#include <WiFiEsp.h>
#include <SoftwareSerial.h>
#include <DHT.h>
#include <MsTimer2.h>
#define AP_SSID "iot0"
#define AP_PASS "iot00000"
#define SERVER_NAME "10.10.141.81"
#define SERVER_PORT 5000
#define LOGID "LJH_ARD"
#define PASSWD "PASSWD"
#define calibration_factor -7050.0

#define TRIG 9 //TRIG 핀 설정 (초음파 보내는 핀)
#define ECHO 8 //ECHO 핀 설정 (초음파 받는 핀)
#define CDS_PIN A0
#define DOUT 3
#define CLK 2
#define DHTPIN 4
#define WIFIRX 6  //6:RX-->ESP8266 TX
#define WIFITX 7  //7:TX -->ESP8266 RX
#define LED_BUILTIN_PIN 13 
#define PulseWire A1
#define CMD_SIZE 100
#define ARR_CNT 5
#define DHTTYPE DHT11
bool timerIsrFlag = false;
bool pigFlag = false;

char sendId[10] = "KSH_ARD";
char sendStm[10] = "LJH_STM";
char sendSql[10] = "LJH_SQL";
char sendAni[10] = "LJH_SQL";
char sendAnd[10] = "LJH_AND";
char sendFcm[10] = "FCM";

char sendBuf[CMD_SIZE];

int cds = 0;
unsigned int secCount;
long duration, dis;
   
char getSensorId[10];
int sensorTime;
float temp = 0.0;
float humi = 0.0;
int weight = 0;
int Threshold = 550;
int myBPM = 0;
HX711 scale(DOUT, CLK);
DHT dht(DHTPIN, DHTTYPE);
SoftwareSerial wifiSerial(WIFIRX, WIFITX);
WiFiEspClient client;
PulseSensorPlayground pulseSensor;

void setup() {

  pinMode(CDS_PIN, INPUT);    // 조도 핀을 입력으로 설정 (생략 가능)
  pinMode(TRIG, OUTPUT);
  pinMode(ECHO, INPUT);
  pulseSensor.analogInput(PulseWire); 
  pulseSensor.setThreshold(Threshold); 
  scale.set_scale(calibration_factor);
  scale.tare();

#ifdef DEBUG
  Serial.begin(115200); //DEBUG
#endif
  wifi_Setup();
  MsTimer2::set(1000, timerIsr); // 1000ms period
  MsTimer2::start();
  dht.begin();
}

void loop() {
  if (client.available()) {
    socketEvent();
  }
  cds = map(analogRead(CDS_PIN), 0, 1023, 0, 100);
  digitalWrite(TRIG, LOW);
  delayMicroseconds(2);
  digitalWrite(TRIG, HIGH);
  delayMicroseconds(10);
  digitalWrite(TRIG, LOW);
  duration = pulseIn (ECHO, HIGH);
  dis = duration * 17 / 1000; 
  humi = dht.readHumidity();
  temp = dht.readTemperature();
  myBPM = pulseSensor.getBeatsPerMinute();
  weight = (int)(scale.get_units()*100); 
      
  if (timerIsrFlag) //1초에 한번씩 실행
  {
    timerIsrFlag = false;
    if (!(secCount % 10)) //5초에 한번씩 실행
    {
      sprintf(sendBuf, "[%s]SENSOR@%d@%d@%d\n", sendSql, cds,(int)temp, dis);
      client.write(sendBuf, strlen(sendBuf));
      client.flush();

      delay(500);
  
      if(pigFlag == false){
        sprintf(sendBuf, "[%s]INFO@Pig@%d@78\n", sendAni, weight);
        client.write(sendBuf, strlen(sendBuf));
        client.flush();
        pigFlag = true;
      }
      if(pigFlag == true){
        sprintf(sendBuf, "[%s]INFO@Cow@%d@86\n", sendAni, weight);
        client.write(sendBuf, strlen(sendBuf));
        client.flush();
        pigFlag = false;
      }
      if(cds<50){
        sprintf(sendBuf, "[%s]LED@ON@2\n", sendStm);
        client.write(sendBuf, strlen(sendBuf));
        client.flush();
        delay(100);
      }
      
      if(dis>10){
        sprintf(sendBuf, "[%s]SMOTOR@ON@\n", sendStm);
        client.write(sendBuf, strlen(sendBuf));
        client.flush();
        sprintf(sendBuf, "[%s]FEEDINGNOW\n", sendFcm);
        client.write(sendBuf, strlen(sendBuf));
        client.flush();
        delay(100);
      }
      if(temp>24)
      {
        sprintf(sendBuf, "[%s]MOTOR@ON\n", sendStm);
        client.write(sendBuf, strlen(sendBuf));
        client.flush();
        delay(100);
      }
      else if(temp<20)
      {        
        sprintf(sendBuf, "[%s]MOTOR@OFF\n", sendStm);
        client.write(sendBuf, strlen(sendBuf));
        client.flush();
        delay(100);
      }

#ifdef DEBUG
      
      Serial.print("Cds: ");
      Serial.print(cds);
      Serial.print(" Temperature: ");
      Serial.print(temp);
      Serial.print(" Distance: ");
      Serial.print(dis);
      Serial.print(" weight: ");
      Serial.println(scale.get_units(),1);
#endif     
    }
    if (sensorTime != 0 && !(secCount % sensorTime ))
    {
      sprintf(sendBuf, "[%s]SENSOR@%d@%d@%d\r\n", getSensorId, cds, (int)temp, (int)dis);
      client.write(sendBuf, strlen(sendBuf));
      client.flush();
    }
  }

}
void socketEvent()
{
  int i = 0;
  char * pToken;
  char * pArray[ARR_CNT] = {0};
  char recvBuf[CMD_SIZE] = {0};
  int len;

  sendBuf[0] = '\0';
  len = client.readBytesUntil('\n', recvBuf, CMD_SIZE);
  client.flush();
#ifdef DEBUG
  Serial.print("recv : ");
  Serial.println(recvBuf);
#endif
  pToken = strtok(recvBuf, "[@]");
  while (pToken != NULL)
  {
    pArray[i] =  pToken;
    if (++i >= ARR_CNT)
      break;
    pToken = strtok(NULL, "[@]");
  }
  //[KSH_ARD]LED@ON : pArray[0] = "KSH_ARD", pArray[1] = "LED", pArray[2] = "ON"

  if (!strncmp(pArray[1], " Alr", 4)) //Already logged
  {
#ifdef DEBUG
    Serial.write('\n');
#endif
    client.stop();
    server_Connect();
    return ;
  }
  else if (!strncmp(pArray[1], "GETSENSOR", 9)) {
    if (pArray[2] != NULL) {
      sensorTime = atoi(pArray[2]);
      strcpy(getSensorId, pArray[0]);
      return;
    } else {
      sensorTime = 0;
      sprintf(sendBuf, "[%s]%s@%d@%d@%d\n", pArray[0], "SENSOR", cds, (int)temp, (int)dis);
    }
  }
  else
    return;

  client.write(sendBuf, strlen(sendBuf));
  client.flush();

#ifdef DEBUG
  Serial.print(", send : ");
  Serial.print(sendBuf);
#endif
}
void timerIsr()
{
  timerIsrFlag = true;
  secCount++;
}
void wifi_Setup() {
  wifiSerial.begin(9600);
  wifi_Init();
  server_Connect();
}
void wifi_Init()
{
  do {
    WiFi.init(&wifiSerial);
    if (WiFi.status() == WL_NO_SHIELD) {
#ifdef DEBUG_WIFI
      Serial.println("WiFi shield not present");
#endif
    }
    else
      break;
  } while (1);

#ifdef DEBUG_WIFI
  Serial.print("Attempting to connect to WPA SSID: ");
  Serial.println(AP_SSID);
#endif
  while (WiFi.begin(AP_SSID, AP_PASS) != WL_CONNECTED) {
#ifdef DEBUG_WIFI
    Serial.print("Attempting to connect to WPA SSID: ");
    Serial.println(AP_SSID);
#endif
  }

#ifdef DEBUG_WIFI
  Serial.println("You're connected to the network");
  printWifiStatus();
#endif
}
int server_Connect()
{
#ifdef DEBUG_WIFI
  Serial.println("Starting connection to server...");
#endif

  if (client.connect(SERVER_NAME, SERVER_PORT)) {
#ifdef DEBUG_WIFI
    Serial.println("Connect to server");
#endif
    client.print("["LOGID":"PASSWD"]");
  }
  else
  {
#ifdef DEBUG_WIFI
    Serial.println("server connection failure");
#endif
  }
}
void printWifiStatus()
{
  // print the SSID of the network you're attached to

  Serial.print("SSID: ");
  Serial.println(WiFi.SSID());

  // print your WiFi shield's IP address
  IPAddress ip = WiFi.localIP();
  Serial.print("IP Address: ");
  Serial.println(ip);

  // print the received signal strength
  long rssi = WiFi.RSSI();
  Serial.print("Signal strength (RSSI):");
  Serial.print(rssi);
  Serial.println(" dBm");
}
