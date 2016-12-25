#include <xinu.h>

extern sid32 adc_readsem;

/*
 * Interrupt handler code for ADC temperature sensor - AINO
 */
void adchandler(uint32 num)
{
	#include "../gen/adchandler_io.c"
	/*
	struct dentry *devptr;
	struct adc_csreg *csrptr;
	volatile uint32 irq_status = 0;


	devptr = (struct dentry *) &devtab[ADC_SENSOR];
	csrptr = (struct adc_csreg *) devptr->dvcsr;
	irq_status = csrptr->irq_status;
	csrptr->irq_status = irq_status;

	adc_step_disable(csrptr, 0);
	*/
	semcount(adc_readsem);
	signal(adc_readsem);
	
	return;
	
}
