package cm.kfokam48.backend.controller;

import cm.kfokam48.backend.dto.request.DeposerExerciceRequest;
import cm.kfokam48.backend.dto.response.ExerciceResponse;
import cm.kfokam48.backend.service.ExerciceService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/exercices")
public class ExerciceController {

    private final ExerciceService exerciceService;

    public ExerciceController(ExerciceService exerciceService) {
        this.exerciceService = exerciceService;
    }

    @PostMapping
    public ResponseEntity<ExerciceResponse> deposer(
            @Valid @RequestBody DeposerExerciceRequest request) {
        ExerciceResponse response = exerciceService.deposer(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }
}