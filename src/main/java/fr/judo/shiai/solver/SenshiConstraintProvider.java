package fr.judo.shiai.solver;

import fr.judo.shiai.domain.Judoka;
import org.optaplanner.core.api.score.buildin.hardsoft.HardSoftScore;
import org.optaplanner.core.api.score.stream.Constraint;
import org.optaplanner.core.api.score.stream.ConstraintFactory;
import org.optaplanner.core.api.score.stream.ConstraintProvider;
import org.optaplanner.core.api.score.stream.Joiners;

public class SenshiConstraintProvider implements ConstraintProvider {

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

    Constraint genderConflict(ConstraintFactory constraintFactory) {
        // Two judokas  must have same gender
        return constraintFactory
                .forEachUniquePair(Judoka.class,
                        Joiners.equal(Judoka::getPool))
                .filter((judoka1, judoka2) -> !judoka1.getGender().equals(judoka2.getGender()))
                .penalize(HardSoftScore.ONE_HARD)
                .asConstraint("Gender conflict");
    }

    Constraint categoryConflict(ConstraintFactory constraintFactory) {
        // Two judokas  must be in the same category
        return constraintFactory
                .forEachUniquePair(Judoka.class,
                        Joiners.equal(Judoka::getPool))
                .filter((judoka1, judoka2) -> !judoka1.getCategory().equals(judoka2.getCategory()))
                .penalize(HardSoftScore.ONE_HARD)
                .asConstraint("Category conflict");
    }

    Constraint clubVariety(ConstraintFactory constraintFactory) {
        // A student can attend at most one lesson at the same time.
        return constraintFactory
                .forEachUniquePair(Judoka.class,
                        Joiners.equal(Judoka::getPool))
                .filter((judoka1, judoka2) -> !judoka1.getClub().equals(judoka2.getClub()))
                .reward(HardSoftScore.ONE_SOFT)
                .asConstraint("club variety");
    }

    Constraint weightRange(ConstraintFactory constraintFactory) {
        // A student can attend at most one lesson at the same time.
        return constraintFactory
                .forEachUniquePair(Judoka.class,
                        Joiners.equal(Judoka::getPool))
                .filter((judoka1, judoka2) -> 0.1 * Math.min(judoka1.getWeight(), judoka2.getWeight()) < Math.abs(judoka1.getWeight() - judoka2.getWeight()))
                .reward(HardSoftScore.ofSoft(2))
                .asConstraint("weight range");
    }

}
