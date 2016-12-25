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

public class GPIODomParser {

	// static HashMap<String, String> readXML = new HashMap<String, String>();

	public static void main(String args[]) {
		try {

			File ddlInput = new File("Input.xml");
			DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
			DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
			Document doc = dBuilder.parse(ddlInput);
			doc.getDocumentElement().normalize();
			genRead(doc);
			genWrite(doc); genHeader(doc); genInit(doc);
			 

		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}

	private static void genRead(Document doc) {
		HashMap<String, String> readXML = new HashMap<String, String>();
		ArrayList<String> tags = new ArrayList<String>();
		tags.add("gpioRead");
		putXmlToMap(doc, readXML, tags, "functionName");
		tags.clear();
		tags.add("sensorStructure");
		tags.add("deviceReg");
		tags.add("semaphoreStructure");
		tags.add("semaphoreTable");
		tags.add("deviceStructure");
		// build ReadHashMap
		putXmlToMap(doc, readXML, tags, "read");
		printHashMap(readXML);
		generateReadCode(readXML);

	}

	private static void generateReadCode(HashMap<String, String> readXML) {
		try {
			File file = new File("..//device//gpio_sensor//gpioread.c");			
			PrintWriter pr = new PrintWriter(file);
			pr.write(getReadCodeBlock1(readXML));
			pr.write("struct " + readXML.get("sensorStructure") + " *reg = (struct " + readXML.get("sensorStructure")
					+ "*) devptr->" + readXML.get("deviceReg") + ";\n");
			pr.write("uint32 readcount = 0;\nintmask mask;\nstruct " + readXML.get("semaphoreStructure")
					+ " *semptr;\n");
			pr.write(getReadCodeBlock2(readXML));
			pr.close();
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	private static void genWrite(Document doc) {
		HashMap<String, String> writeXML = new HashMap<String, String>();
		ArrayList<String> tags = new ArrayList<String>();
		tags.add("gpio_sensor_csreg");
		putXmlToMap(doc, writeXML, tags, "read");
		tags.clear();
		tags.add("clear");
		tags.add("clearReg");
		tags.add("clearPin");
		tags.add("set");
		tags.add("setReg");
		tags.add("setPin");
		// build ReadHashMap
		putXmlToMap(doc, writeXML, tags, "regAndPin");
		printHashMap(writeXML);
		generateWriteCode(writeXML);
	}

	private static void generateWriteCode(HashMap<String, String> writeXML) {

		try {
			File file = new File("..//gen//gpiowrite_io.c");			
			PrintWriter pr = new PrintWriter(file);
			pr.write("case " + writeXML.get("clear") + ":\n");
			pr.write("\t" + writeXML.get("gpio_sensor_csreg") + "->" + writeXML.get("clearReg") + "|=(1 << "
					+ writeXML.get("clearPin") + ");\nbreak;\n");
			pr.write("case " + writeXML.get("set") + ":\n");
			pr.write("\t" + writeXML.get("gpio_sensor_csreg") + "->" + writeXML.get("setReg") + "|=(1 << "
					+ writeXML.get("setPin") + ");\nbreak;\n");
			pr.close();
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	private static void genHeader(Document doc) {
		HashMap<String, String> headerXML = new HashMap<String, String>();
		ArrayList<String> tags = new ArrayList<String>();
		tags.add("REG_HW");
		tags.add("GPIO_BASE_ADDR");
		tags.add("GPIO_SET");
		tags.add("GPIO_CLEAR");
		tags.add("GPIO_WRITE");
		putXmlToMap(doc, headerXML, tags, "sensor");
		printHashMap(headerXML);
		generateHeaderCode(headerXML);
	}

	private static void generateHeaderCode(HashMap<String, String> writeXML) {

		try {
			File file=  new File("..//gen//gpioheader_io.c");
			PrintWriter pr = new PrintWriter(file);
			pr.write("#define REG_HW(n) " + writeXML.get("REG_HW") + "\n");
			pr.write("#define GPIO_BASE_ADDR " + writeXML.get("GPIO_BASE_ADDR") + "\n");
			pr.write("#define GPIO_SET " + writeXML.get("GPIO_SET") + "\n");
			pr.write("#define GPIO_CLEAR " + writeXML.get("GPIO_CLEAR") + "\n");
			pr.write("#define GPIO_WRITE " + writeXML.get("GPIO_WRITE") + "\n");

			pr.close();
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	private static void genInit(Document doc) {
		HashMap<String, String> initXML = new HashMap<String, String>();
		ArrayList<String> tags = new ArrayList<String>();
		tags.add("INTERRUPT_CONTROLLER");
		tags.add("INTR_BIT");
		tags.add("clearInterruptStatus");
		// build ReadHashMap
		putXmlToMap(doc, initXML, tags, "sensor");
		tags.clear();
		tags.add("gpioSetInterrupt");
		tags.add("clearInterrupt");
		tags.add("riseEdgeSetInterrupt");
		putXmlToMap(doc, initXML, tags, "functionName");
		tags.clear();
		tags.add("riseDetectReg");
		tags.add("irqStatus");
		tags.add("irqStatusSet");
		tags.add("irqEnableBit");
		putXmlToMap(doc, initXML, tags, "regAndPin");
		tags.clear();
		tags.add("gpio_sensor_csreg");
		putXmlToMap(doc, initXML, tags, "read");
		printHashMap(initXML);
		generateInitCode(initXML);

	}

	private static void generateInitCode(HashMap<String, String> initXML) {
		try {
			File file = new File ("..//gen//gpioinit_ioheader.c");			
			PrintWriter pr = new PrintWriter(file);
			pr.write("#define INTERRUPT_CONTROLLER (" + initXML.get("INTERRUPT_CONTROLLER") + ")\n");
			pr.write("#define INTR_BIT (" + initXML.get("INTR_BIT") + ")\n");
			pr.write("void " + initXML.get("gpioSetInterrupt") + "(struct gpio_sensor_csreg *"
					+ initXML.get("gpio_sensor_csreg") + ", uint32 " + initXML.get("irqEnableBit") + "){\n");
			pr.write(initXML.get("gpio_sensor_csreg") + "->" + initXML.get("irqStatusSet") + "|="
					+ initXML.get("irqEnableBit") + ";\n}\n");

			pr.write("void " + initXML.get("clearInterrupt") + "(struct gpio_sensor_csreg *"
					+ initXML.get("gpio_sensor_csreg") + "){\n");
			pr.write(initXML.get("gpio_sensor_csreg") + "->" + initXML.get("irqStatus") + "|="
					+ initXML.get("clearInterruptStatus") + ";\n}\n");

			pr.write("void " + initXML.get("riseEdgeSetInterrupt") + "(struct gpio_sensor_csreg *"
					+ initXML.get("gpio_sensor_csreg") + ", uint32 " + initXML.get("irqEnableBit") + "){\n");
			pr.write(initXML.get("gpio_sensor_csreg") + "->" + initXML.get("riseDetectReg") + "|="
					+ initXML.get("irqEnableBit") + ";\n}\n");

			pr.close();
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	/*
	 * Utility functions putXmlToMap getValue printHashMap getReadCodeBlock1
	 * getReadCodeBlock2
	 */
	/* get value by tag and element */

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

	private static String getReadCodeBlock1(HashMap<String, String> readXML) {
		return "#include<xinu.h>\nextern sid32 gpio_readsem;\ndevcall " + readXML.get("gpioRead") + "(struct "
				+ readXML.get("deviceStructure") + " * devptr, char *buff, int32 count){\n";
	}

	private static String getReadCodeBlock2(HashMap<String, String> readXML) {
		return "mask = disable();\nsemptr = &" + readXML.get("semaphoreTable")
				+ "[gpio_readsem];\nreadcount = semptr->scount;\nif(readcount > 0) {\nsemptr->scount = 0;\n}\nrestore(mask);\ngpio_clear_interrupt_status(reg);\ngpio_set_interrupt_bit(reg, INTR_BIT);\nwait(gpio_readsem);\n write(GPIO_SENSOR,NULL,1);\nsleep(1);\n write(GPIO_SENSOR,NULL,0);\nreturn OK;\n}";

	}
}
