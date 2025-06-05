package com.rucc.campuscard.controller;

import com.rucc.campuscard.common.ApiResponse;
import com.rucc.campuscard.dto.CardDTO;
import com.rucc.campuscard.dto.CreateCardRequest;
import com.rucc.campuscard.entity.Card;
import com.rucc.campuscard.model.dto.*;
import com.rucc.campuscard.util.CardMapper;
import org.springframework.data.domain.Page;
import com.rucc.campuscard.service.CardService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.NoSuchElementException;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/cards")
public class CardController {
    private static final Logger logger = LoggerFactory.getLogger(CardController.class);

    private final CardService cardService;

    public CardController(CardService cardService) {
        this.cardService = cardService;
    }

    @PostMapping
    public ResponseEntity<ApiResponse<CardDTO>> createCard(
            @RequestBody CreateCardRequest request) {
        logger.info("收到创建校园卡请求 - studentId: {}, studentName: {}", request.getStudentId(), request.getStudentName());
        try {
            BigDecimal balance = BigDecimal.ZERO.setScale(2, RoundingMode.HALF_UP);
            if (request.getBalance() != null) {
                try {
                    balance = request.getBalance().setScale(2, RoundingMode.HALF_UP);
                    if (balance.compareTo(BigDecimal.ZERO) < 0) {
                        throw new IllegalArgumentException("余额不能为负数");
                    }
                } catch (ArithmeticException | IllegalArgumentException e) {
                    logger.warn("无效的余额值: {}, 使用默认值0", request.getBalance());
                    balance = BigDecimal.ZERO.setScale(2, RoundingMode.HALF_UP);
                }
            }
            Card card = cardService.createCard(request.getStudentId(), request.getStudentName(), balance);
            CardDTO cardDTO = CardMapper.toDTO(card);
            logger.info("成功创建校园卡 - cardId: {}, studentId: {}", card.getId(), request.getStudentId());
            return ResponseEntity.status(HttpStatus.CREATED)
                    .body(ApiResponse.success(cardDTO));
        } catch (IllegalStateException e) {
            logger.error("创建校园卡失败 - studentId: {}, 错误: {}", request.getStudentId(), e.getMessage());
            return ResponseEntity.badRequest()
                    .body(ApiResponse.error(HttpStatus.BAD_REQUEST.value(), e.getMessage()));
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<CardDTO>> getCard(
            @PathVariable UUID id) {
        try {
            Card card = cardService.getCard(id);
            CardDTO cardDTO = CardMapper.toDTO(card);
            return ResponseEntity.ok(ApiResponse.success(cardDTO));
        } catch (NoSuchElementException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(ApiResponse.error(HttpStatus.NOT_FOUND.value(), "校园卡不存在"));
        }
    }

    @GetMapping("/student/{studentId}")
    public ResponseEntity<ApiResponse<CardDTO>> getCardByStudentId(
            @PathVariable String studentId) {
        try {
            Card card = cardService.getCardByStudentId(studentId);
            CardDTO cardDTO = CardMapper.toDTO(card);
            return ResponseEntity.ok(ApiResponse.success(cardDTO));
        } catch (NoSuchElementException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(ApiResponse.error(HttpStatus.NOT_FOUND.value(), "未找到该学生的校园卡"));
        }
    }

    @PutMapping("/{id}/balance")
    public ResponseEntity<ApiResponse<CardDTO>> updateBalance(
            @PathVariable UUID id,
            @RequestBody UpdateBalanceDTO request) {
        try {
            Card card = cardService.updateBalance(id, request.getAmount());
            CardDTO cardDTO = CardMapper.toDTO(card);
            return ResponseEntity.ok(ApiResponse.success(cardDTO));
        } catch (NoSuchElementException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(ApiResponse.error(HttpStatus.NOT_FOUND.value(), "校园卡不存在"));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest()
                    .body(ApiResponse.error(HttpStatus.BAD_REQUEST.value(), e.getMessage()));
        }
    }

    @PutMapping("/student/{studentId}/balance")
    public ResponseEntity<ApiResponse<CardDTO>> updateBalanceByStudentId(
            @PathVariable String studentId,
            @RequestBody UpdateBalanceDTO request) {
        try {
            Card card = cardService.updateBalanceByStudentId(studentId, request.getAmount());
            CardDTO cardDTO = CardMapper.toDTO(card);
            return ResponseEntity.ok(ApiResponse.success(cardDTO));
        } catch (NoSuchElementException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(ApiResponse.error(HttpStatus.NOT_FOUND.value(), "未找到该学生的校园卡"));
        } catch (IllegalArgumentException e) {
            logger.warn("更新余额失败 - studentId: {}, amount: {}, 错误: {}", studentId, request.getAmount(), e.getMessage());
            return ResponseEntity.badRequest()
                    .body(ApiResponse.error(HttpStatus.BAD_REQUEST.value(), e.getMessage()));
        }
    }

    @PutMapping("/{id}/name")
    public ResponseEntity<ApiResponse<CardDTO>> updateStudentName(
            @PathVariable UUID id,
            @RequestParam String studentName) {
        try {
            Card card = cardService.updateStudentName(id, studentName);
            CardDTO cardDTO = CardMapper.toDTO(card);
            return ResponseEntity.ok(ApiResponse.success(cardDTO));
        } catch (NoSuchElementException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(ApiResponse.error(HttpStatus.NOT_FOUND.value(), "校园卡不存在"));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest()
                    .body(ApiResponse.error(HttpStatus.BAD_REQUEST.value(), e.getMessage()));
        }
    }

    @PutMapping("/student/{studentId}/name")
    public ResponseEntity<ApiResponse<CardDTO>> updateStudentNameByStudentId(
            @PathVariable String studentId,
            @RequestParam String studentName) {
        try {
            Card card = cardService.updateStudentNameByStudentId(studentId, studentName);
            CardDTO cardDTO = CardMapper.toDTO(card);
            return ResponseEntity.ok(ApiResponse.success(cardDTO));
        } catch (NoSuchElementException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(ApiResponse.error(HttpStatus.NOT_FOUND.value(), "未找到该学生的校园卡"));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest()
                    .body(ApiResponse.error(HttpStatus.BAD_REQUEST.value(), e.getMessage()));
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> deleteCard(
            @PathVariable UUID id) {
        try {
            cardService.deleteCard(id);
            return ResponseEntity.ok(ApiResponse.success(null));
        } catch (NoSuchElementException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(ApiResponse.error(HttpStatus.NOT_FOUND.value(), "校园卡不存在"));
        }
    }

    @DeleteMapping("/student/{studentId}")
    public ResponseEntity<ApiResponse<Void>> deleteCardByStudentId(
            @PathVariable String studentId) {
        try {
            cardService.deleteCardByStudentId(studentId);
            return ResponseEntity.ok(ApiResponse.success(null));
        } catch (NoSuchElementException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(ApiResponse.error(HttpStatus.NOT_FOUND.value(), "未找到该学生的校园卡"));
        }
    }

    @PostMapping("/search")
    public ResponseEntity<ApiResponse<Page<CardDTO>>> searchCards(
            @RequestBody CardSearchRequest request) {
        Page<Card> cards = cardService.getAllCards(
            request.getPage(),
            request.getSize(),
            request.getStudentId(),
            request.getStudentName());
        Page<CardDTO> cardDTOs = cards.map(CardMapper::toDTO);
        return ResponseEntity.ok(ApiResponse.success(cardDTOs));
    }
}
