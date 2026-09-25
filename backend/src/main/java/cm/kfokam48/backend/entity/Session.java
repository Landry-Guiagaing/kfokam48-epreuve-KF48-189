package cm.kfokam48.backend.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "session")
public class Session {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 200)
    private String titre;

    @Column(nullable = false, unique = true, length = 6)
    private String code;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "promotion_id", nullable = false)
    private Promotion promotion;

    @Column(name = "ouverture_at", nullable = false)
    private LocalDateTime ouvertureAt;

    @Column(name = "expiration_at", nullable = false)
    private LocalDateTime expirationAt;

    @Column(name = "cloture_at")
    private LocalDateTime clotureAt;

    // Getters / setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getTitre() { return titre; }
    public void setTitre(String titre) { this.titre = titre; }

    public String getCode() { return code; }
    public void setCode(String code) { this.code = code; }

    public Promotion getPromotion() { return promotion; }
    public void setPromotion(Promotion promotion) { this.promotion = promotion; }

    public LocalDateTime getOuvertureAt() { return ouvertureAt; }
    public void setOuvertureAt(LocalDateTime ouvertureAt) { this.ouvertureAt = ouvertureAt; }

    public LocalDateTime getExpirationAt() { return expirationAt; }
    public void setExpirationAt(LocalDateTime expirationAt) { this.expirationAt = expirationAt; }

    public LocalDateTime getClotureAt() { return clotureAt; }
    public void setClotureAt(LocalDateTime clotureAt) { this.clotureAt = clotureAt; }
}