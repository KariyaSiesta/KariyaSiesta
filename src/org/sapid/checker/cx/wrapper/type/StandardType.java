package org.sapid.checker.cx.wrapper.type;

import java.util.HashMap;

import org.sapid.checker.core.ConfigManager;
import org.w3c.dom.Element;

/**
 * 基本型を表すクラス。
 * @author uehara
 */
public class StandardType extends Type {
	
	private static int charLength = 8;
	private static int shortIntLength = 16;
	private static int intLength = 32;
	private static int longIntLength = 64;
	private static int longLongIntLength = 96;
	private static int floatLength = 32;
	private static int doubleLength = 64;
	private static int longDoubleLength = 96;

	private static final String CHAR = "char";
	private static final String SHORT_INT = "short_int";
	private static final String INT = "int";
	private static final String LONG_INT = "long_int";
	private static final String LONG_LONG_INT = "long_long_int";
	private static final String FLOAT = "float";
	private static final String DOUBLE = "double";
	private static final String LONG_DOUBLE = "long_double";
	    	
	private Sort sort;
	private Sign sign = Sign.UNSPECIFIED;
	private Size size = Size.NORMAL;

    public static int getCharLength() {
            return charLength;
    }

    public static int getShortIntLength() {
            return shortIntLength;
    }

    public static int getIntLength() {
            return intLength;
    }

    public static int getLongIntLength() {
            return longIntLength;
    }

    public static int getLongLongIntLength() {
            return longLongIntLength;
    }

    public static int getFloatLength() {
            return floatLength;
    }

    public static int getDoubleLength() {
            return doubleLength;
    }

    public static int getLongDoubleLength() {
            return longDoubleLength;
    }

	static {
		// 各型のビットサイズを設定する．
		// 環境変数SAPID_DESTが正しく設定されていない場合，または，設定ファイル(CXC.conf)が指定の場所に存在しない場合は，デフォルト値(フィールドの初期値)が使用される．
		if (ConfigManager.isEnableConfig()) {
			setSizeOfTypes();
		}
	}
	
	/**
	 * プロパティファイルから各型のサイズを読み取り，その値をフィールドに代入する．
	 * @param propertiesFilePath プロパティファイルへの絶対パス
	 */
	private static void setSizeOfTypes() {
		HashMap<String, String> values = new HashMap<String, String>();
		values.put(CHAR, null);
		values.put(SHORT_INT, null);
		values.put(INT, null);
		values.put(LONG_INT, null);
		values.put(LONG_LONG_INT, null);
		values.put(FLOAT, null);
		values.put(DOUBLE, null);
		values.put(LONG_DOUBLE, null);
		ConfigManager.getProperty(values);
		try {
			charLength = Integer.parseInt(values.get(CHAR));
			shortIntLength = Integer.parseInt(values.get(SHORT_INT));
			intLength = Integer.parseInt(values.get(INT));
			longIntLength = Integer.parseInt(values.get(LONG_INT));
			longLongIntLength = Integer.parseInt(values.get(LONG_LONG_INT));
			floatLength = Integer.parseInt(values.get(FLOAT));
			doubleLength = Integer.parseInt(values.get(DOUBLE));
			longDoubleLength = Integer.parseInt(values.get(LONG_DOUBLE));
		} catch (NumberFormatException e) {
			System.err.println("Error. Illegal format of properties file.");
			e.printStackTrace();
		}
	}
	
	StandardType(Element typeInfoElement) {
		super(typeInfoElement);
		
		if (! typeInfoElement.hasAttribute(TypeInfosConstant.TYPE_ATTRIBUTE_NAME)) {
			throw new IllegalArgumentException();
		}
		
		this.sort = Sort.fromString(typeInfoElement.getAttribute(TypeInfosConstant.TYPE_ATTRIBUTE_NAME));
		
		if (typeInfoElement.hasAttribute(TypeInfosConstant.SIGN_ATTRIBUTE_NAME)) {
			this.sign = Sign.fromString(typeInfoElement.getAttribute(TypeInfosConstant.SIGN_ATTRIBUTE_NAME));
		}
		
		if (typeInfoElement.hasAttribute(TypeInfosConstant.SIZE_ATTRIBUTE_NAME)) {
			this.size = Size.fromString(typeInfoElement.getAttribute(TypeInfosConstant.SIZE_ATTRIBUTE_NAME));
		}
	}
	
