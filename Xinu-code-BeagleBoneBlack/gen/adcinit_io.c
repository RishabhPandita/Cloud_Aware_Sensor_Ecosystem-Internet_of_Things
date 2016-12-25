void adc_disable_write_protect(struct adc_csreg *reg)
{
reg->ctrl &= ~(WRITE_PROTECT_BIT<<2);
reg->ctrl |= (WRITE_PROTECT_BIT<<2);
}
void adc_config_operation_mode(struct adc_csreg *reg,uint32 mode)
{
reg->ctrl &= ~(ADC_OP_MODE_CLEAR);
reg->ctrl |=  (mode<< ADC_OP_MODE_SHIFT);
}
void adc_clear_interrupt_status(struct adc_csreg *reg)
{
reg->irq_status |= 0x7FFF;
}
void adc_set_interrupt_bit(struct adc_csreg *reg,uint32 irq_enable_bit)
{
reg->irq_enable_set |= irq_enable_bit;
}
void adc_enable(struct adc_csreg *reg)
{
reg->ctrl |= ADC_ENABLE_BIT;
}
void adc_step_op_mode(struct adc_csreg *reg,uint32 step_select,  uint32 mode)
{
reg->step[step_select].step_config &= ~(ADC_SC_DIFF_CTRL);
}
