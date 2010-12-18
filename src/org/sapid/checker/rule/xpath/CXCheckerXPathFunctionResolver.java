package org.sapid.checker.rule.xpath;

import javax.xml.namespace.QName;
import javax.xml.xpath.XPathFunction;
import javax.xml.xpath.XPathFunctionResolver;

/**
 * CX-Checker用の{@link XPathFunctionResolver}
 */
public class CXCheckerXPathFunctionResolver implements XPathFunctionResolver {
	
	private static final String NAME_SPACE_URI = "http://www.sapid.org/cx";

	// TODO 各XPath関数クラスのクラス名に統一感が無い
	// TODO クラス自体もっとまとめた方がいい？
	
	private static final QName matchesName = new QName(NAME_SPACE_URI, "matches");
	
	private static final QName typeInfoGetterName = new QName(NAME_SPACE_URI, "get-type-info");
	private static final QName pointeeInfoGetterName = new QName(NAME_SPACE_URI, "get-pointee-info");
	private static final QName trueInfoGetterName = new QName(NAME_SPACE_URI, "get-true-info");
	
	private static final QName typeSortGetterName = new QName(NAME_SPACE_URI, "get-type-sort");
	private static final QName trueTypeSortGetter = new QName(NAME_SPACE_URI, "get-true-sort");
	
	private static final QName isAssignName = new QName(NAME_SPACE_URI, "is-assign");
	private static final QName isArithmeticName = new QName(NAME_SPACE_URI, "is-arithmetic");
	private static final QName isCallName = new QName(NAME_SPACE_URI, "is-call");
	private static final QName isBitName = new QName(NAME_SPACE_URI, "is-bit");
	private static final QName isLogicalName = new QName(NAME_SPACE_URI, "is-logical");
	private static final QName isComparisonName = new QName(NAME_SPACE_URI, "is-comparison");
	private static final QName isShiftName = new QName(NAME_SPACE_URI, "is-shift");
	private static final QName isExplicitCastName = new QName(NAME_SPACE_URI, "is-explicit-cast");
	private static final QName isParentheticName = new QName(NAME_SPACE_URI, "is-parenthetic");
	
	private static final QName equalsName = new QName(NAME_SPACE_URI, "equals");
	private static final QName isLossyName = new QName(NAME_SPACE_URI, "is-lossy");
	private static final QName isVoidName = new QName(NAME_SPACE_URI, "is-void");
	private static final QName isFloatingName = new QName(NAME_SPACE_URI, "is-floating");
	private static final QName isSignedName = new QName(NAME_SPACE_URI, "is-signed");
	private static final QName isUnsignedName = new QName(NAME_SPACE_URI, "is-unsigned");
	private static final QName canBeFloatingName = new QName(NAME_SPACE_URI, "can-be-floating");
	private static final QName mayBeBooleanName = new QName(NAME_SPACE_URI, "may-be-boolean");
	private static final QName isVoidFunctionName = new QName(NAME_SPACE_URI, "is-void-function");
	private static final QName isFunctionPointerName = new QName(NAME_SPACE_URI, "is-function-pointer");
	private static final QName signGetterName = new QName(NAME_SPACE_URI, "get-sign");
	private static final QName typeLengthGetter = new QName(NAME_SPACE_URI, "get-type-length");
	private static final QName pointingLevelGetter = new QName(NAME_SPACE_URI, "get-pointing-level");
	private static final QName isImplicitCastName = new QName(NAME_SPACE_URI, "is-implicit-cast");
	private static final QName getTypeInfosByTagNameName = new QName(NAME_SPACE_URI, "get-type-infos-by-tag-name");
	
	
	@Override
	public XPathFunction resolveFunction(QName functionName, int arity) {
		if (functionName == null) {
			throw new NullPointerException();
		}
		
		// TODO ハッシュマップ化した方がいい？
		if (functionName.equals(matchesName) && arity == 2) {
			return Matches.getInstance();
		} else if (functionName.equals(typeInfoGetterName) && arity == 1) {
			return TypeInfoGetter.getInstance();
		} else if (functionName.equals(pointeeInfoGetterName) && arity == 1) {
			return PointeeInfoGetter.getInstance();
		} else if (functionName.equals(typeSortGetterName) && arity == 1) {
			return TypeSortGetter.getInstance();
		} else if (functionName.equals(isAssignName) && arity == 1) {
			return IsAssign.getInstance();
		} else if (functionName.equals(isArithmeticName) && arity == 1) {
			return IsArithmetic.getInstance();
		} else if (functionName.equals(isCallName) && arity == 1) {
			return IsCall.getInstance();
		} else if (functionName.equals(isBitName) && arity == 1) {
			return IsBit.getInstance();
		} else if (functionName.equals(isLogicalName) && arity == 1) {
			return IsLogical.getInstance();
		} else if (functionName.equals(isComparisonName) && arity == 1) {
			return IsComparison.getInstance();
		} else if (functionName.equals(isShiftName) && arity == 1) {
			return IsShift.getInstance();
		} else if (functionName.equals(isExplicitCastName) && arity == 1) {
			return IsExplicitCast.getInstance();
		} else if (functionName.equals(isParentheticName) && arity == 1) {
			return IsParenthetic.getInstance();
		} else if (functionName.equals(equalsName) && arity == 2) {
			return Equals.getInstance();
		} else if (functionName.equals(isLossyName) && arity == 2) {
			return IsLossy.getInstance();
		} else if (functionName.equals(isVoidName) && arity == 1) {
			return IsVoid.getInstance();
		} else if (functionName.equals(isFloatingName) && arity == 1) {
			return IsFloating.getInstance();
		} else if (functionName.equals(isSignedName) && arity == 1) {
			return IsSigned.getInstance();
		} else if (functionName.equals(isUnsignedName) && arity == 1) {
			return IsUnsigned.getInstance();
		} else if (functionName.equals(canBeFloatingName) && arity == 1) {
			return CanBeFloating.getInstance();
		} else if (functionName.equals(mayBeBooleanName) && arity == 1) {
			return MayBeBoolean.getInstance();
		} else if (functionName.equals(isVoidFunctionName) && arity == 1) {
			return IsVoidFunction.getInstance();
		} else if (functionName.equals(isFunctionPointerName) && arity == 1) {
			return IsFunctionPointer.getInstance();
		} else if (functionName.equals(signGetterName) && arity == 1) {
			return SignGetter.getInstance();
		} else if (functionName.equals(trueTypeSortGetter) && arity == 1) {
			return TrueTypeSortGetter.getInstance();
		} else if (functionName.equals(typeLengthGetter) && arity == 1) {
			return TypeLengthGetter.getInstance();
		} else if (functionName.equals(pointingLevelGetter) && arity == 1) {
			return PointingLevelGetter.getInstance();
		} else if (functionName.equals(trueInfoGetterName) && arity == 1) {
			return TrueInfoGetter.getInstance();
		} else if (functionName.equals(isImplicitCastName) && arity == 1) {
			return IsImplicitCast.getInstance();
		} else if (functionName.equals(getTypeInfosByTagNameName) && arity == 2) {
			return GetTypeInfosByTagName.getInstance();
		}
		
		return null;
	}

}
