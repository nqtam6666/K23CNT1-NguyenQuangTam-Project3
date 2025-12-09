package k23cnt1.nqt.project3.nqtController;

import k23cnt1.nqt.project3.nqtDto.NqtDichVuRequest;
import k23cnt1.nqt.project3.nqtDto.NqtDichVuResponse;
import k23cnt1.nqt.project3.nqtService.NqtDichVuService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/admin/nqtDichVu")
public class NqtDichVuController {
    
    @Autowired
    private NqtDichVuService nqtDichVuService;
    
    @GetMapping
    public ResponseEntity<List<NqtDichVuResponse>> nqtGetAll() {
        try {
            List<NqtDichVuResponse> nqtList = nqtDichVuService.nqtGetAll();
            return ResponseEntity.ok(nqtList);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
    
    @GetMapping("/{nqtId}")
    public ResponseEntity<NqtDichVuResponse> nqtGetById(@PathVariable Integer nqtId) {
        try {
            NqtDichVuResponse nqtResponse = nqtDichVuService.nqtGetById(nqtId);
            return ResponseEntity.ok(nqtResponse);
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
    
    @PostMapping
    public ResponseEntity<NqtDichVuResponse> nqtCreate(@RequestBody NqtDichVuRequest nqtRequest) {
        try {
            NqtDichVuResponse nqtResponse = nqtDichVuService.nqtCreate(nqtRequest);
            return ResponseEntity.status(HttpStatus.CREATED).body(nqtResponse);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
    
    @PutMapping("/{nqtId}")
    public ResponseEntity<NqtDichVuResponse> nqtUpdate(
            @PathVariable Integer nqtId,
            @RequestBody NqtDichVuRequest nqtRequest) {
        try {
            NqtDichVuResponse nqtResponse = nqtDichVuService.nqtUpdate(nqtId, nqtRequest);
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
            nqtDichVuService.nqtDelete(nqtId);
            return ResponseEntity.noContent().build();
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
}

