package k23cnt1.nqt.project3.nqtController;

import k23cnt1.nqt.project3.nqtDto.NqtBlogRequest;
import k23cnt1.nqt.project3.nqtDto.NqtBlogResponse;
import k23cnt1.nqt.project3.nqtService.NqtBlogService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/admin/nqtBlog")
public class NqtBlogController {
    
    @Autowired
    private NqtBlogService nqtBlogService;
    
    @GetMapping
    public ResponseEntity<List<NqtBlogResponse>> nqtGetAll() {
        try {
            List<NqtBlogResponse> nqtList = nqtBlogService.nqtGetAll();
            return ResponseEntity.ok(nqtList);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
    
    @GetMapping("/{nqtId}")
    public ResponseEntity<NqtBlogResponse> nqtGetById(@PathVariable Integer nqtId) {
        try {
            NqtBlogResponse nqtResponse = nqtBlogService.nqtGetById(nqtId);
            return ResponseEntity.ok(nqtResponse);
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
    
    @PostMapping
    public ResponseEntity<NqtBlogResponse> nqtCreate(@RequestBody NqtBlogRequest nqtRequest) {
        try {
            NqtBlogResponse nqtResponse = nqtBlogService.nqtCreate(nqtRequest);
            return ResponseEntity.status(HttpStatus.CREATED).body(nqtResponse);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
    
    @PutMapping("/{nqtId}")
    public ResponseEntity<NqtBlogResponse> nqtUpdate(
            @PathVariable Integer nqtId,
            @RequestBody NqtBlogRequest nqtRequest) {
        try {
            NqtBlogResponse nqtResponse = nqtBlogService.nqtUpdate(nqtId, nqtRequest);
            return ResponseEntity.ok(nqtResponse);
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
    
    @DeleteMapping("/{nqtId}")
    public ResponseEntity<Void> nqtDelete(@PathVariable Integer nqtId) {
        try {
            nqtBlogService.nqtDelete(nqtId);
            return ResponseEntity.noContent().build();
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
}

