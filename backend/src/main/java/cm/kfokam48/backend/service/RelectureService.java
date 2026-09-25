package cm.kfokam48.backend.service;

import cm.kfokam48.backend.dto.request.RendreRelectureRequest;
import cm.kfokam48.backend.dto.response.RelectureResponse;

public interface RelectureService {

    RelectureResponse rendre(Long relectureId, RendreRelectureRequest request);
}