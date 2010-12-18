/*
 * (c) Copyright 2002 M. van Meegen
 * All Rights Reserved.
 * Sourcecode is licensed under Mozilla Public License 1.1
 */
package org.sapid.checker.eclipse.properties;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.sapid.checker.eclipse.CheckerActivator;
import org.sapid.checker.rule.XPathChecker;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IWorkspaceRoot;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.jface.preference.PreferenceStore;
import org.w3c.dom.Attr;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

/**
 * preference store extension to be able to use field editors in property page
 * 
 * @author Marco van Meegen
 */
public class PropertyStore extends PreferenceStore {

  public static final String  ATTR_WORKSPACE_XMLS = "WORKSPACE_XMLS";
  public static final String  ATTR_ABSOLUTE_XMLS  = "ABSOLUTE_XMLS";
  public static final String  ATTR_OTHER_MODULES  = "OTHER_MODULES";

  private static final String PROPERTYFILE        = ".checker";

  /** project the preference store is associated with */
  private IProject            project             = null;

  /**
   * create a preference store without associated project. Project must be set before
   * store can be used !
   */
  public PropertyStore() {
    this(null);
  }

  /**
   * create a preference store associated with the project.
   * 
   * @param project
   *          project where prefs file is maintained
   */
  public PropertyStore(IProject project) {
    setProject(project);
    this.setDefault(ATTR_ABSOLUTE_XMLS, "");
    this.setDefault(ATTR_WORKSPACE_XMLS, "");
    this.setDefault(ATTR_OTHER_MODULES, org.sapid.checker.LineWidthChecker.class.getCanonicalName() + ";max=80\u0000");

    if (project != null) {
      try {
        load();
      } catch (IOException ex) {
        CheckerActivator.log("project properties: load failed", ex);
      }
    }
  }

  private static final String myXMLNS = "http://www.sapid.org/chekcerplugin";

  private static void appendXPathModule(Document doc, String xml, String relative) {
    Element module = doc.createElement("module");
    module.setAttribute("name", XPathChecker.class.getCanonicalName());
    Element param = doc.createElement("param");
    param.setAttribute("name", "src");
    param.setAttribute("value", xml);
    if (relative != null) {
      Attr relativeAttr = doc.createAttributeNS(myXMLNS, "plugin:relative");
      relativeAttr.setValue(relative);
      param.setAttributeNode(relativeAttr);
    }
    module.appendChild(param);
    doc.getDocumentElement().appendChild(module);
  }

  private static void appendOtherModule(Document doc, ModuleData m) {
    Element module = doc.createElement("module");
    module.setAttribute("name", m.className);
    for (String key : m.params.keySet()) {
      Element param = doc.createElement("param");
      param.setAttribute("name", key);
      param.setAttribute("value", m.params.get(key));
      module.appendChild(param);
    }
    doc.getDocumentElement().appendChild(module);
  }

  /**
   * overridden to avoid saving of dummy store
   * 
   * {@inheritDoc}
   * 
   * @see org.eclipse.jface.preference.IPersistentPreferenceStore#save()
   */
  public void save() throws IOException {

    IFile f = project.getFile(PROPERTYFILE);
    if (!f.exists()) {
      // create it
      try {
        f.create(null, true, null);
      } catch (CoreException e) {
        throw new IOException("CoreException : " + e.getMessage());
      }
    }
    String[] workspaceXMLs = PropertyParser.parsePath(this.getString(ATTR_WORKSPACE_XMLS));
    String[] absoluteXMLs = PropertyParser.parsePath(this.getString(ATTR_ABSOLUTE_XMLS));
    String[] moduleStrs = PropertyParser.parseModules(this.getString(ATTR_OTHER_MODULES));

    // DOMにする
    Document doc;
    try {
      doc = DocumentBuilderFactory.newInstance().newDocumentBuilder().newDocument();
    } catch (ParserConfigurationException e) {
      throw new IOException("ParserConfigurationException : " + e.getMessage());
    }
    // ルートノードの設定
    doc.appendChild(doc.createElement("checker"));
    for (String xml : workspaceXMLs) {
      IWorkspaceRoot root = ResourcesPlugin.getWorkspace().getRoot();

      // Create list of already selected files
      IResource resource = root.findMember(xml);
      if (resource instanceof IFile) {
        IFile file = (IFile) resource;
        String fullpath = file.getRawLocation().toOSString();
        appendXPathModule(doc, fullpath, xml);
      }
    }
    for (String xml : absoluteXMLs) {
      appendXPathModule(doc, xml, null);
    }
    for (String moduleStr : moduleStrs) {
      ArrayList<String> dummy_errors = new ArrayList<String>(); // エラーが出てもとりあえず保存
      ModuleData m = PropertyParser.parseOneModule(moduleStr, dummy_errors);
      appendOtherModule(doc, m);
    }

    // ファイル保存
    Transformer transformer;
    try {
      transformer = TransformerFactory.newInstance().newTransformer();
    } catch (TransformerConfigurationException e) {
      throw new IOException("TransformerConfigurationException : " + e.getMessage());
    }
    DOMSource source = new DOMSource(doc);
    ByteArrayOutputStream out = new ByteArrayOutputStream();
    StreamResult result = new StreamResult(out);
    try {
      transformer.transform(source, result);
    } catch (TransformerException e) {
      throw new IOException("TransformerException : " + e.getMessage());
    }
    try {
      f.setContents(new ByteArrayInputStream(out.toByteArray()), true, false, null);
    } catch (CoreException e) {
      throw new IOException("CoreException : " + e.getMessage());
    }

  }

