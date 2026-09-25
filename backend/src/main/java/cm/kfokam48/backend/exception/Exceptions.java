package cm.kfokam48.backend.exception;

import org.springframework.http.HttpStatus;

public final class Exceptions {

    private Exceptions() {}

    // ---------- ISSUE #1 ----------
    public static class PromotionInconnueException extends ApiException {
        public PromotionInconnueException(Long promotionId) {
            super(HttpStatus.NOT_FOUND, "PROMOTION_INCONNUE",
                  "La promotion " + promotionId + " est inconnue.");
        }
    }

    public static class SessionInconnueException extends ApiException {
        public SessionInconnueException(Long sessionId) {
            super(HttpStatus.NOT_FOUND, "SESSION_INCONNUE",
                  "La session " + sessionId + " est inconnue.");
        }
    }

    // ---------- ISSUE #2 ----------
    public static class CodeInconnuException extends ApiException {
        public CodeInconnuException(String code) {
            super(HttpStatus.BAD_REQUEST, "CODE_INCONNU",
                  "Le code saisi est inconnu.");
        }
    }

    public static class CodeExpireException extends ApiException {
        public CodeExpireException() {
            super(HttpStatus.GONE, "CODE_EXPIRE",
                  "Le code de présence a expiré.");
        }
    }

    public static class DejaPresentException extends ApiException {
        public DejaPresentException() {
            super(HttpStatus.CONFLICT, "DEJA_PRESENT",
                  "Vous avez déjà marqué votre présence.");
        }
    }

    public static class SessionClotureeException extends ApiException {
        public SessionClotureeException() {
            super(HttpStatus.BAD_REQUEST, "SESSION_CLOTUREE",
                  "La session est clôturée.");
        }
    }

    public static class EtudiantInconnuException extends ApiException {
        public EtudiantInconnuException(Long etudiantId) {
            super(HttpStatus.NOT_FOUND, "ETUDIANT_INCONNU",
                  "L'étudiant " + etudiantId + " est inconnu.");
        }
    }

    public static class ValidationException extends ApiException {
        public ValidationException(String message) {
            super(HttpStatus.BAD_REQUEST, "VALIDATION_ERREUR", message);
        }
    }

    // ---------- ISSUE #3 ----------
    public static class LienInvalideException extends ApiException {
        public LienInvalideException() {
            super(HttpStatus.BAD_REQUEST, "LIEN_INVALIDE",
                  "Le lien fourni n'est pas une URL valide.");
        }
    }

    public static class ExerciceDejaDeposeException extends ApiException {
        public ExerciceDejaDeposeException() {
            super(HttpStatus.CONFLICT, "EXERCICE_DEJA_DEPOSE",
                  "Un exercice a déjà été déposé pour cette session.");
        }
    }

    public static class ExerciceInconnuException extends ApiException {
        public ExerciceInconnuException(Long exerciceId) {
            super(HttpStatus.NOT_FOUND, "EXERCICE_INCONNU",
                  "L'exercice " + exerciceId + " est inconnu.");
        }
    }

    // ---------- ISSUE #5 ----------
    public static class NoteInvalideException extends ApiException {
        public NoteInvalideException() {
            super(HttpStatus.BAD_REQUEST, "NOTE_INVALIDE",
                  "La note doit être un entier compris entre 0 et 20.");
        }
    }

    public static class AutoRelectureException extends ApiException {
        public AutoRelectureException() {
            super(HttpStatus.FORBIDDEN, "AUTO_RELECTURE",
                  "Un étudiant ne peut pas relire son propre exercice.");
        }
    }

    public static class RelectureDejaRendueException extends ApiException {
        public RelectureDejaRendueException() {
            super(HttpStatus.CONFLICT, "RELECTURE_DEJA_RENDUE",
                  "Cette relecture a déjà été rendue.");
        }
    }

    public static class RelectureInconnueException extends ApiException {
        public RelectureInconnueException(Long id) {
            super(HttpStatus.NOT_FOUND, "RELECTURE_INCONNUE",
                  "La relecture " + id + " est inconnue.");
        }
    }

    // ---------- ISSUE #6 ----------
    public static class RelectureCommenceeException extends ApiException {
        public RelectureCommenceeException() {
            super(HttpStatus.CONFLICT, "RELECTURE_COMMENCEE",
                  "Impossible de modifier le lien : une relecture a déjà commencé.");
        }
    }

    // ---------- ISSUE #8 ----------
    public static class SessionDejaClotureeException extends ApiException {
        public SessionDejaClotureeException() {
            super(HttpStatus.CONFLICT, "SESSION_DEJA_CLOTUREE",
                  "Cette session est déjà clôturée.");
        }
    }
}