package com.naitei.group3.movie_ticket_booking_system.controller.admin;

import com.naitei.group3.movie_ticket_booking_system.dto.request.ShowtimeFilterReq;
import com.naitei.group3.movie_ticket_booking_system.dto.response.ShowtimeDTO;
import com.naitei.group3.movie_ticket_booking_system.exception.ExcelValidationException;
import com.naitei.group3.movie_ticket_booking_system.service.ShowtimeService;
import com.naitei.group3.movie_ticket_booking_system.service.impl.ExcelShowtimeServiceImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequiredArgsConstructor
@RequestMapping("/admin/showtimes")
public class ShowtimeController extends BaseAdminController {

    private static final String UPLOAD_REDIRECT = "redirect:/admin/showtimes/upload";

    private final ShowtimeService service;
    private final ExcelShowtimeServiceImpl excelShowtimeService;
    private final MessageSource messageSource;

    @GetMapping
    public String showTimes(
            @ModelAttribute ShowtimeFilterReq filter,
            Model model) {

        PageRequest pageable = PageRequest.of(filter.getPage(), filter.getSize());
        Page<ShowtimeDTO> showtimes = service.filterShowtime(filter, pageable);

        model.addAttribute("showtimes", showtimes);
        model.addAttribute("filter", filter);

        return getAdminView("showtimes/index");
    }

    @GetMapping("/{id}")
    public String getShowtimeDetail(@PathVariable Long id, Model model) {
        ShowtimeDTO showtime = service.getShowtimeById(id);
        model.addAttribute("showtime", showtime);
        return getAdminView("showtimes/show");
    }

    @GetMapping("/upload")
    public String showUploadPage() {
        return "admin/showtimes/upload-form-showtimes";
    }

    @PostMapping("/upload")
    public String uploadFile(@RequestParam("file") MultipartFile file, RedirectAttributes redirectAttributes) {
        String validationMsg = validateFile(file);
        if (validationMsg != null) {
            redirectAttributes.addFlashAttribute("message", validationMsg);
            redirectAttributes.addFlashAttribute("messageClass", "text-red-500");
            redirectAttributes.addFlashAttribute("excelErrors", null);
            return UPLOAD_REDIRECT;
        }

        String fileName = file.getOriginalFilename();
        try {
            excelShowtimeService.save(file);
            String msg = messageSource.getMessage("excel.upload.success", new Object[] { fileName },
                    LocaleContextHolder.getLocale());
            redirectAttributes.addFlashAttribute("message", msg);
            redirectAttributes.addFlashAttribute("messageClass", "text-green-500");
            redirectAttributes.addFlashAttribute("excelErrors", null);
        } catch (ExcelValidationException ex) {
            redirectAttributes.addFlashAttribute("message", messageSource.getMessage("excel.upload.validationError",
                    new Object[] { ex.getErrors().size() }, LocaleContextHolder.getLocale()));
            redirectAttributes.addFlashAttribute("messageClass", "text-red-500");
            redirectAttributes.addFlashAttribute("excelErrors", ex.getErrors());
        } catch (Exception e) {
            String msg = messageSource.getMessage("excel.upload.error", new Object[] { e.getMessage() },
                    LocaleContextHolder.getLocale());
            redirectAttributes.addFlashAttribute("message", msg);
            redirectAttributes.addFlashAttribute("messageClass", "text-red-500");
            redirectAttributes.addFlashAttribute("excelErrors", null);
        }
        return UPLOAD_REDIRECT;
    }

    private String validateFile(MultipartFile file) {
        if (file == null || file.isEmpty()) {
            return messageSource.getMessage("excel.upload.chooseFile", null, LocaleContextHolder.getLocale());
        }
        String fileName = file.getOriginalFilename();
        if (fileName == null || !(fileName.endsWith(".xlsx") || fileName.endsWith(".xls"))) {
            return messageSource.getMessage("excel.upload.invalidFormat", null, LocaleContextHolder.getLocale());
        }
        return null;
    }
}
