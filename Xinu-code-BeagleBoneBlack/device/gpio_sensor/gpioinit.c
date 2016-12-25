#include <xinu.h>

sid32 gpio_readsem;

#include "../gen/gpioinit_ioheader.c"

/*
 * Clear interrupt bit in GPIO irq status register for GPIO_65
 */
void gpio_clear_interrupt_bit(struct gpio_sensor_csreg *reg, uint32 irq_enable_bit)
{
	reg->irq_status_clr_0 |= irq_enable_bit;
}

/*
 * Initilaise interrupt for gpio - GPIO_65
 */
void gpio_interrupt_init(struct dentry *devptr)
{
	struct	intc_csreg *csrptr = (struct intc_csreg *) INTERRUPT_CONTROLLER;   
	set_evec(devptr->dvirq, (uint32)devptr->dvintr);                
}

/*
 * Configure GPIO for interrupts and initilisation
 */
void gpio_configure(struct gpio_sensor_csreg *reg)
{
	gpio_clear_interrupt_status(reg);
	//gpio_set_interrupt_bit(reg, INTR_BIT);
	gpio_set_rising_edge_detect(reg, INTR_BIT);
}

/*
 * Initilaise GPIO_65 for Sound Sensor
 */
devcall gpioinit(struct dentry *devptr)
{
	struct gpio_sensor_csreg* reg = (struct gpio_sensor_csreg *) devptr->dvcsr;

	gpio_readsem = semcreate(0);

	gpio_interrupt_init(devptr);

	gpio_configure(reg);

	return OK;
}
