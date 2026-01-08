package com.divination.backend.service;

import com.divination.backend.dto.AdminDTO;
import com.divination.backend.model.ManjuPoem;
import com.divination.backend.model.AvaloPoem;
import com.divination.backend.model.JapanPoem;
import com.divination.backend.repository.ManjuPoemRepository;
import com.divination.backend.repository.AvaloPoemRepository;
import com.divination.backend.repository.JapanPoemRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AdminService {
    
    private final ManjuPoemRepository manjuPoemRepository;
    private final AvaloPoemRepository avaloPoemRepository;
    private final JapanPoemRepository japanPoemRepository;
    
    // 新增或更新籤詩
    public void saveManjuPoem(AdminDTO.PoemRequest request) {
        ManjuPoem poem;
        
        if (request.getId() != null) {
            // 編輯現有籤詩
            poem = manjuPoemRepository.findById(request.getId())
                    .orElseThrow(() -> new RuntimeException("籤詩不存在"));
        } else {
            // 新增籤詩
            poem = new ManjuPoem();
        }
        
        poem.setTibetanCode(request.getTibetanCode());
        poem.setDirection(request.getDirection());
        poem.setTitle(request.getTitle());
        poem.setSummary(request.getSummary());
        poem.setFamilyLife(request.getFamilyLife());
        poem.setStrategy(request.getStrategy());
        poem.setFortune(request.getFortune());
        poem.setEnemy(request.getEnemy());
        poem.setTravel(request.getTravel());
        poem.setIllness(request.getIllness());
        poem.setPractice(request.getPractice());
        poem.setLostItem(request.getLostItem());
        poem.setVisitor(request.getVisitor());
        poem.setOtherMatters(request.getOtherMatters());
        poem.setAdvice(request.getAdvice());
        
        manjuPoemRepository.save(poem);
    }
    
    public void saveAvaloPoem(AdminDTO.PoemRequest request) {
        AvaloPoem poem;
        
        if (request.getId() != null) {
            poem = avaloPoemRepository.findById(request.getId())
                    .orElseThrow(() -> new RuntimeException("籤詩不存在"));
        } else {
            poem = new AvaloPoem();
        }
        
        poem.setTibetanCode(request.getTibetanCode());
        poem.setTitle(request.getTitle());
        poem.setSummary(request.getSummary());
        
        avaloPoemRepository.save(poem);
    }
    
    public void saveJapanPoem(AdminDTO.PoemRequest request) {
        JapanPoem poem;
        
        if (request.getId() != null) {
            poem = japanPoemRepository.findById(request.getId())
                    .orElseThrow(() -> new RuntimeException("籤詩不存在"));
        } else {
            poem = new JapanPoem();
        }
        
        poem.setNumber(request.getNumber());
        poem.setFortuneLevel(request.getFortuneLevel());
        poem.setContent(request.getContent());
        poem.setWish(request.getWish());
        poem.setIllness(request.getIllness());
        poem.setWaitingPerson(request.getWaitingPerson());
        poem.setLostItem(request.getLostItem());
        poem.setLifeEvents(request.getLifeEvents());
        
        japanPoemRepository.save(poem);
    }
    
    // 刪除籤詩
    public void deleteManjuPoem(Long id) {
        manjuPoemRepository.deleteById(id);
    }
    
    public void deleteAvaloPoem(Long id) {
        avaloPoemRepository.deleteById(id);
    }
    
    public void deleteJapanPoem(Long id) {
        japanPoemRepository.deleteById(id);
    }
    
    // 查詢單個籤詩（用於編輯）
    public ManjuPoem getManjuPoem(Long id) {
        return manjuPoemRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("籤詩不存在"));
    }
    
    public AvaloPoem getAvaloPoem(Long id) {
        return avaloPoemRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("籤詩不存在"));
    }
    
    public JapanPoem getJapanPoem(Long id) {
        return japanPoemRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("籤詩不存在"));
    }
}