	/**
	 * IDの無い基本型を生成する。
	 * @param sort
	 * @param sign
	 * @param size
	 * @param isConst
	 * @param isVolatile
	 */
	public StandardType(Sort sort, Sign sign, Size size, boolean isConst, boolean isVolatile) {
		super(isConst, isVolatile);
		
		this.sort = sort;
		this.sign = sign;
		this.size = size;
	}
	

	@Override
	public Type.Sort getSort() {
		return Type.Sort.STANDARD;
	}

	@Override
	public String getText() {
		// TODO
		return this.getTypeInfoElement().getAttribute(TypeInfosConstant.TEXT_ATTRIBUTE_NAME);
	}
	
	public Sort getType() {
		return this.sort;
	}
	
	public Sign getSign() {
		return this.sign;
	}
	
	public Size getSize() {
		return this.size;
	}
	
	/**
	 * 通常の算術変換 (Usual Arithmetic Conversions)
	 * @param anotherType
	 * @return
	 */
	public StandardType arithmeticConversion(StandardType another) {
		Sort thisSort = this.getType();
		Sort anotherSort = another.getType();
		
		if (thisSort == Sort.DOUBLE && this.getSize() == Size.LONG
				|| anotherSort == Sort.DOUBLE && another.getSize() == Size.LONG) {
			return new StandardType(Sort.DOUBLE, Sign.UNSPECIFIED, Size.LONG, false, false);
		} else if (thisSort == Sort.DOUBLE || anotherSort == Sort.DOUBLE) {
			return new StandardType(Sort.DOUBLE, Sign.UNSPECIFIED, Size.NORMAL, false, false);
		} else if (thisSort == Sort.FLOAT || anotherSort == Sort.FLOAT) {
			return new StandardType(Sort.FLOAT, Sign.UNSPECIFIED, Size.NORMAL, false, false);
		} else {
			StandardType promotedThis = this.integralPromotion();
			StandardType promotedAnother = another.integralPromotion();
			
			if (promotedThis.equals(promotedAnother)) {
				return promotedThis;
			} else if ((promotedThis.getSign() == Sign.UNSIGNED)
					== (promotedAnother.getSign() == Sign.UNSIGNED)) {
				if (promotedThis.getSize() == Size.LONGLONG || promotedAnother.getSize() == Size.LONGLONG) {
					return new StandardType(Sort.INT, promotedThis.getSign(), Size.LONGLONG, false, false);
				} else if (promotedThis.getSize() == Size.LONG || promotedAnother.getSize() == Size.LONG) {
					return new StandardType(Sort.INT, promotedThis.getSign(), Size.LONG, false, false);
				} else {
					return new StandardType(Sort.INT, promotedThis.getSign(), Size.NORMAL, false, false);
				}
			} else {
				StandardType signed, unsigned;
				if (promotedThis.getSign() != Sign.UNSIGNED) {
					signed = promotedThis;
					unsigned = promotedAnother;
				} else {
					signed = promotedAnother;
					unsigned = promotedThis;
				}
				
				if (unsigned.isEqualOrHigherThanInIntegerConversionRank(signed)) {
					return unsigned;
				} else if (signed.getLength() > unsigned.getLength()) {
					return signed;
				} else {
					return new StandardType(signed.getType(), Sign.UNSIGNED, signed.getSize(), false, false);
				}
			}
		}
	}

