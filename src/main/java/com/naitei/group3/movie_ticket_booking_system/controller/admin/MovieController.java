package com.naitei.group3.movie_ticket_booking_system.controller.admin;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import com.naitei.group3.movie_ticket_booking_system.service.ExcelService;

@Controller
@RequestMapping("/admin/movies")
public class MovieController {

    private static final Logger logger = LoggerFactory.getLogger(MovieController.class);

    @Autowired
    private ExcelService excelService;

    @GetMapping({"/upload", "/"})
    public String showUploadPage(Model model) {
        model.addAttribute("message", "");
        model.addAttribute("messageClass", "");
        return "admin/uploadmovie";
    }

    @PostMapping("/upload")
    public String uploadFile(@RequestParam("file") MultipartFile file, Model model) {
        // Kiểm tra file null hoặc rỗng
        if (file == null || file.isEmpty()) {
            logger.warn("Không có file được tải lên");
            model.addAttribute("message", "Vui lòng chọn một file");
            model.addAttribute("messageClass", "text-red-500");
            return "admin/uploadmovie";
        }

        // Kiểm tra định dạng file
        String fileName = file.getOriginalFilename();
        if (fileName == null || !(fileName.endsWith(".xlsx") || fileName.endsWith(".xls"))) {
            logger.warn("File tải lên không đúng định dạng Excel: {}", fileName);
            model.addAttribute("message", "Vui lòng tải lên file Excel (.xlsx hoặc .xls)");
            model.addAttribute("messageClass", "text-red-500");
            return "admin/uploadmovie";
        }

        try {
            excelService.save(file);
            logger.info("Đã tải lên thành công file: {}", fileName);
            model.addAttribute("message", String.format("Tải file thành công: %s", fileName));
            model.addAttribute("messageClass", "text-green-500");
            return "admin/uploadmovie";
        } catch (Exception e) {
            logger.error("Lỗi khi xử lý file Excel: {}", fileName, e);
            model.addAttribute("message", "Lỗi khi tải file: " + e.getMessage());
            model.addAttribute("messageClass", "text-red-500");
            return "admin/uploadmovie";
        }
    }
}
