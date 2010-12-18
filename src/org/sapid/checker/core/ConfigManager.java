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
 * CXC.confの読み書きを行うマネージャー
 * @author takai
 *
 */
public class ConfigManager {
	/**
	 * <SAPID_DEST> ディレクトリへのパス．
	 * 環境変数 SAPID_DEST が設定されていない場合，値は null．
	 */
	public static final String SAPID_DEST = System.getenv("SAPID_DEST");
	/**
	 * CX-Checkerの設定ファイルへのパス．(SAPID_DESTからの相対パス)．
	 */
	private static final String CXC_PROPERTIES_FILE_PATH = "lib/CXC.conf";

	/**
	 * 設定ファイル読み込みが有効かどうかを返す。
	 */
	public static boolean isEnableConfig() {
		return getPropertiesFilePath()!=null;
	}
	
	/**
	 * 設定ファイル (CXC.conf) へのパスを返す．パスが求められない場合 null を返す．
	 */
	private static String getPropertiesFilePath() {
		String propertiesFilePath = null;
		
		if (SAPID_DEST != null) { // 環境変数 SAPID_DEST が設定されている場合
			SimpleCommandOutput simpleCommandOutput = new SimpleCommandOutput();
			try {
				String sapidDestValidPath = SAPID_DEST;
				if (System.getProperty("os.name").contains("Windows")) { // Windows の場合，Cygwin 形式のパスは Java から扱えないため，Windows形式のパスに変換する必要がある．
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
	 * プロパティの取得
	 * @param properties 取得するプロパティ名をキーに持つマップ
	 */
	public static void getProperty(HashMap<String, String> properties) {
		if (properties!=null) {
			getConfigData(properties);
		}
	}
	
	/**
	 * プロパティの取得
	 * @param key プロパティ名
	 * @return 値
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
	 * プロパティの設定
	 * @param properties 設定するプロパティ名をキーに持つマップ
	 */
	public static void setProperty(HashMap<String, String> properties) {
		if (properties!=null) {
			setConfigData(properties);
		}
	}
	
	/**
	 * プロパティの設定
	 * @param key プロパティ名
	 * @param value 値
	 */
	public static void setProperty(String key, String value) {
		if (value!=null) {
			HashMap<String, String> property = new HashMap<String, String>();
			property.put(key, value);
			setProperty(property);
		}
	}
	
	/**
	 * Propertyオブジェクトの取得。<br>
	 * 欲しい値をキーに持つエントリーを持ったハッシュをもらって、値に読み込んだ値を代入する
	 * @param datas 取得データ用ハッシュ
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
	 * Propertyの設定。<br>
	 * 元々入っていたデータは消えないようにしてある。
	 * @param datas 設定データ用ハッシュ
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
