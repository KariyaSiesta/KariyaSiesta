package org.sapid.checker.core;

import org.sapid.checker.core.Result;
import org.sapid.checker.core.IFile;
import org.sapid.checker.core.Range;

import org.sapid.checker.cx.command.Command;
import org.sapid.checker.cx.command.SimpleCommandOutput;
import org.sapid.checker.cx.wrapper.CFileElement;
import org.sapid.checker.rule.XPathChecker;
import org.sapid.checker.rule.NodeOffsetUtil;
import org.sapid.checker.rule.xpath.CXCheckerNamespaceContext;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import java.text.SimpleDateFormat;
import java.util.Date;

import java.util.ArrayList;
import java.util.HashMap;

import java.io.IOException;
import java.io.FileWriter;
import java.io.Writer;
import java.io.File;

import java.util.UUID;

import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpression;

/**
 * �����ȿ�Υ��󥰤�Ԥ�
 * @author takai
 *
 */
public class Logger {
	/*
	 * ���󥰤�ͭ���ˤ�����ϡ�����ե����ե�����ˡ�LOGGING_KEY=LOGGING_VALUE�פ򵭽�
	 */
	private static final String LOGGING_KEY   = "DoLogging";
	private static final String LOGGING_VALUE = "yes!";
	
	private static final String KEY_LOG_FILE  = "LogFileName";
	private static final String KEY_USER_NAME = "UserName";

	private static final String LOG_DIRECTORY = "./log/";

	/**
	 * LogDataElement<br>
	 * Result��IFile�Υ��å�
	 * @author takai
	 *
	 */
	private class LogDataElement {
		private IFile myFile;
		private Result myResult;
		private CFileElement myFileElement;

		public LogDataElement(IFile file, Result result) {
			myResult = result;
			myFile = file;
			myFileElement = new CFileElement(myFile.getDOM());
		}
		
		public String getFileName() {
			return myFile.getFileName();
		}
		
		public String getLevel() {
			return new Integer(myResult.getLevel()).toString();
		}
		
		public Range getRange() {
			return myResult.getRange();
		}
		
		public String getRuleName() {
			return myResult.getMessage();
		}
		
		public String getLineCount() {
			return new Integer(myFileElement.getElem().getElementsByTagName("nl").getLength()+1).toString();
		}
		
		public String getFileSize() {
			return new Long(new File(getFileName()).length()).toString();
		}
		
		public String getFunctionCount() {
			return new Integer(myFileElement.getFunctions().length).toString();
		}
		
		public String getFunctionName() {
			String result = "%None%";
			//��ȿ�Ρ��ɤ����
			Node node = new NodeOffsetUtil(myFileElement.getElem().getOwnerDocument(), myResult.getRange().getOffset()).getNode();
			//nl��ľ����Function��õ��
			while (node!=null) {
				if (node.getNodeName()!=null && node.getNodeName().equals("Function")) {
					break;
				} else {
					node = node.getParentNode();
				}
			}
			
			if (node!=null) {
				//Function�Ρ��ɤ�ľ���ident�Ρ��ɤ���ؿ�̾�����
				NodeList nl = node.getChildNodes();
				for (int i=0; i<nl.getLength(); i++) {
					Node n = nl.item(i);
					if (n.getNodeName().equals("ident")) {
						result = n.getTextContent();
					}
				}
			}

			return result;
		}
	}
	
	private ArrayList<LogDataElement> resultList;
	private String UserName;
	private String DateTime;

	/**
	 * ���󥰵�ǽ��ͭ�����ɤ������֤�
	 * @return ���󥰵�ǽ��ͭ�����ɤ���
	 */
	public static boolean isEnableSaveLog() {
		boolean result = false;

		String value = ConfigManager.getProperty(LOGGING_KEY);
		if (value!=null) {
			result = value.equals(LOGGING_VALUE);
		}
		
		return result;
	}

	/**
	 * �������󥹥ȥ饯��
	 * �桼����̾�����ꡦ���������ꡦ���ǥ��쥯�ȥ�����ꡦ���ե�����̾�������Ԥ���
	 */
	public Logger() {
		super();
		setUserName();
		setDateTime();
		resultList = new ArrayList<LogDataElement>();
	}
	
