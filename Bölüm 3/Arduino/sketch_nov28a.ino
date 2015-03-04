#include <Max3421e.h>
#include <Usb.h>
#include <AndroidAccessory.h>
#define ARRAY_SIZE 22
#define COMMAND_TEXT 0xF
#define TARGET_DEFAULT 0xF

AndroidAccessory acc("Uretici", "Model", "Aciklama",
"Versiyon", "URI", "Seri");

char hello[ARRAY_SIZE] = {'A','r','d','u','i','n','o',' ','d','a','n',' ','M', 'e', 'r', 'h', 'a','b', 'a', 'l', 'a', 'r'};
byte rcvmsg[255];
byte sntmsg[3 + ARRAY_SIZE];
void setup() {
Serial.begin(115200);
acc.powerOn();
}

void loop() {
if (acc.isConnected()) {

//bayt diziye gönderilen  mesajı okumak için

int len = acc.read(rcvmsg, sizeof(rcvmsg), 1);
if (len > 0) {
if (rcvmsg[0] == COMMAND_TEXT) {
if (rcvmsg[1] == TARGET_DEFAULT){


//checksum baytından textLength’i almak için
byte textLength = rcvmsg[2];
int textEndIndex = 3 + textLength;

//Serial olarak karakterleri basmak için

for(int x = 3; x < textEndIndex; x++) {
Serial.print((char)rcvmsg[x]);
delay(250);
}
Serial.println();

delay(250);
}
}
}
sntmsg[0]=COMMAND_TEXT;
sntmsg[1]=TARGET_DEFAULT;
sntmsg[2]=ARRAY_SIZE;
for(int x=0; x < ARRAY_SIZE; x++) {
  
sntmsg[3 + x] = hello[x];

}

acc.write(sntmsg, 3 + ARRAY_SIZE);
delay(250);
}
}
