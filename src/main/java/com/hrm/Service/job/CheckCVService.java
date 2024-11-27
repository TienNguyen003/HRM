package com.hrm.Service.job;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.hrm.Entity.job.Disqualified;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.text.PDFTextStripper;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class CheckCVService {
    DisqualifiedService disqualifiedService;

    public List<Map<String, Object>> uploadCV(MultipartFile[] file, List<Map<String, String>> data) {
        try {
            List<Map<String, Object>> results = new ArrayList<>();

            for (MultipartFile files : file) {
                String content = extractContentFromPdf(files);
                String text = numberSections(content);

                Map<String, String> info = getInfo(content);

                boolean checkCVExit = disqualifiedService.getDisqualified(info.get("email"), info.get("phone"));
                if(checkCVExit) {
                    Map<String, Object> result = new HashMap<>();
                    result.put("fileName", files.getOriginalFilename());
                    result.put("requirements", "CV đã bị loại");
                    results.add(result);
                    return results;
                }

                List<Map<String, String>> itemsWithRequireZero = new ArrayList<>();
                List<Map<String, String>> matchesRequirements = new ArrayList<>();

                int flagRequireObli = 0;
                int flagRequireObliCount = 0;
                int flagTotal = 0;
                int flagCheck = 0;

                for (Map<String, String> item : data) {
                    if (item.get("require").equals("1")) {
                        flagRequireObli++;
                        boolean contains = content.toLowerCase().contains(item.get("value").toLowerCase());
                        String message = String.format("Từ khóa \"%s\" %s",
                                item.get("value"),
                                contains ? "xuất hiện" : "không xuất hiện");
                        flagRequireObliCount = contains ? flagRequireObliCount++ : flagRequireObliCount;
                        Map<String, String> newItem = new HashMap<>();
                        newItem.put("message", message);
                        matchesRequirements.add(newItem);
                    } else {
                        itemsWithRequireZero.add(item);
                    }
                }

                if (!itemsWithRequireZero.isEmpty()) {
                    List<Map<String, String>> zeroRequirementResults = checkAgainstRequirements(content, text, itemsWithRequireZero);

                    Map<String, String> lastMessage = zeroRequirementResults.getLast();
                    flagTotal = lastMessage.get("flagTotal") != null ? Integer.parseInt(lastMessage.get("flagTotal")) : 0;
                    flagCheck = lastMessage.get("flagCheck") != null ? Integer.parseInt(lastMessage.get("flagCheck")): 0;

                    matchesRequirements.addAll(zeroRequirementResults);
                }

                boolean flagRemove = flagCheck + flagRequireObliCount > (flagTotal + flagRequireObli) / 2;

                Disqualified disqualified = Disqualified.builder()
                                            .dob(info.get("dob"))
                                            .email(info.get("email"))
                                            .phone(info.get("phone")).build();
                if(!flagRemove) disqualifiedService.createDisqualified(disqualified, files, files.getName());

                Map<String, Object> result = new HashMap<>();
                result.put("fileName", files.getOriginalFilename());
                result.put("requirements", matchesRequirements);

                results.add(result);
            }

            return results;
        } catch (IOException ignored) {

        }
        return null;
    }

    private static String numberSections(String text) {
        String regex = "(?m)^[\\p{Lu} ]{2,}$";
        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(text);

        StringBuilder result = new StringBuilder();
        result.append("[");
        boolean firstEntry = true;

        while (matcher.find()) {
            String matchedText = matcher.group().trim();
            if (!matchedText.isEmpty()) {
                if (!firstEntry) {
                    result.append(", ");
                } else {
                    firstEntry = false;
                }
                result.append("{ \"name\": \"").append(matchedText).append("\" , \"value\": ").append(matcher.start()).append("}");
            }
        }

        result.append("]");
        return result.toString();
    }

    private String extractContentFromPdf(MultipartFile file) throws IOException {
        try (PDDocument document = PDDocument.load(file.getInputStream())) {
            PDFTextStripper pdfStripper = new PDFTextStripper();
            return pdfStripper.getText(document);
        }
    }

    private List<Map<String, String>> checkAgainstRequirements(String content, String text, List<Map<String, String>> data) {
        Gson gson = new Gson();
        Type listType = new TypeToken<List<Map<String, Object>>>() {
        }.getType();

        // Phân tích cú pháp JSON để tạo mảng jsonArray
        List<Map<String, Object>> jsonArray = gson.fromJson(text, listType);

        // Sử dụng danh sách để giữ nguyên các mục trùng lặp
        List<Map<String, String>> keywordList = new ArrayList<>(data);

        List<Map<String, Object>> result = new ArrayList<>();

        for (int i = 0; i < jsonArray.size(); i++) {
            Map<String, Object> currentItem = jsonArray.get(i);
            String name = (String) currentItem.get("name");

            // Tìm kiếm tất cả các từ khóa phù hợp
            List<String> matchedKeywords = keywordList.stream().filter(item -> item.get("name").toLowerCase().contains(name.toLowerCase()) || name.toLowerCase().contains(item.get("name").toLowerCase())).map(item -> item.get("value").toString()).collect(Collectors.toList());

            // Nếu có từ khóa phù hợp, tạo mục cho từng từ khóa
            for (String keyword : matchedKeywords) {
                Map<String, Object> newItem = new HashMap<>(currentItem);
                newItem.put("keyword", keyword); // Lưu từng từ khóa tương ứng

                // Thêm nextValue nếu có
                if (i + 1 < jsonArray.size()) {
                    newItem.put("nextValue", jsonArray.get(i + 1).get("value"));
                }

                result.add(newItem);
            }
        }

        List<Map<String, String>> returnCheck = new ArrayList<>();
        if (result.isEmpty()) {
            Map<String, String> newItem = new HashMap<>();
            newItem.put("message", "Không có kết quả phù hợp");
            returnCheck.add(newItem);
            return returnCheck;
        }

        int flagCount = 0;
        for (Map<String, Object> item : result) {
            double start = (double) item.get("value");
            double end = item.get("nextValue") != null ? (double) item.get("nextValue") : content.length(); // Xử lý nextValue null
            if (end == 0) end = content.length();
            String keyword = (String) item.get("keyword");
            String name = (String) item.get("name");

            // Kiểm tra vị trí hợp lệ
            if (start < 0 || end > content.length()) {
                System.out.println("Vị trí không hợp lệ: " + start + ", " + end);
                continue;
            }

            // Kiểm tra sự xuất hiện của từ khóa
            String substring = content.substring((int) start, (int) end);
            boolean contains = substring.toLowerCase().contains(keyword.toLowerCase());
            String message = String.format("Từ khóa \"%s\" %s trong phần %s", keyword, contains ? "xuất hiện" : "không xuất hiện", name);
            if(contains) flagCount++;
            Map<String, String> newItem = new HashMap<>();
            newItem.put("message", message);

            returnCheck.add(newItem);
        }

        Map<String, String> newItem = new HashMap<>();
        newItem.put("flagCheck", String.valueOf(flagCount));
        newItem.put("flagTotal", String.valueOf(data.size()));
        returnCheck.add(newItem);

        return returnCheck;
    }

    private static Map<String, String> getInfo(String content) {
        String dobRegex = "\\d{2}/\\d{2}/\\d{4}";
        String emailRegex = "[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,6}";
        String phoneRegex = "\\d{10,11}";

        Pattern emailPattern = Pattern.compile(emailRegex);
        Matcher emailMatcher = emailPattern.matcher(content);
        Pattern phonePattern = Pattern.compile(phoneRegex);
        Matcher phoneMatcher = phonePattern.matcher(content);
        Pattern dobPattern = Pattern.compile(dobRegex);
        Matcher dobMatcher = dobPattern.matcher(content);

        Map<String, String> newItem = new HashMap<>();

        // Kiểm tra và thêm ngày sinh (dob)
        if (dobMatcher.find()) {
            newItem.put("dob", dobMatcher.group());
        } else {
            newItem.put("dob", "Không tìm thấy ngày sinh");
        }

        // Kiểm tra và thêm email
        if (emailMatcher.find()) {
            newItem.put("email", emailMatcher.group());
        } else {
            newItem.put("email", "Không tìm thấy email");
        }

        // Kiểm tra và thêm số điện thoại
        if (phoneMatcher.find()) {
            newItem.put("phone", phoneMatcher.group());
        } else {
            newItem.put("phone", "Không tìm thấy số điện thoại");
        }

        return newItem;
    }
}