	/**
	 * 整数への格上げ (Integral/Integer Promotion)
	 * @return
	 */
	private StandardType integralPromotion() {
		StandardType promotedType;
		
		if (this.getType() == Sort.CHAR) {
			if (this.getSign() == Sign.UNSIGNED) {
				if (StandardType.intLength > StandardType.charLength) {
					promotedType = new StandardType(Sort.INT, Sign.SIGNED, Size.NORMAL, false, false);
				} else {
					promotedType = new StandardType(Sort.INT, Sign.UNSIGNED, Size.NORMAL, false, false);
				}
			} else {
				promotedType = new StandardType(Sort.INT, Sign.SIGNED, Size.NORMAL, false, false);
			}
		} else if (this.getType() == Sort.INT && this.getSize() == Size.SHORT) {
			if (this.getSign() == Sign.UNSIGNED) {
				if (StandardType.intLength > StandardType.shortIntLength) {
					promotedType = new StandardType(Sort.INT, Sign.SIGNED, Size.NORMAL, false, false);
				} else {
					promotedType = new StandardType(Sort.INT, Sign.UNSIGNED, Size.NORMAL, false, false);
				}
			} else {
				promotedType = new StandardType(Sort.INT, Sign.SIGNED, Size.NORMAL, false, false);
			}
		} else {
			promotedType = this;
		}
		
		return promotedType;
	}
	
	/**
	 * 整数返還の順位 (Integer Conversion Rank)
	 * 現在intにしか対応していない
	 * @param t
	 * @return
	 */
	private boolean isEqualOrHigherThanInIntegerConversionRank(StandardType another) {
		Size thisSize = this.getSize();
		Size anotherSize = another.getSize();
		
		if (thisSize == anotherSize) {
			return true;
		} else if (thisSize == Size.LONGLONG) {
			return true;
		} else if (thisSize == Size.LONG && anotherSize != Size.LONGLONG) {
			return true;
		} else if (thisSize == Size.NORMAL && anotherSize == Size.SHORT) {
			return true;
		} else {
			return false;
		}
	}
	
	@Override
	public boolean equals(Object o) {
		if (o != null && o instanceof StandardType) {
			StandardType t = (StandardType) o;
			
			return this.isConst() == t.isConst()
					&& this.isVolatile() == t.isVolatile() && this.getType() == t.getType()
					&& this.getSign() == t.getSign() && this.getSize() == t.getSize();
		} else {
			return false;
		}
	}
	
	@Override
	public boolean isCompatibleWith(Type anotherType) {
		if (anotherType == null) {
			return false;
		}
		
		if (anotherType.getSort() == Type.Sort.TYPEDEF) {
			anotherType = ((TypedefType) anotherType).getTrueTypeRecursively();
		}
		
		if (anotherType.getSort() != Type.Sort.STANDARD) {
			return false;
		}
		StandardType t = (StandardType) anotherType;
		
		// TODO 現在のところは 符号有無指定無し＝符号あり と仮定している
		boolean typeCompatible = this.getType() == t.getType()
				|| this.getType() == Sort.INT && t.getType() == Sort.UNSPECIFIED
				|| this.getType() == Sort.UNSPECIFIED && t.getType() == Sort.INT;
		boolean signCompatible = this.getSign() == t.getSign()
				|| this.getSign() == Sign.SIGNED && t.getSign() == Sign.UNSPECIFIED
				|| this.getSign() == Sign.UNSPECIFIED && t.getSign() == Sign.SIGNED;
		
		boolean sizeEquals = this.getSize() == t.getSize();
		
		return typeCompatible && signCompatible && sizeEquals;
	}
	
	/**
	 * this を another に変換するとき情報の損失が起こるか否かを調べる
	 * @param another
	 * @return this を another に変換するとき情報の損失が起こるか否か
	 */
	public boolean isLossy(StandardType another) {
		// TODO これで正しいのか自信無し
		Sign thisSign = this.getSign();
		Sign anotherSign = another.getSign();
		int thisLength = this.getLength();
		int anotherLength = another.getLength();
		
		if (thisSign == Sign.SIGNED && anotherSign != Sign.SIGNED) {
			return true;
		}
		
		if (thisLength > anotherLength) {
			return true;
		}
		
		if (thisLength == anotherLength) {
			if (thisSign == Sign.UNSPECIFIED && anotherSign == Sign.UNSIGNED) {
				return true;
			}
			if (thisSign == Sign.SIGNED && anotherSign != Sign.SIGNED) {
				return true;
			}
			if (thisSign == Sign.UNSIGNED && anotherSign != Sign.UNSIGNED) {
				return true;
			}
		}
		
		return false;
	}
	
