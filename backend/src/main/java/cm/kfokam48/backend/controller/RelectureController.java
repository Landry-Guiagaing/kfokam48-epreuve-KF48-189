package cm.kfokam48.backend.controller;

import cm.kfokam48.backend.dto.request.RendreRelectureRequest;
import cm.kfokam48.backend.dto.response.RelectureResponse;
import cm.kfokam48.backend.service.RelectureService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/lectures")
public class RelectureController {

    private final RelectureService relectureService;

    public RelectureController(RelectureService relectureService) {
        this.relectureService = relectureService;
    }

    @PostMapping("/{id}")
    public ResponseEntity<RelectureResponse> rendre(
            @PathVariable Long id,
            @Valid @RequestBody RendreRelectureRequest request) {
        RelectureResponse response = relectureService.rendre(id, request);
        return ResponseEntity.ok(response);
    }
}