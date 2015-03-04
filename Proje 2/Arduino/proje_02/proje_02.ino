#include <Max3421e.h>
#include <Usb.h>
#include <AndroidAccessory.h>

#define COMMAND_LED 0x2
#define TARGET_PIN_2 0x2

#define PIN 2

AndroidAccessory acc("Uretici",
                     "Project02",
		     "Aciklama",
		     "Versiyon",
		     "URI",
		     "Seri");

byte rcvmsg[3];

void setup() {
  Serial.begin(19200);
  acc.powerOn();
  pinMode(PIN, OUTPUT);
}

void loop() {
  if (acc.isConnected()) {
    // alınan veriyi bayt dizisinde okumak için
    int len = acc.read(rcvmsg, sizeof(rcvmsg), 1);
    if (len > 0) {
      if (rcvmsg[0] == COMMAND_LED) {
        if (rcvmsg[1] == TARGET_PIN_2){
          // analog değeri alma
	  byte value = rcvmsg[2];
          // çıkış pinini analog değere göre ayarlamak için
          analogWrite(PIN, value);
        }
      }
    }
  }
}