	/**
	 * この型のビット数を返す。voidの場合は0を返す。
	 * @return ビット数
	 */
	public int getLength() {
		Sort sort = this.getType();
		
		if (sort == Sort.VOID) {
			return 0;
		} else if (sort == Sort.CHAR) {
			return StandardType.charLength;
		} else if (sort == Sort.INT) {
			Size size = this.getSize();
			
			if (size == Size.LONGLONG) {
				return StandardType.longLongIntLength;
			} else if (size == Size.LONG) {
				return StandardType.longIntLength;
			} else if (size == Size.NORMAL) {
				return StandardType.intLength;
			} else if (size == Size.SHORT) {
				return StandardType.shortIntLength;
			} else {
				throw new IllegalStateException();
			}
		} else if (sort == Sort.FLOAT) {
			return StandardType.floatLength;
		} else if (sort == Sort.DOUBLE) {
			Size size = this.getSize();
			
			if (size == Size.NORMAL) {
				return StandardType.doubleLength;
			} else if (size == Size.LONG) {
				return StandardType.longDoubleLength;
			} else {
				throw new IllegalStateException();
			}
		} else {
			throw new UnsupportedOperationException();
		}
	}
	
	public static StandardType fromLiteral(String string) {
		StandardType type;
		
		if (string.contains(".") || string.contains("e") || string.contains("E")) {
			if (string.endsWith("f") || string.endsWith("F")) {
				type = new StandardType(Sort.FLOAT, Sign.UNSPECIFIED, Size.NORMAL, false, false);
			} else if (string.endsWith("l") || string.endsWith("L")) {
				type = new StandardType(Sort.DOUBLE, Sign.UNSPECIFIED, Size.LONG, false, false);
			} else {
				type = new StandardType(Sort.DOUBLE, Sign.UNSPECIFIED, Size.NORMAL, false, false);
			}
		} else {
			boolean unsignedFlag = string.contains("u") || string.contains("U");
			boolean longFlag = string.contains("l") || string.contains("L");
			
			if (unsignedFlag && longFlag) {
				long value = Long.parseLong(string.replaceAll("[uUlL]", ""));
				if (value < 0) {
					type = null;
				} else if (value <= Math.scalb(1.0, StandardType.longIntLength) - 1) {
					type = new StandardType(Sort.INT, Sign.UNSIGNED, Size.LONG, false, false);
				} else if (value <= Math.scalb(1.0, StandardType.longLongIntLength) - 1) {
					type = new StandardType(Sort.INT, Sign.UNSIGNED, Size.LONGLONG, false, false);
				} else {
					type = null;
				}
			} else if (unsignedFlag) {
				long value = Long.parseLong(string.replaceAll("[uU]", ""));
				if (value < 0) {
					type = null;
				} else if (value <= Math.scalb(1.0, StandardType.intLength) - 1) {
					type = new StandardType(Sort.INT, Sign.UNSIGNED, Size.NORMAL, false, false);
				} else if (value <= Math.scalb(1.0, StandardType.longIntLength) - 1) {
					type = new StandardType(Sort.INT, Sign.UNSIGNED, Size.LONG, false, false);
				} else if (value <= Math.scalb(1.0, StandardType.longLongIntLength) - 1) {
					type = new StandardType(Sort.INT, Sign.UNSIGNED, Size.LONGLONG, false, false);
				} else {
					type = null;
				}
			} else if (longFlag) {
				long value = Long.parseLong(string.replaceAll("[lL]", ""));
				if (value >= - Math.scalb(1.0, StandardType.longIntLength - 1) && value <= Math.scalb(1.0, StandardType.longIntLength - 1) - 1) {
					type = new StandardType(Sort.INT, Sign.UNSPECIFIED, Size.LONG, false, false);
				} else if (value >= 0 && value <= Math.scalb(1.0, StandardType.longIntLength) - 1) {
					type = new StandardType(Sort.INT, Sign.UNSIGNED, Size.LONG, false, false);
				} else if (value >= - Math.scalb(1.0, StandardType.longLongIntLength - 1) && value <= Math.scalb(1.0, StandardType.longLongIntLength - 1) - 1) {
					type = new StandardType(Sort.INT, Sign.UNSPECIFIED, Size.LONGLONG, false, false);
				} else if (value >= 0 && value <= Math.scalb(1.0, StandardType.longLongIntLength) - 1) {
					type = new StandardType(Sort.INT, Sign.UNSIGNED, Size.LONGLONG, false, false);
				} else {
					type = null;
				}
			} else {
				if (string.startsWith("0")) {
					long value;
					if (string.startsWith("0x") || string.startsWith("0X")) {
						value = Long.parseLong(string.substring(2), 16);
					} else {
						value = Long.parseLong(string, 8);
					}
					
					if (value >= - Math.scalb(1.0, StandardType.intLength - 1) && value <= Math.scalb(1.0, StandardType.intLength - 1) - 1) {
						type = new StandardType(Sort.INT, Sign.UNSPECIFIED, Size.NORMAL, false, false);
					} else if (value >= 0 && value <= Math.scalb(1.0, StandardType.intLength) - 1) {
						type = new StandardType(Sort.INT, Sign.UNSIGNED, Size.NORMAL, false, false);
					} else if (value >= - Math.scalb(1.0, StandardType.longIntLength - 1) && value <= Math.scalb(1.0, StandardType.longIntLength - 1) - 1) {
						type = new StandardType(Sort.INT, Sign.UNSPECIFIED, Size.LONG, false, false);
					} else if (value >= 0 && value <= Math.scalb(1.0, StandardType.longIntLength) - 1) {
						type = new StandardType(Sort.INT, Sign.UNSIGNED, Size.LONG, false, false);
					} else if (value >= - Math.scalb(1.0, StandardType.longLongIntLength - 1) && value <= Math.scalb(1.0, StandardType.longLongIntLength - 1) - 1) {
						type = new StandardType(Sort.INT, Sign.UNSPECIFIED, Size.LONGLONG, false, false);
					} else if (value >= 0 && value <= Math.scalb(1.0, StandardType.longLongIntLength) - 1) {
						type = new StandardType(Sort.INT, Sign.UNSIGNED, Size.LONGLONG, false, false);
					} else {
						type = null;
					}
				} else {
					long value = Long.parseLong(string);
					if (value >= - Math.scalb(1.0, StandardType.intLength - 1) && value <= Math.scalb(1.0, StandardType.intLength - 1) - 1) {
						type = new StandardType(Sort.INT, Sign.UNSPECIFIED, Size.NORMAL, false, false);
					} else if (value >= - Math.scalb(1.0, StandardType.longIntLength - 1) && value <= Math.scalb(1.0, StandardType.longIntLength - 1) - 1) {
						type = new StandardType(Sort.INT, Sign.UNSPECIFIED, Size.LONG, false, false);
					} else if (value >= - Math.scalb(1.0, StandardType.longLongIntLength - 1) && value <= Math.scalb(1.0, StandardType.longLongIntLength - 1) - 1) {
						type = new StandardType(Sort.INT, Sign.UNSPECIFIED, Size.LONGLONG, false, false);
					} else if (value >= 0 && value <= Math.scalb(1.0, StandardType.longLongIntLength) - 1) {
						type = new StandardType(Sort.INT, Sign.UNSIGNED, Size.LONGLONG, false, false);
					} else {
						type = null;
					}
				}
			}
		}
		
		return type;
	}
	
