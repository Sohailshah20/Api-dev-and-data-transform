package com.shah.volopay.Assignment;

import com.univocity.parsers.common.record.Record;
import com.univocity.parsers.csv.CsvParser;
import com.univocity.parsers.csv.CsvParserSettings;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import java.io.IOException;
import java.io.InputStream;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.time.YearMonth;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class ServiceClass {

    private final SoftwarePurchaseRepository repository;

    public ServiceClass(SoftwarePurchaseRepository repository) {
        this.repository = repository;
    }


    public String uploadCsvToDb(MultipartFile multipartFile) throws IOException {
        List<SoftwarePurchase> softwarePurchases = new ArrayList<>(10000);
        InputStream inputStream = multipartFile.getInputStream();
        CsvParserSettings settings = new CsvParserSettings();
        settings.setHeaderExtractionEnabled(true);
        settings.getFormat().setLineSeparator("\n");
        CsvParser parser = new CsvParser(settings);
        List<Record> records = parser.parseAllRecords(inputStream);
        String pattern = "yyyy-MM-dd HH:mm:ss Z";
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern(pattern);

        records.forEach(record -> {
            SoftwarePurchase purchase = new SoftwarePurchase();
            purchase.setId(Integer.parseInt(record.getString("id")));
            String date = record.getString("date");
            OffsetDateTime offsetDateTime = OffsetDateTime.parse(date, formatter);
            Date forDate = Date.from(offsetDateTime.toInstant());
            purchase.setDate(forDate);
            purchase.setUser(record.getString("user"));
            purchase.setDepartment(record.getString("department"));
            purchase.setSoftware(record.getString("software"));
            purchase.setSeats(Integer.parseInt(record.getString("seats")));
            try {

                purchase.setAmount(record.getDouble("amount"));
            }catch (NumberFormatException e){
                System.out.println(record.getString("amount"));
            }
            softwarePurchases.add(purchase);
        });
        repository.saveAll(softwarePurchases);
        return "saved successfully";
    }
    public ResponseEntity<Integer> getTotalItems(String start, String end, String department) throws ParseException {
        Date s = parseStringToDate(start);
        Date e = parseStringToDate(end);
        boolean q3 = isQ3(s, e);
        if (!q3){
            return ResponseEntity.badRequest().build();
        }
        List<SoftwarePurchase> allPurchases = repository.findAllByDepartmentAndDateBetween(department, s,e);
        int sum = allPurchases.stream()
                .mapToInt(SoftwarePurchase::getSeats)
                .sum();
        return ResponseEntity.ok(sum);

    }

    public ResponseEntity<String> getMostTotalItem(String itemBy,String start, String end ) throws ParseException {
        Date s = parseStringToDate(start);
        Date e = parseStringToDate(end);
        if (itemBy.equals("quantity")){
            if(!isQ4(s,e)){
                return ResponseEntity.badRequest().build();
            }
        }
        if (itemBy.equals("price")){
            if(!isQ2(s,e)){
                return ResponseEntity.badRequest().build();
            }
        }
        List<SoftwarePurchase> purchases = repository.findAllByDateBetween(s, e);
        Map<String, List<SoftwarePurchase>> purchasesBySoftware = purchases.stream()
                .collect(Collectors.groupingBy(SoftwarePurchase::getSoftware));
        Map<String, Integer> seatsSoldBySoftware = purchasesBySoftware.entrySet().stream()
                .collect(Collectors.toMap(Map.Entry::getKey,
                        entry -> entry.getValue().stream().mapToInt(SoftwarePurchase::getSeats).sum()));
        List<String> sortedSoftwarebyquantity = seatsSoldBySoftware.entrySet().stream()
                .sorted(Map.Entry.comparingByValue(Comparator.reverseOrder()))
                .map(Map.Entry::getKey)
                .collect(Collectors.toList());
        sortedSoftwarebyquantity.forEach(System.out::println);
        String secondMostSoldSoftware = sortedSoftwarebyquantity.get(1);
        Map<String, Double> totalPriceBySoftware = purchasesBySoftware.entrySet().stream()
                .collect(Collectors.toMap(Map.Entry::getKey,
                        entry -> entry.getValue().stream().mapToDouble(sa -> sa.getAmount() * sa.getSeats()).sum()));
        List<String> sortedSoftwarebyprice = totalPriceBySoftware.entrySet().stream()
                .sorted(Map.Entry.comparingByValue(Comparator.reverseOrder()))
                .map(Map.Entry::getKey)
                .collect(Collectors.toList());
        String fourthMostSoldSoftware = sortedSoftwarebyprice.get(3);
        if (itemBy.equals("quantity")) return ResponseEntity.ok(secondMostSoldSoftware);
        if (itemBy.equals("price")) return ResponseEntity.ok(fourthMostSoldSoftware);
        return ResponseEntity.badRequest().build();
    }

    public ResponseEntity<List<String>> departmentWiseSold(String start, String end ) throws ParseException {
        Date s = parseStringToDate(start);
        Date e = parseStringToDate(end);
        List<SoftwarePurchase> purchases = repository.findAllByDateBetween(s, e);
        Map<String, List<SoftwarePurchase>> purchasesByDepartment = purchases.stream()
                .collect(Collectors.groupingBy(SoftwarePurchase::getDepartment));
        Map<String, Integer> soldItemsByDepartment = purchasesByDepartment.entrySet().stream()
                .collect(Collectors.toMap(Map.Entry::getKey,
                        entry -> entry.getValue().stream().mapToInt(SoftwarePurchase::getSeats).sum()));
        int totalSoldItems = soldItemsByDepartment.values().stream()
                .mapToInt(Integer::intValue)
                .sum();
        List<String> departmentSalesList = soldItemsByDepartment.entrySet().stream()
                .map(entry -> entry.getKey() + ": " + ((double) entry.getValue() / totalSoldItems) * 100 + "%")
                .collect(Collectors.toList());
        return ResponseEntity.ok(departmentSalesList);

    }

    public List<Integer> monthlySales(String software, String year) throws ParseException {
        Date fyear = parseStringToDate(year + "-01-01 00:00:00");
        System.out.println("***************************" + fyear);
        Date eyear = parseStringToDate(year+"-12-31 23:59:59");
        System.out.println("***************************" + eyear);
        List<SoftwarePurchase> swpurchases = repository.findAllBySoftwareAndDateBetween(software, fyear,eyear);
        System.out.println("*************************"+swpurchases);
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        Map<YearMonth, Integer> sumOfSeatsByMonth = swpurchases.stream()
                .collect(Collectors.groupingBy(purchase -> {
                    SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                    java.util.Date utilDate = purchase.getDate();
                    String dateString = dateFormat.format(utilDate);
                    LocalDateTime localDateTime = LocalDateTime.parse(dateString, formatter);
                    return YearMonth.from(localDateTime);
                }, Collectors.summingInt(SoftwarePurchase::getSeats)));

// Get the list of integers representing the sum of seats for each month
        List<Integer> monthlySales = sumOfSeatsByMonth.values().stream()
                .collect(Collectors.toList());
        return monthlySales;


    }

    public  Date parseStringToDate(String dateString) throws ParseException {
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        return dateFormat.parse(dateString);
    }

    public  boolean isQ3(Date startDate, Date endDate) {
        Calendar startCalendar = Calendar.getInstance();
        startCalendar.setTime(startDate);
        int startMonth = startCalendar.get(Calendar.MONTH);

        Calendar endCalendar = Calendar.getInstance();
        endCalendar.setTime(endDate);
        int endMonth = endCalendar.get(Calendar.MONTH);

        // Check if the start and end dates are in Q3
        boolean isStartInQ3 = startMonth >= Calendar.JULY;
        boolean isEndInQ3 = endMonth <= Calendar.SEPTEMBER;

        // Check if both start and end dates are in Q3
        return isStartInQ3 && isEndInQ3;
    }
    public  boolean isQ4(Date startDate, Date endDate) {
        Calendar startCalendar = Calendar.getInstance();
        startCalendar.setTime(startDate);
        int startMonth = startCalendar.get(Calendar.MONTH);

        Calendar endCalendar = Calendar.getInstance();
        endCalendar.setTime(endDate);
        int endMonth = endCalendar.get(Calendar.MONTH);

        // Check if the start and end dates are in Q3
        boolean isStartInQ3 = startMonth >= Calendar.OCTOBER;
        boolean isEndInQ3 = endMonth <= Calendar.DECEMBER;

        // Check if both start and end dates are in Q3
        return isStartInQ3 && isEndInQ3;
    }

    public  boolean isQ2(Date startDate, Date endDate) {
        Calendar startCalendar = Calendar.getInstance();
        startCalendar.setTime(startDate);
        int startMonth = startCalendar.get(Calendar.MONTH);

        Calendar endCalendar = Calendar.getInstance();
        endCalendar.setTime(endDate);
        int endMonth = endCalendar.get(Calendar.MONTH);

        // Check if the start and end dates are in Q3
        boolean isStartInQ3 = startMonth >= Calendar.APRIL;
        boolean isEndInQ3 = endMonth <= Calendar.JUNE;

        // Check if both start and end dates are in Q3
        return isStartInQ3 && isEndInQ3;
    }

}
