package cm.kfokam48.backend.controller;

import cm.kfokam48.backend.dto.request.MarquerPresenceRequest;
import cm.kfokam48.backend.dto.response.PresenceResponse;
import cm.kfokam48.backend.service.PresenceService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/presences")
public class PresenceController {

    private final PresenceService presenceService;

    public PresenceController(PresenceService presenceService) {
        this.presenceService = presenceService;
    }

    @PostMapping
    public ResponseEntity<PresenceResponse> marquer(
            @Valid @RequestBody MarquerPresenceRequest request) {
        PresenceResponse response = presenceService.marquer(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }
}