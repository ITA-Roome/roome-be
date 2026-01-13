package com.roome.roome.be.domain.board.repository;

import com.roome.roome.be.domain.board.entity.Board;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface BoardRepository extends JpaRepository<Board, Long> {

    List<Board> findAllByUserIdOrderByCreatedAtDesc(Long userId);

    Page<Board> findAllByUserId(Long userId, Pageable pageable);
}
