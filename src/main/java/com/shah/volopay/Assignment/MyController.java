package com.shah.volopay.Assignment;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

@RestController
@RequestMapping("/api")
public class MyController {
    private final ServiceClass serviceClass;

    public MyController(ServiceClass serviceClass) {
        this.serviceClass = serviceClass;
    }

    @PostMapping("/upload")
    public String uploadCsvFile(
            @RequestParam("file") MultipartFile multipartFile
    ) throws Exception {
        return serviceClass.uploadCsvToDb(multipartFile);
    }

    @GetMapping("/total_items")
    public ResponseEntity<Integer> getTotalItems(
            @RequestParam("startDate") String startDate,
            @RequestParam("endDate") String endDate,
            @RequestParam("department") String department
    ) throws ParseException {

        return serviceClass.getTotalItems(startDate,endDate,department);
    }

    @GetMapping("/nth_most_total_item")
    public ResponseEntity<String> getMostTotalItem(
            @RequestParam("itemBy") String itemBy,
            @RequestParam("startDate") String startDate,
            @RequestParam("endDate") String endDate

    ) throws ParseException {

        return serviceClass.getMostTotalItem(itemBy,startDate,endDate);
    }

    @GetMapping("/percentage_of_department_wise_sold_items")
    public ResponseEntity<List<String>> departmentWiseSold(
            @RequestParam("startDate") String startDate,
            @RequestParam("endDate") String endDate

    ) throws ParseException {

        return serviceClass.departmentWiseSold(startDate,endDate);
    }

    @GetMapping("/monthly_sales")
    public List<Integer> monthlySales(
            @RequestParam("software") String software,
            @RequestParam("year") String year

    ) throws ParseException {

        return serviceClass.monthlySales(software,year);
    }

    public  Date formatDate(String dateString) throws ParseException {
        SimpleDateFormat inputFormat = new SimpleDateFormat("yyyy-MM-dd");
        return inputFormat.parse(dateString);
    }
}