  private static ModuleData[] loadFromDOM(Document doc) {
    ArrayList<ModuleData> ret = new ArrayList<ModuleData>();

    NodeList modules = doc.getDocumentElement().getChildNodes();
    for (int i = 0; i < modules.getLength(); i++) {
      Node item = modules.item(i);
      if (item instanceof Element) {
        Element module = (Element) item;
        if (!"module".equals(module.getNodeName()))
          continue;
        String className = module.getAttribute("name");
        if (className == null || "".equals(className))
          continue;
        ModuleData m = new ModuleData();
        m.className = className;
        NodeList params = module.getChildNodes();
        for (int j = 0; j < params.getLength(); j++) {
          Node item2 = params.item(j);
          if (!(item2 instanceof Element))
            continue;
          Element param = (Element) item2;
          if (!"param".equals(param.getNodeName()))
            continue;
          String key = param.getAttribute("name");
          String value = param.getAttribute("value");
          if (key != null && !"".equals(key) && value != null) {
            m.params.put(key, value);
          }
          String relative = param.getAttributeNS(myXMLNS, "relative");
          if (relative != null && !"".equals(relative)) {
            m.params.put("relative", relative);
          }
        }
        ret.add(m);
      }
    }
    return ret.toArray(new ModuleData[0]);
  }

  /**
   * overridden to load preferences from PROPERTYFILE file in associated project
   * 
   * {@inheritDoc}
   * 
   * @see org.eclipse.jface.preference.PreferenceStore#load()
   */
  public void load() throws IOException {
    IFile f = project.getFile(PROPERTYFILE);

    if (f.exists()) {
      ArrayList<String> absolutes = new ArrayList<String>();
      ArrayList<String> relatives = new ArrayList<String>();
      ArrayList<ModuleData> others = new ArrayList<ModuleData>();

      try {

        InputStream is = f.getContents();
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        factory.setNamespaceAware(true);
        DocumentBuilder builder = factory.newDocumentBuilder();
        Document doc = builder.parse(is);
        ModuleData[] modules = loadFromDOM(doc);
        for (ModuleData module : modules) {
          if (module.className.equals(XPathChecker.class.getCanonicalName())) {
            String relative = module.params.get("relative");
            if (relative != null) {
              relatives.add(relative);
            } else {
              String absolute = module.params.get("src");
              absolutes.add(absolute);
            }
          } else {
            others.add(module);
          }
        }
        this.setValue(ATTR_ABSOLUTE_XMLS, PropertyParser.unparsePath(absolutes.toArray(new String[0])));
        this.setValue(ATTR_WORKSPACE_XMLS, PropertyParser.unparsePath(relatives.toArray(new String[0])));
        this.setValue(ATTR_OTHER_MODULES, PropertyParser.unparseModules(others));
      } catch (CoreException e) {
        throw new IOException("CoreException : " + e.getMessage());
      } catch (ParserConfigurationException e) {
        throw new IOException("ParserConfigurationException : " + e.getMessage());
      } catch (SAXException e) {
        throw new IOException("SAXException : " + e.getMessage());
      }

    }
  }

  /**
   * sets the project where preferences are maintained for. must be set prior to open/save
   * operations
   * 
   * @param project
   *          project
   */
  public void setProject(IProject project) {
    this.project = project;
  }

  public static PropertyStore getProjectSetting(IProject project) {
    return new PropertyStore(project);
  }

  public static IFile getProjectSettingAsFile(IProject project) throws IOException {
    IFile f = project.getFile(PROPERTYFILE);
    if (!f.exists()) {
      new PropertyStore(project).save();
    }
    return f;

  }
}
