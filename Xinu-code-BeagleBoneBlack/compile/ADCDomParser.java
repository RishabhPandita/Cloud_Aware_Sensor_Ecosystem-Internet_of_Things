//package parser;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.HashMap;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

public class ADCDomParser {

	public static void main(String[] args) {
		try {

			File ddlInput = new File("ADCInput.xml");
			DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
			DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
			Document doc = dBuilder.parse(ddlInput);
			doc.getDocumentElement().normalize();
			genHeader(doc);
			genHandler(doc);
			genInit(doc);
		} catch (Exception ex) {
			ex.printStackTrace();
		}

	}

	private static void genInit(Document doc) {
		
		HashMap<String, String> initXML = new HashMap<String, String>();
		ArrayList<String> tags = new ArrayList<String>();
		tags.add("adcDisableWriteProtect");
		tags.add("adcConfigOperationMode");
		tags.add("adcClearInterruptStatus");
		tags.add("adcSetInterruptBit");
		tags.add("adcStepOpMode");
		tags.add("adcEnable");
		tags.add("adcStepAverage");
		tags.add("adcStepEnable");
		tags.add("adcStepDisable");
		putXmlToMap(doc, initXML, tags, "functionName");
		tags.clear();
		tags.add("sensorStructure");
		tags.add("sensor_csreg");
		putXmlToMap(doc, initXML, tags, "read");
		tags.clear();
		tags.add("adc_cntrl");
		tags.add("irqStatus");
		tags.add("irqEnable");
		tags.add("step");
		tags.add("stepConfig");
		tags.add("stepEnable");
		putXmlToMap(doc, initXML, tags, "regAndPin");
		printHashMap(initXML);
		generateInitCode(initXML);
		
		
	}
	
	
	private static void generateInitCode(HashMap<String, String> initXML) {

		try {
			File file = new File("..//gen//adcinit_io.c");
			PrintWriter pr = new PrintWriter(file);
			pr.write("void "+initXML.get("adcDisableWriteProtect")+"(struct "+initXML.get("sensorStructure")+" *"+initXML.get("sensor_csreg")+")\n{\n");
			pr.write(initXML.get("sensor_csreg")+"->"+initXML.get("adc_cntrl")+" &= ~(WRITE_PROTECT_BIT<<2);\n");
			pr.write(initXML.get("sensor_csreg")+"->"+initXML.get("adc_cntrl")+" |= (WRITE_PROTECT_BIT<<2);\n}\n");			
			
			pr.write("void "+initXML.get("adcConfigOperationMode")+"(struct "+initXML.get("sensorStructure")+" *"+initXML.get("sensor_csreg")+",uint32 mode"+")\n{\n");
			pr.write(initXML.get("sensor_csreg")+"->"+initXML.get("adc_cntrl")+" &= ~(ADC_OP_MODE_CLEAR);\n");
			pr.write(initXML.get("sensor_csreg")+"->"+initXML.get("adc_cntrl")+" |=  (mode<< ADC_OP_MODE_SHIFT);\n}\n");
			
			pr.write("void "+initXML.get("adcClearInterruptStatus")+"(struct "+initXML.get("sensorStructure")+" *"+initXML.get("sensor_csreg")+")\n{\n");
			pr.write(initXML.get("sensor_csreg")+"->"+initXML.get("irqStatus")+" |= 0x7FFF;\n}\n");
			
			pr.write("void "+initXML.get("adcSetInterruptBit")+"(struct "+initXML.get("sensorStructure")+" *"+initXML.get("sensor_csreg")+",uint32 irq_enable_bit"+")\n{\n");
			pr.write(initXML.get("sensor_csreg")+"->"+initXML.get("irqEnable")+" |= irq_enable_bit;\n}\n");
			
			pr.write("void "+initXML.get("adcEnable")+"(struct "+initXML.get("sensorStructure")+" *"+initXML.get("sensor_csreg")+")\n{\n");
			pr.write(initXML.get("sensor_csreg")+"->"+initXML.get("adc_cntrl")+" |= ADC_ENABLE_BIT;\n}\n");
			
			pr.write("void "+initXML.get("adcStepOpMode")+"(struct "+initXML.get("sensorStructure")+" *"+initXML.get("sensor_csreg")+",uint32 step_select,  uint32 mode"+")\n{\n");
			pr.write(initXML.get("sensor_csreg")+"->"+initXML.get("step")+"[step_select]."+initXML.get("stepConfig")+" &= ~(ADC_SC_DIFF_CTRL);\n}\n");
			pr.close();
			
			PrintWriter pr1 = new PrintWriter("..//gen//adcinit_io2.c");
			pr1.write("void "+initXML.get("adcStepAverage")+"(struct "+initXML.get("sensorStructure")+" *"+initXML.get("sensor_csreg")+",int step_select"+")\n{\n");
			pr1.write(initXML.get("sensor_csreg")+"->"+initXML.get("step")+"[step_select]."+initXML.get("stepConfig")+" |= (STEP_AVG << 2);\n}\n");

			pr1.write("void "+initXML.get("adcStepEnable")+"(struct "+initXML.get("sensorStructure")+" *"+initXML.get("sensor_csreg")+",uint32 step_select"+")\n{\n");
			pr1.write(initXML.get("sensor_csreg")+"->"+initXML.get("stepEnable")+ " |= (0x01<<step_select);\n}\n");
			
			pr1.write("void "+initXML.get("adcStepDisable")+"(struct "+initXML.get("sensorStructure")+" *"+initXML.get("sensor_csreg")+",uint32 step_select"+")\n{\n");
			pr1.write(initXML.get("sensor_csreg")+"->"+initXML.get("stepEnable")+ " &= ~(0x01<<step_select);\n}\n");
			
			pr1.close();
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	
	
	
	
	
	private static void genHeader(Document doc) {
		HashMap<String, String> headerXML = new HashMap<String, String>();
		ArrayList<String> tags = new ArrayList<String>();
		tags.add("ADC_BASE_ADDR");
		tags.add("INTERRUPT_CONTROLLER_BASE_ADDR");
		tags.add("ADC_ENABLE_BIT");
		tags.add("CLOCK_FREQ_MOD");
		tags.add("CLOCK_FREQ");
		tags.add("ADC_GP_MODE");
		tags.add("INTR_BIT");
		tags.add("WRITE_PROTECT_BIT");
		tags.add("ADC_OP_MODE_CLEAR");
		tags.add("ADC_OP_MODE_SHIFT");
		tags.add("STEP_AVG");
		tags.add("ADC_FIFO_0");
		tags.add("ADC_FIFO_SELECTION_CLEAR");
		tags.add("ADC_FIFO_SELECTION_SHIFT");
		tags.add("ADC_SC_DIFF_CTRL");
		tags.add("SEL_RFM_SWC_CLEAR");
		tags.add("SEL_RFM_SWC_SHIFT");
		tags.add("SEL_INP_SWC_CLEAR");
		tags.add("SEL_INP_SWC_SHIFT");
		tags.add("SEL_INM_SWC_CLEAR");
		tags.add("SEL_INM_SWC_SHIFT");
		tags.add("SEL_RFP_SWC_CLEAR");
		tags.add("SEL_RFP_SWC_SHIFT");
		tags.add("ADC_STEPCONFIG_WPNSW_SWC_CLEAR");
		tags.add("ADC_STEPCONFIG_WPNSW_SWC_SHIFT");
		tags.add("ADC_STEPCONFIG_XNNSW_SWC_CLEAR");
		tags.add("ADC_STEPCONFIG_XNNSW_SWC_SHIFT");
		tags.add("ADC_STEPCONFIG_XNPSW_SWC_CLEAR");
		tags.add("ADC_STEPCONFIG_XNPSW_SWC_SHIFT");
		tags.add("ADC_STEPCONFIG_XPPSW_SWC_CLEAR");
		tags.add("ADC_STEPCONFIG_XPPSW_SWC_SHIFT");
		tags.add("ADC_STEPCONFIG_YNNSW_SWC_CLEAR");
		tags.add("ADC_STEPCONFIG_YNNSW_SWC_SHIFT");
		tags.add("ADC_STEPCONFIG_YPNSW_SWC_CLEAR");
		tags.add("ADC_STEPCONFIG_YPNSW_SWC_SHIFT");
		tags.add("ADC_STEPCONFIG_YPPSW_SWC_CLEAR");
		tags.add("ADC_STEPCONFIG_YPPSW_SWC_SHIFT");
		tags.add("ADC_MODE_CLEAR");
		tags.add("ADC_MODE_SHIFT");
		tags.add("CM_PER");
		tags.add("CM_PER_L3S_CLKSTCTRL");
		tags.add("CM_PER_L3_CLKSTCTRL");
		tags.add("CM_PER_L3_INSTR_CLKCTRL");
		tags.add("CM_PER_L3_CLKCTRL");
		tags.add("CM_PER_OCPWP_L3_CLKSTCTRL");
		tags.add("CM_WKUP");
		tags.add("CM_WKUP_CLKSTCTRL");
		tags.add("CM_WKUP_CONTROL_CLKCTRL");
		tags.add("CM_WKUP_L4WKUP_CLKCTRL");
		tags.add("CM_L3_AON_CLKSTCTRL");
		tags.add("CM_WKUP_ADC_TSC_CLKCTRL");
		tags.add("CM_WKUP_CM_L4_WKUP_AON_CLKSTCTRL");
		tags.add("ADC_CHANNEL_1");
		tags.add("ADC_NEGATIVE_REF");
		tags.add("ADC_POSITIVE_REF");
		tags.add("ADC_STEP_CONT");
		tags.add("ADC_SE_OP_MODE");
		tags.add("HWREG");

		putXmlToMap(doc, headerXML, tags, "sensor");
		printHashMap(headerXML);
		generateHeaderCode(headerXML);
	}

	private static void generateHeaderCode(HashMap<String, String> writeXML) {

		try {
			File file = new File("..//gen//adcheader_io.c");
			PrintWriter pr = new PrintWriter(file);
			pr.write("#define ADC_BASE_ADDR (" + writeXML.get("ADC_BASE_ADDR") + ")\n");
			pr.write("#define INTERRUPT_CONTROLLER_BASE_ADDR (" + writeXML.get("INTERRUPT_CONTROLLER_BASE_ADDR")
					+ ")\n");
			pr.write("#define ADC_ENABLE_BIT (" + writeXML.get("ADC_ENABLE_BIT") + ")\n");
			pr.write("#define CLOCK_FREQ_MOD (" + writeXML.get("CLOCK_FREQ_MOD") + ")\n");
			pr.write("#define CLOCK_FREQ (" + writeXML.get("CLOCK_FREQ") + ")\n");
			pr.write("#define ADC_GP_MODE (" + writeXML.get("ADC_GP_MODE") + ")\n");
			pr.write("#define INTR_BIT (" + writeXML.get("INTR_BIT") + "<<1)\n");
			pr.write("#define WRITE_PROTECT_BIT (" + writeXML.get("WRITE_PROTECT_BIT") + ")\n");
			pr.write("#define ADC_OP_MODE_CLEAR (" + writeXML.get("ADC_OP_MODE_CLEAR") + "<<"
					+ writeXML.get("ADC_OP_MODE_SHIFT") + ")\n");
			pr.write("#define ADC_OP_MODE_SHIFT (" + writeXML.get("ADC_OP_MODE_SHIFT") + ")\n");
			pr.write("#define STEP_AVG (" + writeXML.get("STEP_AVG") + ")\n");
			pr.write("#define ADC_FIFO_0 (" + writeXML.get("ADC_FIFO_0") + ")\n");
			pr.write("#define ADC_FIFO_SELECTION_CLEAR (" + writeXML.get("ADC_FIFO_SELECTION_CLEAR") + "<<"
					+ writeXML.get("ADC_FIFO_SELECTION_SHIFT") + ")\n");
			pr.write("#define ADC_FIFO_SELECTION_SHIFT (" + writeXML.get("ADC_FIFO_SELECTION_SHIFT") + ")\n");
			pr.write("#define ADC_SC_DIFF_CTRL (" + writeXML.get("ADC_FIFO_SELECTION_CLEAR") + "<<25)\n");
			pr.write("#define SEL_RFM_SWC_CLEAR (" + writeXML.get("SEL_RFM_SWC_CLEAR") + "<<"
					+ writeXML.get("SEL_RFM_SWC_SHIFT") + ")\n");
			pr.write("#define SEL_RFM_SWC_SHIFT (" + writeXML.get("SEL_RFM_SWC_SHIFT") + ")\n");
			pr.write("#define SEL_INP_SWC_CLEAR (" + writeXML.get("SEL_INP_SWC_CLEAR") + "<<"
					+ writeXML.get("SEL_INP_SWC_SHIFT") + ")\n");
			pr.write("#define SEL_INP_SWC_SHIFT (" + writeXML.get("SEL_INP_SWC_SHIFT") + ")\n");
			pr.write("#define SEL_INM_SWC_CLEAR (" + writeXML.get("SEL_INM_SWC_CLEAR") + "<<"
					+ writeXML.get("SEL_INM_SWC_SHIFT") + ")\n");
			pr.write("#define SEL_INM_SWC_SHIFT (" + writeXML.get("SEL_INM_SWC_SHIFT") + ")\n");
			pr.write("#define SEL_RFP_SWC_CLEAR (" + writeXML.get("SEL_RFP_SWC_CLEAR") + "<<"
					+ writeXML.get("SEL_RFP_SWC_SHIFT") + ")\n");
			pr.write("#define SEL_RFP_SWC_SHIFT (" + writeXML.get("SEL_RFP_SWC_SHIFT") + ")\n");
			pr.write("#define ADC_STEPCONFIG_WPNSW_SWC_CLEAR (" + writeXML.get("ADC_STEPCONFIG_WPNSW_SWC_CLEAR")
					+ "<<11)\n");
			pr.write("#define ADC_STEPCONFIG_WPNSW_SWC_SHIFT (" + writeXML.get("ADC_STEPCONFIG_WPNSW_SWC_SHIFT")
					+ ")\n");
			pr.write("#define ADC_STEPCONFIG_XNNSW_SWC_CLEAR (" + writeXML.get("ADC_STEPCONFIG_XNNSW_SWC_CLEAR")
					+ "<<6)\n");
			pr.write("#define ADC_STEPCONFIG_XNNSW_SWC_SHIFT (" + writeXML.get("ADC_STEPCONFIG_XNNSW_SWC_SHIFT")
					+ ")\n");
			pr.write("#define ADC_STEPCONFIG_XNPSW_SWC_CLEAR (" + writeXML.get("ADC_STEPCONFIG_XNPSW_SWC_CLEAR")
					+ "<<9)\n");
			pr.write("#define ADC_STEPCONFIG_XNPSW_SWC_SHIFT (" + writeXML.get("ADC_STEPCONFIG_XNPSW_SWC_SHIFT")
					+ ")\n");
			pr.write("#define ADC_STEPCONFIG_XPPSW_SWC_CLEAR (" + writeXML.get("ADC_STEPCONFIG_XPPSW_SWC_CLEAR")
					+ "<<5)\n");
			pr.write("#define ADC_STEPCONFIG_XPPSW_SWC_SHIFT (" + writeXML.get("ADC_STEPCONFIG_XPPSW_SWC_SHIFT")
					+ ")\n");
			pr.write("#define ADC_STEPCONFIG_YNNSW_SWC_CLEAR (" + writeXML.get("ADC_STEPCONFIG_YNNSW_SWC_CLEAR")
					+ "<<8)\n");
			pr.write("#define ADC_STEPCONFIG_YNNSW_SWC_SHIFT (" + writeXML.get("ADC_STEPCONFIG_YNNSW_SWC_SHIFT")
					+ ")\n");
			pr.write("#define ADC_STEPCONFIG_YPNSW_SWC_CLEAR (" + writeXML.get("ADC_STEPCONFIG_YPNSW_SWC_CLEAR")
					+ "<<10)\n");
			pr.write("#define ADC_STEPCONFIG_YPNSW_SWC_SHIFT (" + writeXML.get("ADC_STEPCONFIG_YPNSW_SWC_SHIFT")
					+ ")\n");
			pr.write("#define ADC_STEPCONFIG_YPPSW_SWC_CLEAR (" + writeXML.get("ADC_STEPCONFIG_YPPSW_SWC_CLEAR")
					+ "<<7)\n");
			pr.write("#define ADC_STEPCONFIG_YPPSW_SWC_SHIFT (" + writeXML.get("ADC_STEPCONFIG_YPPSW_SWC_SHIFT")
					+ ")\n");
			pr.write("#define ADC_MODE_CLEAR (" + writeXML.get("ADC_MODE_CLEAR") + ")\n");
			pr.write("#define ADC_MODE_SHIFT (" + writeXML.get("ADC_MODE_SHIFT") + ")\n");
			pr.write("#define CM_PER (" + writeXML.get("CM_PER") + ")\n");
			pr.write("#define CM_PER_L3S_CLKSTCTRL (" + writeXML.get("CM_PER_L3S_CLKSTCTRL") + ")\n");
			pr.write("#define CM_PER_L3_CLKSTCTRL (" + writeXML.get("CM_PER_L3_CLKSTCTRL") + ")\n");
			pr.write("#define CM_PER_L3_INSTR_CLKCTRL (" + writeXML.get("CM_PER_L3_INSTR_CLKCTRL") + ")\n");
			pr.write("#define CM_PER_L3_CLKCTRL (" + writeXML.get("CM_PER_L3_CLKCTRL") + ")\n");
			pr.write("#define CM_PER_OCPWP_L3_CLKSTCTRL (" + writeXML.get("CM_PER_OCPWP_L3_CLKSTCTRL") + ")\n");
			pr.write("#define CM_WKUP (" + writeXML.get("CM_WKUP") + ")\n");
			pr.write("#define CM_WKUP_CLKSTCTRL (" + writeXML.get("CM_WKUP_CLKSTCTRL") + ")\n");
			pr.write("#define CM_WKUP_CONTROL_CLKCTRL (" + writeXML.get("CM_WKUP_CONTROL_CLKCTRL") + ")\n");
			pr.write("#define CM_WKUP_L4WKUP_CLKCTRL (" + writeXML.get("CM_WKUP_L4WKUP_CLKCTRL") + ")\n");
			pr.write("#define CM_L3_AON_CLKSTCTRL (" + writeXML.get("CM_L3_AON_CLKSTCTRL") + ")\n");
			pr.write("#define CM_WKUP_ADC_TSC_CLKCTRL (" + writeXML.get("CM_WKUP_ADC_TSC_CLKCTRL") + ")\n");
			pr.write("#define CM_WKUP_CM_L4_WKUP_AON_CLKSTCTRL (" + writeXML.get("CM_WKUP_CM_L4_WKUP_AON_CLKSTCTRL")
					+ ")\n");
			pr.write("#define ADC_CHANNEL_1 (" + writeXML.get("ADC_CHANNEL_1") + ")\n");
			pr.write("#define ADC_NEGATIVE_REF (" + writeXML.get("ADC_NEGATIVE_REF") + ")\n");
			pr.write("#define ADC_POSITIVE_REF (" + writeXML.get("ADC_POSITIVE_REF") + ")\n");
			pr.write("#define ADC_STEP_CONT (" + writeXML.get("ADC_STEP_CONT") + ")\n");
			pr.write("#define ADC_SE_OP_MODE (" + writeXML.get("ADC_SE_OP_MODE") + ")\n");
			pr.write("#define HWREG(n) " + writeXML.get("HWREG") + "\n");
			pr.close();
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	private static void genHandler(Document doc) {

		HashMap<String, String> handlerXML = new HashMap<String, String>();
		ArrayList<String> tags = new ArrayList<String>();
		tags.add("irqStatus");
		putXmlToMap(doc, handlerXML, tags, "regAndPin");
		tags.clear();
		tags.add("deviceStructure");
		tags.add("sensorStructure");
		tags.add("deviceTable");
		tags.add("deviceReg");
		tags.add("sensor_csreg");
		putXmlToMap(doc, handlerXML, tags, "read");
		printHashMap(handlerXML);
		generateHandlerCode(handlerXML);

	}

	private static void generateHandlerCode(HashMap<String, String> handlerXML) {

		try {
			File file = new File("..//gen//adchandler_io.c");
			PrintWriter pr = new PrintWriter(file);
			pr.write("struct " + handlerXML.get("deviceStructure") + " *devptr;\n");
			pr.write("struct " + handlerXML.get("sensorStructure") + " *csrptr;\n");
			pr.write("volatile uint32 irq_status = 0;\n");
			pr.write("devptr = (struct " + handlerXML.get("deviceStructure") + " *) &" + handlerXML.get("deviceTable")
					+ "[ADC_SENSOR];\n");
			pr.write("csrptr = (struct " + handlerXML.get("sensorStructure") + " *)" + "devptr->"
					+ handlerXML.get("deviceReg") + ";\n");
			pr.write("irq_status = csrptr->" + handlerXML.get("irqStatus") + ";\n");
			pr.write("csrptr->" + handlerXML.get("irqStatus") + " = irq_status;\n");
			pr.write("adc_step_disable(csrptr, 0);\n");
			pr.close();
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	private static void putXmlToMap(Document doc, HashMap<String, String> headerXML, ArrayList<String> tags,
			String root) {
		NodeList nodes = doc.getElementsByTagName(root);
		int j = 0;
		for (int i = 0; i < nodes.getLength(); i++) {
			Node node = nodes.item(i);
			if (node.getNodeType() == Node.ELEMENT_NODE) {
				Element element = (Element) node;
				while (j < tags.size()) {
					headerXML.put(tags.get(j), getValue(tags.get(j), element));
					j++;
				}
			}
		}
	}

	private static String getValue(String tag, Element element) {
		NodeList nodes = element.getElementsByTagName(tag).item(0).getChildNodes();
		Node node = (Node) nodes.item(0);
		return node.getNodeValue();
	}

	/* Print any Hash Map */
	private static void printHashMap(HashMap<String, String> hashmap) {
		hashmap.forEach((key, value) -> System.out.println(key + " : " + value));
	}

}
