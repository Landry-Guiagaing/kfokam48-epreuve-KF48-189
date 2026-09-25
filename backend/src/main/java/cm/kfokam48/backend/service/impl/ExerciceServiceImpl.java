package cm.kfokam48.backend.service.impl;

import cm.kfokam48.backend.dto.request.DeposerExerciceRequest;
import cm.kfokam48.backend.dto.response.ExerciceDetailResponse;
import cm.kfokam48.backend.dto.response.ExerciceResponse;
import cm.kfokam48.backend.entity.*;
import cm.kfokam48.backend.exception.Exceptions.*;
import cm.kfokam48.backend.repository.*;
import cm.kfokam48.backend.service.AssignationService;
import cm.kfokam48.backend.service.ExerciceService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;

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
        if (!estUrlValide(request.lien())) throw new LienInvalideException();

        Session session = sessionRepository.findById(request.sessionId())
                .orElseThrow(() -> new SessionInconnueException(request.sessionId()));
        Etudiant etudiant = etudiantRepository.findById(request.etudiantId())
                .orElseThrow(() -> new EtudiantInconnuException(request.etudiantId()));

        if (session.getClotureAt() != null) throw new SessionClotureeException();
        if (exerciceRepository.existsBySessionIdAndEtudiantId(session.getId(), etudiant.getId()))
            throw new ExerciceDejaDeposeException();

        Exercice exercice = new Exercice();
        exercice.setSession(session);
        exercice.setEtudiant(etudiant);
        exercice.setLien(request.lien());
        exercice.setStatut(Exercice.StatutExercice.DEPOSE);
        Exercice saved = exerciceRepository.save(exercice);

        assignationService.assigner(saved);

        Exercice rafraichi = exerciceRepository.findById(saved.getId()).orElse(saved);
        return new ExerciceResponse(rafraichi.getId(), rafraichi.getStatut().name());
    }

    @Override
    @Transactional(readOnly = true)
    public ExerciceDetailResponse consulter(Long exerciceId) {
        Exercice exercice = exerciceRepository.findById(exerciceId)
                .orElseThrow(() -> new ExerciceInconnuException(exerciceId));

        List<Relecture> relectures = relectureRepository.findAllByExerciceId(exerciceId);

        List<Integer> notes = relectures.stream()
                .filter(r -> r.getStatut() == Relecture.StatutRelecture.RELUE)
                .map(Relecture::getNote)
                .filter(n -> n != null)
                .toList();

        Integer note = null;
        String commentaire = null;
        boolean provisoire = false;

        if (notes.size() == 1) {
            note = notes.get(0);
            provisoire = true;
            commentaire = relectures.stream()
                    .filter(r -> r.getStatut() == Relecture.StatutRelecture.RELUE)
                    .findFirst().map(Relecture::getCommentaire).orElse(null);
        } else if (notes.size() >= 2) {
            // RG17 : moyenne des 2 notes, arrondie a l'entier
            int moyenne = (int) Math.round(notes.stream().mapToInt(Integer::intValue).average().orElse(0));
            note = moyenne;
            provisoire = false;
            commentaire = "Moyenne de " + notes.size() + " relectures.";
        }

        return new ExerciceDetailResponse(
                exercice.getId(),
                exercice.getSession().getId(),
                exercice.getEtudiant().getId(),
                exercice.getLien(),
                exercice.getStatut().name(),
                note,
                commentaire,
                provisoire
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