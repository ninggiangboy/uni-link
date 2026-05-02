package dev.ngb.app.shared.public_api;

import java.util.Optional;

/**
 * Cross-module read access to attachment metadata. Profiles use this to validate
 * an avatar/banner attachment is owned by the calling account and has been
 * confirmed as available in object storage before persisting its URL.
 */
public interface AttachmentPublicApi {

    /**
     * Returns the public file URL when the attachment exists, is owned by
     * {@code accountId}, and is in the {@code AVAILABLE} state. Empty otherwise.
     */
    Optional<String> resolveAvailableUrl(String attachmentUuid, Long accountId);
}
