package cm.kfokam48.backend.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "relecture",
       uniqueConstraints = @UniqueConstraint(
           name = "uk_relecture_exercice",
           columnNames = {"exercice_id"}))
public class Relecture {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "exercice_id", nullable = false)
    private Exercice exercice;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "relecteur_id", nullable = false)
    private Etudiant relecteur;

    @Column
    private Integer note;

    @Column(length = 2000)
    private String commentaire;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private StatutRelecture statut = StatutRelecture.EN_ATTENTE;

    @Column(name = "assignee_at", nullable = false, updatable = false)
    private LocalDateTime assigneeAt;

    @Column(name = "rendue_at")
    private LocalDateTime rendueAt;

    @PrePersist
    void onCreate() {
        if (assigneeAt == null) assigneeAt = LocalDateTime.now();
    }

    public enum StatutRelecture { EN_ATTENTE, RELUE }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Exercice getExercice() { return exercice; }
    public void setExercice(Exercice exercice) { this.exercice = exercice; }

    public Etudiant getRelecteur() { return relecteur; }
    public void setRelecteur(Etudiant relecteur) { this.relecteur = relecteur; }

    public Integer getNote() { return note; }
    public void setNote(Integer note) { this.note = note; }

    public String getCommentaire() { return commentaire; }
    public void setCommentaire(String commentaire) { this.commentaire = commentaire; }

    public StatutRelecture getStatut() { return statut; }
    public void setStatut(StatutRelecture statut) { this.statut = statut; }

    public LocalDateTime getAssigneeAt() { return assigneeAt; }
    public void setAssigneeAt(LocalDateTime assigneeAt) { this.assigneeAt = assigneeAt; }

    public LocalDateTime getRendueAt() { return rendueAt; }
    public void setRendueAt(LocalDateTime rendueAt) { this.rendueAt = rendueAt; }
}