package doore.document.domain;

import doore.document.exception.DocumentException;
import doore.document.exception.DocumentExceptionType;

public enum DocumentGroupType {
    STUDY("studies"),
    TEAM("teams"),
    ;

    private String groupType;

    DocumentGroupType(String groupType) {
        this.groupType = groupType;
    }
    public static DocumentGroupType value(String value) {
        if(value.equals("studies")) return STUDY;
        if(value.equals("teams")) return TEAM;
        throw new DocumentException(DocumentExceptionType.NOT_FOUND_DOCUMENT);
    }

}
