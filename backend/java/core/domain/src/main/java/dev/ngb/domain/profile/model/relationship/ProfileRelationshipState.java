package dev.ngb.domain.profile.model.relationship;

public record ProfileRelationshipState(
        boolean sourceFollowsTarget,
        boolean targetFollowsSource,
        boolean sourceBlocksTarget,
        boolean targetBlocksSource,
        boolean sourceMutesTarget,
        boolean targetMutesSource
) {

    public static ProfileRelationshipState empty() {
        return new ProfileRelationshipState(false, false, false, false, false, false);
    }
}
