package cm.kfokam48.backend.service;

import cm.kfokam48.backend.dto.request.CreerSessionRequest;
import cm.kfokam48.backend.dto.response.SessionClotureResponse;
import cm.kfokam48.backend.dto.response.SessionResponse;

public interface SessionService {
    SessionResponse ouvrir(CreerSessionRequest request);
    SessionClotureResponse cloturer(Long sessionId);
}