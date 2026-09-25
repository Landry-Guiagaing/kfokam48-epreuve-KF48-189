package cm.kfokam48.backend.service.impl;

import cm.kfokam48.backend.dto.request.MarquerPresenceRequest;
import cm.kfokam48.backend.dto.response.PresenceResponse;
import cm.kfokam48.backend.entity.Etudiant;
import cm.kfokam48.backend.entity.Presence;
import cm.kfokam48.backend.entity.Session;
import cm.kfokam48.backend.exception.Exceptions.CodeExpireException;
import cm.kfokam48.backend.exception.Exceptions.CodeInconnuException;
import cm.kfokam48.backend.exception.Exceptions.DejaPresentException;
import cm.kfokam48.backend.exception.Exceptions.EtudiantInconnuException;
import cm.kfokam48.backend.exception.Exceptions.SessionClotureeException;
import cm.kfokam48.backend.repository.EtudiantRepository;
import cm.kfokam48.backend.repository.PresenceRepository;
import cm.kfokam48.backend.repository.SessionRepository;
import cm.kfokam48.backend.service.PresenceService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
public class PresenceServiceImpl implements PresenceService {

    private final SessionRepository sessionRepository;
    private final EtudiantRepository etudiantRepository;
    private final PresenceRepository presenceRepository;

    public PresenceServiceImpl(SessionRepository sessionRepository,
                               EtudiantRepository etudiantRepository,
                               PresenceRepository presenceRepository) {
        this.sessionRepository = sessionRepository;
        this.etudiantRepository = etudiantRepository;
        this.presenceRepository = presenceRepository;
    }

    @Override
    @Transactional
    public PresenceResponse marquer(MarquerPresenceRequest request) {

        // 1) Trouver la session par code
        Session session = sessionRepository.findByCode(request.code())
                .orElseThrow(() -> new CodeInconnuException(request.code()));

        // 2) Vérifier l'étudiant
        Etudiant etudiant = etudiantRepository.findById(request.etudiantId())
                .orElseThrow(() -> new EtudiantInconnuException(request.etudiantId()));

        // 3) Vérifier la clôture (RG15)
        if (session.getClotureAt() != null) {
            throw new SessionClotureeException();
        }

        // 4) Vérifier l'expiration (RG1)
        if (LocalDateTime.now().isAfter(session.getExpirationAt())) {
            throw new CodeExpireException();
        }

        // 5) Vérifier l'unicité de la présence (RG4)
        if (presenceRepository.existsBySessionIdAndEtudiantId(
                session.getId(), etudiant.getId())) {
            throw new DejaPresentException();
        }

        // 6) Enregistrer la présence
        Presence presence = new Presence();
        presence.setSession(session);
        presence.setEtudiant(etudiant);
        presence.setSource(Presence.SourcePresence.ETUDIANT);

        Presence saved = presenceRepository.save(presence);

        return new PresenceResponse(
                saved.getId(),
                saved.getSession().getId(),
                saved.getEtudiant().getId(),
                saved.getSource().name()
        );
    }
}