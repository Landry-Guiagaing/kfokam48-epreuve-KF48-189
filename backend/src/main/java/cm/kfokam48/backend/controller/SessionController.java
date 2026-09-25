package cm.kfokam48.backend.controller;

import cm.kfokam48.backend.dto.request.CreerSessionRequest;
import cm.kfokam48.backend.dto.response.SessionClotureResponse;
import cm.kfokam48.backend.dto.response.SessionResponse;
import cm.kfokam48.backend.service.SessionService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/sessions")
public class SessionController {

    private final SessionService sessionService;

    public SessionController(SessionService sessionService) {
        this.sessionService = sessionService;
    }

    @PostMapping
    public ResponseEntity<SessionResponse> ouvrir(
            @Valid @RequestBody CreerSessionRequest request) {
        SessionResponse response = sessionService.ouvrir(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @PostMapping("/{id}/cloture")
    public ResponseEntity<SessionClotureResponse> cloturer(@PathVariable Long id) {
        return ResponseEntity.ok(sessionService.cloturer(id));
    }
}