package cm.kfokam48.backend.dto.response;

public record ExerciceDetailResponse(
        Long id,
        Long sessionId,
        Long etudiantId,
        String lien,
        String statut,
        Integer note,
        String commentaire
) {}