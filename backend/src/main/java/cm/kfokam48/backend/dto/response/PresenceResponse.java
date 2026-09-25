package cm.kfokam48.backend.dto.response;

public record PresenceResponse(
        Long id,
        Long sessionId,
        Long etudiantId,
        String source
) {}