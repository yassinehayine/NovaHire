package com.novahire.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Professional report payload: the interview configuration plus its evaluation, composed so the
 * report page renders from a single request. Reuses the existing {@link InterviewResponse} for
 * configuration rather than duplicating those fields.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ReportResponse {

    private InterviewResponse interview;
    private EvaluationResponse evaluation;
}
