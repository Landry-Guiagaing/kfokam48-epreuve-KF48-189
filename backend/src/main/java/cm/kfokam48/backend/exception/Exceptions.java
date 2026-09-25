package cm.kfokam48.backend.exception;

import org.springframework.http.HttpStatus;

public final class Exceptions {

    private Exceptions() {}

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

    public static class ValidationException extends ApiException {
        public ValidationException(String message) {
            super(HttpStatus.BAD_REQUEST, "VALIDATION_ERREUR", message);
        }
    }
    
    public static class EtudiantInconnuException extends ApiException {
        public EtudiantInconnuException(Long etudiantId) {
            super(HttpStatus.NOT_FOUND, "ETUDIANT_INCONNU",
                  "L'étudiant " + etudiantId + " est inconnu.");
        }
    }
}