	/**
	 * �桼����̾�μ����������Ԥ���
	 */
	private void setUserName() {
		String name = ConfigManager.getProperty(KEY_USER_NAME);
		if (name==null) {
			if (System.getProperty("os.name").contains("Windows")) {
				name = System.getenv("username");
			} else {  //UNIX, Mac
				SimpleCommandOutput simpleCommandOutput = new SimpleCommandOutput();
				try {
					new Command("who",  ".").run(simpleCommandOutput);
					name = simpleCommandOutput.getOutput();
					if (name.indexOf(' ')!=-1) {
						name = name.substring(0, name.indexOf(' '));
					}
					if (name.indexOf('\t')!=-1) {
						name = name.substring(0, name.indexOf('\t'));
					}
					name = name.trim();
				}  catch (IOException e) {
					e.printStackTrace();
				}
			}

			//�桼����̾�������Ǥ��ʤ��ä�����UUID
			if (name==null || name.equals("")) {
				name = UUID.randomUUID().toString();
			}

			//�桼����̾����¸
			ConfigManager.setProperty(KEY_USER_NAME, name);
		}
		UserName = name;
	}

	/**
	 * �����μ����������Ԥ���
	 */
	private void setDateTime() {
		Date date = new Date();
		SimpleDateFormat df = new SimpleDateFormat("yyyy/MM/dd hh:mm:ss");

		DateTime = df.format(date);
	}

	/**
	 * ���ե�����̾�μ���
	 */
	private String getLogFileName() {
		String name = ConfigManager.getProperty(KEY_LOG_FILE);
		if (name==null) {
			name = ((ConfigManager.SAPID_DEST==null)? "":(ConfigManager.SAPID_DEST+"/")) + LOG_DIRECTORY + UUID.randomUUID().toString() + ".log";
			SimpleCommandOutput simpleCommandOutput = new SimpleCommandOutput();
			try {
				if (System.getProperty("os.name").contains("Windows")) { // Windows �ξ�硤Cygwin �����Υѥ��� Java ���鰷���ʤ����ᡤWindows�����Υѥ����Ѵ�����ɬ�פ����롥
					new Command("cygpath -w \"" + name + "\"",  System.getProperty("user.dir")).run(simpleCommandOutput);
					name =  simpleCommandOutput.getOutput();
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
			ConfigManager.setProperty(KEY_LOG_FILE, name);
		}
		return name;
	}

	/**
	 * �ꥶ��ȥꥹ�Ȥ��ɲ�
	 * @param FileName
	 * @param results
	 */
	public void addResults(IFile file, ArrayList<Result> results) {
		for (Result res: results) {
			resultList.add(new LogDataElement(file, res));
		}
	}
	
	/**
	 * ������¸��Ԥ�<br>
	 * addResults��Ԥä�ľ��˰����ʤ���saveLog��Ԥ��Τ�Ʊ��
	 * @param file
	 * @param results
	 */
	public void saveLog(IFile file, ArrayList<Result> results) {
		addResults(file, results);
		saveLog();
	}
	
	/**
	 * ������¸��Ԥ�
	 */
	public void saveLog() {
		if (isEnableSaveLog()) {
			try {
				String NewLine = getNewLine();
				FileWriter fw = new FileWriter(getLogFileName(), true);  //�ɵ��⡼�ɤǥ����ץ�
				try {
					for (LogDataElement res: resultList) {
						fw.write(getLogString(res)+NewLine);
					}
				} finally {
					fw.close();
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		resultList.clear();
	}

	/**
	 * ���˽񤭹���ƥ����ȥǡ�������������
	 * @param result
	 * @return
	 */
	private String getLogString(LogDataElement result) {
		String str = 
			  DateTime.replace("\t", "��t") + "\t"
			+ UserName.replace("\t", "��t") + "\t"
			+ result.getFileName().replace("\t", "��t") + "\t"
			+ result.getRuleName().replace("\t", "��t") + "\t"
			+ result.getLevel() + "\t"
			+ result.getRange().getStartLine() + "\t"
			+ result.getRange().getStartColumn() + "\t"
			+ result.getRange().getLength() + "\t"
			+ result.getFileName().replace("\t", "��t") + "\t"
			+ result.getFunctionName().replace("\t", "��t") + "\t"
			+ result.getLineCount() + "\t"
			+ result.getFunctionCount();
		return str;
	}
	
	/**
	 * ���ԥ����ɤ�OS��˼�����<br>
	 * ��äȤ��ޤ���ˡ������褦�ʵ��⤹�뤷����������Ǥ��ɤ��褦�ʵ��⤹�롣
	 * @return
	 */
	private String getNewLine() {
		String NewLine;
		if (System.getProperty("os.name").contains("Windows")) {
			NewLine = "\r\n";
		} else if (System.getProperty("os.name").startsWith("Mac")) {
			NewLine = "\r";
		} else {
			NewLine = "\n";
		}
		return NewLine;
	}
}
