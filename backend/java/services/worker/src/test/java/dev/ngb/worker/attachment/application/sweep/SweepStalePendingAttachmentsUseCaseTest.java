package dev.ngb.worker.attachment.application.sweep;

import dev.ngb.application.port.storage.ObjectStorage;
import dev.ngb.domain.attachment.model.AttachmentUploadStatus;
import dev.ngb.domain.attachment.model.attachment.Attachment;
import dev.ngb.domain.attachment.model.attachment.AttachmentType;
import dev.ngb.domain.attachment.repository.AttachmentRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.Instant;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.atLeastOnce;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class SweepStalePendingAttachmentsUseCaseTest {

    private static final Instant CUTOFF = Instant.parse("2020-01-01T00:00:00Z");

    @Mock
    AttachmentRepository attachmentRepository;

    @Mock
    ObjectStorage objectStorage;

    @Test
    void iteratorReaderAdvancesIdCursorSoSecondPageStartsAfterMaxIdOfFirstChunk() {
        Attachment first = attachment(5L);
        Attachment second = attachment(12L);
        when(attachmentRepository.findPendingPutStaleAfterId(eq(CUTOFF), eq(0L), anyInt()))
                .thenReturn(List.of(first, second));
        when(attachmentRepository.findPendingPutStaleAfterId(eq(CUTOFF), eq(12L), anyInt()))
                .thenReturn(List.of());

        var reader = new SweepStalePendingAttachmentsUseCase.AttachmentItemReader(attachmentRepository, CUTOFF);

        assertThat(reader.read(0, 1)).isSameAs(first);
        assertThat(reader.read(0, 1)).isSameAs(second);
        assertThat(reader.read(0, 1)).isNull();

        verify(attachmentRepository).findPendingPutStaleAfterId(CUTOFF, 0L, 1_000);
        verify(attachmentRepository).findPendingPutStaleAfterId(CUTOFF, 12L, 1_000);
        assertThat(reader.getTotalFetched()).isEqualTo(2);
        assertThat(reader.getChunksLoaded()).isEqualTo(1);
    }

    @Test
    void executeProcessesStaleRowsAndPersistsUpdates() throws Exception {
        Attachment stale = attachment(100L);
        when(attachmentRepository.findPendingPutStaleAfterId(any(Instant.class), eq(0L), anyInt()))
                .thenReturn(List.of(stale));
        when(attachmentRepository.findPendingPutStaleAfterId(any(Instant.class), eq(100L), anyInt()))
                .thenReturn(List.of());
        when(objectStorage.objectExists(any(), any())).thenReturn(true);

        new SweepStalePendingAttachmentsUseCase(attachmentRepository, objectStorage).execute();

        @SuppressWarnings("unchecked")
        ArgumentCaptor<List<Attachment>> captor = ArgumentCaptor.forClass(List.class);
        verify(attachmentRepository, atLeastOnce()).saveAll(captor.capture());
        assertThat(captor.getAllValues().stream().flatMap(List::stream))
                .anyMatch(a -> a.getId().equals(100L) && a.getUploadStatus() == AttachmentUploadStatus.AVAILABLE);
    }

    private static Attachment attachment(long id) {
        return Attachment.reconstruct(
                id,
                "00000000-0000-0000-0000-" + String.format("%012d", id),
                1L,
                Instant.parse("2019-06-01T00:00:00Z"),
                1L,
                Instant.parse("2019-06-01T00:00:00Z"),
                1L,
                AttachmentType.ATTACHMENT,
                "k",
                "f",
                "application/octet-stream",
                1L,
                "u",
                AttachmentUploadStatus.PENDING_PUT,
                null,
                null,
                null
        );
    }
}
