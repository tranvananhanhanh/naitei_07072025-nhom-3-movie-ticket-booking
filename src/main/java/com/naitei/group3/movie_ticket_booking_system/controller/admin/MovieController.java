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
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;

import com.naitei.group3.movie_ticket_booking_system.dto.request.MovieFilterReq;
import com.naitei.group3.movie_ticket_booking_system.dto.response.MovieDTO;
import com.naitei.group3.movie_ticket_booking_system.dto.response.*;
import com.naitei.group3.movie_ticket_booking_system.exception.ExcelValidationException;
import com.naitei.group3.movie_ticket_booking_system.service.MovieService;
import com.naitei.group3.movie_ticket_booking_system.service.impl.ExcelMovieServiceImpl;

import java.util.List;

@Controller
@RequestMapping("/admin/movies")
public class MovieController extends BaseAdminController {

    private static final Logger logger = LoggerFactory.getLogger(MovieController.class);

    private final ExcelMovieServiceImpl excelService;
    private final MovieService movieService;

    @Autowired
    public MovieController(ExcelMovieServiceImpl excelService, MovieService movieService) {
        this.excelService = excelService;
        this.movieService = movieService;
    }

    @GetMapping("/upload")
    public String showUploadPage(Model model) {
        if (!model.containsAttribute("message")) {
            model.addAttribute("message", "");
            model.addAttribute("messageClass", "");
        }
        if (!model.containsAttribute("excelErrors")) {
            model.addAttribute("excelErrors", null);
        }
        return "admin/movies/upload-form";
    }

    @PostMapping("/upload")
    public String uploadFile(@RequestParam("file") MultipartFile file, Model model) {
        String validationMsg = validateFile(file);
        if (validationMsg != null) {
            model.addAttribute("message", validationMsg);
            model.addAttribute("messageClass", "text-red-500");
            model.addAttribute("excelErrors", null);
            return "admin/movies/upload-form";
        }

        String fileName = file.getOriginalFilename();
        try {
            excelService.save(file);
            logger.info("Đã tải lên thành công file: {}", fileName);
            model.addAttribute("message", String.format("Tải file thành công: %s", fileName));
            model.addAttribute("messageClass", "text-green-500");
            model.addAttribute("excelErrors", null);
            return "admin/movies/upload-form";
        } catch (ExcelValidationException ex) {
            List<ExcelErrorDTO> errorList = ex.getErrors();
            logger.warn("File Excel hợp lệ hóa thất bại: {} lỗi", errorList.size());
            model.addAttribute("message", String.format("Có %d lỗi khi nhập file!", errorList.size()));
            model.addAttribute("messageClass", "text-red-500");
            model.addAttribute("excelErrors", errorList);
            return "admin/movies/upload-form";
        } catch (Exception e) {
            logger.error("Lỗi khi xử lý file Excel: {}", fileName, e);
            model.addAttribute("message", "Lỗi khi tải file: " + e.getMessage());
            model.addAttribute("messageClass", "text-red-500");
            model.addAttribute("excelErrors", null);
            return "admin/movies/upload-form";
        }
    }

    @GetMapping
    public String listMovies(
            @ModelAttribute MovieFilterReq filter,
            Model model) {
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

    private String validateFile(MultipartFile file) {
        if (file == null || file.isEmpty()) {
            logger.warn("Không có file được tải lên");
            return "Vui lòng chọn một file";
        }
        String fileName = file.getOriginalFilename();
        if (fileName == null || !(fileName.endsWith(".xlsx") || fileName.endsWith(".xls"))) {
            logger.warn("File tải lên không đúng định dạng Excel: {}", fileName);
            return "Vui lòng tải lên file Excel (.xlsx hoặc .xls)";
        }
        return null;
    }
}
