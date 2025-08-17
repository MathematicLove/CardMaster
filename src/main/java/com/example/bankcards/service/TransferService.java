package com.example.bankcards.service;

import com.example.bankcards.dto.transfer.TransferRequest;
import com.example.bankcards.dto.transfer.TransferResponse;
import com.example.bankcards.entity.Card;
import com.example.bankcards.entity.Transfer;
import com.example.bankcards.exception.BadRequestException;
import com.example.bankcards.exception.ForbiddenException;
import com.example.bankcards.repository.TransferRepository;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;

@Service
public class TransferService {

    private final CardService cardService;
    private final TransferRepository transferRepository;

    public TransferService(CardService cardService, TransferRepository transferRepository) {
        this.cardService = cardService;
        this.transferRepository = transferRepository;
    }

    @Transactional
    public TransferResponse transferBetweenOwnCards(TransferRequest req) {
        if (req.getFromCardId().equals(req.getToCardId())) {
            throw new BadRequestException("Нельзя переводить на ту же карту");
        }
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String username = auth.getName();

        Card from = cardService.getForUpdate(req.getFromCardId());
        Card to = cardService.getForUpdate(req.getToCardId());

        if (!from.getOwner().getUsername().equals(username) || !to.getOwner().getUsername().equals(username)) {
            throw new ForbiddenException("Переводы доступны только между своими картами");
        }

        cardService.ensureActiveAndNotExpired(from);
        cardService.ensureActiveAndNotExpired(to);

        BigDecimal amount = req.getAmount().setScale(2);
        if (from.getBalance().compareTo(amount) < 0) {
            throw new BadRequestException("Недостаточно средств");
        }

        from.setBalance(from.getBalance().subtract(amount));
        to.setBalance(to.getBalance().add(amount));

        Transfer t = new Transfer();
        t.setFromCard(from);
        t.setToCard(to);
        t.setAmount(amount);
        t.setSuccess(true);

        Transfer saved = transferRepository.save(t);
        return TransferResponse.from(saved);
    }
}
