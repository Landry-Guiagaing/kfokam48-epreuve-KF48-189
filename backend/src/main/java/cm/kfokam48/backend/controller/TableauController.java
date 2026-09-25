package cm.kfokam48.backend.controller;

import cm.kfokam48.backend.dto.response.TableauLigneResponse;
import cm.kfokam48.backend.service.TableauService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/tableau")
public class TableauController {

    private final TableauService tableauService;

    public TableauController(TableauService tableauService) {
        this.tableauService = tableauService;
    }

    @GetMapping
    public ResponseEntity<List<TableauLigneResponse>> tableau(
            @RequestParam Long promotionId) {
        return ResponseEntity.ok(tableauService.tableau(promotionId));
    }
}