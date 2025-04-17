package com.LinkVerse.notification.service;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.List;
import javax.imageio.ImageIO;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.rendering.PDFRenderer;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.mail.MailException;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.LinkVerse.notification.dto.ApiResponse;
import com.LinkVerse.notification.dto.request.EmailRequest;
import com.LinkVerse.notification.dto.request.SendEmailRequest;
import com.LinkVerse.notification.dto.request.Sender;
import com.LinkVerse.notification.dto.response.EmailResponse;
import com.LinkVerse.notification.entity.OtpRequest;
import com.LinkVerse.notification.entity.User;
import com.LinkVerse.notification.exception.AppException;
import com.LinkVerse.notification.exception.ErrorCode;
import com.LinkVerse.notification.repository.OtpRequestRepository;
import com.LinkVerse.notification.repository.UserRepository;
import com.LinkVerse.notification.repository.httpclient.EmailClient;
import com.nimbusds.jwt.SignedJWT;
import com.openhtmltopdf.pdfboxout.PdfRendererBuilder;

import feign.FeignException;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.experimental.NonFinal;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Slf4j
public class EmailService {
    final EmailClient emailClient;
    final JavaMailSender javaMailSender;
    final UserRepository userRepository;
    final TokenService tokenService;
    final OtpService otpService;
    final OtpStorageService otpStorageService;
    OtpRequestRepository otpRequestRepository;

    @Value("${notification.email.brevo-apikey}")
    @NonFinal
    String apiKey;

    // ZOOM
    //    public void sendSupportJoinUrl(String email, String joinUrl) {
    //        User user = userRepository.findByEmail(email)
    //                .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_FOUND));
    //
    //        try {
    //            MimeMessage message = javaMailSender.createMimeMessage();
    //            MimeMessageHelper helper = new MimeMessageHelper(message, true);
    //            String htmlContent = "<p>Click the link to join the support session:</p>" +
    //                    "<a href=\"" + joinUrl + "\">Join Support Session</a>";
    //            helper.setTo(email);
    //            helper.setSubject("Support Session Invitation");
    //            helper.setText(htmlContent, true);
    //            javaMailSender.send(message);
    //            log.info("Support session email sent successfully to {}", email);
    //        } catch (MessagingException e) {
    //            log.error("Failed to send support session email to {}: {}", email, e.getMessage());
    //            throw new RuntimeException("Cannot send email", e);
    //        }
    //    }

    public User getUserByUsername(String username) {
        System.out.println("Searching for username: " + username);
        username = username.trim();
        return userRepository.findByUsername(username).orElseThrow(() -> new AppException(ErrorCode.USER_NOT_FOUND));
    }

    public User getUserByEmail(String email) {
        return userRepository.findByEmail(email).orElseThrow(() -> new RuntimeException("User not found"));
    }

    public String getUserEmailByUsername(String username) {
        User user =
                userRepository.findByUsername(username).orElseThrow(() -> new AppException(ErrorCode.USER_NOT_FOUND));
        return user.getEmail();
    }

    //    public void sendOtpEmail(String email, int otp) {
    //        // Tìm User theo email
    //        User user = userRepository.findByEmail(email)
    //                .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_FOUND)); // Lỗi nếu không tìm thấy
    //
    //        // Sinh secretKey và OTP
    //        String secretKey = otpService.generateSecretKey();  // Nếu bạn muốn gửi secretKey qua email, có thể đưa
    // vào email
    //
    //        // Lưu secretKey vào OtpStorageService
    //        otpStorageService.storeSecretKeyForUser(user.getId(), secretKey);
    //
    //        // Gửi email chứa OTP
    //        SimpleMailMessage message = new SimpleMailMessage();
    //        message.setTo(user.getEmail());
    //        message.setSubject("Your OTP Code");
    //        message.setText("Your OTP code is: " + otp);  // Gửi OTP trong email
    //
    //        javaMailSender.send(message);
    //    }
    public void sendOtpEmail(String email, int otp) {
        User user = userRepository.findByEmail(email).orElseThrow(() -> new AppException(ErrorCode.USER_NOT_FOUND));

        OtpRequest otpRequest = otpRequestRepository
                .findByUser(user)
                .orElseGet(() -> new OtpRequest(user, 0, LocalDateTime.now().minusMinutes(4)));

        if (otpRequest.getAttempts() >= 3
                && otpRequest.getLastRequestTime().isAfter(LocalDateTime.now().minusMinutes(3))) {
            throw new AppException(ErrorCode.TOO_MANY_REQUESTS);
        }

        if (otpRequest.getLastRequestTime().isBefore(LocalDateTime.now().minusMinutes(3))) {
            otpRequest.setAttempts(0);
        }

        otpRequest.setAttempts(otpRequest.getAttempts() + 1);
        otpRequest.setLastRequestTime(LocalDateTime.now());
        otpRequestRepository.save(otpRequest);

        String secretKey = otpService.generateSecretKey();
        otpStorageService.storeSecretKeyForUser(user.getId(), secretKey);

        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(user.getEmail());
        message.setSubject("Your OTP Code");
        message.setText("Your OTP code is: " + otp);

        javaMailSender.send(message);
    }