	@Override
	public String toString() {
		StringBuilder string = new StringBuilder();
		
		if (this.isConst()) {
			string.append("const ");
		}
		
		if (this.isVolatile()) {
			string.append("volatile ");
		}
		
		switch (this.getSign()) {
		case SIGNED:
			string.append("signed ");
			break;
		case UNSIGNED:
			string.append("unsigned ");
			break;
		}
		
		switch (this.getSize()) {
		case SHORT:
			string.append("short ");
			break;
		case LONG:
			string.append("long ");
			break;
		case LONGLONG:
			string.append("long long ");
			break;
		}
		
		switch (this.getType()) {
		case VOID:
			string.append("void");
			break;
		case CHAR:
			string.append("char");
			break;
		case INT:
			string.append("int");
			break;
		case FLOAT:
			string.append("float");
			break;
		case DOUBLE:
			string.append("double");
			break;
		case UNSPECIFIED:
			if (string.length() > 0) {
				string.deleteCharAt(string.length() - 1);
			}
			break;
		}
		
		return string.toString();
	}
	
	
	public static enum Sort {
		
		VOID,
		INT,
		CHAR,
		FLOAT,
		DOUBLE,
		UNSPECIFIED;
		
		
		private static Sort fromString(String string) {
			Sort sort;
			
			if (string.equals(TypeInfosConstant.TYPE_ATTRIBUTE_VALUE_VOID)) {
				sort = VOID;
			} else if (string.equals(TypeInfosConstant.TYPE_ATTRIBUTE_VALUE_INT)) {
				sort = INT;
			} else if (string.equals(TypeInfosConstant.TYPE_ATTRIBUTE_VALUE_CHAR)) {
				sort = CHAR;
			} else if (string.equals(TypeInfosConstant.TYPE_ATTRIBUTE_VALUE_FLOAT)) {
				sort = FLOAT;
			} else if (string.equals(TypeInfosConstant.TYPE_ATTRIBUTE_VALUE_DOUBLE)) {
				sort = DOUBLE;
			} else if (string.equals(TypeInfosConstant.TYPE_ATTRIBUTE_VALUE_UNSPECIFIED)) {
				sort = UNSPECIFIED;
			} else {
				throw new IllegalArgumentException("Unknown \"type\" attribute \"" + string + "\".");
			}
			
			return sort;
		}
		
	}
	
