package dev.ngb.domain.chat.repository;

import dev.ngb.domain.Repository;
import dev.ngb.domain.chat.model.moderation.MessageReport;

/**
 * Repository for managing {@link MessageReport} cross-aggregate message reports.
 */
public interface MessageReportRepository extends Repository<MessageReport, Long> {
}

