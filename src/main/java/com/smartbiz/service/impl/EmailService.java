package com.smartbiz.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import jakarta.mail.internet.MimeMessage;

@Service
@RequiredArgsConstructor
@Slf4j
public class EmailService {

    private final JavaMailSender mailSender;

    @Value("${spring.mail.username}")
    private String fromEmail;

    public void sendOtpEmail(String toEmail, String recipientName, String otp) {
        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");
            helper.setFrom(fromEmail);
            helper.setTo(toEmail);
            helper.setSubject("SmartBiz — Password Reset OTP");
            helper.setText(buildOtpEmailHtml(recipientName, otp), true);
            mailSender.send(message);
            log.info("OTP email sent to {}", toEmail);
        } catch (Exception e) {
            log.error("Failed to send OTP email to {}: {}", toEmail, e.getMessage());
            // Still throw so caller knows
            throw new RuntimeException("Failed to send email. Please check your email configuration.");
        }
    }

    private String buildOtpEmailHtml(String name, String otp) {
        return """
            <!DOCTYPE html>
            <html>
            <head>
              <meta charset="UTF-8">
              <style>
                body { font-family: 'DM Sans', Arial, sans-serif; background: #f8f9ff; margin: 0; padding: 0; }
                .container { max-width: 520px; margin: 40px auto; background: #fff; border-radius: 20px; overflow: hidden; box-shadow: 0 4px 24px rgba(99,102,241,0.10); }
                .header { background: linear-gradient(135deg, #312e81 0%%, #4f46e5 100%%); padding: 36px 40px 32px; text-align: center; }
                .header h1 { color: #fff; margin: 0; font-size: 26px; font-weight: 700; letter-spacing: -0.5px; }
                .header p { color: #c7d6fe; margin: 6px 0 0; font-size: 14px; }
                .body { padding: 36px 40px; }
                .greeting { font-size: 16px; color: #1e1b4b; margin-bottom: 16px; }
                .otp-box { background: #f0f4ff; border: 2px dashed #a5b8fc; border-radius: 16px; padding: 28px; text-align: center; margin: 24px 0; }
                .otp-label { font-size: 12px; font-weight: 700; color: #6366f1; text-transform: uppercase; letter-spacing: 1.5px; margin-bottom: 12px; }
                .otp-code { font-size: 42px; font-weight: 800; color: #312e81; letter-spacing: 12px; font-family: 'Courier New', monospace; }
                .expire-note { font-size: 13px; color: #6b7280; text-align: center; margin-top: 8px; }
                .warning { background: #fff7ed; border-left: 4px solid #f97316; border-radius: 8px; padding: 12px 16px; margin: 20px 0; font-size: 13px; color: #92400e; }
                .footer { background: #f8f9ff; border-top: 1px solid #e0e9ff; padding: 20px 40px; text-align: center; font-size: 12px; color: #9ca3af; }
              </style>
            </head>
            <body>
              <div class="container">
                <div class="header">
                  <h1>⚡ SmartBiz</h1>
                  <p>AI-Powered Business Suite</p>
                </div>
                <div class="body">
                  <p class="greeting">Hello <strong>%s</strong>,</p>
                  <p style="color:#4b5563;font-size:14px;line-height:1.6;">
                    We received a request to reset the password for your SmartBiz account. Use the OTP below to proceed.
                  </p>
                  <div class="otp-box">
                    <div class="otp-label">Your One-Time Password</div>
                    <div class="otp-code">%s</div>
                  </div>
                  <p class="expire-note">⏱ This OTP expires in <strong>5 minutes</strong></p>
                  <div class="warning">
                    <strong>Security Notice:</strong> If you did not request a password reset, please ignore this email. Your account remains secure.
                  </div>
                </div>
                <div class="footer">
                  &copy; 2024 SmartBiz &mdash; AI-Powered Business Suite<br>
                  This is an automated message, please do not reply.
                </div>
              </div>
            </body>
            </html>
            """.formatted(name, otp);
    }
}
