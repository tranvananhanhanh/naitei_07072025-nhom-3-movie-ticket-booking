package com.naitei.group3.movie_ticket_booking_system.controller.admin;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;

import com.naitei.group3.movie_ticket_booking_system.dto.request.MovieFilterReq;
import com.naitei.group3.movie_ticket_booking_system.dto.response.MovieDTO;
import com.naitei.group3.movie_ticket_booking_system.dto.response.ExcelErrorDTO;
import com.naitei.group3.movie_ticket_booking_system.exception.ExcelValidationException;
import com.naitei.group3.movie_ticket_booking_system.service.MovieService;
import com.naitei.group3.movie_ticket_booking_system.service.impl.ExcelMovieServiceImpl;
import java.util.List;

@Controller("adminMovieController")
@RequestMapping("/admin/movies")
public class MovieController extends BaseAdminController {

    private static final Logger logger = LoggerFactory.getLogger(MovieController.class);

    private final MessageSource messageSource;
    private final ExcelMovieServiceImpl excelService;
    private final MovieService movieService;

    public MovieController(MessageSource messageSource, ExcelMovieServiceImpl excelService, MovieService movieService) {
        this.messageSource = messageSource;
        this.excelService = excelService;
        this.movieService = movieService;
    }

    @GetMapping("/upload")
    public String showUploadPage(Model model) {
        if (!model.containsAttribute("message")) {
            setMessage(model, "", "", null);
        }
        if (!model.containsAttribute("excelErrors")) {
            model.addAttribute("excelErrors", null);
        }
        return getAdminView("movies/upload-form-movies");
    }

    @PostMapping("/upload")
    public String uploadFile(@RequestParam("file") MultipartFile file, Model model) {
        String validationMsg = validateFile(file);
        if (validationMsg != null) {
            setMessage(model, validationMsg, "text-red-500", null);
            return getAdminView("movies/upload-form-movies");
        }

        String fileName = file.getOriginalFilename();
        try {
            excelService.save(file);
            logger.info("Đã tải lên thành công file: {}", fileName);
            String msg = messageSource.getMessage("excel.upload.success", new Object[] { fileName },
                    LocaleContextHolder.getLocale());
            setMessage(model, msg, "text-green-500", null);
        } catch (ExcelValidationException ex) {
            List<ExcelErrorDTO> errorList = ex.getErrors();
            logger.warn("File Excel hợp lệ hóa thất bại: {} lỗi", errorList.size());
            String msg = messageSource.getMessage("excel.upload.validationError", new Object[] { errorList.size() },
                    LocaleContextHolder.getLocale());
            setMessage(model, msg, "text-red-500", errorList);
        } catch (Exception e) {
            logger.error("Lỗi khi xử lý file Excel: {}", fileName, e);
            String msg = messageSource.getMessage("excel.upload.error", new Object[] { e.getMessage() },
                    LocaleContextHolder.getLocale());
            setMessage(model, msg, "text-red-500", null);
        }
        return getAdminView("movies/upload-form-movies");
    }

    @GetMapping
    public String listMovies(@ModelAttribute MovieFilterReq filter, Model model) {
        PageRequest pageable = PageRequest.of(filter.getPage(), filter.getSize());
        Page<MovieDTO> movies = movieService.filterMovies(
                filter.getKeyword(), filter.getYear(), filter.getGenreName(), filter.getIsActive(), pageable);
        model.addAttribute("movies", movies);
        model.addAttribute("currentPage", filter.getPage());
        model.addAttribute("totalPages", movies.getTotalPages());
        model.addAttribute("keyword", filter.getKeyword());
        model.addAttribute("year", filter.getYear());
        model.addAttribute("genreName", filter.getGenreName());
        model.addAttribute("isActive", filter.getIsActive());
        return getAdminView("movies/index");
    }

    @GetMapping("/{id}")
    public String getMovieDetail(@PathVariable Long id, Model model) {
        MovieDTO movie = movieService.getMovieById(id);
        model.addAttribute("movie", movie);
        return getAdminView("movies/show");
    }

    private void setMessage(Model model, String message, String messageClass, List<ExcelErrorDTO> excelErrors) {
        model.addAttribute("message", message);
        model.addAttribute("messageClass", messageClass);
        model.addAttribute("excelErrors", excelErrors);
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