	public static enum Size {
		
		SHORT,
		NORMAL,
		LONG,
		LONGLONG;
		
		
		private static Size fromString(String string) {
			Size size;
			
			if (string.equals(TypeInfosConstant.SIZE_ATTRIBUTE_VALUE_SHORT)) {
				size = SHORT;
			} else if (string.equals(TypeInfosConstant.SIZE_ATTRIBUTE_VALUE_STANDARD)) {
				size = NORMAL;
			} else if (string.equals(TypeInfosConstant.SIZE_ATTRIBUTE_VALUE_LONG)) {
				size = LONG;
			} else if (string.equals(TypeInfosConstant.SIZE_ATTRIBUTE_VALUE_LONGLONG)) {
				size = LONGLONG;
			} else {
				throw new IllegalArgumentException(string);
			}
			
			return size;
		}
		
	}
	
	public static enum Sign {

		SIGNED(TypeInfosConstant.BOOLEAN_ATTRIBUTE_VALUE_TRUE),
		UNSIGNED(TypeInfosConstant.BOOLEAN_ATTRIBUTE_VALUE_FALSE),
		UNSPECIFIED(TypeInfosConstant.BOOLEAN_ATTRIBUTE_VALUE_UNSPECIFIED);
		
		
		private String string;
		
		
		private Sign(String string) {
			this.string = string;
		}
		
		@Override
		public String toString() {
			return this.string;
		}
		
		private static Sign fromString(String string) {
			Sign sign = null;
			
			for (Sign value : Sign.values()) {
				if (string.equals(value.toString())) {
					sign = value;
				}
			}
			
			return sign;
		}
		
	}
	
}
