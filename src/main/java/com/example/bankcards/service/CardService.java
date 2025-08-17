package com.example.bankcards.service;

import com.example.bankcards.dto.card.*;
import com.example.bankcards.entity.Card;
import com.example.bankcards.entity.User;
import com.example.bankcards.entity.enums.CardStatus;
import com.example.bankcards.exception.BadRequestException;
import com.example.bankcards.exception.ForbiddenException;
import com.example.bankcards.exception.NotFoundException;
import com.example.bankcards.repository.CardRepository;
import com.example.bankcards.repository.UserRepository;
import com.example.bankcards.util.CryptoUtil;
import com.example.bankcards.util.Luhn;
import jakarta.persistence.criteria.Predicate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.*;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Objects;

@Service
public class CardService {
    private static final Logger log = LoggerFactory.getLogger(CardService.class);
    private final CardRepository cardRepository;
    private final UserRepository userRepository;
    private final CryptoUtil cryptoUtil;

    public CardService(CardRepository cardRepository,
                       UserRepository userRepository,
                       CryptoUtil cryptoUtil) {
        this.cardRepository = cardRepository;
        this.userRepository = userRepository;
        this.cryptoUtil = cryptoUtil;
    }

public CardResponse createCard(CardCreateRequest req) {
    User owner = userRepository.findById(req.getOwnerId())
            .orElseThrow(() -> new NotFoundException("Владелец не найден"));

    String cardNumber = req.getCardNumber();
    if (cardNumber == null || cardNumber.isBlank()) {
        cardNumber = Luhn.random16(true);
    }

    int luhn = Luhn.checksum(cardNumber);
    log.info("Card {} Luhn checksum = {} (0 == valid)", (Object) cardNumber, (Object) luhn);

    String last4 = cardNumber.substring(cardNumber.length() - 4);
    String encrypted = cryptoUtil.encrypt(cardNumber);

    Card c = new Card();
    c.setOwner(owner);
    c.setNumberEncrypted(encrypted);
    c.setLast4(last4);
    c.setExpiryMonth(req.getExpiryMonth());
    c.setExpiryYear(req.getExpiryYear());
    c.setStatus(req.getStatus() == null ? CardStatus.ACTIVE : req.getStatus());
    c.setBalance(req.getBalance().setScale(2));

    return CardResponse.from(cardRepository.save(c));
}

    public Page<CardResponse> search(Long ownerId, CardStatus status, String last4, Pageable pageable) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        boolean isAdmin = auth.getAuthorities().stream().anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"));
        String currentUsername = auth.getName();

        Specification<Card> spec = (root, query, cb) -> {
            var predicates = new ArrayList<Predicate>();
            if (ownerId != null) {
                predicates.add(cb.equal(root.get("owner").get("id"), ownerId));
            }
            if (status != null) {
                predicates.add(cb.equal(root.get("status"), status));
            }
            if (last4 != null && !last4.isBlank()) {
                predicates.add(cb.equal(root.get("last4"), last4));
            }
            if (!isAdmin) {
                predicates.add(cb.equal(root.get("owner").get("username"), currentUsername));
            }
            return cb.and(predicates.toArray(new Predicate[0]));
        };

        return cardRepository.findAll(spec, pageable).map(CardResponse::from);
    }

    public CardResponse getOne(Long id) {
        Card c = cardRepository.fetchWithOwner(id)
                .orElseThrow(() -> new NotFoundException("Карта не найдена"));
        enforceOwnerOrAdmin(c.getOwner().getUsername());
        return CardResponse.from(c);
    }

    public CardResponse updateCard(Long id, CardUpdateRequest req) {
        Card c = cardRepository.fetchWithOwner(id)
                .orElseThrow(() -> new NotFoundException("Карта не найдена"));
        c.setExpiryMonth(req.getExpiryMonth());
        c.setExpiryYear(req.getExpiryYear());
        return CardResponse.from(cardRepository.save(c));
    }

    public CardResponse updateStatus(Long id, CardStatus status) {
        Card c = cardRepository.fetchWithOwner(id)
                .orElseThrow(() -> new NotFoundException("Карта не найдена"));
        c.setStatus(Objects.requireNonNullElse(status, CardStatus.ACTIVE));
        return CardResponse.from(cardRepository.save(c));
    }

    public CardResponse requestBlock(Long id) {
        Card c = cardRepository.fetchWithOwner(id)
                .orElseThrow(() -> new NotFoundException("Карта не найдена"));
        String current = SecurityContextHolder.getContext().getAuthentication().getName();
        if (!c.getOwner().getUsername().equals(current)) {
            throw new ForbiddenException("Можно запрашивать блокировку только своей карты");
        }
        c.setStatus(CardStatus.BLOCKED);
        return CardResponse.from(cardRepository.save(c));
    }

    public void delete(Long id) {
        if (!cardRepository.existsById(id)) throw new NotFoundException("Карта не найдена");
        cardRepository.deleteById(id);
    }

    public Card getForUpdate(Long id) {
        return cardRepository.findByIdForUpdate(id)
                .orElseThrow(() -> new NotFoundException("Карта не найдена"));
    }

    public void ensureActiveAndNotExpired(Card card) {
        if (card.getEffectiveStatus() != CardStatus.ACTIVE) {
            throw new BadRequestException("Карта недоступна для операций (заблокирована или истек срок)");
        }
    }

    private void enforceOwnerOrAdmin(String ownerUsername) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        boolean isAdmin = auth.getAuthorities().stream().anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"));
        if (!isAdmin && !auth.getName().equals(ownerUsername)) {
            throw new ForbiddenException("Доступ запрещен");
        }
    }
}
