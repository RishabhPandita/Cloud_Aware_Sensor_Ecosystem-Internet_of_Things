#include <xinu.h>

extern sid32 adc_readsem;

/*
 * Read temperature data from ADC input pin - AIN0
 */
devcall adcread(struct dentry *devptr, char *buff, uint32 count)
{
	
	struct adc_csreg *reg = (struct adc_csreg *) devptr->dvcsr;

	adc_step_enable(reg, 1);

	printf("Inside adc read!\n");
	wait(adc_readsem);

	uint32 data = 0;
	int i = 0;
	int count_fifo = reg->fifo_info[0].fifo_count;

	
	for(i = 0; i < count_fifo; i++) {
		data = reg->fifo_data_0 & (0xFFF);
	}

	float voltage;
	int temp_cel_int, temp_cel_dec;


	voltage = (data*1.8/4095)*1000;
	voltage = (voltage - 500.0)/10.0;

	temp_cel_int = (unsigned int) voltage;
	temp_cel_dec = (unsigned int) ((voltage - temp_cel_int) * 10);
	
	
	sprintf(buff, "%d.%d", temp_cel_int, temp_cel_dec);

	printf("REad called!\n");
	return OK;
}