    public EmailResponse sendEmail(SendEmailRequest request) {
        EmailRequest emailRequest = EmailRequest.builder()
                .sender(Sender.builder()
                        .name("NgocDuong")
                        .email("ngocduong2592003@gmail.com")
                        .build())
                .to(List.of(request.getTo()))
                .subject(request.getSubject())
                .htmlContent(request.getHtmlContent())
                .build();

        try {
            return emailClient.sendEmail(apiKey, emailRequest);
        } catch (FeignException e) {
            return sendEmailFallback(request);
        }
    }

    private EmailResponse sendEmailFallback(SendEmailRequest request) {
        try {
            MimeMessage message = javaMailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true);
            helper.setTo(request.getTo().getEmail());
            helper.setSubject(request.getSubject());
            helper.setText(request.getHtmlContent(), true);
            javaMailSender.send(message);
            return new EmailResponse("Email sent successfully via fallback method");
        } catch (MessagingException e) {
            throw new AppException(ErrorCode.EMAIL_SEND_FAILED);
        }
    }

    public void sendEmailForgetPass(String email, User user) {
        if (!userRepository.existsByEmail(email)) {
            log.error("Email {} does not exist in the system", email);
            throw new RuntimeException("Email does not exist in the system");
        }

        try {
            MimeMessage message = javaMailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true);
            String resetLink = "http://localhost:5173/reset-password?token=" + tokenService.generateToken(user);
            String htmlContent =
                    "<p>Click vao de thay doi mat khau:</p>" + "<a href=\"" + resetLink + "\">Reset Password</a>";
            helper.setTo(email);
            helper.setSubject("Password Reset");
            helper.setText(htmlContent, true);
            javaMailSender.send(message);
            log.info("Email sent successfully to {}", email);
        } catch (MailException | MessagingException e) {
            log.error("Failed to send email to {}: {}", email, e.getMessage());
            throw new RuntimeException("Cannot send email", e);
        }
    }

    public ApiResponse<Void> verifyEmail(String token) {
        try {
            SignedJWT signedJWT = tokenService.verifyToken(token, false);
            String userId = signedJWT.getJWTClaimsSet().getSubject();
            User user = userRepository.findById(userId).orElseThrow(() -> new AppException(ErrorCode.USER_NOT_FOUND));
            user.setEmailVerified(true);
            userRepository.save(user);
            tokenService.invalidateToken(signedJWT.getJWTClaimsSet().getJWTID());
            return ApiResponse.<Void>builder()
                    .code(1000)
                    .message("Email verified successfully")
                    .build();
        } catch (Exception e) {
            return ApiResponse.<Void>builder()
                    .code(500)
                    .message("Failed to verify email: " + e.getMessage())
                    .build();
        }
    }

    public void sendLoginNotificationEmail(User user) {
        try {
            MimeMessage message = javaMailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true);
            String htmlContent = "<p>Dear " + user.getUsername() + ",</p>"
                    + "<p>You have successfully logged in to your account.</p>";
            helper.setTo(user.getEmail());
            helper.setSubject("Login Notification");
            helper.setText(htmlContent, true);
            javaMailSender.send(message);
            log.info("Login notification email sent successfully to {}", user.getEmail());
        } catch (MailException | MessagingException e) {
            log.error("Failed to send login notification email to {}: {}", user.getEmail(), e.getMessage());
            throw new RuntimeException("Cannot send email", e);
        }
    }

    public void sendEmailVerification(String email) {
        User user = userRepository.findByEmail(email).orElseThrow(() -> new AppException(ErrorCode.USER_NOT_FOUND));
        String token = tokenService.generateToken(user);

        try {
            MimeMessage message = javaMailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true);
            String verificationLink = "http://localhost:5173/verify-email?token=" + token;
            String htmlContent = "<p>Click the link to verify your email:</p>" + "<a href=\"" + verificationLink
                    + "\">Verify Email</a>";
            helper.setTo(email);
            helper.setSubject("Email Verification");
            helper.setText(htmlContent, true);
            javaMailSender.send(message);
            log.info("Verification email sent successfully to {}", email);
        } catch (MailException | MessagingException e) {
            log.error("Failed to send verification email to {}: {}", email, e.getMessage());
            throw new RuntimeException("Cannot send email", e);
        }
    }

    public void resetPassword(String token, String newPassword) {
        try {
            SignedJWT signedJWT = tokenService.verifyToken(token, false);
            String userId = signedJWT.getJWTClaimsSet().getSubject();
            User user = userRepository.findById(userId).orElseThrow(() -> new AppException(ErrorCode.USER_NOT_FOUND));
            PasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
            user.setPassword(passwordEncoder.encode(newPassword));
            userRepository.save(user);
            tokenService.invalidateToken(signedJWT.getJWTClaimsSet().getJWTID());
        } catch (Exception e) {
            throw new RuntimeException("Failed to reset password: " + e.getMessage(), e);
        }
    }


    private byte[] generatePdfFromHtml(String html) {
        try (ByteArrayOutputStream outputStream = new ByteArrayOutputStream()) {
            PdfRendererBuilder builder = new PdfRendererBuilder();
            builder.useFastMode();

            // ✅ Load font từ resources
            try (var fontStream = getClass().getClassLoader().getResourceAsStream("fonts/DejaVuSans.ttf")) {
                if (fontStream == null) {
                    throw new RuntimeException("Font not found in classpath: fonts/DejaVuSans.ttf");
                }

                builder.useFont(() -> fontStream, "DejaVu");
            }

            builder.withHtmlContent(html, null);
            builder.toStream(outputStream);
            builder.run();

            return outputStream.toByteArray();
        } catch (IOException e) {
            throw new RuntimeException("Failed to generate PDF", e);
        }
    }

    private BufferedImage convertPdfToImage(byte[] pdfBytes) {
        try (PDDocument document = PDDocument.load(pdfBytes)) {
            PDFRenderer renderer = new PDFRenderer(document);
            BufferedImage image = renderer.renderImageWithDPI(0, 150);
            return resizeImage(image, 600);
        } catch (IOException e) {
            throw new RuntimeException("Failed to convert PDF to image", e);
        }
    }

    private BufferedImage resizeImage(BufferedImage originalImage, int targetWidth) {
        int width = originalImage.getWidth();
        int height = originalImage.getHeight();
        int targetHeight = (int) (height * (targetWidth / (double) width));

        Image tmp = originalImage.getScaledInstance(targetWidth, targetHeight, Image.SCALE_SMOOTH);
        BufferedImage resized = new BufferedImage(targetWidth, targetHeight, BufferedImage.TYPE_INT_RGB);
        Graphics2D g2d = resized.createGraphics();
        g2d.drawImage(tmp, 0, 0, null);
        g2d.dispose();
        return resized;
    }

    private byte[] bufferedImageToPngBytes(BufferedImage image) {
        try (ByteArrayOutputStream baos = new ByteArrayOutputStream()) {
            ImageIO.write(image, "png", baos);
            return baos.toByteArray();
        } catch (IOException e) {
            throw new RuntimeException("Failed to convert image to bytes", e);
        }
    }
}
