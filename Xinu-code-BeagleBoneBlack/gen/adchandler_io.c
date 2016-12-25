struct dentry *devptr;
struct adc_csreg *csrptr;
volatile uint32 irq_status = 0;
devptr = (struct dentry *) &devtab[ADC_SENSOR];
csrptr = (struct adc_csreg *)devptr->dvcsr;
irq_status = csrptr->irq_status;
csrptr->irq_status = irq_status;
adc_step_disable(csrptr, 0);
