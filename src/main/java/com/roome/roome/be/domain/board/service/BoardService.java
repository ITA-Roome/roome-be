package com.roome.roome.be.domain.board.service;

import com.roome.roome.be.common.exception.GeneralException;
import com.roome.roome.be.common.status.ErrorStatus;
import com.roome.roome.be.domain.board.dto.response.BoardListResponse;
import com.roome.roome.be.domain.board.entity.Board;
import com.roome.roome.be.domain.board.entity.BoardProduct;
import com.roome.roome.be.domain.board.entity.BoardReference;
import com.roome.roome.be.domain.board.repository.BoardRepository;
import com.roome.roome.be.domain.chat.dto.response.ChatProductScenarioResponse;
import com.roome.roome.be.domain.chat.dto.response.ChatReferenceScenarioResponse;
import com.roome.roome.be.domain.chat.dto.response.ProductSummaryResponse;
import com.roome.roome.be.domain.chat.enums.ChatMode;
import com.roome.roome.be.domain.chat.model.ChatSession;
import com.roome.roome.be.domain.chat.repository.ChatSessionRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
@Slf4j
public class BoardService {

    private final ChatSessionRepository chatSessionRepository;
    private final BoardRepository boardRepository;

    public Long saveBoardFromSession(String sessionId, Long currentUserId) {

        ChatSession session = chatSessionRepository.find(sessionId)
                .orElseThrow(() -> new GeneralException(ErrorStatus.SESSION_NOT_FOUND));

        if (!session.userId().equals(currentUserId)) {
            throw new GeneralException(ErrorStatus.SESSION_USER_MISMATCH);
        }

        if (session.mode() == ChatMode.PRODUCT) {
            return saveProductBoard(session);
        } else if (session.mode() == ChatMode.REFERENCE) {
            return saveReferenceBoard(session);
        } else {
            throw new GeneralException(ErrorStatus.RESULT_NOT_FOUND);
        }
    }

    //  인테리어 레퍼런스 저장 로직
    private Long saveReferenceBoard(ChatSession session) {
        ChatReferenceScenarioResponse result = session.lastReferenceResult();

        if (result == null) {
            throw new GeneralException(ErrorStatus.RESULT_NOT_FOUND);
        }

        String title = (result.title() != null && !result.title().isBlank())
                ? result.title()
                : generateDefaultTitle("인테리어 무드 추천");

        String fullDescription = buildDescription(result.moodDescription(), result.summary());

        String keywords = (result.moodKeywords() != null)
                ? String.join(", ", result.moodKeywords())
                : "";

        Board board = Board.builder()
                .userId(session.userId())
                .title(title)
                .category(ChatMode.REFERENCE)
                .description(fullDescription)
                .keywords(keywords)
                .build();

        List<Long> ids = result.referenceIdList();
        List<String> urls = result.imageUrlList();

        if (ids != null && urls != null && ids.size() == urls.size()) {
            for (int i = 0; i < ids.size(); i++) {
                BoardReference ref = BoardReference.builder()
                        .referenceId(ids.get(i))
                        .imageUrl(urls.get(i))
                        .build();
                board.addReference(ref);
            }
        } else {
            log.warn("레퍼런스 ID 목록과 이미지 URL 목록의 개수가 일치하지 않아 일부 데이터가 누락될 수 있습니다.");
        }

        return boardRepository.save(board).getId();
    }

    // 제품 추천 저장 로직
    private Long saveProductBoard(ChatSession session) {
        ChatProductScenarioResponse result = session.lastProductResult();

        if (result == null || result.products() == null) {
            throw new GeneralException(ErrorStatus.RESULT_NOT_FOUND);
        }

        String title = generateDefaultTitle("맞춤 제품 추천");

        String summaryDescription = result.products().stream()
                .findFirst()
                .map(p -> {
                    String productName = (p.name() != null) ? p.name() : "제품";
                    String reason = (p.reason() != null) ? p.reason() : "추천 사유가 있습니다.";
                    return "메인 추천: " + productName + "\n\n[AI 추천 사유]\n" + reason;
                })
                .orElse("고객님의 취향에 맞는 가구들을 찾아보았습니다.");

        String keywords = result.products().stream()
                .map(ProductSummaryResponse::recommendedPlace)
                .filter(place -> place != null && !place.isBlank())
                .flatMap(place -> Arrays.stream(place.split("/")))
                .map(String::trim)
                .distinct()
                .collect(Collectors.joining(", "));

        if (keywords.isBlank()) {
            keywords = "인테리어 가구";
        }

        Board board = Board.builder()
                .userId(session.userId())
                .title(title)
                .category(ChatMode.PRODUCT)
                .description(summaryDescription)
                .keywords(keywords)
                .build();

        for (ProductSummaryResponse p : result.products()) {
            BoardProduct product = BoardProduct.builder()
                    .productId(p.productId())
                    .name(p.name())
                    .imageUrl(p.imageUrl())
                    .reason(p.reason())
                    .advantage(p.advantage())
                    .mood(p.mood())
                    .recommendedPlace(p.recommendedPlace())
                    .build();
            board.addProduct(product);
        }

        return boardRepository.save(board).getId();
    }

    private String buildDescription(String moodDesc, String summary) {
        StringBuilder sb = new StringBuilder();
        if (moodDesc != null && !moodDesc.isBlank()) {
            sb.append(moodDesc).append("\n\n");
        }
        if (summary != null && !summary.isBlank()) {
            sb.append("[AI 요약]\n").append(summary);
        }
        return sb.toString();
    }

    private String generateDefaultTitle(String suffix) {
        return LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd ")) + suffix;
    }

    public Page<BoardListResponse> getBoardList(Long userId, Pageable pageable) {
        Page<Board> boardPage = boardRepository.findAllByUserId(userId, pageable);

        return boardPage.map(BoardListResponse::from);
    }
}