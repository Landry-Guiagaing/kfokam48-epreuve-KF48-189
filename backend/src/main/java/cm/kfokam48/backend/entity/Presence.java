package cm.kfokam48.backend.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "presence",
       uniqueConstraints = @UniqueConstraint(
           name = "uk_presence_session_etudiant",
           columnNames = {"session_id", "etudiant_id"}))
public class Presence {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "session_id", nullable = false)
    private Session session;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "etudiant_id", nullable = false)
    private Etudiant etudiant;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private SourcePresence source;

    @Column(name = "marquee_at", nullable = false, updatable = false)
    private LocalDateTime marqueeAt;

    @PrePersist
    void onCreate() {
        if (marqueeAt == null) marqueeAt = LocalDateTime.now();
    }

    public enum SourcePresence { ETUDIANT, FORMATEUR }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Session getSession() { return session; }
    public void setSession(Session session) { this.session = session; }

    public Etudiant getEtudiant() { return etudiant; }
    public void setEtudiant(Etudiant etudiant) { this.etudiant = etudiant; }

    public SourcePresence getSource() { return source; }
    public void setSource(SourcePresence source) { this.source = source; }

    public LocalDateTime getMarqueeAt() { return marqueeAt; }
    public void setMarqueeAt(LocalDateTime marqueeAt) { this.marqueeAt = marqueeAt; }
}