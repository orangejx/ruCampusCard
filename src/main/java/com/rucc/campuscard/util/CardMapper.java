package com.rucc.campuscard.util;

import com.rucc.campuscard.dto.CardDTO;
import com.rucc.campuscard.entity.Card;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 卡片实体与DTO之间的转换工具类
 */
public class CardMapper {

    /**
     * 将Card实体转换为CardDTO
     *
     * @param card Card实体
     * @return CardDTO对象
     */
    public static CardDTO toDTO(Card card) {
        if (card == null) {
            return null;
        }

        CardDTO dto = new CardDTO();
        dto.setId(card.getId());
        dto.setStudentId(card.getStudentId());
        dto.setStudentName(card.getStudentName());
        dto.setStatus(card.getStatus().toString());
        dto.setBalance(card.getBalance());
        dto.setCreatedAt(card.getCreatedAt());
        dto.setUpdatedAt(card.getUpdatedAt());

        return dto;
    }

    /**
     * 将Card实体列表转换为CardDTO列表
     *
     * @param cards Card实体列表
     * @return CardDTO对象列表
     */
    public static List<CardDTO> toDTOList(List<Card> cards) {
        if (cards == null) {
            return null;
        }

        return cards.stream()
                .map(CardMapper::toDTO)
                .collect(Collectors.toList());
    }
}
