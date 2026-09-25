package cm.kfokam48.backend.service;

import cm.kfokam48.backend.dto.response.TableauLigneResponse;

import java.util.List;

public interface TableauService {
    List<TableauLigneResponse> tableau(Long promotionId);
}