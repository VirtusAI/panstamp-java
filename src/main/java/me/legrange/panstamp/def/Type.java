package me.legrange.panstamp.def;

/**
 * Enum for the types of data defined for panStamp data points
 * @author gideon
 */
public enum Type {
    BINARY("bin"), NUMBER("num"), STRING("str"), INTEGER("int");

    static Type forTag(String tag) {
        for (Type type : Type.values()) {
            if (type.tag.equals(tag)) {
                return type;
            }
        }
        return null;
    }

    private Type(String tag) {
        this.tag = tag;
    }
    private final String tag;
    
}
