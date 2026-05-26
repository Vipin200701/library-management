package com.vipin.library_management.service;

import com.vipin.library_management.dto.BorrowRequestDTO;
import com.vipin.library_management.dto.BorrowResponseDTO;
import com.vipin.library_management.entity.Book;
import com.vipin.library_management.entity.BorrowRecord;
import com.vipin.library_management.entity.BorrowStatus;
import com.vipin.library_management.entity.Member;
import com.vipin.library_management.exception.LibraryException;
import com.vipin.library_management.exception.ResourceNotFoundException;
import com.vipin.library_management.repository.BookRepository;
import com.vipin.library_management.repository.BorrowRecordRepository;
import com.vipin.library_management.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class BorrowService {

    private final BorrowRecordRepository borrowRecordRepository;
    private final BookRepository bookRepository;
    private final MemberRepository memberRepository;

    // ── Issue a book ───────────────────────────────────
    @Transactional
    public BorrowResponseDTO borrowBook(BorrowRequestDTO dto) {

        Member member = memberRepository.findById(dto.getMemberId())
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Member not found with id: " + dto.getMemberId()));

        if (!member.getIsActive()) {
            throw new LibraryException("Member is inactive and cannot borrow books");
        }

        Book book = bookRepository.findById(dto.getBookId())
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Book not found with id: " + dto.getBookId()));

        if (book.getAvailableCopies() <= 0) {
            throw new LibraryException("No copies available for: " + book.getTitle());
        }

        borrowRecordRepository.findByBookIdAndMemberIdAndStatus(
                        dto.getBookId(), dto.getMemberId(), BorrowStatus.BORROWED)
                .ifPresent(r -> { throw new LibraryException(
                        "Member has already borrowed this book"); });

        LocalDate dueDate = (dto.getDueDate() != null)
                ? dto.getDueDate()
                : LocalDate.now().plusDays(14);

        BorrowRecord record = BorrowRecord.builder()
                .book(book)
                .member(member)
                .borrowDate(LocalDate.now())
                .dueDate(dueDate)
                .status(BorrowStatus.BORROWED)
                .build();

        borrowRecordRepository.save(record);

        book.setAvailableCopies(book.getAvailableCopies() - 1);
        bookRepository.save(book);

        return toDTO(record);
    }

    // ── Return a book ──────────────────────────────────
    @Transactional
    public BorrowResponseDTO returnBook(Long borrowRecordId) {

        BorrowRecord record = borrowRecordRepository.findByIdWithDetails(borrowRecordId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Borrow record not found with id: " + borrowRecordId));

        if (record.getStatus() == BorrowStatus.RETURNED) {
            throw new LibraryException("This book has already been returned");
        }

        record.setStatus(BorrowStatus.RETURNED);
        record.setReturnDate(LocalDate.now());
        borrowRecordRepository.save(record);

        Book book = record.getBook();
        book.setAvailableCopies(book.getAvailableCopies() + 1);
        bookRepository.save(book);

        return toDTO(record);
    }

    // ── Get all currently borrowed ─────────────────────
    @Transactional(readOnly = true)
    public List<BorrowResponseDTO> getAllBorrowedBooks() {
        return borrowRecordRepository.findByStatus(BorrowStatus.BORROWED)
                .stream().map(this::toDTO).collect(Collectors.toList());
    }

    // ── Get overdue records ────────────────────────────
    @Transactional(readOnly = true)
    public List<BorrowResponseDTO> getOverdueBooks() {
        return borrowRecordRepository.findOverdueRecords(LocalDate.now())
                .stream().map(this::toDTO).collect(Collectors.toList());
    }

    // ── Member borrow history ──────────────────────────
    @Transactional(readOnly = true)
    public List<BorrowResponseDTO> getMemberHistory(Long memberId) {
        if (!memberRepository.existsById(memberId))
            throw new ResourceNotFoundException("Member not found with id: " + memberId);
        return borrowRecordRepository.findByMemberId(memberId)
                .stream().map(this::toDTO).collect(Collectors.toList());
    }

    // ── Book borrow history ────────────────────────────
    @Transactional(readOnly = true)
    public List<BorrowResponseDTO> getBookHistory(Long bookId) {
        if (!bookRepository.existsById(bookId))
            throw new ResourceNotFoundException("Book not found with id: " + bookId);
        return borrowRecordRepository.findByBookId(bookId)
                .stream().map(this::toDTO).collect(Collectors.toList());
    }

    // ── Single borrow record ───────────────────────────
    @Transactional(readOnly = true)
    public BorrowResponseDTO getBorrowRecord(Long id) {
        BorrowRecord record = borrowRecordRepository.findByIdWithDetails(id)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Borrow record not found with id: " + id));
        return toDTO(record);
    }

    // ── Mark overdue ───────────────────────────────────
    @Transactional
    public int markOverdueRecords() {
        List<BorrowRecord> overdueList =
                borrowRecordRepository.findOverdueRecords(LocalDate.now());
        overdueList.forEach(r -> r.setStatus(BorrowStatus.OVERDUE));
        borrowRecordRepository.saveAll(overdueList);
        return overdueList.size();
    }

    // ── Entity → DTO ───────────────────────────────────
    private BorrowResponseDTO toDTO(BorrowRecord record) {
        return BorrowResponseDTO.builder()
                .id(record.getId())
                .bookId(record.getBook().getId())
                .bookTitle(record.getBook().getTitle())
                .memberId(record.getMember().getId())
                .memberName(record.getMember().getName())
                .borrowDate(record.getBorrowDate())
                .dueDate(record.getDueDate())
                .returnDate(record.getReturnDate())
                .status(record.getStatus().name())
                .createdAt(record.getCreatedAt())
                .build();
    }
}