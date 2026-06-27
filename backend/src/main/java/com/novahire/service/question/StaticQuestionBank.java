package com.novahire.service.question;

import com.novahire.entity.Interview;
import com.novahire.entity.InterviewQuestion.QuestionCategory;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

import static com.novahire.entity.Interview.ExperienceLevel.*;
import static com.novahire.entity.InterviewQuestion.QuestionCategory.BEHAVIORAL;
import static com.novahire.entity.InterviewQuestion.QuestionCategory.TECHNICAL;

/**
 * Generic, role/tech-independent question content — keyed only by (category, experienceLevel,
 * language), not by the free-form {@code targetRole}/{@code technologies} a user picks.
 *
 * <p>Authoring tech-specific content for ~40 free-form technology strings x 3 languages isn't a
 * finite task, so Sprint 3 content stays generic enough to apply to any role. Sprint 4's AI
 * strategy is what actually personalizes questions using {@code targetRole}/{@code technologies}.
 */
@Component
public class StaticQuestionBank {

    public record BankedQuestion(
            String text,
            QuestionCategory category,
            Interview.ExperienceLevel level,
            String language,
            String sourceRef
    ) {}

    private final List<BankedQuestion> questions = build();

    public List<BankedQuestion> findFor(Interview.ExperienceLevel level, String language) {
        return questions.stream()
                .filter(q -> q.level() == level && q.language().equalsIgnoreCase(language))
                .toList();
    }

    private static List<BankedQuestion> build() {
        List<BankedQuestion> all = new ArrayList<>();
        addEnglish(all);
        addFrench(all);
        addGerman(all);
        return List.copyOf(all);
    }

    private static void add(List<BankedQuestion> list, String language, Interview.ExperienceLevel level,
                             QuestionCategory category, String ref, String text) {
        list.add(new BankedQuestion(text, category, level, language, ref));
    }

    // ── English ──────────────────────────────────────────────────────────────────

