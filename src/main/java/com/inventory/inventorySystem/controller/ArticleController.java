package com.inventory.inventorySystem.controller;

import com.inventory.inventorySystem.dto.OnCreate;
import com.inventory.inventorySystem.dto.OnUpdate;
import com.inventory.inventorySystem.dto.request.ArticleRequest;
import com.inventory.inventorySystem.dto.response.ArticleResponse;
import com.inventory.inventorySystem.dto.response.PaginatedResponse;
import com.inventory.inventorySystem.service.interfaces.ArticleService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/v1/articles")
@Tag(
        name = "Articles",
        description = "Article and inventory item management"
)

public class ArticleController {

    private final ArticleService articleService;

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(
            summary = "Create article",
            description = "Creates a new article. Requires ADMIN role."
    )
    public ResponseEntity<ArticleResponse> saveArticle(@Validated(OnCreate.class) @RequestBody ArticleRequest articleRequest){
        ArticleResponse articleResponse = articleService.saveArticle(articleRequest);
        return ResponseEntity.status(HttpStatus.CREATED).body(articleResponse);
    }

    @PatchMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(
            summary = "Update article",
            description = "Updates an existing article by ID. Requires ADMIN role."
    )
    public ResponseEntity<ArticleResponse> updateCategory(@PathVariable UUID id, @Validated(OnUpdate.class)  @RequestBody ArticleRequest articleRequest){
        ArticleResponse articleResponse = articleService.updateArticle(id, articleRequest);
        return ResponseEntity.status(HttpStatus.OK).body(articleResponse);
    }

    @GetMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'CASHIER')")
    @Operation(
            summary = "Get articles",
            description = "Returns a paginated list of articles. Accessible by ADMIN and CASHIER roles."
    )
    public ResponseEntity<PaginatedResponse<ArticleResponse>> getAllArticles(@RequestParam(required = false) String name, @PageableDefault(page = 0, size = 10, sort = "name", direction = Sort.Direction.ASC) Pageable pageable){
        PaginatedResponse<ArticleResponse> articleResponse = articleService.getAllArticles(name, pageable);
        return ResponseEntity.status(HttpStatus.OK).body(articleResponse);
    }

}
