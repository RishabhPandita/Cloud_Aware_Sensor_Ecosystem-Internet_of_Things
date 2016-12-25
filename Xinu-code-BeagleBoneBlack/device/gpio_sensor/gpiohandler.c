#include <xinu.h>

extern sid32 gpio_readsem;

/*
 * Interrupt handler code for GPIO_SENSOR
 */
void gpiohandler(uint32 num)
{
	struct dentry *devptr;
	struct	gpio_sensor_csreg *csrptr;


	printf("Handler called!\n");
	
	volatile uint32 irq_status = 0;

	devptr = (struct dentry *) &devtab[GPIO_SENSOR];
	csrptr = (struct gpio_sensor_csreg *) devptr->dvcsr;

	irq_status = csrptr->irq_status_0;
	csrptr->irq_status_0 = irq_status;

	gpio_clear_interrupt_bit(csrptr, INTR_BIT);
	
	signal(gpio_readsem);

	return;
	
}


