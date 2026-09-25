package cm.kfokam48.backend.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record DeposerExerciceRequest(
        @NotNull(message = "L'identifiant de session est obligatoire")
        Long sessionId,

        @NotNull(message = "L'identifiant étudiant est obligatoire")
        Long etudiantId,

        @NotBlank(message = "Le lien est obligatoire")
        String lien
) {}