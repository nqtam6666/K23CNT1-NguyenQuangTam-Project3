package k23cnt1.nqt.project3.nqtController;

import k23cnt1.nqt.project3.nqtDto.NqtDonGiaDichVuRequest;
import k23cnt1.nqt.project3.nqtDto.NqtDonGiaDichVuResponse;
import k23cnt1.nqt.project3.nqtService.NqtDonGiaDichVuService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/admin/nqtDonGiaDichVu")
public class NqtDonGiaDichVuController {
    
    @Autowired
    private NqtDonGiaDichVuService nqtDonGiaDichVuService;
    
    @GetMapping
    public ResponseEntity<List<NqtDonGiaDichVuResponse>> nqtGetAll() {
        try {
            List<NqtDonGiaDichVuResponse> nqtList = nqtDonGiaDichVuService.nqtGetAll();
            return ResponseEntity.ok(nqtList);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
    
    @GetMapping("/{nqtId}")
    public ResponseEntity<NqtDonGiaDichVuResponse> nqtGetById(@PathVariable Integer nqtId) {
        try {
            NqtDonGiaDichVuResponse nqtResponse = nqtDonGiaDichVuService.nqtGetById(nqtId);
            return ResponseEntity.ok(nqtResponse);
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
    
    @PostMapping
    public ResponseEntity<NqtDonGiaDichVuResponse> nqtCreate(@RequestBody NqtDonGiaDichVuRequest nqtRequest) {
        try {
            NqtDonGiaDichVuResponse nqtResponse = nqtDonGiaDichVuService.nqtCreate(nqtRequest);
            return ResponseEntity.status(HttpStatus.CREATED).body(nqtResponse);
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
    
    @PutMapping("/{nqtId}")
    public ResponseEntity<NqtDonGiaDichVuResponse> nqtUpdate(
            @PathVariable Integer nqtId,
            @RequestBody NqtDonGiaDichVuRequest nqtRequest) {
        try {
            NqtDonGiaDichVuResponse nqtResponse = nqtDonGiaDichVuService.nqtUpdate(nqtId, nqtRequest);
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
            nqtDonGiaDichVuService.nqtDelete(nqtId);
            return ResponseEntity.noContent().build();
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
}

