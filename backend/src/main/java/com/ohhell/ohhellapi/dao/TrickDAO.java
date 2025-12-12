package com.ohhell.ohhellapi.dao;

import com.ohhell.ohhellapi.models.Trick;
import com.ohhell.ohhellapi.models.Card;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import java.util.List;

public class TrickDAO {

    @PersistenceContext(unitName = "ohhellPU")
    private EntityManager em;

    // Métodos básicos existentes
    public Trick findById(Long id) {
        return em.find(Trick.class, id);
    }

    public List<Trick> findByRoundId(Long roundId) {
        return em.createQuery(
                        "SELECT t FROM Trick t WHERE t.roundId = :roundId ORDER BY t.trickNumber",
                        Trick.class)
                .setParameter("roundId", roundId)
                .getResultList();
    }

    public Trick save(Trick trick) {
        if (trick.getId() == null) {
            em.persist(trick);
            return trick;
        } else {
            return em.merge(trick);
        }
    }

    public void delete(Long id) {
        Trick trick = findById(id);
        if (trick != null) {
            em.remove(trick);
        }
    }

    // --- NUEVOS MÉTODOS REQUERIDOS POR LOS SERVICES ---

    public int countCompletedTricksInRound(Long roundId) {
        try {
            Long count = em.createQuery(
                            "SELECT COUNT(t) FROM Trick t WHERE t.roundId = :roundId AND t.completed = true",
                            Long.class)
                    .setParameter("roundId", roundId)
                    .getSingleResult();
            return count != null ? count.intValue() : 0;
        } catch (Exception e) {
            return 0;
        }
    }

    public int countTotalTricksInRound(Long roundId) {
        try {
            Long count = em.createQuery(
                            "SELECT COUNT(t) FROM Trick t WHERE t.roundId = :roundId",
                            Long.class)
                    .setParameter("roundId", roundId)
                    .getSingleResult();
            return count != null ? count.intValue() : 0;
        } catch (Exception e) {
            return 0;
        }
    }

    public Trick createTrick(Trick trick) {
        return save(trick);
    }

    public Trick getTrickById(Long trickId) {
        return findById(trickId);
    }

    public void updateLeadCard(Long trickId, Card leadCard) {
        Trick trick = findById(trickId);
        if (trick != null) {
            trick.setLeadSuit(leadCard.getSuit());
            save(trick);
        }
    }

    public void completeTrick(Long trickId, Long winnerPlayerId) {
        Trick trick = findById(trickId);
        if (trick != null) {
            trick.setCompleted(true);
            trick.setWinnerId(winnerPlayerId);
            save(trick);
        }
    }

    public Trick getCurrentTrick(Long roundId) {
        try {
            return em.createQuery(
                            "SELECT t FROM Trick t WHERE t.roundId = :roundId AND t.completed = false ORDER BY t.trickNumber",
                            Trick.class)
                    .setParameter("roundId", roundId)
                    .setMaxResults(1)
                    .getSingleResult();
        } catch (Exception e) {
            return null;
        }
    }

    public List<Trick> getTricksByRoundId(Long roundId) {
        return findByRoundId(roundId);
    }

    public Trick getTrickByNumber(Long roundId, int trickNumber) {
        try {
            return em.createQuery(
                            "SELECT t FROM Trick t WHERE t.roundId = :roundId AND t.trickNumber = :trickNumber",
                            Trick.class)
                    .setParameter("roundId", roundId)
                    .setParameter("trickNumber", trickNumber)
                    .getSingleResult();
        } catch (Exception e) {
            return null;
        }
    }
}