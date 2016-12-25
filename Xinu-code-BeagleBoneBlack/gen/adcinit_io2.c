void adc_step_average(struct adc_csreg *reg,int step_select)
{
reg->step[step_select].step_config |= (STEP_AVG << 2);
}
void adc_step_enable(struct adc_csreg *reg,uint32 step_select)
{
reg->step_enable |= (0x01<<step_select);
}
void adc_step_disable(struct adc_csreg *reg,uint32 step_select)
{
reg->step_enable &= ~(0x01<<step_select);
}
