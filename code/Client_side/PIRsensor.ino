int IRswitch = 8; //熱釋電紅外感測器信號輸出端；
int led = 13; //信號指示燈輸入； 
boolean val = false; //讀取感測器即時信號； 



void setup() {
  // put your setup code here, to run once:
  

pinMode(IRswitch,INPUT); 
pinMode(led,OUTPUT); //定義埠屬性； 

}

void loop() {
  // put your main code here, to run repeatedly:

  val=digitalRead(IRswitch);
  
  if (val) {  
    digitalWrite(led,HIGH); //有人在監測範圍內活動，燈亮； 
    Serial.println("someone moving");
  }

  else {
    digitalWrite(led,LOW);
    Serial.println("2");
    
  }

  delay(1000);

}
