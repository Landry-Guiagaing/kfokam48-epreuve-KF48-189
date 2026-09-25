package cm.kfokam48.backend.dto.request;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

public record RendreRelectureRequest(
        @NotNull(message = "La note est obligatoire")
        @Min(value = 0, message = "La note doit être au minimum de 0")
        @Max(value = 20, message = "La note doit être au maximum de 20")
        Integer note,

        String commentaire
) {}