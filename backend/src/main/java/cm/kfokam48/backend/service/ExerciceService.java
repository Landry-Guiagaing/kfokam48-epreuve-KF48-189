package cm.kfokam48.backend.service;

import cm.kfokam48.backend.dto.request.DeposerExerciceRequest;
import cm.kfokam48.backend.dto.response.ExerciceDetailResponse;
import cm.kfokam48.backend.dto.response.ExerciceResponse;

public interface ExerciceService {
    ExerciceResponse deposer(DeposerExerciceRequest request);
    ExerciceDetailResponse consulter(Long exerciceId);
}