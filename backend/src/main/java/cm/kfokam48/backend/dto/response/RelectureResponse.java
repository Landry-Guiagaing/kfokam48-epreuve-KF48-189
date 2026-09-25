package cm.kfokam48.backend.dto.response;

import java.time.LocalDateTime;

public record RelectureResponse(
        Long id,
        Long exerciceId,
        Integer note,
        String commentaire,
        String statut,
        LocalDateTime rendueAt
) {}