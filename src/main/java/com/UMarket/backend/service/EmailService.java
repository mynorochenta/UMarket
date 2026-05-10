package com.UMarket.backend.service;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

@Service
public class EmailService {

    @Autowired
    private JavaMailSender mailSender;

    @Value("${spring.mail.username}")
    private String senderEmail;

    @Value("${app.url}")
    private String appUrl;

    public void sendVerificationEmail(String destinationEmail, String token) {
        String link = appUrl + "/api/users/verify?token=" + token;

        try {
            MimeMessage mime = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(mime, true, "UTF-8");

            helper.setFrom(senderEmail);
            helper.setTo(destinationEmail);
            helper.setSubject("Verifica tu cuenta de UMarket");
            helper.setText(buildVerificationHtml(link), true);

            mailSender.send(mime);
        } catch (MessagingException e) {
            throw new RuntimeException("Error al enviar el correo de verificación", e);
        }
    }

    private String buildVerificationHtml(String link) {
        return """
            <!DOCTYPE html>
            <html lang="es">
            <head>
              <meta charset="UTF-8">
              <meta name="viewport" content="width=device-width, initial-scale=1.0">
            </head>
            <body style="margin:0;padding:0;background-color:#f3f4f6;font-family:Arial,Helvetica,sans-serif;">
              <table width="100%%" cellpadding="0" cellspacing="0" style="background-color:#f3f4f6;padding:48px 0;">
                <tr>
                  <td align="center">
                    <table width="580" cellpadding="0" cellspacing="0"
                      style="background-color:#ffffff;border-radius:16px;overflow:hidden;
                             box-shadow:0 4px 24px rgba(109,40,217,0.10);">

                      <!-- Encabezado morado -->
                      <tr>
                        <td style="background:linear-gradient(135deg,#7c3aed 0%%,#6d28d9 100%%);
                                   padding:36px 40px;text-align:center;">
                          <div style="font-size:36px;font-weight:900;color:#ffffff;
                                      letter-spacing:3px;line-height:1;">UMarket</div>
                          <div style="color:#ddd6fe;font-size:13px;margin-top:8px;
                                      letter-spacing:1px;">
                            El marketplace universitario de Guatemala
                          </div>
                        </td>
                      </tr>

                      <!-- Cuerpo -->
                      <tr>
                        <td style="padding:44px 48px 36px;">
                          <p style="font-size:22px;font-weight:700;color:#1e1b4b;margin:0 0 16px;">
                            ¡Bienvenido a UMarket! 👋
                          </p>
                          <p style="font-size:15px;color:#4b5563;line-height:1.7;margin:0 0 12px;">
                            Gracias por registrarte. Ya casi puedes empezar a comprar y
                            vender dentro de la comunidad universitaria guatemalteca.
                          </p>
                          <p style="font-size:15px;color:#4b5563;line-height:1.7;margin:0 0 36px;">
                            Para activar tu cuenta haz clic en el botón:
                          </p>

                          <!-- Botón -->
                          <table width="100%%" cellpadding="0" cellspacing="0">
                            <tr>
                              <td align="center" style="padding-bottom:36px;">
                                <a href="%s"
                                   style="display:inline-block;background-color:#7c3aed;
                                          color:#ffffff;text-decoration:none;
                                          padding:16px 48px;border-radius:10px;
                                          font-size:16px;font-weight:700;
                                          letter-spacing:0.5px;">
                                  Verificar mi cuenta
                                </a>
                              </td>
                            </tr>
                          </table>

                          <p style="font-size:13px;color:#9ca3af;margin:0 0 6px;">
                            O copia este enlace en tu navegador:
                          </p>
                          <p style="font-size:12px;color:#7c3aed;word-break:break-all;
                                    margin:0 0 36px;">%s</p>

                          <hr style="border:none;border-top:1px solid #e5e7eb;margin:0 0 24px;">

                          <p style="font-size:13px;color:#6b7280;line-height:1.6;margin:0;">
                            Este enlace expira en <strong>24 horas</strong>.
                          </p>
                        </td>
                      </tr>

                      <!-- Pie de página -->
                      <tr>
                        <td style="background-color:#f9fafb;border-top:1px solid #e5e7eb;
                                   padding:20px 48px;text-align:center;">
                          <p style="font-size:11px;color:#9ca3af;margin:0;line-height:1.6;">
                            Si no creaste esta cuenta en UMarket, puedes ignorar este correo
                            con total seguridad.<br>
                            © 2025 UMarket · Solo para estudiantes universitarios de Guatemala
                          </p>
                        </td>
                      </tr>

                    </table>
                  </td>
                </tr>
              </table>
            </body>
            </html>
            """.formatted(link, link);
    }
}
