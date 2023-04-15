package fr.judo.shiai.solver;

import fr.judo.shiai.domain.Judoka;
import fr.judo.shiai.domain.Pool;
import org.optaplanner.core.api.score.buildin.hardsoft.HardSoftScore;
import org.optaplanner.core.api.score.stream.Constraint;
import org.optaplanner.core.api.score.stream.ConstraintFactory;
import org.optaplanner.core.api.score.stream.ConstraintProvider;
import org.optaplanner.core.api.score.stream.Joiners;

public class SenshiConstraintProvider implements ConstraintProvider {

    public static final int MAX_PREFERED_POOL_SIZE = 4;

    private static final int MIN_POOL_SIZE = 2;

    private static final String WEIGHT_CONSTRAINT_LABEL = "weight range";
    private static final String CLUB_VARIETY_CONSTRAINT_LABEL = "club variety";

    private static final String GENDER_CONFLICT_CONSTRAINT_LABEL = "gender conflict";

    private static final String CATEGORY_CONFLICT_CONSTRAINT_LABEL = "category conflict";

    private static final String MIN_2_CONFLICT_CONSTRAINT_LABEL = "min pool size conflict";

    private static final String MAX_4_CONFLICT_CONSTRAINT_LABEL = "max pool size conflict";
    private static final String PREFERED_POOL_SIZE_LABEL = "Preferred pool size is 4";
    private static final int MAX_WEIGHT_DIFFERENCE = 3;

    @Override
    public Constraint[] defineConstraints(ConstraintFactory constraintFactory) {
        return new Constraint[]{
                // Hard constraints
                 genderConflict(constraintFactory),
                weightRangeConflict(constraintFactory),
                categoryConflict(constraintFactory),
                minPoolSizeConflict(constraintFactory),
                maxPoolSizeConflict(constraintFactory),
                // Soft constraints
                clubVariety(constraintFactory),
                preferedPoolSize(constraintFactory)

        };
    }

    /**
     * @param constraintFactory manage all constraints
     * @return Min 2 judokas per pool
     */
    Constraint minPoolSizeConflict(ConstraintFactory constraintFactory) {
        return constraintFactory
                .forEach(Pool.class)
                .filter(pool -> pool.getJudokaList().size() < MIN_POOL_SIZE)
                .penalize(HardSoftScore.ONE_HARD)
                .asConstraint(MIN_2_CONFLICT_CONSTRAINT_LABEL);
    }

    /**
     * @param constraintFactory manage all constraints
     * @return Max 4 judokas per pool
     */
    Constraint maxPoolSizeConflict(ConstraintFactory constraintFactory) {
        return constraintFactory
                .forEach(Pool.class)
                .filter(pool -> pool.getJudokaList().size() > MAX_PREFERED_POOL_SIZE)
                .penalize(HardSoftScore.ONE_HARD)
                .asConstraint(MAX_4_CONFLICT_CONSTRAINT_LABEL);
    }


    /**
     * @param constraintFactory manage all constraints
     * @return Two judokas  must have same gender hard constraint
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
     * @param constraintFactory manage all constraints
     * @return Two judokas  must be in the same category hard constraint
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
     * @param constraintFactory manage all constraints
     * @return Maximum 3 kg weight difference between two judokas of same pool hard constraint
     */
    Constraint weightRangeConflict(ConstraintFactory constraintFactory) {
        return constraintFactory
                .forEachUniquePair(Judoka.class,
                        Joiners.equal(Judoka::getPool))
                .filter((judoka1, judoka2) ->
                        MAX_WEIGHT_DIFFERENCE
                                < Math.abs(judoka1.getWeight() - judoka2.getWeight()))
                .penalize(HardSoftScore.ONE_HARD)
                .asConstraint(WEIGHT_CONSTRAINT_LABEL);

    }

    /**
     * @param constraintFactory manage all constraints
     * @return Min 2 judokas per pool
     */
    Constraint preferedPoolSize(ConstraintFactory constraintFactory) {
        return constraintFactory
                .forEach(Pool.class)
                .filter(pool -> pool.getJudokaList().size() == MAX_PREFERED_POOL_SIZE)
                .reward(HardSoftScore.ONE_SOFT)
                .asConstraint(PREFERED_POOL_SIZE_LABEL);
    }

    /**
     * @param constraintFactory manage all constraints
     * @return Ideally judokas of a same pool are from different clubs soft constraint
     */
    Constraint clubVariety(ConstraintFactory constraintFactory) {
        return constraintFactory
                .forEachUniquePair(Judoka.class,
                        Joiners.equal(Judoka::getPool))
                .filter((judoka1, judoka2) -> !judoka1.getClub().getId().equals(judoka2.getClub().getId()))
                .reward(HardSoftScore.ONE_SOFT)
                .asConstraint(CLUB_VARIETY_CONSTRAINT_LABEL);
    }

}
