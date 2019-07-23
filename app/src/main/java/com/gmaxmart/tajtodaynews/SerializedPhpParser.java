package com.gmaxmart.tajtodaynews;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.regex.Pattern;

/**
 * Created by RACHIT on 6/23/2017.
 */

public class SerializedPhpParser {
    private final String input;

    private int index;

    private boolean assumeUTF8 = true;

    private Pattern acceptedAttributeNameRegex = null;

    public SerializedPhpParser(String input) {
        this.input = input;
    }

    public SerializedPhpParser(String input, boolean assumeUTF8) {
        this.input = input;
        this.assumeUTF8 = assumeUTF8;
    }

    public Object parse() {
        char type = input.charAt(index);
        switch (type) {
            case 'i':
                index += 2;
                // Patch Integer/Double for the PHP x64.
                Object tmp;
                tmp = parseInt();
                if (tmp == null) {
                    tmp = parseFloat();
                }
                return tmp;
            // End of Patch Integer/Double for the PHP x64.
            case 'd':
                index += 2;
                return parseFloat();
            case 'b':
                index += 2;
                return parseBoolean();
            case 's':
                index += 2;
                return parseString();
            case 'a':
                index += 2;
                return parseArray();
            case 'O':
                index += 2;
                return parseObject();
            case 'N':
                index += 2;
                return NULL;
            default:
                throw new IllegalStateException("Encountered unknown type [" + type
                        + "]");
        }
    }

    private Object parseObject() {
        PhpObject phpObject = new PhpObject();
        int strLen = readLength();
        phpObject.name = input.substring(index, index + strLen);
        index = index + strLen + 2;
        int attrLen = readLength();
        for (int i = 0; i < attrLen; i++) {
            Object key = parse();
            Object value = parse();
            if (isAcceptedAttribute(key)) {
                phpObject.attributes.put(key, value);
            }
        }
        index++;
        return phpObject;
    }

    private Map<Object, Object> parseArray() {
        int arrayLen = readLength();
        Map<Object, Object> result = new LinkedHashMap<Object, Object>();
        for (int i = 0; i < arrayLen; i++) {
            Object key = parse();
            Object value = parse();
            if (isAcceptedAttribute(key)) {
                result.put(key, value);
            }
        }
        index++;
        return result;
    }

    private boolean isAcceptedAttribute(Object key) {
        if (acceptedAttributeNameRegex == null) {
            return true;
        }
        if (!(key instanceof String)) {
            return true;
        }
        return acceptedAttributeNameRegex.matcher((String)key).matches();
    }

    private int readLength() {
        int delimiter = input.indexOf(':', index);
        int arrayLen = Integer.valueOf(input.substring(index, delimiter));
        index = delimiter + 2;
        return arrayLen;
    }

    /**
     * Assumes strings are utf8 encoded
     *
     * @return
     */
    private String parseString() {
        int strLen = readLength();

        int utfStrLen = 0;
        int byteCount = 0;
        while (byteCount != strLen) {
            char ch = input.charAt(index + utfStrLen++);
            if (assumeUTF8) {
                if ((ch >= 0x0001) && (ch <= 0x007F)) {
                    byteCount++;
                } else if (ch > 0x07FF) {
                    byteCount += 3;
                } else {
                    byteCount += 2;
                }
            } else {
                byteCount++;
            }
        }
        String value = input.substring(index, index + utfStrLen);
        index = index + utfStrLen + 2;
        return value;
    }

    private Boolean parseBoolean() {
        int delimiter = input.indexOf(';', index);
        String value = input.substring(index, delimiter);
        if (value.equals("1")) {
            value = "true";
        } else if (value.equals("0")) {
            value = "false";
        }
        index = delimiter + 1;
        return Boolean.valueOf(value);
    }

    private Double parseFloat() {
        int delimiter = input.indexOf(';', index);
        String value = input.substring(index, delimiter);
        index = delimiter + 1;
        return Double.valueOf(value);
    }

    private Integer parseInt() {
        int delimiter = input.indexOf(';', index);
        // Let's store old value of the index for the patch Integer/Double for the PHP x64.
        int index_old=index;
        String value = input.substring(index, delimiter);
        index = delimiter + 1;
        // Patch Integer/Double for the PHP x64.
        try {
            return Integer.valueOf(value);
        } catch (Exception ex) {
            index=index_old;
        }
        return null;
        // End of Patch Integer/Double for the PHP x64.
    }

    public void setAcceptedAttributeNameRegex(String acceptedAttributeNameRegex) {
        this.acceptedAttributeNameRegex = Pattern.compile(acceptedAttributeNameRegex);
    }

    public static final Object NULL = new Object() {
        @Override
        public String toString() {
            return "NULL";
        }
    };

    /**
     * Represents an object that has a name and a map of attributes
     */
    public static class PhpObject {
        public String name;
        public Map<Object, Object> attributes = new HashMap<Object, Object>();

        @Override
        public String toString() {
            return "\"" + name + "\" : " + attributes.toString();
        }
    }

}
