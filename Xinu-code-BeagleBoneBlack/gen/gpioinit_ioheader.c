#define INTERRUPT_CONTROLLER (0x48200000)
#define INTR_BIT (2)
void gpio_set_interrupt_bit(struct gpio_sensor_csreg *reg, uint32 irq_enable_bit){
reg->irq_status_set_0|=irq_enable_bit;
}
void gpio_clear_interrupt_status(struct gpio_sensor_csreg *reg){
reg->irq_status_0|=0xFFFF;
}
void gpio_set_rising_edge_detect(struct gpio_sensor_csreg *reg, uint32 irq_enable_bit){
reg->rising_detect|=irq_enable_bit;
}
