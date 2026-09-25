package cm.kfokam48.backend.service;

import cm.kfokam48.backend.entity.Exercice;
import cm.kfokam48.backend.entity.Relecture;

import java.util.Optional;

public interface AssignationService {

    /**
     * Tente d'assigner un relecteur à un exercice donné.
     * RG2 : pas d'auto-relecture.
     * RG6 : un seul relecteur par exercice.
     * RG7 : tirage au hasard parmi les étudiants présents ET ayant déposé.
     * @return la relecture créée, ou vide si aucun relecteur disponible.
     */
    Optional<Relecture> assigner(Exercice exercice);
}