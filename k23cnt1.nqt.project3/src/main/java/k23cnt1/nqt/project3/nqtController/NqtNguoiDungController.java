package k23cnt1.nqt.project3.nqtController;

import k23cnt1.nqt.project3.nqtDto.NqtNguoiDungRequest;
import k23cnt1.nqt.project3.nqtDto.NqtNguoiDungResponse;
import k23cnt1.nqt.project3.nqtService.NqtNguoiDungService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/admin/nqtNguoiDung")
public class NqtNguoiDungController {
    
    @Autowired
    private NqtNguoiDungService nqtNguoiDungService;
    
    @GetMapping
    public ResponseEntity<List<NqtNguoiDungResponse>> nqtGetAll() {
        try {
            List<NqtNguoiDungResponse> nqtList = nqtNguoiDungService.nqtGetAll();
            return ResponseEntity.ok(nqtList);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
    
    @GetMapping("/{nqtId}")
    public ResponseEntity<NqtNguoiDungResponse> nqtGetById(@PathVariable Integer nqtId) {
        try {
            NqtNguoiDungResponse nqtResponse = nqtNguoiDungService.nqtGetById(nqtId);
            return ResponseEntity.ok(nqtResponse);
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
    
    @PostMapping
    public ResponseEntity<NqtNguoiDungResponse> nqtCreate(@RequestBody NqtNguoiDungRequest nqtRequest) {
        try {
            NqtNguoiDungResponse nqtResponse = nqtNguoiDungService.nqtCreate(nqtRequest);
            return ResponseEntity.status(HttpStatus.CREATED).body(nqtResponse);
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
    
    @PutMapping("/{nqtId}")
    public ResponseEntity<NqtNguoiDungResponse> nqtUpdate(
            @PathVariable Integer nqtId,
            @RequestBody NqtNguoiDungRequest nqtRequest) {
        try {
            NqtNguoiDungResponse nqtResponse = nqtNguoiDungService.nqtUpdate(nqtId, nqtRequest);
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
            nqtNguoiDungService.nqtDelete(nqtId);
            return ResponseEntity.noContent().build();
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
}

