package k23cnt1.nqt.project3.nqtController;

import k23cnt1.nqt.project3.nqtDto.NqtDatPhongRequest;
import k23cnt1.nqt.project3.nqtDto.NqtDatPhongResponse;
import k23cnt1.nqt.project3.nqtService.NqtDatPhongService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/admin/nqtDatPhong")
public class NqtDatPhongController {
    
    @Autowired
    private NqtDatPhongService nqtDatPhongService;
    
    @GetMapping
    public ResponseEntity<List<NqtDatPhongResponse>> nqtGetAll() {
        try {
            List<NqtDatPhongResponse> nqtList = nqtDatPhongService.nqtGetAll();
            return ResponseEntity.ok(nqtList);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
    
    @GetMapping("/{nqtId}")
    public ResponseEntity<NqtDatPhongResponse> nqtGetById(@PathVariable Integer nqtId) {
        try {
            NqtDatPhongResponse nqtResponse = nqtDatPhongService.nqtGetById(nqtId);
            return ResponseEntity.ok(nqtResponse);
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
    
    @PostMapping
    public ResponseEntity<NqtDatPhongResponse> nqtCreate(@RequestBody NqtDatPhongRequest nqtRequest) {
        try {
            NqtDatPhongResponse nqtResponse = nqtDatPhongService.nqtCreate(nqtRequest);
            return ResponseEntity.status(HttpStatus.CREATED).body(nqtResponse);
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
    
    @PutMapping("/{nqtId}")
    public ResponseEntity<NqtDatPhongResponse> nqtUpdate(
            @PathVariable Integer nqtId,
            @RequestBody NqtDatPhongRequest nqtRequest) {
        try {
            NqtDatPhongResponse nqtResponse = nqtDatPhongService.nqtUpdate(nqtId, nqtRequest);
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
            nqtDatPhongService.nqtDelete(nqtId);
            return ResponseEntity.noContent().build();
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
}

