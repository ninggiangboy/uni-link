package dev.ngb.worker.profile.application.usecase.stats_delta;

import com.fasterxml.jackson.databind.ObjectMapper;
import dev.ngb.domain.profile.model.stats.ProfileStatsCountDelta;
import dev.ngb.domain.profile.repository.ProfileStatsRepository;
import dev.ngb.event.ProfileFollowStatsDeltaEvent;
import dev.ngb.worker.profile.application.port.ProfileStatsDeltaEventDedupePort;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.Instant;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ApplyProfileFollowStatsDeltaBatchUseCaseTest {

    @Mock
    ProfileStatsDeltaEventDedupePort dedupeRepository;

    @Mock
    ProfileStatsRepository profileStatsRepository;

    private final ObjectMapper objectMapper = new ObjectMapper().findAndRegisterModules();

    @Test
    void executeSkipsWhenPayloadsEmpty() {
        var useCase = new ApplyProfileFollowStatsDeltaBatchUseCase(objectMapper, dedupeRepository, profileStatsRepository);

        useCase.execute(List.of());

        verify(dedupeRepository, never()).tryClaimEvent(anyString());
        verify(profileStatsRepository, never()).adjustCountsBulk(anyList());
    }

    @Test
    void executeDedupesAndMergesDeltasAcrossBatch() throws Exception {
        var useCase = new ApplyProfileFollowStatsDeltaBatchUseCase(objectMapper, dedupeRepository, profileStatsRepository);
        var first = event("u-1", 10L, 1, 20L, 1);
        var duplicate = event("u-dup", 10L, 1, 20L, 1);
        var sameProfileSecondEvent = event("u-2", 10L, 2, 99L, 0);

        when(dedupeRepository.tryClaimEvent("u-1")).thenReturn(true);
        when(dedupeRepository.tryClaimEvent("u-dup")).thenReturn(false);
        when(dedupeRepository.tryClaimEvent("u-2")).thenReturn(true);

        useCase.execute(List.of(json(first), json(duplicate), json(sameProfileSecondEvent)));

        @SuppressWarnings("unchecked")
        ArgumentCaptor<List<ProfileStatsCountDelta>> captor = ArgumentCaptor.forClass(List.class);
        verify(profileStatsRepository).adjustCountsBulk(captor.capture());

        assertThat(captor.getValue())
                .containsExactlyInAnyOrder(
                        new ProfileStatsCountDelta(10L, 3L, 0L),
                        new ProfileStatsCountDelta(20L, 0L, 1L)
                );
    }

    @Test
    void executeWrapsInvalidJsonAsPipelineFailure() {
        var useCase = new ApplyProfileFollowStatsDeltaBatchUseCase(objectMapper, dedupeRepository, profileStatsRepository);

        assertThatThrownBy(() -> useCase.execute(List.of("{invalid-json}")))
                .isInstanceOf(IllegalStateException.class)
                .hasMessageContaining("Profile follow stats delta pipeline failed");
        verify(profileStatsRepository, never()).adjustCountsBulk(anyList());
    }

    private static ProfileFollowStatsDeltaEvent event(
            String uuid,
            long targetProfileId,
            int followerDelta,
            long followerProfileId,
            int followingDelta
    ) {
        return new ProfileFollowStatsDeltaEvent(
                uuid,
                Instant.parse("2026-01-01T00:00:00Z"),
                targetProfileId,
                followerDelta,
                followerProfileId,
                followingDelta
        );
    }

    private static String json(ProfileFollowStatsDeltaEvent event) {
        return """
                {"uuid":"%s","occurredAt":"2026-01-01T00:00:00Z","targetProfileId":%d,"followerDelta":%d,"followerProfileId":%d,"followingDelta":%d}
                """.formatted(
                event.uuid(),
                event.targetProfileId(),
                event.followerDelta(),
                event.followerProfileId(),
                event.followingDelta()
        );
    }
}
