case GPIO_CLEAR:
	reg->clear_data_out|=(1 << GPIO_WRITE);
break;
case GPIO_SET:
	reg->data_out|=(1 << GPIO_WRITE);
break;
