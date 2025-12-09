package k23cnt1.nqt.project3.nqtController;

import k23cnt1.nqt.project3.nqtEntity.NqtLoaiPhong;
import k23cnt1.nqt.project3.nqtEntity.NqtPhong;
import k23cnt1.nqt.project3.nqtRepository.NqtLoaiPhongRepository;
import k23cnt1.nqt.project3.nqtRepository.NqtPhongRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;
import java.util.Optional;

@Controller
public class NqtKhachPhongController {

    @Autowired
    private NqtPhongRepository nqtPhongRepository;

    @Autowired
    private NqtLoaiPhongRepository nqtLoaiPhongRepository;

    @Autowired
    private k23cnt1.nqt.project3.nqtService.NqtRoomStatusService nqtRoomStatusService;

    // Room Listing
    @GetMapping("/nqtPhong")
    public String nqtPhong(@RequestParam(value = "loaiPhong", required = false) Integer loaiPhongId,
            @RequestParam(value = "search", required = false) String search,
            Model model) {
        // Tự động kiểm tra và cập nhật trạng thái phòng
        nqtRoomStatusService.checkAndUpdateAllRooms();
        
        List<NqtPhong> rooms;

        if (loaiPhongId != null) {
            // Filter by room type
            Optional<NqtLoaiPhong> loaiPhong = nqtLoaiPhongRepository.findById(loaiPhongId);
            if (loaiPhong.isPresent()) {
                rooms = nqtPhongRepository.findByNqtLoaiPhongAndNqtStatus(loaiPhong.get(), true);
            } else {
                rooms = nqtPhongRepository.findByNqtStatus(true);
            }
        } else if (search != null && !search.isEmpty()) {
            // Search by room name or number
            rooms = nqtPhongRepository.findByNqtTenPhongContainingOrNqtSoPhongContaining(search, search);
        } else {
            // Show all available rooms
            rooms = nqtPhongRepository.findByNqtStatus(true);
        }

        // Get all room types for filter
        List<NqtLoaiPhong> roomTypes = nqtLoaiPhongRepository.findByNqtStatus(true);

        model.addAttribute("rooms", rooms);
        model.addAttribute("roomTypes", roomTypes);
        model.addAttribute("selectedLoaiPhong", loaiPhongId);
        model.addAttribute("searchQuery", search);

        return "nqtCustomer/nqtPhong/nqtList";
    }

    // Room Detail
    @GetMapping("/nqtPhong/{id}")
    public String nqtPhongDetail(@PathVariable("id") Integer id, Model model) {
        Optional<NqtPhong> roomOptional = nqtPhongRepository.findById(id);

        if (roomOptional.isEmpty()) {
            return "redirect:/nqtPhong";
        }

        NqtPhong room = roomOptional.get();

        // Get related rooms (same room type, different room)
        List<NqtPhong> relatedRooms = nqtPhongRepository
                .findByNqtLoaiPhongAndNqtStatus(room.getNqtLoaiPhong(), true)
                .stream()
                .filter(r -> !r.getNqtId().equals(id))
                .limit(4)
                .toList();

        model.addAttribute("room", room);
        model.addAttribute("relatedRooms", relatedRooms);

        return "nqtCustomer/nqtPhong/nqtDetail";
    }

    // Room Types Listing
    @GetMapping("/nqtLoaiPhong")
    public String nqtLoaiPhong(Model model) {
        List<NqtLoaiPhong> roomTypes = nqtLoaiPhongRepository.findByNqtStatus(true);
        model.addAttribute("roomTypes", roomTypes);
        return "nqtCustomer/nqtLoaiPhong/nqtList";
    }

    // Room Type Detail
    @GetMapping("/nqtLoaiPhong/{id}")
    public String nqtLoaiPhongDetail(@PathVariable("id") Integer id, Model model) {
        Optional<NqtLoaiPhong> roomTypeOptional = nqtLoaiPhongRepository.findById(id);

        if (roomTypeOptional.isEmpty()) {
            return "redirect:/nqtLoaiPhong";
        }

        NqtLoaiPhong roomType = roomTypeOptional.get();

        // Get available rooms of this type
        List<NqtPhong> availableRooms = nqtPhongRepository.findByNqtLoaiPhongAndNqtStatus(roomType, true);

        model.addAttribute("roomType", roomType);
        model.addAttribute("availableRooms", availableRooms);

        return "nqtCustomer/nqtLoaiPhong/nqtDetail";
    }
}
