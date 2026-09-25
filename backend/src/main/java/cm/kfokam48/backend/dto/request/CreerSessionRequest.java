package cm.kfokam48.backend.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record CreerSessionRequest(
        @NotBlank(message = "Le titre est obligatoire")
        String titre,

        @NotNull(message = "La promotion est obligatoire")
        Long promotionId
) {}