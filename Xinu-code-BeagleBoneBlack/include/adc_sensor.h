#ifndef ADC_SENSOR_H
#define ADC_SENSOR_H

/*
 * ADC Control and Data Registers
 */
struct adc_csreg
{
	volatile uint32 revison;
	volatile uint32 reserved_1[3];
	volatile uint32 sys_config;
	volatile uint32 reserved_2[4];
	volatile uint32 irq_status_raw;
	volatile uint32 irq_status;
	volatile uint32 irq_enable_set;	
	volatile uint32 irq_enable_clear;
	volatile uint32 irq_wakeup;
	volatile uint32 dma_enable_set;
	volatile uint32 dma_enable_clear;
	volatile uint32 ctrl;
	volatile uint32 adc_stat;
	volatile uint32 adc_range;
	volatile uint32 adc_clk_div;
	volatile uint32 adc_misc;
	volatile uint32 step_enable;
	volatile uint32 idle_config;
	volatile uint32 ts_charge_step_config;
	volatile uint32 ts_charge_delay;
	struct 
	{
		volatile uint32 step_config;
		volatile uint32 step_delay;
	}step[16];
	struct 
	{
		volatile uint32 fifo_count;
		volatile uint32 fifo_threshold;
		volatile uint32 dma_req;
	}fifo_info[2];
	volatile uint32 reserved_3;
	volatile uint32 fifo_data_0;
	volatile uint32 reserved_4[63];
	volatile uint32 fifo_data_1;
};

devcall adcread(struct dentry *devptr, char *buff, uint32 count);
devcall adcinit(struct dentry *devptr);
void adchandler(uint32 num);

#include "../gen/adcheader_io.c"

/*
#define ADC_BASE_ADDR (0x44E0D000)
#define INTERRUPT_CONTROLLER_BASE_ADDR (0x48200000)
#define ADC_ENABLE_BIT (0x01)
#define CLOCK_FREQ_MOD (24000000)
#define CLOCK_FREQ (3000000)
#define ADC_GP_MODE (0)
#define INTR_BIT (0x01<<1)
#define WRITE_PROTECT_BIT (0x01)
#define ADC_OP_MODE_CLEAR (0x03<<5)
#define ADC_OP_MODE_SHIFT (5)
#define STEP_AVG (0x04)


#define ADC_FIFO_0 (0)
#define ADC_FIFO_SELECTION_CLEAR (0X01<<26)
#define ADC_FIFO_SELECTION_SHIFT (26)


#define ADC_SC_DIFF_CTRL (0x01<<25)

#define SEL_RFM_SWC_CLEAR (0X03<<23)	
#define SEL_RFM_SWC_SHIFT (23)		
#define SEL_INP_SWC_CLEAR (0XF<<19)	
#define SEL_INP_SWC_SHIFT (19)		
#define SEL_INM_SWC_CLEAR (0X0F<<15)	
#define SEL_INM_SWC_SHIFT (15)		
#define SEL_RFP_SWC_CLEAR (0x07<<12)	
#define SEL_RFP_SWC_SHIFT (12)		


#define ADC_STEPCONFIG_WPNSW_SWC_CLEAR (0x01<<11)
#define ADC_STEPCONFIG_WPNSW_SWC_SHIFT (0x0000000Bu)

#define ADC_STEPCONFIG_XNNSW_SWC_CLEAR (0x01<<6)
#define ADC_STEPCONFIG_XNNSW_SWC_SHIFT (0x00000006u)

#define ADC_STEPCONFIG_XNPSW_SWC_CLEAR (0x01<<9)
#define ADC_STEPCONFIG_XNPSW_SWC_SHIFT (0x00000009u)

#define ADC_STEPCONFIG_XPPSW_SWC_CLEAR (0x01<<5)
#define ADC_STEPCONFIG_XPPSW_SWC_SHIFT (0x00000005u)

#define ADC_STEPCONFIG_YNNSW_SWC_CLEAR (0x01<<8)
#define ADC_STEPCONFIG_YNNSW_SWC_SHIFT (0x00000008u)

#define ADC_STEPCONFIG_YPNSW_SWC_CLEAR (0x01<<10)
#define ADC_STEPCONFIG_YPNSW_SWC_SHIFT (0x0000000Au)

#define ADC_STEPCONFIG_YPPSW_SWC_CLEAR (0x01<<7)
#define ADC_STEPCONFIG_YPPSW_SWC_SHIFT (0x00000007u)


#define ADC_MODE_CLEAR (0X03)
#define ADC_MODE_SHIFT (0x00)


#define CM_PER                                (0x44E00000)		
#define CM_PER_L3S_CLKSTCTRL                  (0x44E00004)
#define CM_PER_L3_CLKSTCTRL                   (0x44E0000C)
#define CM_PER_L3_INSTR_CLKCTRL               (0x44E000DC)
#define CM_PER_L3_CLKCTRL                     (0x44E000E0)
#define CM_PER_OCPWP_L3_CLKSTCTRL             (0x44E0012C)

#define CM_WKUP                               (0x44E00400)		
#define CM_WKUP_CLKSTCTRL                     (0x44E00400)
#define CM_WKUP_CONTROL_CLKCTRL               (0x44E00404)
#define CM_WKUP_L4WKUP_CLKCTRL                (0x44E0040C)
#define CM_L3_AON_CLKSTCTRL                   (0x44E00418)
#define CM_WKUP_ADC_TSC_CLKCTRL               (0x44E004BC)
#define CM_WKUP_CM_L4_WKUP_AON_CLKSTCTRL      (0x44E004CC)

#define ADC_CHANNEL_1 (0)
#define ADC_NEGATIVE_REF (0)
#define ADC_POSITIVE_REF (0)
#define ADC_STEP_CONT (0x01)

#define ADC_SE_OP_MODE (0)

#define HWREG(n)	(*((volatile unsigned int *)(n)))
*/

#endif
