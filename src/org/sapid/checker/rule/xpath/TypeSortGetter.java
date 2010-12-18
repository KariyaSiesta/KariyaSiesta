package org.sapid.checker.rule.xpath;

import java.util.List;

import javax.xml.xpath.XPathFunction;
import javax.xml.xpath.XPathFunctionException;

import org.sapid.checker.cx.wrapper.type.Type;
import org.sapid.checker.cx.wrapper.type.TypeInfosConstant;
import org.w3c.dom.Element;

/**
 * ����Expr���ǡˤ�����˼�ꡢ
 * ���μ��η��μ����"standard"��"pointer"�ʤɤ�ʸ������֤���
 * ��������Ǥ��ʤ��ä����϶�ʸ������֤���
 * @author uehara
 */
public class TypeSortGetter implements XPathFunction {

	private static TypeSortGetter instance;
	
	
	private TypeSortGetter() {
	}
	
	
	public static TypeSortGetter getInstance() {
		if (instance == null) {
			instance = new TypeSortGetter();
		}
		
		return instance;
	}
	
	
	@Override
	@SuppressWarnings("unchecked")
	public Object evaluate(List args) throws XPathFunctionException {
		Element typeInfoElement = (Element) TypeInfoGetter.getInstance().evaluate(args);
		
		if (typeInfoElement == null) {
			return "";
		}
		
		Type.Sort typeSort = this.getTypeSort(typeInfoElement);
		if (typeSort != null) {
			return typeSort.toString();
		} else {
			// TODO ���ξ�粿���֤��٤�����
			return "";
		}
	}
	
	private Type.Sort getTypeSort(Element typeInfoElement) {
		Type.Sort typeSort = null;
		
		if (typeInfoElement != null) {
			String sortAttributeName = typeInfoElement.getAttribute(TypeInfosConstant.SORT_ATTRIBUTE_NAME);
			typeSort = Type.Sort.fromString(sortAttributeName);
		}
		
		return typeSort;
	}
	
}
