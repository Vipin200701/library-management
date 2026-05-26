package com.vipin.library_management.repository;

import com.vipin.library_management.entity.BorrowRecord;
import com.vipin.library_management.entity.BorrowStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface BorrowRecordRepository extends JpaRepository<BorrowRecord, Long> {

    @Query("SELECT r FROM BorrowRecord r JOIN FETCH r.book JOIN FETCH r.member WHERE r.member.id = :memberId")
    List<BorrowRecord> findByMemberId(@Param("memberId") Long memberId);

    @Query("SELECT r FROM BorrowRecord r JOIN FETCH r.book JOIN FETCH r.member WHERE r.book.id = :bookId")
    List<BorrowRecord> findByBookId(@Param("bookId") Long bookId);

    @Query("SELECT r FROM BorrowRecord r JOIN FETCH r.book JOIN FETCH r.member WHERE r.status = :status")
    List<BorrowRecord> findByStatus(@Param("status") BorrowStatus status);

    @Query("SELECT r FROM BorrowRecord r JOIN FETCH r.book JOIN FETCH r.member WHERE r.member.id = :memberId AND r.status = :status")
    List<BorrowRecord> findByMemberIdAndStatus(@Param("memberId") Long memberId, @Param("status") BorrowStatus status);

    @Query("SELECT r FROM BorrowRecord r JOIN FETCH r.book JOIN FETCH r.member WHERE r.book.id = :bookId AND r.member.id = :memberId AND r.status = :status")
    Optional<BorrowRecord> findByBookIdAndMemberIdAndStatus(@Param("bookId") Long bookId, @Param("memberId") Long memberId, @Param("status") BorrowStatus status);

    @Query("SELECT r FROM BorrowRecord r JOIN FETCH r.book JOIN FETCH r.member WHERE r.dueDate < :today AND r.status = 'BORROWED'")
    List<BorrowRecord> findOverdueRecords(@Param("today") LocalDate today);

    @Query("SELECT r FROM BorrowRecord r JOIN FETCH r.book JOIN FETCH r.member WHERE r.id = :id")
    Optional<BorrowRecord> findByIdWithDetails(@Param("id") Long id);
}