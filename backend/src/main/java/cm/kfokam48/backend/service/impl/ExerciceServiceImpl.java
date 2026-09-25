package cm.kfokam48.backend.service.impl;

import cm.kfokam48.backend.dto.request.DeposerExerciceRequest;
import cm.kfokam48.backend.dto.response.ExerciceResponse;
import cm.kfokam48.backend.entity.Etudiant;
import cm.kfokam48.backend.entity.Exercice;
import cm.kfokam48.backend.entity.Session;
import cm.kfokam48.backend.exception.Exceptions.EtudiantInconnuException;
import cm.kfokam48.backend.exception.Exceptions.ExerciceDejaDeposeException;
import cm.kfokam48.backend.exception.Exceptions.LienInvalideException;
import cm.kfokam48.backend.exception.Exceptions.SessionClotureeException;
import cm.kfokam48.backend.exception.Exceptions.SessionInconnueException;
import cm.kfokam48.backend.repository.EtudiantRepository;
import cm.kfokam48.backend.repository.ExerciceRepository;
import cm.kfokam48.backend.repository.SessionRepository;
import cm.kfokam48.backend.service.ExerciceService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.net.URI;
import java.net.URISyntaxException;

@Service
public class ExerciceServiceImpl implements ExerciceService {

    private final SessionRepository sessionRepository;
    private final EtudiantRepository etudiantRepository;
    private final ExerciceRepository exerciceRepository;

    public ExerciceServiceImpl(SessionRepository sessionRepository,
                               EtudiantRepository etudiantRepository,
                               ExerciceRepository exerciceRepository) {
        this.sessionRepository = sessionRepository;
        this.etudiantRepository = etudiantRepository;
        this.exerciceRepository = exerciceRepository;
    }

    @Override
    @Transactional
    public ExerciceResponse deposer(DeposerExerciceRequest request) {

        // 1) Vérifier le lien
        if (!estUrlValide(request.lien())) {
            throw new LienInvalideException();
        }

        // 2) Vérifier la session
        Session session = sessionRepository.findById(request.sessionId())
                .orElseThrow(() -> new SessionInconnueException(request.sessionId()));

        // 3) Vérifier l'étudiant
        Etudiant etudiant = etudiantRepository.findById(request.etudiantId())
                .orElseThrow(() -> new EtudiantInconnuException(request.etudiantId()));

        // 4) Vérifier la clôture (RG11)
        if (session.getClotureAt() != null) {
            throw new SessionClotureeException();
        }

        // 5) Vérifier l'unicité (RG5)
        if (exerciceRepository.existsBySessionIdAndEtudiantId(
                session.getId(), etudiant.getId())) {
            throw new ExerciceDejaDeposeException();
        }

        // 6) Créer l'exercice
        Exercice exercice = new Exercice();
        exercice.setSession(session);
        exercice.setEtudiant(etudiant);
        exercice.setLien(request.lien());
        exercice.setStatut(Exercice.StatutExercice.DEPOSE);

        Exercice saved = exerciceRepository.save(exercice);

        return new ExerciceResponse(saved.getId(), saved.getStatut().name());
    }

    private boolean estUrlValide(String lien) {
        try {
            URI uri = new URI(lien);
            return uri.getScheme() != null
                    && (uri.getScheme().equals("http") || uri.getScheme().equals("https"))
                    && uri.getHost() != null;
        } catch (URISyntaxException e) {
            return false;
        }
    }
}