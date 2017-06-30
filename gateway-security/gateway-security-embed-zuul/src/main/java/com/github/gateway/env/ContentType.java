package com.github.gateway.env;

/**
 * contentType类型
 */
public enum ContentType {

    FORM_URLENCODED("application/x-www-form-urlencoded"),
    FORM_DATA("multipart/form-data"),
    APPLICATION_JSON("application/json"),
    TEXT_XML("text/xml")
    ;

    private String type;

    ContentType(String type) {
        this.type = type;
    }

    public String getType() {
        return type;
    }

    public boolean isFormUrlencoded() {
        return this.equals(FORM_URLENCODED);
    }
    public boolean isFormData() {
        return this.equals(FORM_DATA);
    }
    public boolean isApplicationJson() {
        return this.equals(APPLICATION_JSON);
    }
    public boolean isTextXml() {
        return this.equals(TEXT_XML);
    }

    public static boolean isFormUrlencoded(String type) {
        return FORM_URLENCODED.getType().equals(type);
    }

    /**
     * 由type取得ContentType
     * @param type
     * @param defaultContentType 如果type为null或者找不到， 返回{@code defaultContentType}
     */
    public static ContentType of(String type, ContentType defaultContentType) {
        if(type == null) {
            return defaultContentType;
        }
        ContentType[] types = ContentType.values();
        for(ContentType contentType : types) {
            if(contentType.getType().equals(type)) {
                return contentType;
            }
        }
        return defaultContentType;
    }
}
