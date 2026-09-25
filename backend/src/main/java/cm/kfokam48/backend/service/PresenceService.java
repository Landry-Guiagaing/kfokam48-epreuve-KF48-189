package cm.kfokam48.backend.service;

import cm.kfokam48.backend.dto.request.MarquerPresenceRequest;
import cm.kfokam48.backend.dto.response.PresenceResponse;

public interface PresenceService {
    PresenceResponse marquer(MarquerPresenceRequest request);
}