package org.sapid.checker.core;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.Reader;
import java.io.FileWriter;
import java.io.Writer;
import java.util.Properties;
import java.util.HashMap;
import java.util.Map;

import org.sapid.checker.cx.command.Command;
import org.sapid.checker.cx.command.SimpleCommandOutput;

/**
 * CXC.conf���ɤ߽񤭤�Ԥ��ޥ͡����㡼
 * @author takai
 *
 */
public class ConfigManager {
	/**
	 * <SAPID_DEST> �ǥ��쥯�ȥ�ؤΥѥ���
	 * �Ķ��ѿ� SAPID_DEST �����ꤵ��Ƥ��ʤ���硤�ͤ� null��
	 */
	public static final String SAPID_DEST = System.getenv("SAPID_DEST");
	/**
	 * CX-Checker������ե�����ؤΥѥ���(SAPID_DEST��������Хѥ�)��
	 */
	private static final String CXC_PROPERTIES_FILE_PATH = "lib/CXC.conf";

	/**
	 * ����ե������ɤ߹��ߤ�ͭ�����ɤ������֤���
	 */
	public static boolean isEnableConfig() {
		return getPropertiesFilePath()!=null;
	}
	
	/**
	 * ����ե����� (CXC.conf) �ؤΥѥ����֤����ѥ��������ʤ���� null ���֤���
	 */
	private static String getPropertiesFilePath() {
		String propertiesFilePath = null;
		
		if (SAPID_DEST != null) { // �Ķ��ѿ� SAPID_DEST �����ꤵ��Ƥ�����
			SimpleCommandOutput simpleCommandOutput = new SimpleCommandOutput();
			try {
				String sapidDestValidPath = SAPID_DEST;
				if (System.getProperty("os.name").contains("Windows")) { // Windows �ξ�硤Cygwin �����Υѥ��� Java ���鰷���ʤ����ᡤWindows�����Υѥ����Ѵ�����ɬ�פ����롥
					new Command("cygpath -w \"" + SAPID_DEST + "\"",  System.getProperty("user.dir")).run(simpleCommandOutput);
					sapidDestValidPath =  simpleCommandOutput.getOutput();
				}
				
				if (sapidDestValidPath.endsWith(File.separator)) {
					propertiesFilePath = sapidDestValidPath + CXC_PROPERTIES_FILE_PATH;
				} else {
					propertiesFilePath = sapidDestValidPath + File.separator + CXC_PROPERTIES_FILE_PATH;
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		
		return propertiesFilePath;
	}
	
	/**
	 * �ץ�ѥƥ��μ���
	 * @param properties ��������ץ�ѥƥ�̾�򥭡��˻��ĥޥå�
	 */
	public static void getProperty(HashMap<String, String> properties) {
		if (properties!=null) {
			getConfigData(properties);
		}
	}
	
	/**
	 * �ץ�ѥƥ��μ���
	 * @param key �ץ�ѥƥ�̾
	 * @return ��
	 */
	public static String getProperty(String key) {
		String result = null;
		if (key!=null) {
			HashMap<String, String> property = new HashMap<String, String>();
			property.put(key, null);
			getProperty(property);
			result = property.get(key);
		}
		return result;
	}
	
	/**
	 * �ץ�ѥƥ�������
	 * @param properties ���ꤹ��ץ�ѥƥ�̾�򥭡��˻��ĥޥå�
	 */
	public static void setProperty(HashMap<String, String> properties) {
		if (properties!=null) {
			setConfigData(properties);
		}
	}
	
	/**
	 * �ץ�ѥƥ�������
	 * @param key �ץ�ѥƥ�̾
	 * @param value ��
	 */
	public static void setProperty(String key, String value) {
		if (value!=null) {
			HashMap<String, String> property = new HashMap<String, String>();
			property.put(key, value);
			setProperty(property);
		}
	}
	
	/**
	 * Property���֥������Ȥμ�����<br>
	 * �ߤ����ͤ򥭡��˻��ĥ���ȥ꡼����ä��ϥå������äơ��ͤ��ɤ߹�����ͤ���������
	 * @param datas �����ǡ����ѥϥå���
	 */
	private static void getConfigData(HashMap<String, String> datas) {
		String Path = getPropertiesFilePath();
		if (Path != null) {
			Properties properties = new Properties();
			Reader reader = null;
			File file = new File(Path);
			try {
				try {
					reader = new FileReader(file);
					properties.load(reader);
				} finally {
					reader.close();
				}
				for (Map.Entry<String, String> item : datas.entrySet()) {
					item.setValue(properties.getProperty(item.getKey()));
				}
			} catch (FileNotFoundException e) {
				System.err.println("Error. Can't open properties file: " + file.getAbsolutePath());
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
	
	/**
	 * Property�����ꡣ<br>
	 * �������äƤ����ǡ����Ͼä��ʤ��褦�ˤ��Ƥ��롣
	 * @param datas ����ǡ����ѥϥå���
	 */
	private static void setConfigData(HashMap<String, String> datas) {
		String Path = getPropertiesFilePath();
		if (Path != null) {
			Properties properties = new Properties();
			Reader reader = null;
			Writer writer = null;
			File file = new File(Path);
			try {
				reader = new FileReader(file);
				try {
					properties.load(reader);
				} finally {
					reader.close();
				}
				for (Map.Entry item: properties.entrySet()) {
					if (! datas.containsKey(item.getKey())) {
						datas.put((String)item.getKey(), (String)item.getValue());
					}
				}
				for (Map.Entry<String, String> item : datas.entrySet()) {
					properties.setProperty(item.getKey(), item.getValue());
				}
				writer = new FileWriter(file);
				try {
					properties.store(writer, null);
				} finally {
					writer.close();
				}
			} catch (FileNotFoundException e) {
				System.err.println("Error. Can't open properties file: " + file.getAbsolutePath());
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
}
