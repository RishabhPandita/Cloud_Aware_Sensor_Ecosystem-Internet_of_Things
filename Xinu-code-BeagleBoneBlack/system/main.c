/*  main.c  - main */
#include <xinu.h>
#define DEVICE_MODE '1'          // '1' for Temp mode  '2' for Sound Mode

pid32 receiver1_id;              // Receiver Process 
pid32 tft_id;                    // TFT Mode process
sid32 mutex;                     
int k = 0;                       // UDP Register Value


process tftMode(char *buff)       // TFT Mode Process
{ char buffcopy[9];
  strcpy(buffcopy,buff);
  printf(" TFT Mode On\n");
  while (1)
   {  printf("Inside TFT Send\n");
      read(GPIO_SENSOR, NULL, 0);
      buffcopy[4] = '9';  // Sending 9999 for TFT on Mode
      buffcopy[5] = '9'; 
      buffcopy[6] = '9'; 
      buffcopy[7] = '9';
      send_udp(buffcopy);
   }

	
}

void sendProcess(char recbuff[16])
    {
            
            int i,j;
            char buff[9];
            char set_tf[8];   // TFT Mode Variable ( settfon, settfof)
            char dev_id[5];   // Device ID ( eg: 2222)
            char req_id[4];   // Request ID ( eg: 1001)
            char ret[8];           
            for ( j = 0; j<15; j++)
              {
                if( j < 7 )  
                   set_tf[j] = recbuff[j];
                else if( j > 6 && j< 11 )  
                  dev_id[j-7] = recbuff[j];
                else   
                  req_id[j-11] = recbuff[j];
               }

	    dev_id[4] = '\0';
	    set_tf[7] = '\0';
            strcpy(buff, req_id);
//	    printf("recbuff=%s TFT Mode:%s, devid:%s\n", recbuff, set_tf, dev_id);
   
            if (strcmp(set_tf,"gettemp") == 0 && strcmp(dev_id,"1111") == 0)  // Gettin Temp
             {  read(ADC_SENSOR,ret,0);
                printf(" Temp %s", ret);
                for (j = 0 ; j<4; j++)
                    buff[j+4] = ret[j];
                send_udp(buff);                                               // Calling UDP send function
             }                
              
            else if (strcmp(set_tf,"gtsound") == 0 && strcmp(dev_id,"2222") == 0) // Getting Sound
             {  read(GPIO_SENSOR, NULL, 0);
                buff[4] = '1';
                buff[5] = '1';
                buff[6] = '1'; 
                buff[7] = '1'; 
                send_udp(buff);                                               // Calling UDP send function  
              }    
       
            else if (strcmp(set_tf,"settfon") == 0 && strcmp(dev_id,"2222") == 0) // Checking TFT Mode On
             { 
	            printf("Inside tfon section\n");
	            tft_id = create(tftMode, 4096, 50, "tft", 1, buff);             // Creating Sound Read process to listen for sound interrupt
	            resume(tft_id);                                           // Resuming Sound Read process 
	     } 

            else if (strcmp(set_tf,"settfof") == 0 && strcmp(dev_id,"2222") == 0) // Checking TFT Mode OFF
             {      struct dentry *devptr;
	            struct gpio_sensor_csreg *csrptr;
                    devptr = (struct dentry *) &devtab[GPIO_SENSOR];
	            csrptr = (struct gpio_sensor_csreg *) devptr->dvcsr;
                    gpio_clear_interrupt_bit(csrptr, INTR_BIT);
                    gpio_clear_interrupt_status(csrptr);
		    kill(tft_id);						   // Killing Sound-Read process to disable TFT Mode

                     printf(" TFT Mode Disabled\n");                                  
                     buff[4] = '9';                                                // Sending 9998 to acknowledge TFT Mode OFF
                     buff[5] = '9';
                     buff[6] = '9'; 
                     buff[7] = '8'; 
                     send_udp(buff);                                               // Calling UDP send function to acknowledge TFT Mode OFF
 
	      }
            else if (strcmp(set_tf,"tfttemp") == 0 && strcmp(dev_id,"2222") == 0) // Printing Temp in TFT Mode 
                 {
                     req_id[4] ='\0';
                     printf(" Temperature from TFT Mode is %s\n ",req_id);        // Req_id contains temperature in this case
                     buff[4] = '0';                                                // Sending 0000 to acknowledge temperature receive
                     buff[5] = '0';
                     buff[6] = '0'; 
                     buff[7] = '0'; 
                     send_udp(buff);
                  } 
   }



void send_udp(char *buff)

{
            buff[8] = '\0';               // Terminating return string
            printf("\nSend Started");
            if (k == 0) 
                k=udp_register(3232235623,8888,500); //remote ip, remote port, local
 //           printf("Slot is  %d", k);
 //           printf(" Buffer is %s", buff);
            
            if(udp_sendto(k,3232235623,8888,buff,strlen(buff)) == SYSERR)        // Sending Reply to Edge Server (eg: temp,sound,tft mode)
               printf("\nError in Send");
            else
               printf("\n Sent");
               
              


    }




    process recProcess(void)
    {
//          wait(mutex);
            printf("\nRECV Started");
            char buff[15];
            sleepms(5000);
            int k=udp_register(0,0,600); //remote ip, remote port, local port. 0 means any
            	
            while (1)
	    {
		int r = udp_recv(k,buff,100, 1000);
	       
		if (r == TIMEOUT)
		{
	//		printf("time out\n");
			continue;
		}
		else if (r == SYSERR)
		{
			printf("syserr\n");
		}
		else
		{        
	//	  printf("Buffer is %s\n",buff);
        //     	  printf(" len= %d\n",r);
                  sendProcess(buff);
                        
		}
	
	
	
        }
               freemem(buff,15);
           
//                signal(mutex);
                return OK;

    }





process	main(void)
 {


	recvclr();
        uint32 ipaddr;
        char	str[128];
        char    buff[20];
        uint32 i;
        uint32 len;
        int r;
        ipaddr = NetData.ipucast;
		sprintf(str, "%d.%d.%d.%d",
			(ipaddr>>24)&0xff, (ipaddr>>16)&0xff,
			(ipaddr>>8)&0xff,        ipaddr&0xff);
        len = strlen(str);
        
        for ( i = 0; i < (len+4) ; i++)
          {   if (i < 4)                      // For Device ID
                 buff[i] = DEVICE_MODE;         
              else
                 buff[i] = str[i-4];          // For IP Address
          }         

        buff[len+4] = '\0';
//        printf("Buffer IP packet %s ",buff);
  
        r = udp_register(3232235623,9991,900); //remote ip, remote port, local            
        if(udp_sendto(r,3232235623,9991,buff,strlen(buff)) == SYSERR)
           printf("\nError in IP Send");
        else
           printf("\n Sent IP %s ",str);

        receiver1_id = create(recProcess, 4096, 50, "receive", 0);
        mutex = semcreate(1);
          
        resched_cntl(DEFER_START);
        resume(receiver1_id);
        resched_cntl(DEFER_STOP);
	return OK;
    
 }


 	
