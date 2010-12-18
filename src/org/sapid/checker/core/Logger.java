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
 * 規約違反のロギングを行う
 * @author takai
 *
 */
public class Logger {
	/*
	 * ロギングを有効にする場合は、コンフィグファイルに「LOGGING_KEY=LOGGING_VALUE」を記述
	 */
	private static final String LOGGING_KEY   = "DoLogging";
	private static final String LOGGING_VALUE = "yes!";
	
	private static final String KEY_LOG_FILE  = "LogFileName";
	private static final String KEY_USER_NAME = "UserName";

	private static final String LOG_DIRECTORY = "./log/";

	/**
	 * LogDataElement<br>
	 * ResultとIFileのセット
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
			//違反ノードを取得
			Node node = new NodeOffsetUtil(myFileElement.getElem().getOwnerDocument(), myResult.getRange().getOffset()).getNode();
			//nlの直前のFunctionを探す
			while (node!=null) {
				if (node.getNodeName()!=null && node.getNodeName().equals("Function")) {
					break;
				} else {
					node = node.getParentNode();
				}
			}
			
			if (node!=null) {
				//Functionノードの直近のidentノードから関数名を取得
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
	 * ロギング機能が有効かどうかを返す
	 * @return ロギング機能が有効かどうか
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
	 * ロガーコンストラクタ
	 * ユーザー名の設定・日時の設定・ログディレクトリの設定・ログファイル名の設定を行う。
	 */
	public Logger() {
		super();
		setUserName();
		setDateTime();
		resultList = new ArrayList<LogDataElement>();
	}
	
	/**
	 * ユーザー名の取得と設定を行う。
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

			//ユーザー名が取得できなかった場合はUUID
			if (name==null || name.equals("")) {
				name = UUID.randomUUID().toString();
			}

			//ユーザー名の保存
			ConfigManager.setProperty(KEY_USER_NAME, name);
		}
		UserName = name;
	}

	/**
	 * 日時の取得と設定を行う。
	 */
	private void setDateTime() {
		Date date = new Date();
		SimpleDateFormat df = new SimpleDateFormat("yyyy/MM/dd hh:mm:ss");

		DateTime = df.format(date);
	}

	/**
	 * ログファイル名の取得
	 */
	private String getLogFileName() {
		String name = ConfigManager.getProperty(KEY_LOG_FILE);
		if (name==null) {
			name = ((ConfigManager.SAPID_DEST==null)? "":(ConfigManager.SAPID_DEST+"/")) + LOG_DIRECTORY + UUID.randomUUID().toString() + ".log";
			SimpleCommandOutput simpleCommandOutput = new SimpleCommandOutput();
			try {
				if (System.getProperty("os.name").contains("Windows")) { // Windows の場合，Cygwin 形式のパスは Java から扱えないため，Windows形式のパスに変換する必要がある．
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
	 * リザルトリストの追加
	 * @param FileName
	 * @param results
	 */
	public void addResults(IFile file, ArrayList<Result> results) {
		for (Result res: results) {
			resultList.add(new LogDataElement(file, res));
		}
	}
	
	/**
	 * ログの保存を行う<br>
	 * addResultsを行った直後に引数なしのsaveLogを行うのと同義
	 * @param file
	 * @param results
	 */
	public void saveLog(IFile file, ArrayList<Result> results) {
		addResults(file, results);
		saveLog();
	}
	
	/**
	 * ログの保存を行う
	 */
	public void saveLog() {
		if (isEnableSaveLog()) {
			try {
				String NewLine = getNewLine();
				FileWriter fw = new FileWriter(getLogFileName(), true);  //追記モードでオープン
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
	 * ログに書き込むテキストデータを生成する
	 * @param result
	 * @return
	 */
	private String getLogString(LogDataElement result) {
		String str = 
			  DateTime.replace("\t", "￥t") + "\t"
			+ UserName.replace("\t", "￥t") + "\t"
			+ result.getFileName().replace("\t", "￥t") + "\t"
			+ result.getRuleName().replace("\t", "￥t") + "\t"
			+ result.getLevel() + "\t"
			+ result.getRange().getStartLine() + "\t"
			+ result.getRange().getStartColumn() + "\t"
			+ result.getRange().getLength() + "\t"
			+ result.getFileName().replace("\t", "￥t") + "\t"
			+ result.getFunctionName().replace("\t", "￥t") + "\t"
			+ result.getLineCount() + "\t"
			+ result.getFunctionCount();
		return str;
	}
	
	/**
	 * 改行コードをOS毎に取得。<br>
	 * もっとうまい方法があるような気もするし、全て統一でも良いような気もする。
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
