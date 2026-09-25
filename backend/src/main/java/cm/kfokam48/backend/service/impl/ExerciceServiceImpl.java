package cm.kfokam48.backend.service.impl;

import cm.kfokam48.backend.dto.request.DeposerExerciceRequest;
import cm.kfokam48.backend.dto.response.ExerciceDetailResponse;
import cm.kfokam48.backend.dto.response.ExerciceResponse;
import cm.kfokam48.backend.entity.Etudiant;
import cm.kfokam48.backend.entity.Exercice;
import cm.kfokam48.backend.entity.Relecture;
import cm.kfokam48.backend.entity.Session;
import cm.kfokam48.backend.exception.Exceptions.EtudiantInconnuException;
import cm.kfokam48.backend.exception.Exceptions.ExerciceDejaDeposeException;
import cm.kfokam48.backend.exception.Exceptions.ExerciceInconnuException;
import cm.kfokam48.backend.exception.Exceptions.LienInvalideException;
import cm.kfokam48.backend.exception.Exceptions.SessionClotureeException;
import cm.kfokam48.backend.exception.Exceptions.SessionInconnueException;
import cm.kfokam48.backend.repository.EtudiantRepository;
import cm.kfokam48.backend.repository.ExerciceRepository;
import cm.kfokam48.backend.repository.RelectureRepository;
import cm.kfokam48.backend.repository.SessionRepository;
import cm.kfokam48.backend.service.AssignationService;
import cm.kfokam48.backend.service.ExerciceService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.Optional;

@Service
public class ExerciceServiceImpl implements ExerciceService {

    private final SessionRepository sessionRepository;
    private final EtudiantRepository etudiantRepository;
    private final ExerciceRepository exerciceRepository;
    private final RelectureRepository relectureRepository;
    private final AssignationService assignationService;

    public ExerciceServiceImpl(SessionRepository sessionRepository,
                               EtudiantRepository etudiantRepository,
                               ExerciceRepository exerciceRepository,
                               RelectureRepository relectureRepository,
                               AssignationService assignationService) {
        this.sessionRepository = sessionRepository;
        this.etudiantRepository = etudiantRepository;
        this.exerciceRepository = exerciceRepository;
        this.relectureRepository = relectureRepository;
        this.assignationService = assignationService;
    }

    @Override
    @Transactional
    public ExerciceResponse deposer(DeposerExerciceRequest request) {

        if (!estUrlValide(request.lien())) {
            throw new LienInvalideException();
        }

        Session session = sessionRepository.findById(request.sessionId())
                .orElseThrow(() -> new SessionInconnueException(request.sessionId()));

        Etudiant etudiant = etudiantRepository.findById(request.etudiantId())
                .orElseThrow(() -> new EtudiantInconnuException(request.etudiantId()));

        if (session.getClotureAt() != null) {
            throw new SessionClotureeException();
        }

        if (exerciceRepository.existsBySessionIdAndEtudiantId(
                session.getId(), etudiant.getId())) {
            throw new ExerciceDejaDeposeException();
        }

        Exercice exercice = new Exercice();
        exercice.setSession(session);
        exercice.setEtudiant(etudiant);
        exercice.setLien(request.lien());
        exercice.setStatut(Exercice.StatutExercice.DEPOSE);

        Exercice saved = exerciceRepository.save(exercice);

        assignationService.assigner(saved);

        Exercice rafraichi = exerciceRepository.findById(saved.getId())
                .orElse(saved);

        return new ExerciceResponse(rafraichi.getId(), rafraichi.getStatut().name());
    }

    @Override
    @Transactional(readOnly = true)
    public ExerciceDetailResponse consulter(Long exerciceId) {

        Exercice exercice = exerciceRepository.findById(exerciceId)
                .orElseThrow(() -> new ExerciceInconnuException(exerciceId));

        // RG8 : on n'expose JAMAIS le relecteurId ni son nom.
        // On lit la relecture uniquement pour récupérer la note et le commentaire.
        Optional<Relecture> relectureOpt = relectureRepository.findByExerciceId(exerciceId);

        Integer note = null;
        String commentaire = null;

        if (relectureOpt.isPresent()
                && relectureOpt.get().getStatut() == Relecture.StatutRelecture.RELUE) {
            note = relectureOpt.get().getNote();
            commentaire = relectureOpt.get().getCommentaire();
        }

        return new ExerciceDetailResponse(
                exercice.getId(),
                exercice.getSession().getId(),
                exercice.getEtudiant().getId(),
                exercice.getLien(),
                exercice.getStatut().name(),
                note,
                commentaire
        );
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