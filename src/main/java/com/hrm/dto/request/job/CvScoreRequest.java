package com.hrm.dto.request.job;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class CvScoreRequest {
    @NotNull
    private Integer assessmentId;

    @Valid
    @NotEmpty
    private List<ScoreItem> scores;

    @Getter
    @Setter
    public static class ScoreItem {
        @NotNull
        private Integer requirementId;
        @NotNull
        private Double score;
        private String comment;
    }
}
