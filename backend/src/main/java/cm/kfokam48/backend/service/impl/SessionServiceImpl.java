package cm.kfokam48.backend.service.impl;

import cm.kfokam48.backend.dto.request.CreerSessionRequest;
import cm.kfokam48.backend.dto.response.SessionClotureResponse;
import cm.kfokam48.backend.dto.response.SessionResponse;
import cm.kfokam48.backend.entity.Promotion;
import cm.kfokam48.backend.entity.Session;
import cm.kfokam48.backend.exception.Exceptions.PromotionInconnueException;
import cm.kfokam48.backend.exception.Exceptions.SessionDejaClotureeException;
import cm.kfokam48.backend.exception.Exceptions.SessionInconnueException;
import cm.kfokam48.backend.repository.PromotionRepository;
import cm.kfokam48.backend.repository.SessionRepository;
import cm.kfokam48.backend.service.SessionService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.security.SecureRandom;
import java.time.LocalDateTime;

@Service
public class SessionServiceImpl implements SessionService {

    private static final int DUREE_MINUTES = 15;
    private static final int LONGUEUR_CODE = 6;
    private static final String ALPHABET = "ABCDEFGHJKLMNPQRSTUVWXYZ23456789";
    private static final SecureRandom RANDOM = new SecureRandom();

    private final SessionRepository sessionRepository;
    private final PromotionRepository promotionRepository;

    public SessionServiceImpl(SessionRepository sessionRepository,
                              PromotionRepository promotionRepository) {
        this.sessionRepository = sessionRepository;
        this.promotionRepository = promotionRepository;
    }

    @Override
    @Transactional
    public SessionResponse ouvrir(CreerSessionRequest request) {
        Promotion promotion = promotionRepository.findById(request.promotionId())
                .orElseThrow(() -> new PromotionInconnueException(request.promotionId()));

        LocalDateTime ouverture = LocalDateTime.now();
        LocalDateTime expiration = ouverture.plusMinutes(DUREE_MINUTES);

        Session session = new Session();
        session.setTitre(request.titre());
        session.setPromotion(promotion);
        session.setOuvertureAt(ouverture);
        session.setExpirationAt(expiration);
        session.setCode(genererCodeUnique());

        Session saved = sessionRepository.save(session);

        return new SessionResponse(
                saved.getId(),
                saved.getCode(),
                saved.getOuvertureAt(),
                saved.getExpirationAt()
        );
    }

    @Override
    @Transactional
    public SessionClotureResponse cloturer(Long sessionId) {
        Session session = sessionRepository.findById(sessionId)
                .orElseThrow(() -> new SessionInconnueException(sessionId));

        if (session.getClotureAt() != null) {
            throw new SessionDejaClotureeException();
        }

        session.setClotureAt(LocalDateTime.now());
        Session saved = sessionRepository.save(session);

        return new SessionClotureResponse(saved.getId(), saved.getClotureAt());
    }

    private String genererCodeUnique() {
        for (int i = 0; i < 50; i++) {
            String code = codeAleatoire();
            if (!sessionRepository.existsByCode(code)) {
                return code;
            }
        }
        throw new IllegalStateException("Impossible de générer un code unique");
    }

    private String codeAleatoire() {
        StringBuilder sb = new StringBuilder(LONGUEUR_CODE);
        for (int i = 0; i < LONGUEUR_CODE; i++) {
            sb.append(ALPHABET.charAt(RANDOM.nextInt(ALPHABET.length())));
        }
        return sb.toString();
    }
}