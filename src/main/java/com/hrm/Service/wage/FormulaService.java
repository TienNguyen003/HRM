package com.hrm.Service.wage;

import com.hrm.Entity.PageCustom;
import com.hrm.Entity.wage.Formula;
import com.hrm.Exception.AppException;
import com.hrm.Exception.ErrorCode;
import com.hrm.Mapper.wage.FormulaMapper;
import com.hrm.dto.request.wage.FormulaRequest;
import com.hrm.dto.response.wage.FormulaResponse;
import com.hrm.repository.wage.FormulaRepository;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class FormulaService {
    FormulaRepository formulaRepository;
    FormulaMapper formulaMapper;

    // thêm danh sách
    public FormulaResponse create(FormulaRequest request){
        Formula formula = formulaMapper.toFormula(request);

        return formulaMapper.toFormulaResponse(formulaRepository.save(formula));
    }

    // cập nhật
    public FormulaResponse update(int advanceId, FormulaRequest request){
        Formula formula = formulaRepository.findById(advanceId)
                .orElseThrow(() -> new AppException(ErrorCode.FORMULA_NOT_EXISTED));

        formulaMapper.updateFormula(formula, request);

        return formulaMapper.toFormulaResponse(formulaRepository.save(formula));
    }

    // cập nhật status
    public String updateStt(int id, int status){
        Formula formula = formulaRepository.findById(id)
                .orElseThrow(() -> new AppException(ErrorCode.ADVANCE_NOT_EXISTED));
        formula.setStatus(status);
        formulaMapper.toFormulaResponse(formulaRepository.save(formula));
        return "Update success";
    }

    // lấy ra tất cả
    public List<FormulaResponse> getAll(){
        return formulaRepository.findAll()
                .stream().map(formulaMapper::toFormulaResponse).toList();
    }

    // lấy theo id
    public FormulaResponse getFormula(int id){
        Formula formula = formulaRepository.findById(id)
                .orElseThrow(() -> new AppException(ErrorCode.FORMULA_NOT_EXISTED));

        return formulaMapper.toFormulaResponse(formula);
    }

    // tìm kiếm
    public List<FormulaResponse> search(int pageNumber, int pageSize, String name, Integer status){
        Pageable pageable = PageRequest.of(pageNumber - 1, pageSize);
        return formulaRepository.findByNameStatus(name, status, pageable)
                .stream().map(formulaMapper::toFormulaResponse).toList();
    }
    public PageCustom getPagination(int pageNumber, int pageSize, String name, Integer status){
        Pageable pageable = PageRequest.of(pageNumber - 1, pageSize);
        Page<Formula> page = formulaRepository.findByNameStatus(name, status, pageable);
        return PageCustom.builder()
                .totalPages(String.valueOf(page.getTotalPages()))
                .totalItems(String.valueOf(page.getTotalElements()))
                .totalItemsPerPage(String.valueOf(page.getNumberOfElements()))
                .currentPage(String.valueOf(pageNumber))
                .build();
    }

    // xóa
    public void delete(int id){
        formulaRepository.deleteById(id);
    }

}