    private static void addEnglish(List<BankedQuestion> list) {
        add(list, "ENGLISH", JUNIOR, TECHNICAL, "en-jr-t1", "What is the difference between a stack and a queue, and when would you use each?");
        add(list, "ENGLISH", JUNIOR, TECHNICAL, "en-jr-t2", "Explain what Big-O notation is and why it matters when writing code.");
        add(list, "ENGLISH", JUNIOR, TECHNICAL, "en-jr-t3", "What is the difference between an array and a linked list?");
        add(list, "ENGLISH", JUNIOR, TECHNICAL, "en-jr-t4", "How do you handle errors or exceptions in your code? Give an example.");
        add(list, "ENGLISH", JUNIOR, TECHNICAL, "en-jr-t5", "What is version control and why do teams use it?");
        add(list, "ENGLISH", JUNIOR, TECHNICAL, "en-jr-t6", "Explain the difference between SQL and NoSQL databases.");
        add(list, "ENGLISH", JUNIOR, TECHNICAL, "en-jr-t7", "What does it mean for a function to be \"pure\"? Why might that matter?");
        add(list, "ENGLISH", JUNIOR, TECHNICAL, "en-jr-t8", "How would you debug a piece of code that isn't producing the expected output?");
        add(list, "ENGLISH", JUNIOR, TECHNICAL, "en-jr-t9", "What is the difference between synchronous and asynchronous code execution?");
        add(list, "ENGLISH", JUNIOR, TECHNICAL, "en-jr-t10", "Explain what an API is, using a simple real-world analogy.");
        add(list, "ENGLISH", JUNIOR, BEHAVIORAL, "en-jr-b1", "Tell me about a time you got stuck on a problem. How did you find a way forward?");
        add(list, "ENGLISH", JUNIOR, BEHAVIORAL, "en-jr-b2", "Describe a situation where you received critical feedback. How did you respond?");
        add(list, "ENGLISH", JUNIOR, BEHAVIORAL, "en-jr-b3", "Tell me about a time you had to learn a new technology quickly. How did you approach it?");
        add(list, "ENGLISH", JUNIOR, BEHAVIORAL, "en-jr-b4", "Describe a time you worked on a team project. What was your role?");
        add(list, "ENGLISH", JUNIOR, BEHAVIORAL, "en-jr-b5", "Tell me about a mistake you made and what you learned from it.");
        add(list, "ENGLISH", JUNIOR, BEHAVIORAL, "en-jr-b6", "How do you prioritize your work when you have multiple tasks due at once?");

        add(list, "ENGLISH", MID, TECHNICAL, "en-mid-t1", "How would you design a simple caching layer for a frequently-read resource?");
        add(list, "ENGLISH", MID, TECHNICAL, "en-mid-t2", "What's the difference between horizontal and vertical scaling?");
        add(list, "ENGLISH", MID, TECHNICAL, "en-mid-t3", "Explain the SOLID principles and why they matter for maintainable code.");
        add(list, "ENGLISH", MID, TECHNICAL, "en-mid-t4", "How do you decide between a relational and a non-relational database for a new feature?");
        add(list, "ENGLISH", MID, TECHNICAL, "en-mid-t5", "Walk me through how you would test a piece of business logic before shipping it.");
        add(list, "ENGLISH", MID, TECHNICAL, "en-mid-t6", "What is a race condition, and how would you prevent one?");
        add(list, "ENGLISH", MID, TECHNICAL, "en-mid-t7", "Explain the tradeoffs between monolithic and microservices architectures.");
        add(list, "ENGLISH", MID, TECHNICAL, "en-mid-t8", "How would you approach optimizing a slow database query?");
        add(list, "ENGLISH", MID, TECHNICAL, "en-mid-t9", "What's the role of an index in a database, and when can it hurt performance?");
        add(list, "ENGLISH", MID, TECHNICAL, "en-mid-t10", "Describe how you'd design a rate limiter for a public API.");
        add(list, "ENGLISH", MID, BEHAVIORAL, "en-mid-b1", "Tell me about a time you disagreed with a teammate's technical approach. How did you handle it?");
        add(list, "ENGLISH", MID, BEHAVIORAL, "en-mid-b2", "Describe a project where requirements changed midway through. How did you adapt?");
        add(list, "ENGLISH", MID, BEHAVIORAL, "en-mid-b3", "Tell me about a time you had to make a decision with incomplete information.");
        add(list, "ENGLISH", MID, BEHAVIORAL, "en-mid-b4", "Describe a time you mentored or helped a less experienced colleague.");
        add(list, "ENGLISH", MID, BEHAVIORAL, "en-mid-b5", "Tell me about a time you missed a deadline. What happened and what did you change afterward?");
        add(list, "ENGLISH", MID, BEHAVIORAL, "en-mid-b6", "How do you handle competing priorities from different stakeholders?");

        add(list, "ENGLISH", SENIOR, TECHNICAL, "en-sr-t1", "Design a system that needs to handle a sudden 10x spike in traffic. Walk me through your approach.");
        add(list, "ENGLISH", SENIOR, TECHNICAL, "en-sr-t2", "How would you design a notification system that needs to reach millions of users reliably?");
        add(list, "ENGLISH", SENIOR, TECHNICAL, "en-sr-t3", "Explain how you'd approach migrating a live system to a new database with zero downtime.");
        add(list, "ENGLISH", SENIOR, TECHNICAL, "en-sr-t4", "What are the key tradeoffs between strong and eventual consistency, and when would you choose each?");
        add(list, "ENGLISH", SENIOR, TECHNICAL, "en-sr-t5", "How would you design an architecture for a multi-tenant SaaS application?");
        add(list, "ENGLISH", SENIOR, TECHNICAL, "en-sr-t6", "Walk me through how you'd debug a production incident where latency suddenly spiked.");
        add(list, "ENGLISH", SENIOR, TECHNICAL, "en-sr-t7", "How do you approach technical debt — when do you pay it down versus ship around it?");
        add(list, "ENGLISH", SENIOR, TECHNICAL, "en-sr-t8", "Describe how you'd design a system for idempotent payment processing.");
        add(list, "ENGLISH", SENIOR, TECHNICAL, "en-sr-t9", "What's your approach to designing APIs that need to remain backward-compatible over time?");
        add(list, "ENGLISH", SENIOR, TECHNICAL, "en-sr-t10", "How would you structure a system to support feature flags and gradual rollouts?");
        add(list, "ENGLISH", SENIOR, BEHAVIORAL, "en-sr-b1", "Tell me about a time you influenced a major technical decision without having formal authority.");
        add(list, "ENGLISH", SENIOR, BEHAVIORAL, "en-sr-b2", "Describe a time you had to push back on a deadline you believed was unrealistic.");
        add(list, "ENGLISH", SENIOR, BEHAVIORAL, "en-sr-b3", "Tell me about a difficult tradeoff you made between speed and quality, and how you justified it.");
        add(list, "ENGLISH", SENIOR, BEHAVIORAL, "en-sr-b4", "Describe a time you had to resolve a conflict between two team members or teams.");
        add(list, "ENGLISH", SENIOR, BEHAVIORAL, "en-sr-b5", "Tell me about a time a project you led failed or fell short. What did you do next?");
        add(list, "ENGLISH", SENIOR, BEHAVIORAL, "en-sr-b6", "How do you build trust with stakeholders who are skeptical of a technical proposal?");

        add(list, "ENGLISH", LEAD, TECHNICAL, "en-lead-t1", "How do you evaluate whether to build a capability in-house versus adopt a third-party solution?");
        add(list, "ENGLISH", LEAD, TECHNICAL, "en-lead-t2", "Walk me through how you'd lead an architecture review for a system with conflicting stakeholder requirements.");
        add(list, "ENGLISH", LEAD, TECHNICAL, "en-lead-t3", "How do you balance long-term technical vision with short-term delivery pressure across multiple teams?");
        add(list, "ENGLISH", LEAD, TECHNICAL, "en-lead-t4", "Describe how you'd design an engineering roadmap for scaling a platform from thousands to millions of users.");
        add(list, "ENGLISH", LEAD, TECHNICAL, "en-lead-t5", "How do you approach setting technical standards across teams without slowing them down?");
        add(list, "ENGLISH", LEAD, TECHNICAL, "en-lead-t6", "What's your strategy for managing risk in a major system migration affecting multiple teams?");
        add(list, "ENGLISH", LEAD, TECHNICAL, "en-lead-t7", "How would you structure on-call and incident response for a growing engineering organization?");
        add(list, "ENGLISH", LEAD, TECHNICAL, "en-lead-t8", "Describe how you evaluate and mitigate single points of failure across a distributed system.");
        add(list, "ENGLISH", LEAD, TECHNICAL, "en-lead-t9", "How do you decide when it's time to re-architect a system versus continuing to iterate on it?");
        add(list, "ENGLISH", LEAD, TECHNICAL, "en-lead-t10", "What's your approach to mentoring senior engineers into architectural decision-making roles?");
        add(list, "ENGLISH", LEAD, BEHAVIORAL, "en-lead-b1", "Tell me about a time you had to align multiple teams around a shared technical direction.");
        add(list, "ENGLISH", LEAD, BEHAVIORAL, "en-lead-b2", "Describe how you've handled a situation where business priorities conflicted with engineering best practices.");
        add(list, "ENGLISH", LEAD, BEHAVIORAL, "en-lead-b3", "Tell me about a time you had to deliver difficult feedback to a senior engineer or peer.");
        add(list, "ENGLISH", LEAD, BEHAVIORAL, "en-lead-b4", "Describe how you've grown other engineers into leadership roles.");
        add(list, "ENGLISH", LEAD, BEHAVIORAL, "en-lead-b5", "Tell me about a time you had to make an unpopular decision for the long-term health of a system or team.");
        add(list, "ENGLISH", LEAD, BEHAVIORAL, "en-lead-b6", "How do you measure whether your technical leadership is actually having a positive impact?");
    }

