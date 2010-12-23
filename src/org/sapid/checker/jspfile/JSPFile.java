package org.sapid.checker.jspfile;

import java.io.FileNotFoundException;
import java.io.IOException;

import javax.xml.parsers.ParserConfigurationException;

import org.sapid.checker.core.IFile;
import org.sapid.checker.eclipse.CheckerActivator;
import org.sapid.model.JSPModel;
import org.sapid.parser.common.ParseException;
import org.w3c.dom.Document;
import org.xml.sax.SAXException;

/**
 * JSPFileの情報を保持するクラス
 * @author tani
 */
public class JSPFile implements IFile {
    private JSPModel jspmodel = null;
    private Document jspdom = null;
    private String filename = null;

    /**
     * JSPファイルをセットする
     * @param filepath 対象のJSPファイル名(String)
     * @throws ParseException
     */
    public void setFileName(String filename) {
        this.filename = filename;
    }

    public JSPFile(String filename) {
        super();
        this.filename = filename;
    }

    public void buildDOM() throws ParseException, IOException {
        if (jspdom == null) {
            try {
                jspmodel = new JSPModel(filename);
                jspmodel.analyze();
                jspmodel.outputModelToFile();
                jspdom = jspmodel.getModelByDOM();
            } catch (FileNotFoundException e) {
                CheckerActivator.log(e);
                System.exit(1);
            } catch (IOException e) {
                CheckerActivator.log(e);
            } catch (ParserConfigurationException e) {
                CheckerActivator.log(e);
            } catch (SAXException e) {
                CheckerActivator.log(e);
            }
        }
    }
    
    public Document getDOM() {
        return jspdom;
    }

    /**
     * ファイル名を返す
     * @return
     */
    public String getFileName() {
        return filename;
    }
}
