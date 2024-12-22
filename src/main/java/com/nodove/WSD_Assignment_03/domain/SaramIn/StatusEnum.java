package com.nodove.WSD_Assignment_03.domain.SaramIn;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;
import lombok.Data;

public enum StatusEnum {
    PENDING, REVIEWING, INTERVIEW, OFFERED, REJECTED, WITHDRAWN;

    @JsonCreator
    public static StatusEnum fromString(String value) {
        try {
            return value == null ? null : StatusEnum.valueOf(value.toUpperCase());
        } catch (IllegalArgumentException e) {
            return null; // 또는 기본값 설정
        }
    }

    @JsonValue
    public String toValue() {
        return this.name();
    }
}
