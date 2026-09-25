package cm.kfokam48.backend.dto.response;

public record TableauLigneResponse(
        Long etudiantId,
        String nom,
        Long presences,
        Long exercicesDeposes,
        Double moyenne,
        Long relecturesEnAttente
) {}