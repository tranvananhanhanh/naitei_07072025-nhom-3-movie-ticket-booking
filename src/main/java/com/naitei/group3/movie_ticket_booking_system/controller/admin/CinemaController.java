package com.naitei.group3.movie_ticket_booking_system.controller.admin;

import com.naitei.group3.movie_ticket_booking_system.dto.request.CinemaFilterReq;
import com.naitei.group3.movie_ticket_booking_system.dto.response.CinemaDTO;
import com.naitei.group3.movie_ticket_booking_system.entity.Hall;
import com.naitei.group3.movie_ticket_booking_system.dto.response.ExcelErrorDTO;
import com.naitei.group3.movie_ticket_booking_system.exception.ExcelValidationException;
import com.naitei.group3.movie_ticket_booking_system.service.CinemaService;
import com.naitei.group3.movie_ticket_booking_system.service.HallService;

import com.naitei.group3.movie_ticket_booking_system.service.impl.ExcelCinemaServiceImpl;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;

@Controller
@RequestMapping("/admin/cinemas")
@RequiredArgsConstructor
public class CinemaController extends BaseAdminController {

    private static final Logger logger = LoggerFactory.getLogger(CinemaController.class);

    private final CinemaService cinemaService;
    private final HallService hallService;
    private final ExcelCinemaServiceImpl excelCinemaService;
    private final MessageSource messageSource;

    @GetMapping
    public String listCinemas(@ModelAttribute CinemaFilterReq filter, Model model) {
        var pageable = PageRequest.of(filter.getPage(), filter.getSize());
        Page<CinemaDTO> cinemas = cinemaService.searchCinema(filter.getKeyword(), filter.getCity(), pageable);

        model.addAttribute("cinemas", cinemas);
        model.addAttribute("totalPages", cinemas.getTotalPages());
        model.addAttribute("filter", filter);
        model.addAttribute("cities", cinemaService.getAllCities());

        return getAdminView("cinemas/index");
    }

    @GetMapping("/{id}")
    public String getCinemaDetail(@PathVariable Long id, Model model) {
        CinemaDTO cinema = cinemaService.getCinemaById(id); // nếu không có sẽ throw
        List<Hall> halls = hallService.getHallsByCinemaId(id);

        model.addAttribute("cinema", cinema);
        model.addAttribute("halls", halls);
        return getAdminView("cinemas/detail");
    }

    @GetMapping("/upload")
    public String showUploadPage(Model model) {
        return "admin/cinemas/upload-form-cinemas";
    }

    @PostMapping("/upload")
    public String uploadFile(@RequestParam("file") MultipartFile file, RedirectAttributes redirectAttributes) {
        String validationMsg = validateFile(file);
        if (validationMsg != null) {
            redirectAttributes.addFlashAttribute("message", validationMsg);
            redirectAttributes.addFlashAttribute("messageClass", "text-red-500");
            redirectAttributes.addFlashAttribute("excelErrors", null);
            return "redirect:/admin/cinemas/upload";
        }

        String fileName = file.getOriginalFilename();
        try {
            excelCinemaService.save(file);
            logger.info("Đã tải lên thành công file: {}", fileName);
            String msg = messageSource.getMessage("excel.upload.success", new Object[] { fileName },
                    LocaleContextHolder.getLocale());
            redirectAttributes.addFlashAttribute("message", msg);
            redirectAttributes.addFlashAttribute("messageClass", "text-green-500");
            redirectAttributes.addFlashAttribute("excelErrors", null);
        } catch (ExcelValidationException ex) {
            List<ExcelErrorDTO> errorList = ex.getErrors();
            logger.warn("File Excel hợp lệ hóa thất bại: {} lỗi", errorList.size());
            String msg = messageSource.getMessage("excel.upload.validationError", new Object[] { errorList.size() },
                    LocaleContextHolder.getLocale());
            redirectAttributes.addFlashAttribute("message", msg);
            redirectAttributes.addFlashAttribute("messageClass", "text-red-500");
            redirectAttributes.addFlashAttribute("excelErrors", errorList);
        } catch (Exception e) {
            logger.error("Lỗi khi xử lý file Excel: {}", fileName, e);
            String msg = messageSource.getMessage("excel.upload.error", new Object[] { e.getMessage() },
                    LocaleContextHolder.getLocale());
            redirectAttributes.addFlashAttribute("message", msg);
            redirectAttributes.addFlashAttribute("messageClass", "text-red-500");
            redirectAttributes.addFlashAttribute("excelErrors", null);
        }
        return "redirect:/admin/cinemas/upload";
    }

    private String validateFile(MultipartFile file) {
        if (file == null || file.isEmpty()) {
            logger.warn("Không có file được tải lên");
            return messageSource.getMessage("excel.upload.chooseFile", null, LocaleContextHolder.getLocale());
        }
        String fileName = file.getOriginalFilename();
        if (fileName == null || !(fileName.endsWith(".xlsx") || fileName.endsWith(".xls"))) {
            logger.warn("File tải lên không đúng định dạng Excel: {}", fileName);
            return messageSource.getMessage("excel.upload.invalidFormat", null, LocaleContextHolder.getLocale());
        }
        return null;
    }
}
