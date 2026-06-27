package com.novahire.service.question;

import com.novahire.entity.Interview;
import com.novahire.entity.InterviewQuestion;
import com.novahire.entity.InterviewQuestion.QuestionCategory;
import com.novahire.service.question.StaticQuestionBank.BankedQuestion;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Sprint 3 implementation of {@link QuestionGenerationStrategy} — pulls a randomized mix of
 * generic, already-authored questions from {@link StaticQuestionBank}. Sprint 4 swaps in an
 * AI-backed implementation of the same interface; nothing here is called directly by callers.
 */
@Service
@RequiredArgsConstructor
public class StaticQuestionGenerationStrategy implements QuestionGenerationStrategy {

    private static final double TECHNICAL_RATIO = 0.6;

    private final StaticQuestionBank bank;

    @Override
    public List<InterviewQuestion> generateQuestions(Interview interview) {
        List<BankedQuestion> pool = bank.findFor(interview.getExperienceLevel(), interview.getLanguage());
        if (pool.isEmpty()) {
            pool = bank.findFor(interview.getExperienceLevel(), "ENGLISH");
        }

        int questionCount = interview.getQuestionCount();
        int technicalTarget = Math.round((float) (questionCount * TECHNICAL_RATIO));
        int behavioralTarget = questionCount - technicalTarget;

        List<BankedQuestion> technical = shuffledByCategory(pool, QuestionCategory.TECHNICAL);
        List<BankedQuestion> behavioral = shuffledByCategory(pool, QuestionCategory.BEHAVIORAL);

        List<BankedQuestion> selected = new ArrayList<>();
        selected.addAll(takeUpTo(technical, technicalTarget));
        selected.addAll(takeUpTo(behavioral, behavioralTarget));

        // Backfill from whichever pool still has spare questions if one category fell short.
        int shortfall = questionCount - selected.size();
        if (shortfall > 0) {
            List<BankedQuestion> leftovers = new ArrayList<>(pool);
            leftovers.removeAll(selected);
            Collections.shuffle(leftovers);
            selected.addAll(takeUpTo(leftovers, shortfall));
        }

        Collections.shuffle(selected);

        List<InterviewQuestion> result = new ArrayList<>();
        for (int i = 0; i < selected.size(); i++) {
            BankedQuestion bq = selected.get(i);
            result.add(InterviewQuestion.builder()
                    .interview(interview)
                    .orderIndex(i)
                    .text(bq.text())
                    .category(bq.category())
                    .difficulty(interview.getExperienceLevel())
                    .source(InterviewQuestion.QuestionSource.STATIC)
                    .sourceRef(bq.sourceRef())
                    .createdAt(LocalDateTime.now())
                    .build());
        }
        return result;
    }

    private List<BankedQuestion> shuffledByCategory(List<BankedQuestion> pool, QuestionCategory category) {
        List<BankedQuestion> filtered = pool.stream()
                .filter(q -> q.category() == category)
                .collect(Collectors.toCollection(ArrayList::new));
        Collections.shuffle(filtered);
        return filtered;
    }

    private List<BankedQuestion> takeUpTo(List<BankedQuestion> source, int count) {
        return source.subList(0, Math.min(count, source.size()));
    }
}
