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

import com.naitei.group3.movie_ticket_booking_system.dto.request.MovieFilterReq;
import com.naitei.group3.movie_ticket_booking_system.dto.response.MovieDTO;
import com.naitei.group3.movie_ticket_booking_system.service.MovieService;
import org.springframework.web.bind.annotation.PathVariable;
import com.naitei.group3.movie_ticket_booking_system.service.impl.ExcelMovieServiceImpl;

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

    // Trang upload chỉ nhận /admin/movies/upload (GET)
    @GetMapping("/upload")
    public String showUploadPage(Model model) {
        if (!model.containsAttribute("message")) {
            model.addAttribute("message", "");
            model.addAttribute("messageClass", "");
        }
        return "admin/movies/upload-form";
    }

    // Xử lý upload chỉ nhận /admin/movies/upload (POST)
    @PostMapping("/upload")
    public String uploadFile(@RequestParam("file") MultipartFile file, Model model) {
        String validationMsg = validateFile(file);
        if (validationMsg != null) {
            model.addAttribute("message", validationMsg);
            model.addAttribute("messageClass", "text-red-500");
            return "admin/movies/upload-form";
        }

        String fileName = file.getOriginalFilename();
        try {
            excelService.save(file);
            logger.info("Đã tải lên thành công file: {}", fileName);
            model.addAttribute("message", String.format("Tải file thành công: %s", fileName));
            model.addAttribute("messageClass", "text-green-500");
            return "admin/movies/upload-form";
        } catch (Exception e) {
            logger.error("Lỗi khi xử lý file Excel: {}", fileName, e);
            model.addAttribute("message", "Lỗi khi tải file: " + e.getMessage());
            model.addAttribute("messageClass", "text-red-500");
            return "admin/movies/upload-form";
        }
    }

    // Trang danh sách phim chỉ nhận /admin/movies (GET)
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

    // Đặt private method ở cuối class
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
