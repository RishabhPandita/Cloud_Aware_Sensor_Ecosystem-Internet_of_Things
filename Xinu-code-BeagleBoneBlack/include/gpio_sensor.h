#ifndef GPIO_SENSOR_H
#define GPIO_SENSOR_H

/*
 * GPIO Control Registers
 */
struct gpio_sensor_csreg
{
	volatile uint32 revision;
	volatile uint32 reserved_1[3];
	volatile uint32 sysconfig;
	volatile uint32 reserved_2[3];
	volatile uint32 eoi;
	volatile uint32 irq_status_raw0;
	volatile uint32 irq_status_raw1;
	volatile uint32 irq_status_0;
	volatile uint32 irq_status_1;
	volatile uint32 irq_status_set_0;
	volatile uint32 irq_status_set_1;
	volatile uint32 irq_status_clr_0;
	volatile uint32 irq_status_clr_1;
	volatile uint32 irq_waken_0;
	volatile uint32 irq_waken_1;
	volatile uint32 reserved_3[50];
	volatile uint32 sys_status;
	volatile uint32 reserved_4[6];
	volatile uint32 ctrl;
	volatile uint32 oe;
	volatile uint32 data_in;
	volatile uint32 data_out;
	volatile uint32 level_detect_0;
	volatile uint32 level_detect_1;
	volatile uint32 rising_detect;
	volatile uint32 falling_detect;
	volatile uint32 debouncing_enable;
	volatile uint32 debouncing_time;
	volatile uint32 reserved_5[14];
	volatile uint32 clear_data_out;
	volatile uint32 set_data_out;
	
};

devcall gpioread(struct dentry *devptr, char *buff, int32 count);
devcall gpiowrite(struct dentry *devptr, char *buff, int32 count);
devcall gpioinit(struct dentry *devptr);
void gpiohandler(uint32 num);

void gpio_set_interrupt_bit(struct gpio_sensor_csreg *reg, uint32 irq_enable_bit);

void gpio_clear_interrupt_bit(struct gpio_sensor_csreg *reg, uint32 irq_enable_bit);
void gpio_clear_interrupt_status(struct gpio_sensor_csreg *reg);
#include "../gen/gpioheader_io.c"

/*
#define REG_HW(n) (*((volatile uint32 *)(n)))

//GPIO2
#define GPIO_BASE_ADDR 0x481AC000

#define GPIO_SET 1
#define GPIO_CLEAR 0

#define GPIO_WRITE 2
*/
#endif

