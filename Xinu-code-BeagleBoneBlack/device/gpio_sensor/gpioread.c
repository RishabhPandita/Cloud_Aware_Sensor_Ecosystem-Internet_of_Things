#include<xinu.h>
extern sid32 gpio_readsem;
devcall gpioread(struct dentry * devptr, char *buff, int32 count){
struct gpio_sensor_csreg *reg = (struct gpio_sensor_csreg*) devptr->dvcsr;
uint32 readcount = 0;
intmask mask;
struct sentry *semptr;
mask = disable();
semptr = &semtab[gpio_readsem];
readcount = semptr->scount;
if(readcount > 0) {
semptr->scount = 0;
}
restore(mask);
gpio_clear_interrupt_status(reg);
gpio_set_interrupt_bit(reg, INTR_BIT);
wait(gpio_readsem);
 write(GPIO_SENSOR,NULL,1);
sleep(1);
 write(GPIO_SENSOR,NULL,0);
return OK;
}