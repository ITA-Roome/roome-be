package com.roome.roome.be.domain.reference.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.Arrays;
import java.util.Set;

@Getter
@AllArgsConstructor
public enum ReferenceMoodMapping {

    // 한글 설명 -> ReferenceMood(무드) 매핑
    RELAXED("편안·차분", Set.of(ReferenceMood.CALM, ReferenceMood.COMFORTABLE, ReferenceMood.NEAT)),
    WARM_FEEL("따뜻·포근", Set.of(ReferenceMood.WARM, ReferenceMood.COZY, ReferenceMood.SOFT)),
    BRIGHT("밝고 산뜻", Set.of(ReferenceMood.COOL, ReferenceMood.NEAT, ReferenceMood.SOFT)),
    CHIC("시크·도시적", Set.of(ReferenceMood.MODERN, ReferenceMood.COOL, ReferenceMood.LUXURIOUS)),
    TRENDY("감각·트렌디", Set.of(ReferenceMood.DECORATIVE, ReferenceMood.PERSONAL, ReferenceMood.MODERN)),
    UNKNOWN("잘 모르겠어요", Set.of(ReferenceMood.COMFORTABLE, ReferenceMood.NEAT)); // 무드 기본값

    private final String description;
    private final Set<ReferenceMood> moods; // *오직 Mood 타입만 가짐

    public static Set<ReferenceMood> findMoodsByDescription(String input) {
        return Arrays.stream(values())
                .filter(m -> m.description.equals(input))
                .findFirst()
                .map(ReferenceMoodMapping::getMoods)
                .orElse(UNKNOWN.getMoods());
    }
}