package k23cnt1.nqt.project3.nqtController;

import k23cnt1.nqt.project3.nqtDto.NqtLoaiPhongRequest;
import k23cnt1.nqt.project3.nqtDto.NqtLoaiPhongResponse;
import k23cnt1.nqt.project3.nqtService.NqtLoaiPhongService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/admin/nqtLoaiPhong")
public class NqtLoaiPhongController {
    
    @Autowired
    private NqtLoaiPhongService nqtLoaiPhongService;
    
    @GetMapping
    public ResponseEntity<List<NqtLoaiPhongResponse>> nqtGetAll() {
        try {
            List<NqtLoaiPhongResponse> nqtList = nqtLoaiPhongService.nqtGetAll();
            return ResponseEntity.ok(nqtList);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
    
    @GetMapping("/{nqtId}")
    public ResponseEntity<NqtLoaiPhongResponse> nqtGetById(@PathVariable Integer nqtId) {
        try {
            NqtLoaiPhongResponse nqtResponse = nqtLoaiPhongService.nqtGetById(nqtId);
            return ResponseEntity.ok(nqtResponse);
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
    
    @PostMapping
    public ResponseEntity<NqtLoaiPhongResponse> nqtCreate(@RequestBody NqtLoaiPhongRequest nqtRequest) {
        try {
            NqtLoaiPhongResponse nqtResponse = nqtLoaiPhongService.nqtCreate(nqtRequest);
            return ResponseEntity.status(HttpStatus.CREATED).body(nqtResponse);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
    
    @PutMapping("/{nqtId}")
    public ResponseEntity<NqtLoaiPhongResponse> nqtUpdate(
            @PathVariable Integer nqtId,
            @RequestBody NqtLoaiPhongRequest nqtRequest) {
        try {
            NqtLoaiPhongResponse nqtResponse = nqtLoaiPhongService.nqtUpdate(nqtId, nqtRequest);
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
            nqtLoaiPhongService.nqtDelete(nqtId);
            return ResponseEntity.noContent().build();
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
}

