package com.ohhell.ohhellapi.dao;

import com.ohhell.ohhellapi.models.Bid;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.enterprise.context.ApplicationScoped;
import java.util.List;

@ApplicationScoped
public class BidDAO {

    // Constructor vacío REQUERIDO
    public BidDAO() {
    }

    @PersistenceContext(unitName = "ohhellPU")
    private EntityManager em;

    // --- METODOS BASICOS ---
    public Bid findById(Long id) {
        return em.find(Bid.class, id);
    }

    public List<Bid> findByRoundId(Long roundId) {
        return em.createQuery(
                        "SELECT b FROM Bid b WHERE b.roundId = :roundId ORDER BY b.timestamp",
                        Bid.class)
                .setParameter("roundId", roundId)
                .getResultList();
    }

    public Bid findByPlayerAndRound(Long playerId, Long roundId) {
        try {
            return em.createQuery(
                            "SELECT b FROM Bid b WHERE b.playerId = :playerId AND b.roundId = :roundId",
                            Bid.class)
                    .setParameter("playerId", playerId)
                    .setParameter("roundId", roundId)
                    .getSingleResult();
        } catch (Exception e) {
            return null;
        }
    }

    public Bid save(Bid bid) {
        if (bid.getId() == null) {
            em.persist(bid);
            return bid;
        } else {
            return em.merge(bid);
        }
    }

    public boolean delete(Long id) {
        Bid bid = findById(id);
        if (bid != null) {
            em.remove(bid);
            return true;
        }
        return false;
    }

    // --- METODOS ESPECIFICOS ---
    public int getTotalBidsForRound(Long roundId) {
        try {
            Long result = em.createQuery(
                            "SELECT SUM(b.bidAmount) FROM Bid b WHERE b.roundId = :roundId",
                            Long.class)
                    .setParameter("roundId", roundId)
                    .getSingleResult();
            return result != null ? result.intValue() : 0;
        } catch (Exception e) {
            return 0;
        }
    }

    public int countBidsInRound(Long roundId) {
        try {
            Long count = em.createQuery(
                            "SELECT COUNT(b) FROM Bid b WHERE b.roundId = :roundId",
                            Long.class)
                    .setParameter("roundId", roundId)
                    .getSingleResult();
            return count != null ? count.intValue() : 0;
        } catch (Exception e) {
            return 0;
        }
    }

    public boolean incrementTricksWon(Long bidId) {
        try {
            Bid bid = findById(bidId);
            if (bid != null) {
                bid.setTricksWon(bid.getTricksWon() + 1);
                save(bid);
                return true;
            }
            return false;
        } catch (Exception e) {
            return false;
        }
    }

    // --- METODOS AÑADIDOS PARA LOS SERVICES ---
    public boolean playerHasBidInRound(Long playerId, Long roundId) {
        try {
            Long count = em.createQuery(
                            "SELECT COUNT(b) FROM Bid b WHERE b.playerId = :playerId AND b.roundId = :roundId",
                            Long.class)
                    .setParameter("playerId", playerId)
                    .setParameter("roundId", roundId)
                    .getSingleResult();
            return count != null && count > 0;
        } catch (Exception e) {
            return false;
        }
    }

    public List<Bid> findByPlayerId(Long playerId) {
        return em.createQuery(
                        "SELECT b FROM Bid b WHERE b.playerId = :playerId ORDER BY b.timestamp",
                        Bid.class)
                .setParameter("playerId", playerId)
                .getResultList();
    }

    public int getTotalBidAmountForRound(Long roundId) {
        try {
            Long result = em.createQuery(
                            "SELECT SUM(b.bidAmount) FROM Bid b WHERE b.roundId = :roundId",
                            Long.class)
                    .setParameter("roundId", roundId)
                    .getSingleResult();
            return result != null ? result.intValue() : 0;
        } catch (Exception e) {
            return 0;
        }
    }

    public Bid getBidByPlayerAndRound(Long playerId, Long roundId) {
        return findByPlayerAndRound(playerId, roundId);
    }

    public List<Bid> getBidsByRoundId(Long roundId) {
        return findByRoundId(roundId);
    }
}