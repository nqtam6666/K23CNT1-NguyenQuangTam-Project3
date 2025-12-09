package k23cnt1.nqt.lesson06.nqt_lesson06.nqtController;

import jakarta.servlet.RequestDispatcher;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.boot.web.servlet.error.ErrorController;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
public class nqtErrorController implements ErrorController {

    @RequestMapping("/error")
    public String handleError(HttpServletRequest request, Model model) {
        Object status = request.getAttribute(RequestDispatcher.ERROR_STATUS_CODE);

        if (status != null) {
            Integer statusCode = Integer.valueOf(status.toString());

            if (statusCode == HttpStatus.NOT_FOUND.value()) {
                model.addAttribute("errorCode", "404");
                model.addAttribute("errorMessage", "Trang không tồn tại");
                model.addAttribute("errorDescription", "Xin lỗi, trang bạn đang tìm kiếm không tồn tại.");
            } else if (statusCode == HttpStatus.INTERNAL_SERVER_ERROR.value()) {
                model.addAttribute("errorCode", "500");
                model.addAttribute("errorMessage", "Lỗi máy chủ");
                model.addAttribute("errorDescription", "Đã xảy ra lỗi bên trong máy chủ.");
            } else if (statusCode == HttpStatus.FORBIDDEN.value()) {
                model.addAttribute("errorCode", "403");
                model.addAttribute("errorMessage", "Không có quyền truy cập");
                model.addAttribute("errorDescription", "Bạn không có quyền truy cập trang này.");
            } else {
                model.addAttribute("errorCode", statusCode.toString());
                model.addAttribute("errorMessage", "Đã xảy ra lỗi");
                model.addAttribute("errorDescription", "Vui lòng thử lại sau.");
            }
        } else {
            model.addAttribute("errorCode", "Unknown");
            model.addAttribute("errorMessage", "Đã xảy ra lỗi");
            model.addAttribute("errorDescription", "Vui lòng thử lại sau.");
        }

        return "error";
    }
}

