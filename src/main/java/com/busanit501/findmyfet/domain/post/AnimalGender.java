package com.busanit501.findmyfet.domain.post;

import com.fasterxml.jackson.annotation.JsonCreator; // ❗❗ import 추가
import lombok.Getter;

import java.util.stream.Stream; // ❗❗ import 추가

@Getter
public enum AnimalGender {

    MALE("수컷"),
    FEMALE("암컷"),
    UNKNOWN("모름");

    private final String description;

    AnimalGender(String description) {
        this.description = description;
    }

    // ❗❗❗ [아래 메서드 추가] ❗❗❗
    // "암컷" 과 같은 문자열을 받으면, 해당하는 Enum(FEMALE)을 찾아 반환하는 메서드
    @JsonCreator
    public static AnimalGender from(String description) {
        // description이 null이거나 비어있으면 UNKNOWN 반환
        if (description == null || description.isEmpty()) {
            return UNKNOWN;
        }

        // Enum의 모든 값을 순회하며 description이 일치하는 것을 찾음
        for (AnimalGender gender : AnimalGender.values()) {
            if (gender.getDescription().equals(description)) {
                return gender;
            }
        }

        // 일치하는 것이 없으면 IllegalArgumentException 발생 (또는 UNKNOWN을 반환할 수도 있음)
        throw new IllegalArgumentException(description + "에 해당하는 성별을 찾을 수 없습니다.");
    }
}