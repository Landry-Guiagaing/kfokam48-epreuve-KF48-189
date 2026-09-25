package cm.kfokam48.backend.service.impl;

import cm.kfokam48.backend.dto.response.TableauLigneResponse;
import cm.kfokam48.backend.exception.Exceptions.PromotionInconnueException;
import cm.kfokam48.backend.repository.PromotionRepository;
import cm.kfokam48.backend.repository.TableauRepository;
import cm.kfokam48.backend.service.TableauService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class TableauServiceImpl implements TableauService {

    private final TableauRepository tableauRepository;
    private final PromotionRepository promotionRepository;

    public TableauServiceImpl(TableauRepository tableauRepository,
                              PromotionRepository promotionRepository) {
        this.tableauRepository = tableauRepository;
        this.promotionRepository = promotionRepository;
    }

    @Override
    @Transactional(readOnly = true)
    public List<TableauLigneResponse> tableau(Long promotionId) {

        // 404 si la promotion n'existe pas
        if (!promotionRepository.existsById(promotionId)) {
            throw new PromotionInconnueException(promotionId);
        }

        List<Object[]> rows = tableauRepository.findTableauByPromotion(promotionId);

        return rows.stream()
                .map(r -> new TableauLigneResponse(
                        ((Number) r[0]).longValue(),
                        (String) r[1],
                        ((Number) r[2]).longValue(),
                        ((Number) r[3]).longValue(),
                        r[4] == null ? null : ((Number) r[4]).doubleValue(),
                        ((Number) r[5]).longValue()
                ))
                .toList();
    }
}