#include <xinu.h>

/*
 * Write to pin GPIO_WRITE - For switching LED on/off
 */
devcall gpiowrite( struct dentry *devptr, char *buff, int32 count)
{
	struct gpio_sensor_csreg *reg = (struct gpio_sensor_csreg *) devptr->dvcsr;

	//Using count to check mode 0 - clear, 1- set
	
	//Configuring GPIO for output	
	reg->oe &= ~(1 << GPIO_WRITE);

	switch(count) {

			#include "../gen/gpiowrite_io.c"
	             /*case GPIO_CLEAR:
			     //clear pin GPIO_WRITE
			     reg->clear_data_out |= (1 << GPIO_WRITE);
			     break;
	             case GPIO_SET:
			     // Writng to pin GPIO_WRITE
			     reg->data_out |= (1 << GPIO_WRITE);
			     break;*/

	}
	
	return OK;
}
