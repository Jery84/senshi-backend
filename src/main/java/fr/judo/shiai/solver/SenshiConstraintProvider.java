package fr.judo.shiai.solver;

import fr.judo.shiai.domain.Judoka;
import org.optaplanner.core.api.score.buildin.hardsoft.HardSoftScore;
import org.optaplanner.core.api.score.stream.Constraint;
import org.optaplanner.core.api.score.stream.ConstraintFactory;
import org.optaplanner.core.api.score.stream.ConstraintProvider;
import org.optaplanner.core.api.score.stream.Joiners;

public class SenshiConstraintProvider implements ConstraintProvider {
    private static final double PERCENTAGE_WEIGHT_DIFFERENCE = 0.1;
    private static final String WEIGHT_CONTRAINT_LABEL = "weight range";
    private static final String CLUB_VARIETY_CONSTRAINT_LABEL = "club variety";

    private static final  String GENDER_CONFLICT_CONSTRAINT_LABEL = "gender conflict";

    private static final  String CATEGORY_CONFLICT_CONSTRAINT_LABEL = "category conflict";

    @Override
    public Constraint[] defineConstraints(ConstraintFactory constraintFactory) {
        return new Constraint[]{
                // Hard constraints
                genderConflict(constraintFactory),
                categoryConflict(constraintFactory),
                // Soft constraints
                clubVariety(constraintFactory),
                weightRange(constraintFactory)
        };
    }

    /**
     * Two judokas  must have same gender
     * @param constraintFactory
     * @return
     */
    Constraint genderConflict(ConstraintFactory constraintFactory) {
        return constraintFactory
                .forEachUniquePair(Judoka.class,
                        Joiners.equal(Judoka::getPool))
                .filter((judoka1, judoka2) -> !judoka1.getGender().equals(judoka2.getGender()))
                .penalize(HardSoftScore.ONE_HARD)
                .asConstraint(GENDER_CONFLICT_CONSTRAINT_LABEL);
    }

    /**
     * Two judokas  must be in the same category
     * @param constraintFactory
     * @return
     */
    Constraint categoryConflict(ConstraintFactory constraintFactory) {
        return constraintFactory
                .forEachUniquePair(Judoka.class,
                        Joiners.equal(Judoka::getPool))
                .filter((judoka1, judoka2) -> !judoka1.getCategory().equals(judoka2.getCategory()))
                .penalize(HardSoftScore.ONE_HARD)
                .asConstraint(CATEGORY_CONFLICT_CONSTRAINT_LABEL);
    }

    /**
     * Ideally judokas of a same pool are from different clubs
     *
     * @param constraintFactory
     * @return
     */
    Constraint clubVariety(ConstraintFactory constraintFactory) {
        return constraintFactory
                .forEachUniquePair(Judoka.class,
                        Joiners.equal(Judoka::getPool))
                .filter((judoka1, judoka2) -> !judoka1.getClub().equals(judoka2.getClub()))
                .reward(HardSoftScore.ONE_SOFT)
                .asConstraint(CLUB_VARIETY_CONSTRAINT_LABEL);
    }

    /**
     * Minimum 10% weight difference between two judokas of same pool
     *
     * @param constraintFactory
     * @return
     */
    Constraint weightRange(ConstraintFactory constraintFactory) {
        return constraintFactory
                .forEachUniquePair(Judoka.class,
                        Joiners.equal(Judoka::getPool))
                .filter((judoka1, judoka2) ->
                        PERCENTAGE_WEIGHT_DIFFERENCE * Math.min(judoka1.getWeight(), judoka2.getWeight())
                                < Math.abs(judoka1.getWeight() - judoka2.getWeight()))
                .reward(HardSoftScore.ofSoft(2))
                .asConstraint(WEIGHT_CONTRAINT_LABEL);
    }
}
