#include <Max3421e.h>
#include <Usb.h>
#include <AndroidAccessory.h>


#define COMMAND_LED 0x2
#define TARGET_PIN_2 0x2
#define VALUE_ON 0x1
#define VALUE_OFF 0x0
#define PIN 2

AndroidAccessory acc("Uretici","Model","Aciklama","Versiyon","URI","Seri");
byte rcvmsg[3];

void setup() {
Serial.begin(115200);
acc.powerOn();
pinMode(PIN, OUTPUT);
}

void loop() {
if (acc.isConnected()) {
  
//bayt dizisinde gelen datayı okumak için
int len = acc.read(rcvmsg, sizeof(rcvmsg), 1);
if (len > 0) {
if (rcvmsg[0] == COMMAND_LED) {
if (rcvmsg[1] == TARGET_PIN_2){
//Anahtar(Switch)'ınn durumunu almak için
byte value = rcvmsg[2];

// çıkış pinini anatharın durumuna göre ayarlamak için
if(value == VALUE_ON) {
digitalWrite(PIN, HIGH);

} else if(value == VALUE_OFF) {
digitalWrite(PIN, LOW);
}
}
}
}
}
}

