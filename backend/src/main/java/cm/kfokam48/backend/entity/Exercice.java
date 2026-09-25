package cm.kfokam48.backend.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "exercice",
       uniqueConstraints = @UniqueConstraint(
           name = "uk_exercice_session_etudiant",
           columnNames = {"session_id", "etudiant_id"}))
public class Exercice {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "session_id", nullable = false)
    private Session session;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "etudiant_id", nullable = false)
    private Etudiant etudiant;

    @Column(nullable = false, length = 500)
    private String lien;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private StatutExercice statut = StatutExercice.DEPOSE;

    @Column(name = "depose_at", nullable = false, updatable = false)
    private LocalDateTime deposeAt;

    @PrePersist
    void onCreate() {
        if (deposeAt == null) deposeAt = LocalDateTime.now();
    }

    public enum StatutExercice { DEPOSE, EN_ATTENTE, RELUE, CLOTURE }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Session getSession() { return session; }
    public void setSession(Session session) { this.session = session; }

    public Etudiant getEtudiant() { return etudiant; }
    public void setEtudiant(Etudiant etudiant) { this.etudiant = etudiant; }

    public String getLien() { return lien; }
    public void setLien(String lien) { this.lien = lien; }

    public StatutExercice getStatut() { return statut; }
    public void setStatut(StatutExercice statut) { this.statut = statut; }

    public LocalDateTime getDeposeAt() { return deposeAt; }
    public void setDeposeAt(LocalDateTime deposeAt) { this.deposeAt = deposeAt; }
}