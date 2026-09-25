package cm.kfokam48.backend.dto.response;

import java.time.LocalDateTime;

public record SessionClotureResponse(
        Long id,
        LocalDateTime clotureAt
) {}