package com.hrm.Controller.document;

import com.hrm.Entity.document.Document;
import com.hrm.Service.document.DocumentService;
import com.hrm.dto.request.document.DocumentUploadRequest;
import com.hrm.dto.response.ApiResponse;
import com.hrm.dto.response.document.DocumentDownloadResponse;
import com.hrm.dto.response.document.DocumentPreview;
import com.hrm.dto.response.document.DocumentUploadResponse;
import jakarta.validation.Valid;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("${api.prefix}documents")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class DocumentController {
    DocumentService documentService;

    @PostMapping
    ApiResponse<DocumentUploadResponse> upload(@RequestBody @Valid DocumentUploadRequest request) {
        return ApiResponse.<DocumentUploadResponse>builder()
                .result(documentService.upload(request))
                .build();
    }

    @GetMapping("/download")
    ApiResponse<DocumentDownloadResponse> download(@RequestParam int id) {
        return ApiResponse.<DocumentDownloadResponse>builder()
                .result(documentService.download(id))
                .build();
    }

    @GetMapping("/preview")
    ApiResponse<DocumentPreview> preview(@RequestParam int id) {
        return ApiResponse.<DocumentPreview>builder()
                .result(documentService.preview(id))
                .build();
    }

    @GetMapping("/detail")
    ApiResponse<Document> getDetail(@RequestParam int id) {
        return ApiResponse.<Document>builder()
                .result(documentService.getById(id))
                .build();
    }

    @GetMapping
    ApiResponse<List<Document>> search(@RequestParam("pageNumber") int pageNumber,
                                       @RequestParam(name = "pageSize", defaultValue = "30") int pageSize,
                                       @RequestParam(name = "type", required = false) String type,
                                       @RequestParam(name = "name", required = false) String name) {
        return ApiResponse.<List<Document>>builder()
                .result(documentService.search(pageNumber, pageSize, type, name))
                .page(documentService.getPagination(pageNumber, pageSize, type, name))
                .build();
    }

    @DeleteMapping
    ApiResponse<String> delete(@RequestParam int id) {
        documentService.delete(id);
        return ApiResponse.<String>builder()
                .result("Tài liệu đã được xóa")
                .build();
    }
}
