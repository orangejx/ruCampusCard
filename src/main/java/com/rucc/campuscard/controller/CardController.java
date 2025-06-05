package com.rucc.campuscard.controller;

import com.rucc.campuscard.common.ApiResponse;
import com.rucc.campuscard.dto.CardDTO;
import com.rucc.campuscard.dto.CreateCardRequest;
import com.rucc.campuscard.entity.Card;
import com.rucc.campuscard.model.dto.*;
import com.rucc.campuscard.util.CardMapper;
import org.springframework.data.domain.Page;
import com.rucc.campuscard.service.CardService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
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
@Tag(name = "校园卡管理", description = "提供校园卡的创建、查询、更新、删除等完整的管理功能")
@io.swagger.v3.oas.annotations.security.SecurityRequirement(name = "bearerAuth")
public class CardController {
    private static final Logger logger = LoggerFactory.getLogger(CardController.class);

    private final CardService cardService;

    public CardController(CardService cardService) {
        this.cardService = cardService;
    }

    @PostMapping
    @Operation(summary = "创建新校园卡", description = "为指定学生创建一个新的校园卡，每个学生只能拥有一张校园卡")
    @io.swagger.v3.oas.annotations.responses.ApiResponses(value = {
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "201", description = "校园卡创建成功",
            content = @io.swagger.v3.oas.annotations.media.Content(schema = @io.swagger.v3.oas.annotations.media.Schema(implementation = com.rucc.campuscard.common.ApiResponse.class))),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "请求参数错误，例如：学生已有校园卡、余额为负数等",
            content = @io.swagger.v3.oas.annotations.media.Content(schema = @io.swagger.v3.oas.annotations.media.Schema(implementation = com.rucc.campuscard.common.ApiResponse.class)))
    })
    public ResponseEntity<ApiResponse<CardDTO>> createCard(
            @RequestBody @Parameter(description = "创建校园卡请求") CreateCardRequest request) {
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
    @Operation(summary = "获取校园卡信息", description = "根据卡片ID获取校园卡详细信息")
    @io.swagger.v3.oas.annotations.responses.ApiResponses(value = {
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "成功获取校园卡信息",
            content = @io.swagger.v3.oas.annotations.media.Content(schema = @io.swagger.v3.oas.annotations.media.Schema(implementation = com.rucc.campuscard.common.ApiResponse.class))),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "校园卡不存在",
            content = @io.swagger.v3.oas.annotations.media.Content(schema = @io.swagger.v3.oas.annotations.media.Schema(implementation = com.rucc.campuscard.common.ApiResponse.class)))
    })
    public ResponseEntity<ApiResponse<CardDTO>> getCard(
            @PathVariable @Parameter(description = "校园卡ID") UUID id) {
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
    @Operation(summary = "获取学生的校园卡信息", description = "根据学生ID获取其校园卡详细信息")
    @io.swagger.v3.oas.annotations.responses.ApiResponses(value = {
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "成功获取校园卡信息",
            content = @io.swagger.v3.oas.annotations.media.Content(schema = @io.swagger.v3.oas.annotations.media.Schema(implementation = com.rucc.campuscard.common.ApiResponse.class))),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "未找到该学生的校园卡",
            content = @io.swagger.v3.oas.annotations.media.Content(schema = @io.swagger.v3.oas.annotations.media.Schema(implementation = com.rucc.campuscard.common.ApiResponse.class)))
    })
    public ResponseEntity<ApiResponse<CardDTO>> getCardByStudentId(
            @PathVariable @Parameter(description = "学生ID") String studentId) {
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
    @Operation(summary = "更新卡余额", description = "更新指定校园卡的余额，正数表示充值，负数表示消费")
    @io.swagger.v3.oas.annotations.responses.ApiResponses(value = {
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "余额更新成功",
            content = @io.swagger.v3.oas.annotations.media.Content(schema = @io.swagger.v3.oas.annotations.media.Schema(implementation = com.rucc.campuscard.common.ApiResponse.class))),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "余额更新失败，例如：余额不足等",
            content = @io.swagger.v3.oas.annotations.media.Content(schema = @io.swagger.v3.oas.annotations.media.Schema(implementation = com.rucc.campuscard.common.ApiResponse.class))),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "校园卡不存在",
            content = @io.swagger.v3.oas.annotations.media.Content(schema = @io.swagger.v3.oas.annotations.media.Schema(implementation = com.rucc.campuscard.common.ApiResponse.class)))
    })
    public ResponseEntity<ApiResponse<CardDTO>> updateBalance(
            @PathVariable @Parameter(description = "校园卡ID") UUID id,
            @RequestBody @Parameter(description = "金额变化") UpdateBalanceDTO request) {
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
    @Operation(summary = "根据学生ID更新余额", description = "根据学生ID更新校园卡余额")
    @io.swagger.v3.oas.annotations.responses.ApiResponses(value = {
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "余额更新成功",
            content = @io.swagger.v3.oas.annotations.media.Content(schema = @io.swagger.v3.oas.annotations.media.Schema(implementation = com.rucc.campuscard.common.ApiResponse.class))),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "余额更新失败，例如：余额不足等",
            content = @io.swagger.v3.oas.annotations.media.Content(schema = @io.swagger.v3.oas.annotations.media.Schema(implementation = com.rucc.campuscard.common.ApiResponse.class))),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "未找到该学生的校园卡",
            content = @io.swagger.v3.oas.annotations.media.Content(schema = @io.swagger.v3.oas.annotations.media.Schema(implementation = com.rucc.campuscard.common.ApiResponse.class)))
    })
    public ResponseEntity<ApiResponse<CardDTO>> updateBalanceByStudentId(
            @PathVariable @Parameter(description = "学生ID") String studentId,
            @RequestBody @Parameter(description = "金额变化") UpdateBalanceDTO request) {
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
    @Operation(summary = "更新学生姓名", description = "更新指定校园卡关联的学生姓名")
    public ResponseEntity<ApiResponse<CardDTO>> updateStudentName(
            @PathVariable @Parameter(description = "校园卡ID") UUID id,
            @RequestParam @Parameter(description = "新学生姓名") String studentName) {
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
    @Operation(summary = "根据学生ID更新姓名", description = "根据学生ID更新学生姓名")
    public ResponseEntity<ApiResponse<CardDTO>> updateStudentNameByStudentId(
            @PathVariable @Parameter(description = "学生ID") String studentId,
            @RequestParam @Parameter(description = "新学生姓名") String studentName) {
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
    @Operation(summary = "删除校园卡", description = "通过卡片ID删除校园卡")
    public ResponseEntity<ApiResponse<Void>> deleteCard(
            @PathVariable @Parameter(description = "校园卡ID") UUID id) {
        try {
            cardService.deleteCard(id);
            return ResponseEntity.ok(ApiResponse.success(null));
        } catch (NoSuchElementException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(ApiResponse.error(HttpStatus.NOT_FOUND.value(), "校园卡不存在"));
        }
    }

    @DeleteMapping("/student/{studentId}")
    @Operation(summary = "删除学生的校园卡", description = "通过学生ID删除校园卡")
    public ResponseEntity<ApiResponse<Void>> deleteCardByStudentId(
            @PathVariable @Parameter(description = "学生ID") String studentId) {
        try {
            cardService.deleteCardByStudentId(studentId);
            return ResponseEntity.ok(ApiResponse.success(null));
        } catch (NoSuchElementException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(ApiResponse.error(HttpStatus.NOT_FOUND.value(), "未找到该学生的校园卡"));
        }
    }

    @PostMapping("/search")
    @Operation(summary = "搜索校园卡", description = "搜索校园卡，支持分页和过滤")
    public ResponseEntity<ApiResponse<Page<CardDTO>>> searchCards(
            @RequestBody @Parameter(description = "搜索条件") CardSearchRequest request) {
        Page<Card> cards = cardService.getAllCards(
            request.getPage(),
            request.getSize(),
            request.getStudentId(),
            request.getStudentName());
        Page<CardDTO> cardDTOs = cards.map(CardMapper::toDTO);
        return ResponseEntity.ok(ApiResponse.success(cardDTOs));
    }
}
