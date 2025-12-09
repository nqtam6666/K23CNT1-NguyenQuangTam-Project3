package k23cnt1.nqt.project3.nqtController;

import k23cnt1.nqt.project3.nqtDto.NqtDanhGiaRequest;
import k23cnt1.nqt.project3.nqtDto.NqtDanhGiaResponse;
import k23cnt1.nqt.project3.nqtService.NqtDanhGiaService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/admin/nqtDanhGia")
public class NqtDanhGiaController {
    
    @Autowired
    private NqtDanhGiaService nqtDanhGiaService;
    
    @GetMapping
    public ResponseEntity<List<NqtDanhGiaResponse>> nqtGetAll() {
        try {
            List<NqtDanhGiaResponse> nqtList = nqtDanhGiaService.nqtGetAll();
            return ResponseEntity.ok(nqtList);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
    
    @GetMapping("/{nqtId}")
    public ResponseEntity<NqtDanhGiaResponse> nqtGetById(@PathVariable Integer nqtId) {
        try {
            NqtDanhGiaResponse nqtResponse = nqtDanhGiaService.nqtGetById(nqtId);
            return ResponseEntity.ok(nqtResponse);
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
    
    @PostMapping
    public ResponseEntity<NqtDanhGiaResponse> nqtCreate(@RequestBody NqtDanhGiaRequest nqtRequest) {
        try {
            NqtDanhGiaResponse nqtResponse = nqtDanhGiaService.nqtCreate(nqtRequest);
            return ResponseEntity.status(HttpStatus.CREATED).body(nqtResponse);
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
    
    @PutMapping("/{nqtId}")
    public ResponseEntity<NqtDanhGiaResponse> nqtUpdate(
            @PathVariable Integer nqtId,
            @RequestBody NqtDanhGiaRequest nqtRequest) {
        try {
            NqtDanhGiaResponse nqtResponse = nqtDanhGiaService.nqtUpdate(nqtId, nqtRequest);
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
            nqtDanhGiaService.nqtDelete(nqtId);
            return ResponseEntity.noContent().build();
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
}

