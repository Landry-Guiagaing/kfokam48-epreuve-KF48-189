package cm.kfokam48.backend.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record MarquerPresenceRequest(
        @NotBlank(message = "Le code est obligatoire")
        String code,

        @NotNull(message = "L'identifiant étudiant est obligatoire")
        Long etudiantId
) {}