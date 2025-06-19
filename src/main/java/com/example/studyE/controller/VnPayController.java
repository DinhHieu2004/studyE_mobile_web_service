package com.example.studyE.controller;

import com.example.studyE.dto.request.PaymentRequest;
import com.example.studyE.dto.response.UserResponse;
import com.example.studyE.entity.User;
import com.example.studyE.service.AuthService;
import com.example.studyE.util.Config;
import com.example.studyE.util.JwtUtil;
import com.example.studyE.util.VnPayUtils;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.text.SimpleDateFormat;
import java.util.*;

@RestController
@RequestMapping("/api/payment")
public class VnPayController {

    @Autowired
    private AuthService authService;
    

    @PostMapping("/create")
    public ResponseEntity<Map<String, String>> createPayment(@RequestBody PaymentRequest paymentRequest, HttpServletRequest request) {
        String authHeader = request.getHeader("Authorization");
        String token = authHeader != null ? authHeader.replace("Bearer ", "") : null;
        if (token == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(Map.of("error", "Missing token"));
        }
        String uid = JwtUtil.extractUid(token);
        UserResponse user = authService.getUserByUid(uid);
        if (user == null) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Map.of("error", "User not found"));
        }

        Map<String, String> vnp_Params = new HashMap<>();

        String vnp_TxnRef = Config.getRandomNumber(8);
        String vnp_IpAddr = request.getRemoteAddr();
        long amount = paymentRequest.getAmount() * 100;

        String returnUrl = paymentRequest.getReturnUrl() != null ? paymentRequest.getReturnUrl() : Config.vnp_ReturnUrl;
        String notifyUrl = paymentRequest.getNotifyUrl() != null ? paymentRequest.getNotifyUrl() : Config.vnp_NotifyUrl;

        vnp_Params.put("vnp_Version", "2.1.0");
        vnp_Params.put("vnp_Command", "pay");
        vnp_Params.put("vnp_TmnCode", Config.vnp_TmnCode);
        vnp_Params.put("vnp_Amount", String.valueOf(amount));
        vnp_Params.put("vnp_CurrCode", "VND");
        vnp_Params.put("vnp_TxnRef", vnp_TxnRef);
        vnp_Params.put("vnp_OrderInfo", "subscription|uid=" + user.getUid());
        vnp_Params.put("vnp_OrderType", "other");
        vnp_Params.put("vnp_Locale", "vn");
        vnp_Params.put("vnp_ReturnUrl", returnUrl);
//        vnp_Params.put("vnp_NotifyUrl", notifyUrl);
        vnp_Params.put("vnp_IpAddr", vnp_IpAddr);

        Calendar cld = Calendar.getInstance(TimeZone.getTimeZone("Etc/GMT+7"));
        SimpleDateFormat formatter = new SimpleDateFormat("yyyyMMddHHmmss");
        String vnp_CreateDate = formatter.format(cld.getTime());
        cld.add(Calendar.MINUTE, 15);
        String vnp_ExpireDate = formatter.format(cld.getTime());

        vnp_Params.put("vnp_CreateDate", vnp_CreateDate);
        vnp_Params.put("vnp_ExpireDate", vnp_ExpireDate);

        List<String> fieldNames = new ArrayList<>(vnp_Params.keySet());
        Collections.sort(fieldNames);

        StringBuilder hashData = new StringBuilder();
        StringBuilder query = new StringBuilder();
        for (Iterator<String> it = fieldNames.iterator(); it.hasNext(); ) {
            String field = it.next();
            String value = URLEncoder.encode(vnp_Params.get(field), StandardCharsets.US_ASCII);
            hashData.append(field).append("=").append(value);
            query.append(URLEncoder.encode(field, StandardCharsets.US_ASCII)).append("=").append(value);
            if (it.hasNext()) {
                hashData.append("&");
                query.append("&");
            }
        }

        String vnp_SecureHash = Config.hmacSHA512(Config.secretKey, hashData.toString());
        String paymentUrl = Config.vnp_PayUrl + "?" + query + "&vnp_SecureHash=" + vnp_SecureHash;

        Map<String, String> response = new HashMap<>();
        response.put("paymentUrl", paymentUrl);
        authService.updateSubscription(uid, "super");
        return ResponseEntity.ok(response);
    }

    @GetMapping("/vnpay_return")
    public ResponseEntity<?> handleVnPayReturn(HttpServletRequest request) {
        Map<String, String> vnpParams = VnPayUtils.getVnPayResponseParams(request);
        String orderInfo = vnpParams.get("vnp_OrderInfo");
        String uid = extractUidFromOrderInfo(orderInfo);

        UserResponse userResponse = authService.getUserByUid(uid);
        if (userResponse == null) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("User not found");
        }

        authService.updateSubscription(uid, "super");

        return ResponseEntity.ok("Thanh toán thành công!");
    }

    private String extractUidFromOrderInfo(String orderInfo) {
        System.out.println("OrderInfo: " + orderInfo);
        if (orderInfo == null) return null;
        String[] parts = orderInfo.split("uid=");
        return parts.length == 2 ? parts[1] : null;
    }
}