    // ── French ───────────────────────────────────────────────────────────────────

    private static void addFrench(List<BankedQuestion> list) {
        add(list, "FRENCH", JUNIOR, TECHNICAL, "fr-jr-t1", "Quelle est la différence entre une pile (stack) et une file (queue), et quand utiliser chacune ?");
        add(list, "FRENCH", JUNIOR, TECHNICAL, "fr-jr-t2", "Expliquez ce qu'est la notation Big-O et pourquoi elle est importante en programmation.");
        add(list, "FRENCH", JUNIOR, TECHNICAL, "fr-jr-t3", "Quelle est la différence entre un tableau (array) et une liste chaînée (linked list) ?");
        add(list, "FRENCH", JUNIOR, TECHNICAL, "fr-jr-t4", "Comment gérez-vous les erreurs ou exceptions dans votre code ? Donnez un exemple.");
        add(list, "FRENCH", JUNIOR, TECHNICAL, "fr-jr-t5", "Qu'est-ce que le contrôle de version et pourquoi les équipes l'utilisent-elles ?");
        add(list, "FRENCH", JUNIOR, TECHNICAL, "fr-jr-t6", "Expliquez la différence entre les bases de données SQL et NoSQL.");
        add(list, "FRENCH", JUNIOR, TECHNICAL, "fr-jr-t7", "Que signifie qu'une fonction soit \"pure\" ? Pourquoi cela peut-il être important ?");
        add(list, "FRENCH", JUNIOR, TECHNICAL, "fr-jr-t8", "Comment déboguez-vous un code qui ne produit pas le résultat attendu ?");
        add(list, "FRENCH", JUNIOR, TECHNICAL, "fr-jr-t9", "Quelle est la différence entre une exécution synchrone et asynchrone ?");
        add(list, "FRENCH", JUNIOR, TECHNICAL, "fr-jr-t10", "Expliquez ce qu'est une API à l'aide d'une analogie simple du quotidien.");
        add(list, "FRENCH", JUNIOR, BEHAVIORAL, "fr-jr-b1", "Parlez-moi d'une fois où vous étiez bloqué sur un problème. Comment avez-vous trouvé une solution ?");
        add(list, "FRENCH", JUNIOR, BEHAVIORAL, "fr-jr-b2", "Décrivez une situation où vous avez reçu un retour critique. Comment avez-vous réagi ?");
        add(list, "FRENCH", JUNIOR, BEHAVIORAL, "fr-jr-b3", "Parlez-moi d'une fois où vous avez dû apprendre rapidement une nouvelle technologie.");
        add(list, "FRENCH", JUNIOR, BEHAVIORAL, "fr-jr-b4", "Décrivez un projet d'équipe sur lequel vous avez travaillé. Quel était votre rôle ?");
        add(list, "FRENCH", JUNIOR, BEHAVIORAL, "fr-jr-b5", "Parlez-moi d'une erreur que vous avez commise et de ce que vous en avez retenu.");
        add(list, "FRENCH", JUNIOR, BEHAVIORAL, "fr-jr-b6", "Comment priorisez-vous votre travail lorsque plusieurs tâches sont urgentes en même temps ?");

        add(list, "FRENCH", MID, TECHNICAL, "fr-mid-t1", "Comment concevriez-vous une couche de cache simple pour une ressource fréquemment lue ?");
        add(list, "FRENCH", MID, TECHNICAL, "fr-mid-t2", "Quelle est la différence entre la scalabilité horizontale et verticale ?");
        add(list, "FRENCH", MID, TECHNICAL, "fr-mid-t3", "Expliquez les principes SOLID et pourquoi ils sont importants pour un code maintenable.");
        add(list, "FRENCH", MID, TECHNICAL, "fr-mid-t4", "Comment choisissez-vous entre une base de données relationnelle et non relationnelle pour une nouvelle fonctionnalité ?");
        add(list, "FRENCH", MID, TECHNICAL, "fr-mid-t5", "Expliquez comment vous testeriez une logique métier avant de la mettre en production.");
        add(list, "FRENCH", MID, TECHNICAL, "fr-mid-t6", "Qu'est-ce qu'une race condition, et comment l'éviter ?");
        add(list, "FRENCH", MID, TECHNICAL, "fr-mid-t7", "Expliquez les compromis entre une architecture monolithique et une architecture en microservices.");
        add(list, "FRENCH", MID, TECHNICAL, "fr-mid-t8", "Comment aborderiez-vous l'optimisation d'une requête de base de données lente ?");
        add(list, "FRENCH", MID, TECHNICAL, "fr-mid-t9", "Quel est le rôle d'un index dans une base de données, et quand peut-il nuire aux performances ?");
        add(list, "FRENCH", MID, TECHNICAL, "fr-mid-t10", "Décrivez comment vous concevriez un limiteur de débit (rate limiter) pour une API publique.");
        add(list, "FRENCH", MID, BEHAVIORAL, "fr-mid-b1", "Parlez-moi d'une fois où vous étiez en désaccord avec l'approche technique d'un collègue. Comment l'avez-vous géré ?");
        add(list, "FRENCH", MID, BEHAVIORAL, "fr-mid-b2", "Décrivez un projet où les exigences ont changé en cours de route. Comment vous êtes-vous adapté ?");
        add(list, "FRENCH", MID, BEHAVIORAL, "fr-mid-b3", "Parlez-moi d'une fois où vous avez dû prendre une décision avec des informations incomplètes.");
        add(list, "FRENCH", MID, BEHAVIORAL, "fr-mid-b4", "Décrivez une fois où vous avez encadré ou aidé un collègue moins expérimenté.");
        add(list, "FRENCH", MID, BEHAVIORAL, "fr-mid-b5", "Parlez-moi d'une fois où vous avez manqué une échéance. Que s'est-il passé et qu'avez-vous changé ensuite ?");
        add(list, "FRENCH", MID, BEHAVIORAL, "fr-mid-b6", "Comment gérez-vous des priorités concurrentes venant de différentes parties prenantes ?");

        add(list, "FRENCH", SENIOR, TECHNICAL, "fr-sr-t1", "Concevez un système capable de gérer un pic soudain de trafic x10. Décrivez votre approche.");
        add(list, "FRENCH", SENIOR, TECHNICAL, "fr-sr-t2", "Comment concevriez-vous un système de notifications capable d'atteindre fiablement des millions d'utilisateurs ?");
        add(list, "FRENCH", SENIOR, TECHNICAL, "fr-sr-t3", "Expliquez comment vous aborderiez la migration d'un système en production vers une nouvelle base de données sans interruption de service.");
        add(list, "FRENCH", SENIOR, TECHNICAL, "fr-sr-t4", "Quels sont les compromis entre cohérence forte et cohérence à terme (eventual consistency), et quand choisir l'une ou l'autre ?");
        add(list, "FRENCH", SENIOR, TECHNICAL, "fr-sr-t5", "Comment concevriez-vous une architecture pour une application SaaS multi-tenant ?");
        add(list, "FRENCH", SENIOR, TECHNICAL, "fr-sr-t6", "Décrivez comment vous déboguer un incident en production où la latence augmente soudainement.");
        add(list, "FRENCH", SENIOR, TECHNICAL, "fr-sr-t7", "Comment abordez-vous la dette technique — quand la rembourser plutôt que la contourner ?");
        add(list, "FRENCH", SENIOR, TECHNICAL, "fr-sr-t8", "Décrivez comment vous concevriez un système de traitement de paiements idempotent.");
        add(list, "FRENCH", SENIOR, TECHNICAL, "fr-sr-t9", "Quelle est votre approche pour concevoir des API qui doivent rester rétrocompatibles dans le temps ?");
        add(list, "FRENCH", SENIOR, TECHNICAL, "fr-sr-t10", "Comment structureriez-vous un système pour supporter les feature flags et des déploiements progressifs ?");
        add(list, "FRENCH", SENIOR, BEHAVIORAL, "fr-sr-b1", "Parlez-moi d'une fois où vous avez influencé une décision technique majeure sans autorité formelle.");
        add(list, "FRENCH", SENIOR, BEHAVIORAL, "fr-sr-b2", "Décrivez une fois où vous avez dû repousser un délai que vous jugiez irréaliste.");
        add(list, "FRENCH", SENIOR, BEHAVIORAL, "fr-sr-b3", "Parlez-moi d'un compromis difficile entre vitesse et qualité, et comment vous l'avez justifié.");
        add(list, "FRENCH", SENIOR, BEHAVIORAL, "fr-sr-b4", "Décrivez une fois où vous avez dû résoudre un conflit entre deux collègues ou deux équipes.");
        add(list, "FRENCH", SENIOR, BEHAVIORAL, "fr-sr-b5", "Parlez-moi d'un projet que vous dirigiez et qui a échoué ou n'a pas atteint ses objectifs. Qu'avez-vous fait ensuite ?");
        add(list, "FRENCH", SENIOR, BEHAVIORAL, "fr-sr-b6", "Comment construisez-vous la confiance avec des parties prenantes sceptiques envers une proposition technique ?");

        add(list, "FRENCH", LEAD, TECHNICAL, "fr-lead-t1", "Comment évaluez-vous s'il faut développer une capacité en interne ou adopter une solution tierce ?");
        add(list, "FRENCH", LEAD, TECHNICAL, "fr-lead-t2", "Décrivez comment vous animeriez une revue d'architecture face à des exigences contradictoires entre parties prenantes.");
        add(list, "FRENCH", LEAD, TECHNICAL, "fr-lead-t3", "Comment équilibrez-vous une vision technique à long terme avec la pression de livraison à court terme entre plusieurs équipes ?");
        add(list, "FRENCH", LEAD, TECHNICAL, "fr-lead-t4", "Décrivez comment vous concevriez une feuille de route technique pour faire passer une plateforme de milliers à millions d'utilisateurs.");
        add(list, "FRENCH", LEAD, TECHNICAL, "fr-lead-t5", "Comment définissez-vous des standards techniques communs entre équipes sans freiner leur rythme ?");
        add(list, "FRENCH", LEAD, TECHNICAL, "fr-lead-t6", "Quelle est votre stratégie pour gérer les risques d'une migration majeure impliquant plusieurs équipes ?");
        add(list, "FRENCH", LEAD, TECHNICAL, "fr-lead-t7", "Comment structureriez-vous l'astreinte (on-call) et la gestion d'incidents pour une organisation technique en croissance ?");
        add(list, "FRENCH", LEAD, TECHNICAL, "fr-lead-t8", "Décrivez comment vous identifiez et atténuez les points de défaillance uniques dans un système distribué.");
        add(list, "FRENCH", LEAD, TECHNICAL, "fr-lead-t9", "Comment décidez-vous qu'il est temps de réarchitecturer un système plutôt que de continuer à l'itérer ?");
        add(list, "FRENCH", LEAD, TECHNICAL, "fr-lead-t10", "Quelle est votre approche pour accompagner des ingénieurs seniors vers des rôles de décision architecturale ?");
        add(list, "FRENCH", LEAD, BEHAVIORAL, "fr-lead-b1", "Parlez-moi d'une fois où vous avez dû aligner plusieurs équipes autour d'une direction technique commune.");
        add(list, "FRENCH", LEAD, BEHAVIORAL, "fr-lead-b2", "Décrivez comment vous avez géré un conflit entre priorités business et bonnes pratiques d'ingénierie.");
        add(list, "FRENCH", LEAD, BEHAVIORAL, "fr-lead-b3", "Parlez-moi d'une fois où vous avez dû donner un retour difficile à un ingénieur senior ou un pair.");
        add(list, "FRENCH", LEAD, BEHAVIORAL, "fr-lead-b4", "Décrivez comment vous avez aidé d'autres ingénieurs à évoluer vers des rôles de leadership.");
        add(list, "FRENCH", LEAD, BEHAVIORAL, "fr-lead-b5", "Parlez-moi d'une décision impopulaire que vous avez dû prendre pour la santé à long terme d'un système ou d'une équipe.");
        add(list, "FRENCH", LEAD, BEHAVIORAL, "fr-lead-b6", "Comment mesurez-vous si votre leadership technique a un impact réellement positif ?");
    }

