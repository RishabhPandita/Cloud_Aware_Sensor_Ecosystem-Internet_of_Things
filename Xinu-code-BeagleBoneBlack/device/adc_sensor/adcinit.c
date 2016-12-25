#include <xinu.h>

sid32 adc_readsem;

/*
 * Initiliaze system & adc clock
 */
void initialize_adc_clock()
{
	
	HWREG(CM_WKUP_CONTROL_CLKCTRL) |= 0x02;                             
	while(( HWREG(CM_WKUP_CONTROL_CLKCTRL) & 0x03 ) != 0x02 );          	

	HWREG(CM_WKUP_CLKSTCTRL) |= 0x02;                                   
	while(( HWREG(CM_WKUP_CLKSTCTRL) & 0x03 ) != 0x02 );                 
	
	HWREG(CM_WKUP_CLKSTCTRL) |= 0x02;
	while(( HWREG(CM_WKUP_CLKSTCTRL) & 0x03 ) != 0x02 );                
	
	HWREG(CM_L3_AON_CLKSTCTRL) |= 0x02;                                 
	while(( HWREG(CM_L3_AON_CLKSTCTRL) & 0x03 ) != 0x02 );              
	
	HWREG(CM_WKUP_ADC_TSC_CLKCTRL) |= 0x02;                             
	while(( HWREG(CM_WKUP_ADC_TSC_CLKCTRL) & 0x03 ) != 0x02 );          
	
	while(( HWREG(CM_WKUP_CONTROL_CLKCTRL) & (0x03<<16) ) != 0x00 );            
	while(( HWREG(CM_L3_AON_CLKSTCTRL) & (0x01<<3) ) == 0x00 );                 
	while(( HWREG(CM_WKUP_L4WKUP_CLKCTRL) & (0x03<<16) ) != 0x00 );             
	while(( HWREG(CM_WKUP_CLKSTCTRL) & (0x01<<2) ) == 0x00 );                   
	while(( HWREG(CM_WKUP_CM_L4_WKUP_AON_CLKSTCTRL) & (0x01<<2) ) == 0x00 );    
	while(( HWREG(CM_WKUP_CLKSTCTRL) & (0x01<<14) ) == 0x00 );                  
	while(( HWREG(CM_WKUP_ADC_TSC_CLKCTRL) & (0x03<<16) ) != 0x00 );            

}

/*
 * Initialize and register interrupt for ADC temperaature sensor
 */
void initialize_adc_interrupt(struct dentry *devptr)
{
	struct	intc_csreg *csrptr = (struct intc_csreg *)INTERRUPT_CONTROLLER_BASE_ADDR;   

	csrptr->threshold = 0X0FF;

	set_evec(devptr->dvirq, (uint32) devptr->dvintr);
	
	csrptr->ilr[devptr->dvirq] &= ~(0x01);                          
	csrptr->ilr[devptr->dvirq] |= (0x0A<<2);                        
}

/*
 * Configure clock working frequency
 */
void adc_configure_afe_clock(struct adc_csreg *reg, uint32 clock_freq)
{
	reg->adc_clk_div &= ~(0xFFFF);
	reg->adc_clk_div |= ((CLOCK_FREQ_MOD/clock_freq) - 1);
}

/*
 * Disable write protect bit to enable config write 
 */

#include "../gen/adcinit_io.c"

/*
 * Step config for ADC
 */
void adc_step_config(struct adc_csreg *reg, uint32 step_select, uint32 positive_channel, uint32 positive_ref, uint32 negative_channel, uint32 negative_ref)
{
	
	reg->step[step_select].step_config &= ~ SEL_RFM_SWC_CLEAR;                
	reg->step[step_select].step_config |= negative_ref<<SEL_RFM_SWC_SHIFT;     

	
	reg->step[step_select].step_config &= ~ SEL_INP_SWC_CLEAR;               
	reg->step[step_select].step_config |= positive_channel<<SEL_INP_SWC_SHIFT; 

	reg->step[step_select].step_config &= ~ SEL_INM_SWC_CLEAR;                
	reg->step[step_select].step_config |= negative_channel<<SEL_INM_SWC_SHIFT; 


	reg->step[step_select].step_config &= ~ SEL_RFP_SWC_CLEAR;        
	reg->step[step_select].step_config |= positive_ref<<SEL_RFP_SWC_SHIFT;      
}

/*
 * Configure analog supply config for step 
 */
