package k23cnt1.nqt.project3.nqtController;

import k23cnt1.nqt.project3.nqtRepository.NqtDatPhongRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/admin/thong-ke")
public class NqtThongKeController {

    @Autowired
    private NqtDatPhongRepository nqtDatPhongRepository;

    @GetMapping("/doanh-thu")
    public String nqtDoanhThu(Model model) {
        Double totalRevenue = nqtDatPhongRepository.sumTongTienDaThanhToan();
        model.addAttribute("totalRevenue", totalRevenue != null ? totalRevenue : 0.0);

        java.util.List<Object[]> revenueByMonth = nqtDatPhongRepository.sumTongTienThang();
        java.util.List<Integer> months = new java.util.ArrayList<>();
        java.util.List<Double> revenues = new java.util.ArrayList<>();

        // Initialize all 12 months with 0
        for (int i = 1; i <= 12; i++) {
            months.add(i);
            revenues.add(0.0);
        }

        // Fill in actual data
        if (revenueByMonth != null) {
            for (Object[] row : revenueByMonth) {
                Integer month = (Integer) row[0];
                Double revenue = (Double) row[1];
                if (month != null && month >= 1 && month <= 12) {
                    revenues.set(month - 1, revenue);
                }
            }
        }

        model.addAttribute("revenueMonths", months);
        model.addAttribute("revenueData", revenues);

        return "admin/thong-ke/doanh-thu";
    }
}