    // ── German ───────────────────────────────────────────────────────────────────

    private static void addGerman(List<BankedQuestion> list) {
        add(list, "GERMAN", JUNIOR, TECHNICAL, "de-jr-t1", "Was ist der Unterschied zwischen einem Stack und einer Queue, und wann verwendet man welchen?");
        add(list, "GERMAN", JUNIOR, TECHNICAL, "de-jr-t2", "Erklären Sie die Big-O-Notation und warum sie beim Programmieren wichtig ist.");
        add(list, "GERMAN", JUNIOR, TECHNICAL, "de-jr-t3", "Was ist der Unterschied zwischen einem Array und einer verketteten Liste (Linked List)?");
        add(list, "GERMAN", JUNIOR, TECHNICAL, "de-jr-t4", "Wie behandeln Sie Fehler oder Ausnahmen in Ihrem Code? Geben Sie ein Beispiel.");
        add(list, "GERMAN", JUNIOR, TECHNICAL, "de-jr-t5", "Was ist Versionskontrolle und warum nutzen Teams sie?");
        add(list, "GERMAN", JUNIOR, TECHNICAL, "de-jr-t6", "Erklären Sie den Unterschied zwischen SQL- und NoSQL-Datenbanken.");
        add(list, "GERMAN", JUNIOR, TECHNICAL, "de-jr-t7", "Was bedeutet es, dass eine Funktion \"rein\" (pure) ist? Warum kann das wichtig sein?");
        add(list, "GERMAN", JUNIOR, TECHNICAL, "de-jr-t8", "Wie würden Sie Code debuggen, der nicht das erwartete Ergebnis liefert?");
        add(list, "GERMAN", JUNIOR, TECHNICAL, "de-jr-t9", "Was ist der Unterschied zwischen synchroner und asynchroner Codeausführung?");
        add(list, "GERMAN", JUNIOR, TECHNICAL, "de-jr-t10", "Erklären Sie, was eine API ist, anhand einer einfachen Alltagsanalogie.");
        add(list, "GERMAN", JUNIOR, BEHAVIORAL, "de-jr-b1", "Erzählen Sie von einer Situation, in der Sie bei einem Problem nicht weiterkamen. Wie haben Sie eine Lösung gefunden?");
        add(list, "GERMAN", JUNIOR, BEHAVIORAL, "de-jr-b2", "Beschreiben Sie eine Situation, in der Sie kritisches Feedback erhalten haben. Wie haben Sie reagiert?");
        add(list, "GERMAN", JUNIOR, BEHAVIORAL, "de-jr-b3", "Erzählen Sie von einer Situation, in der Sie schnell eine neue Technologie lernen mussten.");
        add(list, "GERMAN", JUNIOR, BEHAVIORAL, "de-jr-b4", "Beschreiben Sie ein Teamprojekt, an dem Sie mitgearbeitet haben. Was war Ihre Rolle?");
        add(list, "GERMAN", JUNIOR, BEHAVIORAL, "de-jr-b5", "Erzählen Sie von einem Fehler, den Sie gemacht haben, und was Sie daraus gelernt haben.");
        add(list, "GERMAN", JUNIOR, BEHAVIORAL, "de-jr-b6", "Wie priorisieren Sie Ihre Arbeit, wenn mehrere Aufgaben gleichzeitig fällig sind?");

        add(list, "GERMAN", MID, TECHNICAL, "de-mid-t1", "Wie würden Sie eine einfache Caching-Schicht für eine häufig gelesene Ressource entwerfen?");
        add(list, "GERMAN", MID, TECHNICAL, "de-mid-t2", "Was ist der Unterschied zwischen horizontaler und vertikaler Skalierung?");
        add(list, "GERMAN", MID, TECHNICAL, "de-mid-t3", "Erklären Sie die SOLID-Prinzipien und warum sie für wartbaren Code wichtig sind.");
        add(list, "GERMAN", MID, TECHNICAL, "de-mid-t4", "Wie entscheiden Sie zwischen einer relationalen und einer nicht-relationalen Datenbank für ein neues Feature?");
        add(list, "GERMAN", MID, TECHNICAL, "de-mid-t5", "Erklären Sie, wie Sie eine Geschäftslogik testen würden, bevor sie live geht.");
        add(list, "GERMAN", MID, TECHNICAL, "de-mid-t6", "Was ist eine Race Condition, und wie würden Sie sie verhindern?");
        add(list, "GERMAN", MID, TECHNICAL, "de-mid-t7", "Erklären Sie die Kompromisse zwischen monolithischer und Microservices-Architektur.");
        add(list, "GERMAN", MID, TECHNICAL, "de-mid-t8", "Wie würden Sie eine langsame Datenbankabfrage optimieren?");
        add(list, "GERMAN", MID, TECHNICAL, "de-mid-t9", "Welche Rolle spielt ein Index in einer Datenbank, und wann kann er die Performance beeinträchtigen?");
        add(list, "GERMAN", MID, TECHNICAL, "de-mid-t10", "Beschreiben Sie, wie Sie einen Rate Limiter für eine öffentliche API entwerfen würden.");
        add(list, "GERMAN", MID, BEHAVIORAL, "de-mid-b1", "Erzählen Sie von einer Situation, in der Sie mit dem technischen Ansatz eines Kollegen nicht einverstanden waren. Wie sind Sie damit umgegangen?");
        add(list, "GERMAN", MID, BEHAVIORAL, "de-mid-b2", "Beschreiben Sie ein Projekt, bei dem sich die Anforderungen während der Umsetzung geändert haben. Wie haben Sie sich angepasst?");
        add(list, "GERMAN", MID, BEHAVIORAL, "de-mid-b3", "Erzählen Sie von einer Entscheidung, die Sie mit unvollständigen Informationen treffen mussten.");
        add(list, "GERMAN", MID, BEHAVIORAL, "de-mid-b4", "Beschreiben Sie eine Situation, in der Sie einen weniger erfahrenen Kollegen unterstützt oder angeleitet haben.");
        add(list, "GERMAN", MID, BEHAVIORAL, "de-mid-b5", "Erzählen Sie von einer verpassten Deadline. Was ist passiert, und was haben Sie danach geändert?");
        add(list, "GERMAN", MID, BEHAVIORAL, "de-mid-b6", "Wie gehen Sie mit widersprüchlichen Prioritäten verschiedener Stakeholder um?");

        add(list, "GERMAN", SENIOR, TECHNICAL, "de-sr-t1", "Entwerfen Sie ein System, das einen plötzlichen 10-fachen Anstieg des Traffics bewältigen muss. Beschreiben Sie Ihren Ansatz.");
        add(list, "GERMAN", SENIOR, TECHNICAL, "de-sr-t2", "Wie würden Sie ein Benachrichtigungssystem entwerfen, das zuverlässig Millionen von Nutzern erreichen muss?");
        add(list, "GERMAN", SENIOR, TECHNICAL, "de-sr-t3", "Erklären Sie, wie Sie ein Live-System ohne Ausfallzeit zu einer neuen Datenbank migrieren würden.");
        add(list, "GERMAN", SENIOR, TECHNICAL, "de-sr-t4", "Was sind die zentralen Kompromisse zwischen starker und eventueller Konsistenz, und wann würden Sie welche wählen?");
        add(list, "GERMAN", SENIOR, TECHNICAL, "de-sr-t5", "Wie würden Sie eine Architektur für eine Multi-Tenant-SaaS-Anwendung entwerfen?");
        add(list, "GERMAN", SENIOR, TECHNICAL, "de-sr-t6", "Beschreiben Sie, wie Sie einen Produktionsvorfall mit plötzlich erhöhter Latenz debuggen würden.");
        add(list, "GERMAN", SENIOR, TECHNICAL, "de-sr-t7", "Wie gehen Sie mit technischen Schulden um — wann tilgen Sie sie und wann umgehen Sie sie?");
        add(list, "GERMAN", SENIOR, TECHNICAL, "de-sr-t8", "Beschreiben Sie, wie Sie ein System für idempotente Zahlungsabwicklung entwerfen würden.");
        add(list, "GERMAN", SENIOR, TECHNICAL, "de-sr-t9", "Was ist Ihr Ansatz, um APIs zu entwerfen, die langfristig abwärtskompatibel bleiben müssen?");
        add(list, "GERMAN", SENIOR, TECHNICAL, "de-sr-t10", "Wie würden Sie ein System strukturieren, das Feature Flags und schrittweise Rollouts unterstützt?");
        add(list, "GERMAN", SENIOR, BEHAVIORAL, "de-sr-b1", "Erzählen Sie von einer Situation, in der Sie eine wichtige technische Entscheidung ohne formale Autorität beeinflusst haben.");
        add(list, "GERMAN", SENIOR, BEHAVIORAL, "de-sr-b2", "Beschreiben Sie eine Situation, in der Sie eine Deadline ablehnen mussten, die Sie für unrealistisch hielten.");
        add(list, "GERMAN", SENIOR, BEHAVIORAL, "de-sr-b3", "Erzählen Sie von einem schwierigen Kompromiss zwischen Geschwindigkeit und Qualität und wie Sie ihn begründet haben.");
        add(list, "GERMAN", SENIOR, BEHAVIORAL, "de-sr-b4", "Beschreiben Sie, wie Sie einen Konflikt zwischen zwei Kollegen oder Teams gelöst haben.");
        add(list, "GERMAN", SENIOR, BEHAVIORAL, "de-sr-b5", "Erzählen Sie von einem Projekt, das Sie geleitet haben und das gescheitert ist oder die Ziele verfehlt hat. Was haben Sie danach getan?");
        add(list, "GERMAN", SENIOR, BEHAVIORAL, "de-sr-b6", "Wie bauen Sie Vertrauen bei Stakeholdern auf, die einem technischen Vorschlag skeptisch gegenüberstehen?");

        add(list, "GERMAN", LEAD, TECHNICAL, "de-lead-t1", "Wie entscheiden Sie, ob eine Fähigkeit intern entwickelt oder eine Drittlösung übernommen wird?");
        add(list, "GERMAN", LEAD, TECHNICAL, "de-lead-t2", "Beschreiben Sie, wie Sie ein Architektur-Review bei widersprüchlichen Stakeholder-Anforderungen leiten würden.");
        add(list, "GERMAN", LEAD, TECHNICAL, "de-lead-t3", "Wie balancieren Sie eine langfristige technische Vision mit kurzfristigem Lieferdruck über mehrere Teams hinweg?");
        add(list, "GERMAN", LEAD, TECHNICAL, "de-lead-t4", "Beschreiben Sie, wie Sie eine technische Roadmap entwerfen würden, um eine Plattform von Tausenden auf Millionen Nutzer zu skalieren.");
        add(list, "GERMAN", LEAD, TECHNICAL, "de-lead-t5", "Wie setzen Sie technische Standards über Teams hinweg, ohne deren Tempo zu bremsen?");
        add(list, "GERMAN", LEAD, TECHNICAL, "de-lead-t6", "Was ist Ihre Strategie für das Risikomanagement bei einer großen, mehrere Teams betreffenden Systemmigration?");
        add(list, "GERMAN", LEAD, TECHNICAL, "de-lead-t7", "Wie würden Sie Bereitschaftsdienst (On-Call) und Incident Response für eine wachsende Engineering-Organisation strukturieren?");
        add(list, "GERMAN", LEAD, TECHNICAL, "de-lead-t8", "Beschreiben Sie, wie Sie Single Points of Failure in einem verteilten System identifizieren und entschärfen.");
        add(list, "GERMAN", LEAD, TECHNICAL, "de-lead-t9", "Wie entscheiden Sie, wann es Zeit ist, ein System neu zu architektieren, statt es weiter zu iterieren?");
        add(list, "GERMAN", LEAD, TECHNICAL, "de-lead-t10", "Was ist Ihr Ansatz, um leitende Ingenieure in architektonische Entscheidungsrollen hineinwachsen zu lassen?");
        add(list, "GERMAN", LEAD, BEHAVIORAL, "de-lead-b1", "Erzählen Sie von einer Situation, in der Sie mehrere Teams auf eine gemeinsame technische Richtung ausrichten mussten.");
        add(list, "GERMAN", LEAD, BEHAVIORAL, "de-lead-b2", "Beschreiben Sie, wie Sie einen Konflikt zwischen Geschäftsprioritäten und bewährten Engineering-Praktiken gehandhabt haben.");
        add(list, "GERMAN", LEAD, BEHAVIORAL, "de-lead-b3", "Erzählen Sie von einer Situation, in der Sie einem leitenden Ingenieur oder Peer schwieriges Feedback geben mussten.");
        add(list, "GERMAN", LEAD, BEHAVIORAL, "de-lead-b4", "Beschreiben Sie, wie Sie andere Ingenieure in Führungsrollen entwickelt haben.");
        add(list, "GERMAN", LEAD, BEHAVIORAL, "de-lead-b5", "Erzählen Sie von einer unpopulären Entscheidung, die Sie für die langfristige Gesundheit eines Systems oder Teams treffen mussten.");
        add(list, "GERMAN", LEAD, BEHAVIORAL, "de-lead-b6", "Wie messen Sie, ob Ihre technische Führung tatsächlich einen positiven Einfluss hat?");
    }
}
