package com.rucc.campuscard.controller;

import com.rucc.campuscard.common.ApiResponse;
import com.rucc.campuscard.dto.CardDTO;
import com.rucc.campuscard.dto.CreateCardRequest;
import com.rucc.campuscard.entity.Card;
import com.rucc.campuscard.model.dto.*;
import com.rucc.campuscard.util.CardMapper;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
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
@Tag(name = "校园卡管理", description = "校园卡相关操作接口")
public class CardController {
    private static final Logger logger = LoggerFactory.getLogger(CardController.class);

    private final CardService cardService;

    public CardController(CardService cardService) {
        this.cardService = cardService;
    }

    @PostMapping
    @Operation(summary = "创建校园卡", description = "为指定学生创建一张新的校园卡")
    @io.swagger.v3.oas.annotations.responses.ApiResponse(
        responseCode = "201", description = "校园卡创建成功",
        content = @io.swagger.v3.oas.annotations.media.Content(
            mediaType = "application/json",
            schema = @io.swagger.v3.oas.annotations.media.Schema(implementation = ApiResponse.class),
            examples = @io.swagger.v3.oas.annotations.media.ExampleObject(
                value = "{\"code\": 201, \"message\": \"校园卡创建成功\", \"data\": {\"id\": \"3fa85f64-5717-4562-b3fc-2c963f66afa6\", \"studentId\": \"20230001\", \"studentName\": \"张三\", \"balance\": 100.00}}"
            )
        )
    )
    @io.swagger.v3.oas.annotations.responses.ApiResponse(
        responseCode = "400", description = "无效的请求参数",
        content = @io.swagger.v3.oas.annotations.media.Content(
            mediaType = "application/json",
            schema = @io.swagger.v3.oas.annotations.media.Schema(implementation = ApiResponse.class),
            examples = @io.swagger.v3.oas.annotations.media.ExampleObject(
                value = "{\"code\": 400, \"message\": \"学号格式不正确，必须是至少 3 位数字\", \"data\": null}"
            )
        )
    )
    public ResponseEntity<ApiResponse<CardDTO>> createCard(
            @Parameter(description = "创建校园卡请求参数", required = true)
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
    @Operation(summary = "获取校园卡信息", description = "根据校园卡ID获取校园卡详细信息")
    @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "200", description = "获取成功",
            content = @io.swagger.v3.oas.annotations.media.Content(
                    mediaType = "application/json",
                    schema = @io.swagger.v3.oas.annotations.media.Schema(implementation = ApiResponse.class),
                    examples = @io.swagger.v3.oas.annotations.media.ExampleObject(
                            value = "{\"code\": 200, \"message\": \"success\", \"data\": {\"id\": \"3fa85f64-5717-4562-b3fc-2c963f66afa6\", \"studentId\": \"20230001\", \"studentName\": \"张三\", \"balance\": 100.00}}"
                    )
            )
    )
    @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "404", description = "校园卡不存在",
            content = @io.swagger.v3.oas.annotations.media.Content(
                    mediaType = "application/json",
                    schema = @io.swagger.v3.oas.annotations.media.Schema(implementation = ApiResponse.class),
                    examples = @io.swagger.v3.oas.annotations.media.ExampleObject(
                            value = "{\"code\": 404, \"message\": \"校园卡不存在\", \"data\": null}"
                    )
            )
    )
    public ResponseEntity<ApiResponse<CardDTO>> getCard(
            @Parameter(description = "校园卡ID", required = true)
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
    @Operation(summary = "获取校园卡信息", description = "根据学生ID获取校园卡详细信息")
    @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "200", description = "获取成功",
            content = @io.swagger.v3.oas.annotations.media.Content(
                    mediaType = "application/json",
                    schema = @io.swagger.v3.oas.annotations.media.Schema(implementation = ApiResponse.class),
                    examples = @io.swagger.v3.oas.annotations.media.ExampleObject(
                            value = "{\"code\": 200, \"message\": \"success\", \"data\": {\"id\": \"3fa85f64-5717-4562-b3fc-2c963f66afa6\", \"studentId\": \"20230001\", \"studentName\": \"张三\", \"balance\": 100.00}}"
                    )
            )
    )
    @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "404", description = "未找到该学生的校园卡",
            content = @io.swagger.v3.oas.annotations.media.Content(
                    mediaType = "application/json",
                    schema = @io.swagger.v3.oas.annotations.media.Schema(implementation = ApiResponse.class),
                    examples = @io.swagger.v3.oas.annotations.media.ExampleObject(
                            value = "{\"code\": 404, \"message\": \"未找到该学生的校园卡\", \"data\": null}"
                    )
            )
    )
    public ResponseEntity<ApiResponse<CardDTO>> getCardByStudentId(
            @Parameter(description = "学生ID", required = true)
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
    @Operation(summary = "更新校园卡余额", description = "根据校园卡ID更新余额")
    @io.swagger.v3.oas.annotations.responses.ApiResponse(
        responseCode = "200", description = "余额更新成功",
        content = @io.swagger.v3.oas.annotations.media.Content(
            mediaType = "application/json",
            schema = @io.swagger.v3.oas.annotations.media.Schema(implementation = ApiResponse.class)
        )
    )
    @io.swagger.v3.oas.annotations.responses.ApiResponse(
        responseCode = "404", description = "校园卡不存在"
    )
    @io.swagger.v3.oas.annotations.responses.ApiResponse(
        responseCode = "400", description = "无效的金额参数"
    )
    public ResponseEntity<ApiResponse<CardDTO>> updateBalance(
            @Parameter(description = "校园卡ID", required = true)
            @PathVariable UUID id,
            @Parameter(description = "余额更新请求", required = true)
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
    @Operation(summary = "更新校园卡余额(按学号)", description = "根据学生学号更新校园卡余额")
    @io.swagger.v3.oas.annotations.responses.ApiResponse(
        responseCode = "200", description = "余额更新成功",
        content = @io.swagger.v3.oas.annotations.media.Content(
            mediaType = "application/json",
            schema = @io.swagger.v3.oas.annotations.media.Schema(implementation = ApiResponse.class)
        )
    )
    @io.swagger.v3.oas.annotations.responses.ApiResponse(
        responseCode = "404", description = "未找到该学生的校园卡"
    )
    @io.swagger.v3.oas.annotations.responses.ApiResponse(
        responseCode = "400", description = "无效的金额参数"
    )
    public ResponseEntity<ApiResponse<CardDTO>> updateBalanceByStudentId(
            @Parameter(description = "学生学号", required = true)
            @PathVariable String studentId,
            @Parameter(description = "余额更新请求", required = true)
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
    @Operation(summary = "更新学生姓名", description = "根据校园卡ID更新学生姓名")
    @io.swagger.v3.oas.annotations.responses.ApiResponse(
        responseCode = "200", description = "姓名更新成功",
        content = @io.swagger.v3.oas.annotations.media.Content(
            mediaType = "application/json",
            schema = @io.swagger.v3.oas.annotations.media.Schema(implementation = ApiResponse.class)
        )
    )
    @io.swagger.v3.oas.annotations.responses.ApiResponse(
        responseCode = "404", description = "校园卡不存在"
    )
    @io.swagger.v3.oas.annotations.responses.ApiResponse(
        responseCode = "400", description = "无效的姓名参数"
    )
    public ResponseEntity<ApiResponse<CardDTO>> updateStudentName(
            @Parameter(description = "校园卡ID", required = true)
            @PathVariable UUID id,
            @Parameter(description = "新的学生姓名", required = true)
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
    @Operation(summary = "更新学生姓名(按学号)", description = "根据学生学号更新学生姓名")
    @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "200", description = "姓名更新成功",
            content = @io.swagger.v3.oas.annotations.media.Content(
                    mediaType = "application/json",
                    schema = @io.swagger.v3.oas.annotations.media.Schema(implementation = ApiResponse.class)
            )
    )
    @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "404", description = "未找到该学生的校园卡"
    )
    @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "400", description = "无效的姓名参数"
    )
    public ResponseEntity<ApiResponse<CardDTO>> updateStudentNameByStudentId(
            @Parameter(description = "学生学号", required = true)
            @PathVariable String studentId,
            @Parameter(description = "新的学生姓名", required = true)
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
    @Operation(summary = "删除校园卡", description = "根据校园卡ID删除记录")
    @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "204", description = "删除成功"
    )
    @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "404", description = "校园卡不存在"
    )
    public ResponseEntity<ApiResponse<Void>> deleteCard(
            @Parameter(description = "校园卡ID", required = true)
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
    @Operation(summary = "删除校园卡(按学号)", description = "根据学生学号删除校园卡记录")
    @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "204", description = "删除成功"
    )
    @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "404", description = "未找到该学生的校园卡"
    )
    public ResponseEntity<ApiResponse<Void>> deleteCardByStudentId(
            @Parameter(description = "学生学号", required = true)
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
    @Operation(summary = "搜索校园卡", description = "根据条件分页搜索校园卡记录")
    @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "200", description = "搜索成功",
            content = @io.swagger.v3.oas.annotations.media.Content(
                    mediaType = "application/json",
                    schema = @io.swagger.v3.oas.annotations.media.Schema(implementation = ApiResponse.class)
            )
    )
    public ResponseEntity<ApiResponse<Page<CardDTO>>> searchCards(
            @Parameter(description = "搜索条件", required = true)
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
