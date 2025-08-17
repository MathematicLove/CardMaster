package com.example.bankcards.controller;

import com.example.bankcards.dto.card.CardCreateRequest;
import com.example.bankcards.dto.card.CardResponse;
import com.example.bankcards.dto.card.CardStatusUpdateRequest;
import com.example.bankcards.dto.card.CardUpdateRequest;
import com.example.bankcards.entity.enums.CardStatus;
import com.example.bankcards.service.CardService;
import jakarta.validation.Valid;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/cards")
public class CardController {

    private final CardService cardService;
    public CardController(CardService cardService) {
        this.cardService = cardService;
    }

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<CardResponse> create(@Valid @RequestBody CardCreateRequest req) {
        return ResponseEntity.ok(cardService.createCard(req));
    }

    @GetMapping
    public Page<CardResponse> list(
            @RequestParam(required = false) Long ownerId,
            @RequestParam(required = false) CardStatus status,
            @RequestParam(required = false) String last4,
            @ParameterObject
            @PageableDefault(size = 20, sort = "createdAt", direction = Sort.Direction.DESC)
            Pageable pageable
    ) {
        return cardService.search(ownerId, status, last4, pageable);
    }

    @GetMapping("/{id}")
    public ResponseEntity<CardResponse> get(@PathVariable Long id) {
        return ResponseEntity.ok(cardService.getOne(id));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<CardResponse> update(@PathVariable Long id,
                                               @Valid @RequestBody CardUpdateRequest req) {
        return ResponseEntity.ok(cardService.updateCard(id, req));
    }

    @PatchMapping("/{id}/status")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<CardResponse> updateStatus(@PathVariable Long id,
                                                     @Valid @RequestBody CardStatusUpdateRequest req) {
        return ResponseEntity.ok(cardService.updateStatus(id, req.getStatus()));
    }

    @PostMapping("/{id}/request-block")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<CardResponse> requestBlock(@PathVariable Long id) {
        return ResponseEntity.ok(cardService.requestBlock(id));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        cardService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
