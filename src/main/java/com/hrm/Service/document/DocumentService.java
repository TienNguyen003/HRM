package com.hrm.Service.document;

import com.hrm.Entity.PageCustom;
import com.hrm.Entity.document.Document;
import com.hrm.Exception.AppException;
import com.hrm.Exception.ErrorCode;
import com.hrm.dto.request.document.DocumentUploadRequest;
import com.hrm.dto.response.document.DocumentDownloadResponse;
import com.hrm.dto.response.document.DocumentPreview;
import com.hrm.dto.response.document.DocumentUploadResponse;
import com.hrm.enums.DocumentStatus;
import com.hrm.repository.document.DocumentRepository;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class DocumentService {
    DocumentRepository documentRepository;

    // đăng ký tài liệu mới
    public DocumentUploadResponse upload(DocumentUploadRequest request) {
        Document document = Document.builder()
                .name(request.getName())
                .type(request.getType())
                .filePath(request.getFilePath())
                .size(request.getSize())
                .description(request.getDescription())
                .uploadedBy(request.getUploadedBy())
                .status(DocumentStatus.UPLOADED)
                .creationTime(String.valueOf(LocalDateTime.now()))
                .build();
        documentRepository.save(document);

        return DocumentUploadResponse.builder()
                .id(document.getId())
                .name(document.getName())
                .status(document.getStatus().name())
                .message("Tài liệu đã được tải lên")
                .build();
    }

    // thông tin để tải tài liệu
    public DocumentDownloadResponse download(int id) {
        Document document = getDocument(id);
        return DocumentDownloadResponse.builder()
                .id(document.getId())
                .name(document.getName())
                .filePath(document.getFilePath())
                .size(document.getSize())
                .build();
    }

    // thông tin xem trước tài liệu
    public DocumentPreview preview(int id) {
        Document document = getDocument(id);
        return DocumentPreview.builder()
                .id(document.getId())
                .name(document.getName())
                .type(document.getType())
                .filePath(document.getFilePath())
                .size(document.getSize())
                .build();
    }

    // lấy theo id
    public Document getById(int id) {
        return getDocument(id);
    }

    // tìm kiếm theo loại + tên
    public List<Document> search(int pageNumber, int pageSize, String type, String name) {
        return documentRepository.findByFilters(type, name, pageRequest(pageNumber, pageSize)).getContent();
    }

    // thông tin phân trang
    public PageCustom getPagination(int pageNumber, int pageSize, String type, String name) {
        Page<Document> page = documentRepository.findByFilters(type, name, pageRequest(pageNumber, pageSize));
        return PageCustom.builder()
                .totalPages(String.valueOf(page.getTotalPages()))
                .totalItems(String.valueOf(page.getTotalElements()))
                .totalItemsPerPage(String.valueOf(page.getNumberOfElements()))
                .currentPage(String.valueOf(pageNumber))
                .build();
    }

    // xóa mềm tài liệu
    public void delete(int id) {
        Document document = getDocument(id);
        document.setStatus(DocumentStatus.DELETED);
        documentRepository.save(document);
    }

    private Document getDocument(int id) {
        return documentRepository.findById(id)
                .orElseThrow(() -> new AppException(ErrorCode.DOCUMENT_NOT_EXISTED));
    }

    private Pageable pageRequest(int pageNumber, int pageSize) {
        return PageRequest.of(pageNumber - 1, pageSize);
    }
}
