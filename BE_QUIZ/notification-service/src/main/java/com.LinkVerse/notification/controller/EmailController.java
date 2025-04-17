package com.LinkVerse.notification.controller;

import jakarta.mail.internet.MimeMessage;

import org.springframework.http.ResponseEntity;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.web.bind.annotation.*;

import com.LinkVerse.event.dto.BillEmailRequest;
import com.LinkVerse.notification.dto.ApiResponse;
import com.LinkVerse.notification.dto.request.SendEmailRequest;
import com.LinkVerse.notification.dto.response.EmailResponse;
import com.LinkVerse.notification.entity.User;
import com.LinkVerse.notification.exception.AppException;
import com.LinkVerse.notification.exception.ErrorCode;
import com.LinkVerse.notification.repository.UserRepository;
import com.LinkVerse.notification.service.EmailService;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;

@RequestMapping("/email")
@RestController
@RequiredArgsConstructor
@Slf4j
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class EmailController {
    final EmailService emailService;
    final UserRepository userRepository;
    final JavaMailSender javaMailSender;

    @GetMapping("/user/id")
    public ResponseEntity<String> getUserEmailById(@RequestParam("id") String id) {
        User user = userRepository.findById(id).orElseThrow(() -> new AppException(ErrorCode.USER_NOT_FOUND));
        return ResponseEntity.ok(user.getEmail());
    }

    @PostMapping("/bill")
    public ApiResponse<Void> sendBillEmail(@RequestBody BillEmailRequest request) {
        try {
            // ✅ Bước 1: Lấy userId từ request
            String userId = request.getUserId();
            log.info("📥 Nhận yêu cầu gửi bill. userId = {}", userId);

            if (userId == null || userId.isBlank()) {
                log.error("❌ userId không hợp lệ: {}", request);
                throw new AppException(ErrorCode.EMAIL_SEND_FAILED);
            }

            // ✅ Bước 2: Truy vấn user từ DB
            User user = userRepository.findById(userId).orElseThrow(() -> {
                log.error("❌ Không tìm thấy user với id: {}", userId);
                return new AppException(ErrorCode.USER_NOT_FOUND);
            });

            String toEmail = user.getEmail();
            if (toEmail == null || toEmail.isBlank()) {
                log.error("❌ Email của userId {} bị null hoặc rỗng", userId);
                throw new AppException(ErrorCode.EMAIL_SEND_FAILED);
            }

            // ✅ Bước 3: Gửi mail
            String content = "<h3>Cảm ơn bạn đã quyên góp!</h3>"
                    + "<p>Chiến dịch: " + request.getCampaignTitle() + "</p>"
                    + "<p>Số tiền: " + request.getAmount() + " VND</p>"
                    + "<p>Thời gian: " + request.getTime() + "</p>";

            log.info("📨 Gửi email đến: {}\n📄 Nội dung: {}", toEmail, content);

            MimeMessage message = javaMailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true);
            helper.setTo(toEmail);
            helper.setSubject("Hóa đơn quyên góp");
            helper.setText(content, true);
            javaMailSender.send(message);

            return ApiResponse.<Void>builder().code(200).message("Email sent").build();
        } catch (Exception e) {
            log.error("❌ Lỗi gửi email hóa đơn: ", e);
            throw new AppException(ErrorCode.EMAIL_SEND_FAILED);
        }
    }

    @PostMapping("/send")
    ApiResponse<EmailResponse> sendEmail(@RequestBody SendEmailRequest request) {
        return ApiResponse.<EmailResponse>builder()
                .result(emailService.sendEmail(request))
                .build();
    }

    @PostMapping("/send-login-notification")
    public void sendLoginNotification(@RequestBody User user) {
        emailService.sendLoginNotificationEmail(user);
    }
    //    @PostMapping("/send-support-join-url")
    //    public ApiResponse<Void> sendSupportJoinUrl(@RequestParam String email, @RequestParam String joinUrl) {
    //        emailService.sendSupportJoinUrl(email, joinUrl);
    //        return ApiResponse.<Void>builder()
    //                .code(1000)
    //                .message("Support session email sent successfully")
    //                .build();
    //    }

    @GetMapping("/user")
    public ResponseEntity<User> getUserByEmail(@RequestParam("email") String email) {
        User user = emailService.getUserByEmail(email);
        return ResponseEntity.ok(user);
    }

    @GetMapping("/user/email")
    public ResponseEntity<String> getUserEmail(@RequestParam("username") String username) {
        String email = emailService.getUserEmailByUsername(username);
        if (email == null || email.isEmpty()) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(email);
    }

    @PostMapping("/send-forget-pass")
    ApiResponse<Void> sendEmailForgetPass(@RequestParam String email) {
        try {
            User user = userRepository.findByEmail(email).orElseThrow(() -> new AppException(ErrorCode.USER_NOT_FOUND));
            emailService.sendEmailForgetPass(email, user);
            return ApiResponse.<Void>builder()
                    .code(1000)
                    .message("Email sent successfully")
                    .build();
        } catch (Exception e) {
            return ApiResponse.<Void>builder()
                    .code(500)
                    .message("Failed to send email: " + e.getMessage())
                    .build();
        }
    }

    @PostMapping("/reset-password")
    public ApiResponse<Void> resetPassword(@RequestParam String token, @RequestParam String newPassword) {
        try {
            emailService.resetPassword(token, newPassword);
            return ApiResponse.<Void>builder()
                    .code(1000)
                    .message("Password reset successfully")
                    .build();
        } catch (Exception e) {
            return ApiResponse.<Void>builder()
                    .code(500)
                    .message("Failed to reset password: " + e.getMessage())
                    .build();
        }
    }

    @PostMapping("/send-verification")
    public ApiResponse<Void> sendEmailVerification(@RequestParam String email) {
        emailService.sendEmailVerification(email);
        return ApiResponse.<Void>builder()
                .code(1000)
                .message("Verification email sent successfully")
                .build();
    }

    @GetMapping("/verify")
    public ApiResponse<Void> verifyEmail(@RequestParam String token) {
        return emailService.verifyEmail(token);
    }
}
