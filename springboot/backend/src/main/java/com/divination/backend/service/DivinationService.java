package com.divination.backend.service;

import com.divination.backend.dto.DivinationDTO;
import com.divination.backend.model.AvaloPoem;
import com.divination.backend.model.DivinationHistory;
import com.divination.backend.model.DivinationNote;
import com.divination.backend.model.ManjuPoem;
import com.divination.backend.repository.AvaloPoemRepository;
import com.divination.backend.repository.DivinationHistoryRepository;
import com.divination.backend.repository.DivinationNoteRepository;
import com.divination.backend.repository.ManjuPoemRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class DivinationService {

    private final DivinationNoteRepository noteRepository;
    private final ManjuPoemRepository manjuPoemRepository;
    private final AvaloPoemRepository avaloPoemRepository;
    private final DivinationHistoryRepository historyRepository;
    
    // 文殊占卜 - 支援訪客模式
    public DivinationDTO.DivinationResponse doManjuDivination(Long userId, String question) {
        ManjuPoem poem = manjuPoemRepository.findRandomPoem();
        
        if (poem == null) {
            throw new RuntimeException("籤詩資料庫為空");
        }
        
        Long historyId = null;
        LocalDateTime createdAt = LocalDateTime.now();
        
        // 只有登入用戶才儲存歷史記錄
        if (userId != null) {
            DivinationHistory history = new DivinationHistory();
            history.setUserId(userId);
            history.setType("manju");
            history.setQuestion(question);
            history.setPoemId(poem.getId());
            history.setTibetanCode(poem.getTibetanCode());
            history.setTitle(poem.getTitle());
            history.setSummary(poem.getSummary());
            
            history = historyRepository.save(history);
            historyId = history.getId();
            createdAt = history.getCreatedAt();
        }
        
        DivinationDTO.PoemData poemData = new DivinationDTO.PoemData(
            poem.getId(),
            null,
            poem.getTibetanCode(),
            poem.getDirection(),
            poem.getTitle(),
            poem.getSummary(),
            poem.getFamilyLife(),
            poem.getStrategy(),
            poem.getFortune(),
            poem.getEnemy(),
            poem.getTravel(),
            poem.getIllness(),
            poem.getPractice(),
            poem.getLostItem(),
            poem.getVisitor(),
            poem.getOtherMatters(),
            poem.getAdvice()
        );
        
        return new DivinationDTO.DivinationResponse(
            historyId,
            "manju",
            question,
            poemData,
            createdAt
        );
    }
    
    // 觀音占卜 - 支援訪客模式
    public DivinationDTO.DivinationResponse doAvaloDivination(Long userId, String question) {
        AvaloPoem poem = avaloPoemRepository.findRandomPoem();
        
        if (poem == null) {
            throw new RuntimeException("籤詩資料庫為空");
        }
        
        Long historyId = null;
        LocalDateTime createdAt = LocalDateTime.now();
        
        // 只有登入用戶才儲存歷史記錄
        if (userId != null) {
            DivinationHistory history = new DivinationHistory();
            history.setUserId(userId);
            history.setType("avalo");
            history.setQuestion(question);
            history.setPoemId(poem.getId());
            history.setTibetanCode(poem.getTibetanCode());
            history.setTitle(poem.getTitle());
            history.setSummary(poem.getSummary());
            
            history = historyRepository.save(history);
            historyId = history.getId();
            createdAt = history.getCreatedAt();
        }
        
        DivinationDTO.PoemData poemData = new DivinationDTO.PoemData(
            poem.getId(),
            null,
            poem.getTibetanCode(),
            null,
            poem.getTitle(),
            poem.getSummary(),
            null, null, null, null, null, null, null, null, null, null, null
        );
        
        return new DivinationDTO.DivinationResponse(
            historyId,
            "avalo",
            question,
            poemData,
            createdAt
        );
    }
    
    // 查詢用戶的占卜歷史
    public java.util.List<DivinationDTO.HistoryResponse> getUserHistory(Long userId) {
        java.util.List<DivinationHistory> histories = historyRepository.findByUserIdOrderByCreatedAtDesc(userId);
        
        return histories.stream()
            .map(h -> new DivinationDTO.HistoryResponse(
                h.getId(),
                h.getType(),
                h.getQuestion(),
                h.getPoemNumber(),
                h.getTitle(),
                h.getSummary(),
                h.getCreatedAt()
            ))
            .collect(java.util.stream.Collectors.toList());
    }
    
    // 查詢單筆占卜詳情
    public DivinationDTO.DivinationResponse getHistoryDetail(Long userId, Long historyId) {
        DivinationHistory history = historyRepository.findById(historyId)
                .orElseThrow(() -> new RuntimeException("找不到該記錄"));
        
        if (!history.getUserId().equals(userId)) {
            throw new RuntimeException("無權查看此記錄");
        }
        
        if ("manju".equals(history.getType())) {
            ManjuPoem poem = manjuPoemRepository.findById(history.getPoemId())
                    .orElseThrow(() -> new RuntimeException("找不到籤詩資料"));
            
            DivinationDTO.PoemData poemData = new DivinationDTO.PoemData(
                poem.getId(),
                null,
                poem.getTibetanCode(),
                poem.getDirection(),
                poem.getTitle(),
                poem.getSummary(),
                poem.getFamilyLife(),
                poem.getStrategy(),
                poem.getFortune(),
                poem.getEnemy(),
                poem.getTravel(),
                poem.getIllness(),
                poem.getPractice(),
                poem.getLostItem(),
                poem.getVisitor(),
                poem.getOtherMatters(),
                poem.getAdvice()
            );
            
            return new DivinationDTO.DivinationResponse(
                history.getId(),
                "manju",
                history.getQuestion(),
                poemData,
                history.getCreatedAt()
            );
            
        } else {
            AvaloPoem poem = avaloPoemRepository.findById(history.getPoemId())
                    .orElseThrow(() -> new RuntimeException("找不到籤詩資料"));
            
            DivinationDTO.PoemData poemData = new DivinationDTO.PoemData(
                poem.getId(),
                null,
                poem.getTibetanCode(),
                null,
                poem.getTitle(),
                poem.getSummary(),
                null, null, null, null, null, null, null, null, null, null, null
            );
            
            return new DivinationDTO.DivinationResponse(
                history.getId(),
                "avalo",
                history.getQuestion(),
                poemData,
                history.getCreatedAt()
            );
        }
    }
    
    // 刪除占卜記錄
    public void deleteHistory(Long userId, Long historyId) {
        DivinationHistory history = historyRepository.findById(historyId)
                .orElseThrow(() -> new RuntimeException("找不到該記錄"));
        
        if (!history.getUserId().equals(userId)) {
            throw new RuntimeException("無權刪除此記錄");
        }
        
        historyRepository.delete(history);
    }

    // 查詢所有文殊籤詩
    public java.util.List<DivinationDTO.PoemData> getAllManjuPoems() {
        java.util.List<ManjuPoem> poems = manjuPoemRepository.findAll();
        
        return poems.stream()
            .map(poem -> new DivinationDTO.PoemData(
                poem.getId(),
                null,
                poem.getTibetanCode(),
                poem.getDirection(),
                poem.getTitle(),
                poem.getSummary(),
                poem.getFamilyLife(),
                poem.getStrategy(),
                poem.getFortune(),
                poem.getEnemy(),
                poem.getTravel(),
                poem.getIllness(),
                poem.getPractice(),
                poem.getLostItem(),
                poem.getVisitor(),
                poem.getOtherMatters(),
                poem.getAdvice()
            ))
            .collect(java.util.stream.Collectors.toList());
    }

    // 查詢所有觀音籤詩
    public java.util.List<DivinationDTO.PoemData> getAllAvaloPoems() {
        java.util.List<AvaloPoem> poems = avaloPoemRepository.findAll();
        
        return poems.stream()
            .map(poem -> new DivinationDTO.PoemData(
                poem.getId(),
                null,
                poem.getTibetanCode(),
                null,
                poem.getTitle(),
                poem.getSummary(),
                null, null, null, null, null, null, null, null, null, null, null
            ))
            .collect(java.util.stream.Collectors.toList());
    }

    // 查詢用戶統計
    public java.util.Map<String, Object> getUserStats(Long userId) {
        long totalCount = historyRepository.countByUserId(userId);
        long manjuCount = historyRepository.countByUserIdAndType(userId, "manju");
        long avaloCount = historyRepository.countByUserIdAndType(userId, "avalo");
        
        java.util.Map<String, Object> stats = new java.util.HashMap<>();
        stats.put("totalCount", totalCount);
        stats.put("manjuCount", manjuCount);
        stats.put("avaloCount", avaloCount);
        
        return stats;
    }

    // 儲存或更新日記
    public DivinationDTO.NoteResponse saveNote(Long userId, DivinationDTO.NoteRequest request) {
        DivinationHistory history = historyRepository.findById(request.getHistoryId())
                .orElseThrow(() -> new RuntimeException("找不到該記錄"));
        
        if (!history.getUserId().equals(userId)) {
            throw new RuntimeException("無權編輯此日記");
        }
        
        DivinationNote note = noteRepository.findByHistoryId(request.getHistoryId())
                .orElse(new DivinationNote());
        
        note.setHistoryId(request.getHistoryId());
        note.setUserId(userId);
        note.setNote(request.getNote());
        note.setIsAccurate(request.getIsAccurate());
        
        note = noteRepository.save(note);
        
        return new DivinationDTO.NoteResponse(
            note.getId(),
            note.getHistoryId(),
            note.getNote(),
            note.getIsAccurate(),
            note.getCreatedAt(),
            note.getUpdatedAt()
        );
    }

    // 查詢日記
    public DivinationDTO.NoteResponse getNote(Long userId, Long historyId) {
        DivinationNote note = noteRepository.findByUserIdAndHistoryId(userId, historyId)
                .orElseThrow(() -> new RuntimeException("找不到日記"));
        
        return new DivinationDTO.NoteResponse(
            note.getId(),
            note.getHistoryId(),
            note.getNote(),
            note.getIsAccurate(),
            note.getCreatedAt(),
            note.getUpdatedAt()
        );
    }

    // 刪除日記
    public void deleteNote(Long userId, Long historyId) {
        DivinationNote note = noteRepository.findByUserIdAndHistoryId(userId, historyId)
                .orElseThrow(() -> new RuntimeException("找不到日記"));
        
        noteRepository.delete(note);
    }

    // 查詢用戶歷史（含日記資訊）
    public java.util.List<DivinationDTO.HistoryWithNoteResponse> getUserHistoryWithNotes(Long userId) {
        java.util.List<DivinationHistory> histories = historyRepository.findByUserIdOrderByCreatedAtDesc(userId);
        
        return histories.stream()
            .map(h -> {
                java.util.Optional<DivinationNote> noteOpt = noteRepository.findByHistoryId(h.getId());
                
                boolean hasNote = noteOpt.isPresent();
                String notePreview = hasNote && noteOpt.get().getNote() != null 
                    ? (noteOpt.get().getNote().length() > 50 
                        ? noteOpt.get().getNote().substring(0, 50) + "..." 
                        : noteOpt.get().getNote())
                    : null;
                Boolean isAccurate = hasNote ? noteOpt.get().getIsAccurate() : null;
                
                return new DivinationDTO.HistoryWithNoteResponse(
                    h.getId(),
                    h.getType(),
                    h.getQuestion(),
                    h.getPoemNumber(),
                    h.getTitle(),
                    h.getSummary(),
                    h.getCreatedAt(),
                    hasNote,
                    notePreview,
                    isAccurate
                );
            })
            .collect(java.util.stream.Collectors.toList());
    }
}