void adc_step_analog_supply_config(struct adc_csreg *reg, uint32 step_select, uint32 xppsw, uint32 xnpsw, uint32 yppsw)
{
	reg->step[step_select].step_config &= ~ ADC_STEPCONFIG_XPPSW_SWC_CLEAR;
	reg->step[step_select].step_config |= xppsw<<ADC_STEPCONFIG_XPPSW_SWC_SHIFT;

	reg->step[step_select].step_config &= ~ ADC_STEPCONFIG_XNPSW_SWC_CLEAR;
	reg->step[step_select].step_config |= xnpsw<<ADC_STEPCONFIG_XNPSW_SWC_SHIFT;

	reg->step[step_select].step_config &= ~ ADC_STEPCONFIG_YPPSW_SWC_CLEAR;
	reg->step[step_select].step_config |= yppsw<<ADC_STEPCONFIG_YPPSW_SWC_SHIFT;
}

/*
 * Configure analog ground for step
 */
void adc_step_analog_ground_config(struct adc_csreg *reg, uint32 step_select,  uint32 xnnsw, uint32 ypnsw, uint32 ynnsw, uint32 wpnsw)
{
	reg->step[step_select].step_config &= ~ ADC_STEPCONFIG_XNNSW_SWC_CLEAR;
	reg->step[step_select].step_config |=  xnnsw<<ADC_STEPCONFIG_XNNSW_SWC_SHIFT;

	reg->step[step_select].step_config &= ~ ADC_STEPCONFIG_YPNSW_SWC_CLEAR;
	reg->step[step_select].step_config |=  ypnsw<<ADC_STEPCONFIG_YPNSW_SWC_SHIFT;

	reg->step[step_select].step_config &= ~ ADC_STEPCONFIG_YNNSW_SWC_CLEAR;
	reg->step[step_select].step_config |=  ynnsw<<ADC_STEPCONFIG_YPNSW_SWC_SHIFT;

	reg->step[step_select].step_config &= ~ ADC_STEPCONFIG_WPNSW_SWC_CLEAR;
	reg->step[step_select].step_config |=  wpnsw<<ADC_STEPCONFIG_WPNSW_SWC_SHIFT;	
}

/*
 * Select fifo pipe to use
 */
void adc_fifo_select(struct adc_csreg *reg, unsigned int step_select,unsigned int fifo_no)
{
	reg->step[step_select].step_config &= ~ ADC_FIFO_SELECTION_CLEAR;
	reg->step[step_select].step_config |= fifo_no <<ADC_FIFO_SELECTION_SHIFT;
}


/*
 * Set mode of operation for step
 */
void adc_step_mode(struct adc_csreg *reg, unsigned int step_select,unsigned int mode)
{
	reg->step[step_select].step_config &= ~ADC_MODE_CLEAR;
	reg->step[step_select].step_config |= mode<<ADC_MODE_SHIFT;
}

/*
 * Set avergage step value for ADC
 */

#include "../gen/adcinit_io2.c"


/*
 * Configure step registers for ADC
 */
void step_config(struct adc_csreg *reg)
{
	adc_step_op_mode(reg, 0, ADC_SE_OP_MODE);

	adc_step_config(reg, 0, ADC_CHANNEL_1, ADC_POSITIVE_REF, ADC_CHANNEL_1, ADC_NEGATIVE_REF);

	adc_step_analog_supply_config(reg, 0, 0, 0, 0);

	adc_step_analog_ground_config(reg, 0, 0, 0, 0, 0);

	adc_fifo_select(reg, 0, ADC_FIFO_0);

	adc_step_mode(reg, 0, ADC_STEP_CONT);

	adc_step_average(reg, 0);
}


/*
 * Configure ADC config registers and interrupts
 */
void adc_configure(struct adc_csreg *reg)
{
	adc_configure_afe_clock(reg, CLOCK_FREQ);

	adc_disable_write_protect(reg);

	step_config(reg);

	adc_config_operation_mode(reg, ADC_GP_MODE);

	adc_clear_interrupt_status(reg);

	adc_set_interrupt_bit(reg, INTR_BIT);

	adc_enable(reg);
}


/*
 * Initiliase ADC cotrol registers and interrupts
 */
devcall adcinit(struct dentry *devptr)
{
	struct adc_csreg *reg = (struct adc_csreg *) devptr->dvcsr;
	
	adc_readsem = semcreate(0);

	initialize_adc_clock();

	initialize_adc_interrupt(devptr);

	adc_configure(reg);

	return OK;
}
